package org.mevenide.idea.project.goals;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import java.util.*;
import org.jdom.Element;
import org.mevenide.idea.global.MavenPluginsManager;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.support.AbstractPomSettingsManager;

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

    public void addPluginGoal(final String pUrl, final PluginGoal pGoal) {
        if (!isRegistered(pUrl))
            return;

        Set<PluginGoal> goals = get(KEY, pUrl);
        if (goals == null) {
            goals = new HashSet<PluginGoal>(10);
            put(KEY, pUrl, goals);
        }
        else if (goals.contains(pGoal))
            return;

        goals.add(pGoal);
        firePluginGoalAddedEvent(pUrl, pGoal);
    }

    public PluginGoal[] getPluginGoals(final String pUrl) {
        if (!isRegistered(pUrl))
            return new PluginGoal[0];

        final Set<PluginGoal> goals = get(KEY, pUrl);
        if (goals == null)
            return new PluginGoal[0];

        return goals.toArray(new PluginGoal[goals.size()]);
    }

    public void removePluginGoal(final String pUrl, final PluginGoal pGoal) {
        if (!isRegistered(pUrl))
            return;

        Set<PluginGoal> goals = get(KEY, pUrl);
        if (goals == null)
            return;
        else if (!goals.contains(pGoal))
            return;

        goals.remove(pGoal);
        firePluginGoalRemovedEvent(pUrl, pGoal);
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

    protected void firePluginGoalAddedEvent(final String pUrl, final PluginGoal pGoal) {
        final PomPluginGoalsListener[] listeners = listenerList.getListeners(PomPluginGoalsListener.class);
        PomPluginGoalEvent event = null;
        for (PomPluginGoalsListener listener : listeners) {
            if (event == null)
                event = new PomPluginGoalEvent(this, pUrl, pGoal, null);
            listener.pomPluginGoalAdded(event);
        }
    }

    protected void firePluginGoalRemovedEvent(final String pUrl, final PluginGoal pGoal) {
        final PomPluginGoalsListener[] listeners = listenerList.getListeners(PomPluginGoalsListener.class);
        PomPluginGoalEvent event = null;
        for (PomPluginGoalsListener listener : listeners) {
            if (event == null)
                event = new PomPluginGoalEvent(this, pUrl, null, pGoal);
            listener.pomPluginGoalRemoved(event);
        }
    }

    public static PomPluginGoalsManager getInstance(final Project pProject) {
        return pProject.getComponent(PomPluginGoalsManager.class);
    }
}
