package org.mevenide.idea.project.goals;

import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.filters.ExceptionFilter;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.RegexpFilter;
import com.intellij.ide.DataManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import java.util.*;
import org.jdom.Element;
import org.mevenide.idea.global.MavenHomeUndefinedException;
import org.mevenide.idea.global.MavenPluginsManager;
import org.mevenide.idea.project.AbstractPomSettingsManager;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.jdk.PomJdkManager;
import org.mevenide.idea.util.ui.UIUtils;

/**
 * @author Arik
 */
public class PomPluginGoalsManager extends AbstractPomSettingsManager
        implements JDOMExternalizable {
    private static final Key<Set<PluginGoal>> KEY = Key.create(PluginGoal.class.getName());

    public PomPluginGoalsManager(final Project pProject) {
        super(pProject);
    }

    /**
     * Registers the given listener for POM events.
     *
     * @param pListener the listener
     */
    public void addPomPluginGoalsListener(final PomPluginGoalsListener pListener) {
        listenerList.add(PomPluginGoalsListener.class, pListener);
    }

    /**
     * Unregisters the given listener from POM events.
     *
     * @param pListener the listener
     */
    public void removePomPluginGoalsListener(final PomPluginGoalsListener pListener) {
        listenerList.remove(PomPluginGoalsListener.class, pListener);
    }

    public void addPluginGoal(final String pPomUrl, final PluginGoal pGoal) {
        if (!isRegistered(pPomUrl))
            return;

        Set<PluginGoal> goals = get(KEY, pPomUrl);
        if (goals == null) {
            goals = new HashSet<PluginGoal>(10);
            put(KEY, pPomUrl, goals);
        }
        else if (goals.contains(pGoal))
            return;

        goals.add(pGoal);
        firePluginGoalAddedEvent(pPomUrl, pGoal);
    }

    public PluginGoal[] getPluginGoals(final String pPomUrl) {
        if (!isRegistered(pPomUrl))
            return new PluginGoal[0];

        final Set<PluginGoal> goals = get(KEY, pPomUrl);
        if (goals == null)
            return new PluginGoal[0];

        return goals.toArray(new PluginGoal[goals.size()]);
    }

    public void removePluginGoal(final String pPomUrl, final PluginGoal pGoal) {
        if (!isRegistered(pPomUrl))
            return;

        Set<PluginGoal> goals = get(KEY, pPomUrl);
        if (goals == null)
            return;
        else if (!goals.contains(pGoal))
            return;

        goals.remove(pGoal);
        firePluginGoalRemovedEvent(pPomUrl, pGoal);
    }

    public void execute(final VirtualFile pPomFile, final Goal... pGoals) {
        final VirtualFile dir = pPomFile.getParent();
        if (dir == null)
            return;

        //
        //always save all modified files before invoking maven
        //
        ApplicationManager.getApplication().saveAll();

        try {
            //
            //create the process descriptor
            //
            final ProjectJdk jdk = PomJdkManager.getInstance(project).getJdk(pPomFile.getUrl());
            if (jdk == null)
                throw CantRunException.noJdkConfigured();
            MavenJavaParameters p = new MavenJavaParameters(dir, jdk, pGoals);

            //
            //provide filters which allow linking compilation errors to source files
            //
            Filter[] filters = new Filter[]{
                    new ExceptionFilter(project),
                    new RegexpFilter(project, MavenJavaParameters.COMPILE_REGEXP)
            };

            //
            //executes the process, creating a console window for it
            //
            final StringBuilder contentNameBuf = new StringBuilder();
            for (Goal goal : pGoals) {
                if (contentNameBuf.length() > 0)
                    contentNameBuf.append(' ');
                contentNameBuf.append(goal.getName());
            }
            final String contentName = contentNameBuf.toString();
            ExecutionManager.getInstance(project).execute(p,
                                                          contentName,
                                                          DataManager.getInstance().getDataContext(),
                                                          filters);
        }
        catch (MavenHomeUndefinedException e) {
            UIUtils.showError(project, e);
            LOG.trace(e.getMessage(), e);
        }
        catch (CantRunException e) {
            UIUtils.showError(project, e);
            LOG.trace(e.getMessage(), e);
        }
        catch (ExecutionException e) {
            UIUtils.showError(project, e);
            LOG.trace(e.getMessage(), e);
        }
    }

    public void readExternal(final Element pElt) throws InvalidDataException {
        final Map<String, Map<String, Set<String>>> data = new HashMap<String, Map<String, Set<String>>>(
                10);

        //noinspection UNCHECKED_WARNING
        final List<Element> pomElts = pElt.getChildren("pom");
        for (Element pomElt : pomElts) {
            final String url = pomElt.getAttributeValue("url");
            if (url == null || url.trim().length() == 0)
                continue;

            final Map<String, Set<String>> goals = new HashMap<String, Set<String>>();
            data.put(url, goals);

            //noinspection UNCHECKED_WARNING
            final List<Element> goalElts = pomElt.getChildren("goal");
            for (Element goalElt : goalElts) {
                final String goal = goalElt.getAttributeValue("name");
                final String plugin = goalElt.getAttributeValue("plugin");

                if (!goals.containsKey(plugin))
                    goals.put(plugin, new HashSet<String>());

                goals.get(plugin).add(goal);
            }
        }

        final MavenPluginsManager pluginsMgr = MavenPluginsManager.getInstance(project);
        final Runnable loader = new Runnable() {
            public void run() {
                final Set<Map.Entry<String, Map<String, Set<String>>>> entries = data.entrySet();
                for (Map.Entry<String, Map<String, Set<String>>> pomEntry : entries) {
                    final String url = pomEntry.getKey();
                    final Map<String, Set<String>> goals = pomEntry.getValue();

                    final Set<Map.Entry<String, Set<String>>> goalEntries = goals.entrySet();
                    for (Map.Entry<String, Set<String>> goalEntry : goalEntries) {
                        final String id = goalEntry.getKey();
                        final PluginGoalContainer plugin = pluginsMgr.getPlugin(id);
                        if (plugin == null)
                            continue;

                        final Set<String> pluginGoals = goalEntry.getValue();
                        for (String goalName : pluginGoals) {
                            final PluginGoal goal = plugin.getGoal(goalName);
                            if (goal != null)
                                addPluginGoal(url, goal);
                        }
                    }
                }

            }
        };

        StartupManager.getInstance(project).registerPostStartupActivity(loader);
    }

    public void writeExternal(final Element pElt) throws WriteExternalException {
        final PomManager pomMgr = PomManager.getInstance(project);
        final VirtualFilePointer[] filePointers = pomMgr.getFilePointers();

        for (VirtualFilePointer pointer : filePointers) {
            final Element pomElt = new Element("pom");
            pomElt.setAttribute("url", pointer.getUrl());

            final PluginGoal[] goals = getPluginGoals(pointer.getUrl());
            for (PluginGoal goal : goals) {
                final Element goalElt = new Element("goal");
                goalElt.setAttribute("plugin", goal.getContainer().getId());
                goalElt.setAttribute("name", goal.getName());
                pomElt.addContent(goalElt);
            }

            pElt.addContent(pomElt);
        }
    }

    protected void firePluginGoalAddedEvent(final String pPomUrl, final PluginGoal pGoal) {
        final PomPluginGoalsListener[] listeners = listenerList.getListeners(PomPluginGoalsListener.class);
        PomPluginGoalEvent event = null;
        for (PomPluginGoalsListener listener : listeners) {
            if (event == null)
                event = new PomPluginGoalEvent(this, pPomUrl, pGoal, null);
            listener.pomPluginGoalAdded(event);
        }
    }

    protected void firePluginGoalRemovedEvent(final String pPomUrl, final PluginGoal pGoal) {
        final PomPluginGoalsListener[] listeners = listenerList.getListeners(PomPluginGoalsListener.class);
        PomPluginGoalEvent event = null;
        for (PomPluginGoalsListener listener : listeners) {
            if (event == null)
                event = new PomPluginGoalEvent(this, pPomUrl, null, pGoal);
            listener.pomPluginGoalRemoved(event);
        }
    }

    public static PomPluginGoalsManager getInstance(final Project pProject) {
        return pProject.getComponent(PomPluginGoalsManager.class);
    }
}
