package org.mevenide.idea.project;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerListener;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.xml.XmlFile;
import java.util.*;
import javax.swing.event.EventListenerList;
import org.jdom.Element;
import org.mevenide.idea.global.MavenPluginsManager;
import org.mevenide.idea.project.model.GoalInfo;
import org.mevenide.idea.project.model.PluginInfo;
import org.mevenide.idea.project.ui.PomManagerPanel;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.project.impl.DefaultPsiProject;
import org.mevenide.idea.psi.util.PsiUtils;
import org.mevenide.idea.util.components.AbstractProjectComponent;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class PomManager extends AbstractProjectComponent
        implements JDOMExternalizable, VirtualFilePointerListener {
    /**
     * The key under which the POM entries are stored in the file pointers.
     */
    private static final Key<PsiProject> POM_KEY = Key.create(PsiProject.class.getName());

    /**
     * The key under which the POM entries are stored in the file pointers.
     */
    private static final Key<Set<GoalInfo>> GOALS_KEY = Key.create("goals");

    /**
     * The key under which the JDK to use for running goals is stored.
     */
    private static final Key<ProjectJdk> JDK_KEY = Key.create(ProjectJdk.class.getName());

    /**
     * Tracks file pointers.
     */
    private final VirtualFilePointerHelper helper = new VirtualFilePointerHelper(this);

    /**
     * Event listeners support.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * Used as a temporary cache for data loaded in {@link #readExternal(org.jdom.Element)}. This
     * data will then be incorporated into saved file pointers in the {@link #projectOpened()}
     * method.
     *
     * <p>The reason the {@link #readExternal(org.jdom.Element)} method doesn't load the data into
     * the file pointers is simply because PSI access is disallowed in IDEA before startup has
     * completed.</p>
     */
    private final Map<String, Map<String, Set<String>>> loadedGoals = new HashMap<String, Map<String, Set<String>>>(
            10);

    /**
     * Creates an instance for the given project. Called by IDEA on project load.
     *
     * @param pProject the project this component will serve
     */
    public PomManager(final Project pProject) {
        super(pProject);
    }

    public void add(final VirtualFile pFile) {
        add(pFile.getUrl());
    }

    public void add(final String pUrl) {
        final VirtualFilePointer pointer = helper.add(pUrl);
        firePomAddedEvent(pointer);
    }

    public boolean contains(final String pUrl) {
        return helper.isRegistered(pUrl);
    }

    public boolean contains(final VirtualFile pFile) {
        return helper.isRegistered(pFile);
    }

    public VirtualFilePointer[] getPomPointers() {
        return helper.getFilePointers();
    }

    public void remove(final VirtualFile pFile) {
        remove(pFile.getUrl());
    }

    public void remove(final String pUrl) {
        final VirtualFilePointer pointer = helper.remove(pUrl);
        firePomRemovedEvent(pointer);
    }

    public ProjectJdk getJdk(final VirtualFile pFile) {
        return getJdk(pFile.getUrl());
    }

    public ProjectJdk getJdk(final String pUrl) {
        final ProjectJdk defJDk = ProjectRootManager.getInstance(project).getProjectJdk();
        final VirtualFilePointer filePointer = helper.get(pUrl);
        if (filePointer == null)
            return defJDk;

        final ProjectJdk jdk = filePointer.getUserData(JDK_KEY);
        return jdk == null ? defJDk : jdk;
    }

    public void setJdk(final VirtualFile pFile, final ProjectJdk pJdk) {
        setJdk(pFile.getUrl(), pJdk);
    }

    public void setJdk(final String pUrl, final ProjectJdk pJdk) {
        if (!contains(pUrl))
            return;

        final VirtualFilePointer filePointer = helper.get(pUrl);
        if (filePointer == null)
            return;

        filePointer.putUserData(JDK_KEY, pJdk);
        firePomJdkChangedEvent(filePointer);
    }

    public PsiProject getPsiProject(final VirtualFile pFile) {
        return getPsiProject(pFile.getUrl());
    }

    public PsiProject getPsiProject(final String pUrl) {
        final VirtualFilePointer filePointer = helper.get(pUrl);
        if (filePointer == null || !filePointer.isValid())
            return null;

        final VirtualFile file = filePointer.getFile();
        if (file == null || !file.isValid() || file.isDirectory())
            return null;

        PsiProject psi = filePointer.getUserData(POM_KEY);
        if (psi == null || !file.equals(psi.getXmlFile().getVirtualFile())) {
            final XmlFile xmlFile = PsiUtils.findXmlFile(project, file);
            assert xmlFile != null;
            psi = new DefaultPsiProject(xmlFile);
        }

        return psi;
    }

    public void addGoal(final VirtualFile pFile, final GoalInfo pGoal) {
        addGoal(pFile.getUrl(), pGoal);
    }

    public void addGoal(final String pUrl, final GoalInfo pGoal) {
        if (!contains(pUrl) || pGoal == null)
            return;

        final VirtualFilePointer filePointer = helper.get(pUrl);
        if (filePointer == null)
            return;

        Set<GoalInfo> goals = filePointer.getUserData(GOALS_KEY);
        if (goals == null) {
            goals = new HashSet<GoalInfo>(10);
            filePointer.putUserData(GOALS_KEY, goals);
        }

        goals.add(pGoal);
        firePomGoalsChangedEvent(filePointer);
    }

    public GoalInfo[] getGoals(final VirtualFile pFile) {
        return getGoals(pFile.getUrl());
    }

    public GoalInfo[] getGoals(final String pUrl) {
        final VirtualFilePointer filePointer = helper.get(pUrl);
        if (filePointer == null)
            return new GoalInfo[0];

        final Set<GoalInfo> goals = filePointer.getUserData(GOALS_KEY);
        if (goals == null)
            return new GoalInfo[0];

        return goals.toArray(new GoalInfo[goals.size()]);
    }

    public void removeGoal(final VirtualFile pFile, final GoalInfo pGoal) {
        removeGoal(pFile.getUrl(), pGoal);
    }

    public void removeGoal(final String pUrl, final GoalInfo pGoal) {
        if (!contains(pUrl) || pGoal == null)
            return;

        final VirtualFilePointer filePointer = helper.get(pUrl);
        if (filePointer == null)
            return;

        Set<GoalInfo> goals = filePointer.getUserData(GOALS_KEY);
        if (goals == null)
            return;

        goals.remove(pGoal);
        firePomGoalsChangedEvent(filePointer);
    }

    /**
     * Registers the given listener for POM events.
     *
     * @param pListener the listener
     */
    public void addPomManagerListener(final PomManagerListener pListener) {
        listenerList.add(PomManagerListener.class, pListener);
    }

    /**
     * Unregisters the given listener from POM events.
     *
     * @param pListener the listener
     */
    public void removePomManagerListener(final PomManagerListener pListener) {
        listenerList.remove(PomManagerListener.class, pListener);
    }

    public void beforeValidityChanged(VirtualFilePointer[] pointers) {
    }

    /**
     * Activated by IDEA when one or more of the registered POM files changed validity.
     *
     * <p>This method will notify all registered {@link PomManagerListener POM listeners} that the
     * specified files have changed validitiy.</p>
     *
     * @param pPointers
     */
    public void validityChanged(final VirtualFilePointer[] pPointers) {
        for (VirtualFilePointer pointer : pPointers)
            firePomValidityChangedEvent(pointer);
    }

    @Override
    public void projectOpened() {
        final Runnable regToolWinRunnable = new Runnable() {
            public void run() {
                final MavenPluginsManager pluginsMgr = MavenPluginsManager.getInstance(project);

                //
                //load registered POMs and goals
                //
                final Set<Map.Entry<String, Map<String, Set<String>>>> entries = loadedGoals.entrySet();
                for (Map.Entry<String, Map<String, Set<String>>> pomEntry : entries) {
                    final String url = pomEntry.getKey();
                    final Map<String, Set<String>> goalsCache = pomEntry.getValue();

                    final Set<Map.Entry<String, Set<String>>> goalEntries = goalsCache.entrySet();
                    for (Map.Entry<String, Set<String>> goalEntry : goalEntries) {
                        final String id = goalEntry.getKey();
                        final PluginInfo plugin = pluginsMgr.getPlugin(id);

                        final Set<String> goals = goalEntry.getValue();
                        for (String goalName : goals) {
                            final GoalInfo goal = plugin.getGoal(goalName);
                            addGoal(url, goal);
                        }
                    }
                }

                //
                //register tool window - this is done in a future runnable
                //because if we create the pom manager panel now, it will
                //try to load available goals from maven. this is done via
                //PSI, but unfortunately, PSI is not available at this stage.
                //
                final ToolWindowManager twMgr = ToolWindowManager.getInstance(project);
                final PomManagerPanel ui = new PomManagerPanel(project);
                final ToolWindow tw = twMgr.registerToolWindow(PomManagerPanel.TITLE,
                                                               ui,
                                                               ToolWindowAnchor.RIGHT);
                tw.setIcon(Icons.MAVEN);
            }
        };

        ApplicationManager.getApplication().invokeLater(regToolWinRunnable);
    }

    @Override
    public void projectClosed() {
        final ToolWindowManager twMgr = ToolWindowManager.getInstance(project);
        twMgr.unregisterToolWindow(PomManagerPanel.TITLE);
    }

    /**
     * Returns the POM manager's tool window.
     *
     * @return the tool window
     */
    public ToolWindow getToolWindow() {
        final ToolWindowManager mgr = ToolWindowManager.getInstance(project);
        return mgr.getToolWindow(PomManagerPanel.TITLE);
    }

    public PomManagerPanel getToolWindowComponent() {
        final ToolWindow tw = getToolWindow();
        if (tw != null)
            return (PomManagerPanel) tw.getComponent();

        return null;
    }

    /**
     * Fires the {@link PomManagerListener#pomAdded(PomManagerEvent)} event.
     *
     * @param pPointer the file pointer of the modified POM
     */
    protected void firePomAddedEvent(final VirtualFilePointer pPointer) {
        final PomManagerListener[] listeners = listenerList.getListeners(PomManagerListener.class);
        PomManagerEvent event = null;
        for (PomManagerListener listener : listeners) {
            if (event == null) {
                event = new PomManagerEvent(this, pPointer);
            }
            listener.pomAdded(event);
        }
    }

    /**
     * Fires the {@link PomManagerListener#pomRemoved(PomManagerEvent)} event.
     *
     * @param pPointer the file pointer of the modified POM
     */
    protected void firePomRemovedEvent(final VirtualFilePointer pPointer) {
        final PomManagerListener[] listeners = listenerList.getListeners(PomManagerListener.class);
        PomManagerEvent event = null;
        for (PomManagerListener listener : listeners) {
            if (event == null)
                event = new PomManagerEvent(this, pPointer);
            listener.pomRemoved(event);
        }
    }

    /**
     * Fires the {@link PomManagerListener#pomValidityChanged(PomManagerEvent)} event.
     *
     * @param pPointer the file pointer of the modified POM
     */
    protected void firePomValidityChangedEvent(final VirtualFilePointer pPointer) {
        final PomManagerListener[] listeners = listenerList.getListeners(PomManagerListener.class);
        PomManagerEvent event = null;
        for (PomManagerListener listener : listeners) {
            if (event == null)
                event = new PomManagerEvent(this, pPointer);
            listener.pomValidityChanged(event);
        }
    }

    /**
     * Fires the {@link PomManagerListener#pomGoalsChanged(PomManagerEvent)} event.
     *
     * @param pPointer the file pointer of the modified POM
     */
    protected void firePomGoalsChangedEvent(final VirtualFilePointer pPointer) {
        final PomManagerListener[] listeners = listenerList.getListeners(PomManagerListener.class);
        PomManagerEvent event = null;
        for (PomManagerListener listener : listeners) {
            if (event == null)
                event = new PomManagerEvent(this, pPointer);
            listener.pomGoalsChanged(event);
        }
    }

    /**
     * Fires the {@link PomManagerListener#pomJdkChanged(PomManagerEvent)} event.
     *
     * @param pPointer the file pointer of the modified POM
     */
    protected void firePomJdkChangedEvent(final VirtualFilePointer pPointer) {
        final PomManagerListener[] listeners = listenerList.getListeners(PomManagerListener.class);
        PomManagerEvent event = null;
        for (PomManagerListener listener : listeners) {
            if (event == null)
                event = new PomManagerEvent(this, pPointer);
            listener.pomJdkChanged(event);
        }
    }

    public void readExternal(final Element pElt) throws InvalidDataException {
        loadedGoals.clear();

        //noinspection UNCHECKED_WARNING
        final List<Element> pomElts = pElt.getChildren("pom");
        for (Element pomElt : pomElts) {
            final String url = pomElt.getAttributeValue("url");
            if (url == null || url.trim().length() == 0)
                continue;

            add(url);
            final String jdkName = pomElt.getAttributeValue("jdk");
            if (jdkName != null && jdkName.trim().length() > 0) {
                final ProjectJdk jdk = ProjectJdkTable.getInstance().findJdk(jdkName);
                setJdk(url, jdk);
            }

            final Map<String, Set<String>> goalsCache = new HashMap<String, Set<String>>();
            loadedGoals.put(url, goalsCache);

            //noinspection UNCHECKED_WARNING
            final List<Element> goalElts = pomElt.getChildren("goal");
            for (Element goalElt : goalElts) {
                final String goal = goalElt.getAttributeValue("name");
                final String plugin = goalElt.getAttributeValue("plugin");

                if (!goalsCache.containsKey(plugin))
                    goalsCache.put(plugin, new HashSet<String>());

                goalsCache.get(plugin).add(goal);
            }
        }
    }

    public void writeExternal(final Element pElt) throws WriteExternalException {
        final VirtualFilePointer[] filePointers = helper.getFilePointers();
        for (VirtualFilePointer pointer : filePointers) {
            final Element pomElt = new Element("pom");
            pomElt.setAttribute("url", pointer.getUrl());
            final ProjectJdk jdk = getJdk(pointer.getUrl());
            if (jdk != null)
                pomElt.setAttribute("jdk", jdk.getName());

            final GoalInfo[] goals = getGoals(pointer.getUrl());
            for (GoalInfo goal : goals) {
                final Element goalElt = new Element("goal");
                goalElt.setAttribute("plugin", goal.getPlugin().getId());
                goalElt.setAttribute("name", goal.getName());
                pomElt.addContent(goalElt);
            }

            pElt.addContent(pomElt);
        }
    }

    /**
     * Returns the PSI-POM manager for the specified project.
     *
     * @param pProject the project to retrieve the component for
     *
     * @return instance
     */
    public static PomManager getInstance(final Project pProject) {
        return pProject.getComponent(PomManager.class);
    }


}
