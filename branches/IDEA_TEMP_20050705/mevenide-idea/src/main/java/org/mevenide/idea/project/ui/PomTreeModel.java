package org.mevenide.idea.project.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import org.mevenide.idea.global.MavenManager;
import org.mevenide.idea.global.MavenPluginsManager;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.PomManagerEvent;
import org.mevenide.idea.project.PomManagerListener;
import org.mevenide.idea.project.model.GoalInfo;
import org.mevenide.idea.project.model.PluginInfo;

/**
 * @author Arik
 */
public class PomTreeModel extends DefaultTreeModel implements Disposable, PomManagerListener,
                                                              PropertyChangeListener {
    /**
     * The project this tree is created for.
     */
    private final Project project;

    /**
     * The node containing all available Maven plugins.
     */
    private DefaultMutableTreeNode pluginsNode = new DefaultMutableTreeNode("Plugins");

    /**
     * The node containing all available Maven plugins.
     */
    private DefaultMutableTreeNode projectsNode = new DefaultMutableTreeNode("Projects");

    /**
     * Creates an instance for the given project.
     *
     * @param pProject the project
     */
    public PomTreeModel(final Project pProject) {
        super(new DefaultMutableTreeNode(), true);
        project = pProject;

        //
        //register for events, to refresh when needed
        //
        MavenManager.getInstance().addPropertyChangeListener("mavenHome", this);
        PomManager.getInstance(project).addPomManagerListener(this);

        //
        //build model
        //
        final DefaultMutableTreeNode root = (DefaultMutableTreeNode) super.root;
        root.insert(projectsNode, root.getChildCount());
        root.insert(pluginsNode, root.getChildCount());
        refresh(false);
    }

    public void dispose() {
        MavenManager.getInstance().removePropertyChangeListener("mavenHome", this);
        PomManager.getInstance(project).removePomManagerListener(this);
    }

    public PomNode[] getProjectNodes() {
        final PomNode[] pomNodes = new PomNode[projectsNode.getChildCount()];
        int i = 0;
        //noinspection UNCHECKED_WARNING
        final Enumeration<PomNode> children = projectsNode.children();
        while (children.hasMoreElements()) pomNodes[i++] = children.nextElement();

        return pomNodes;
    }

    public void refresh() {
        refresh(true);
    }

    public void refresh(final boolean pNotifyListeners) {
        refreshProjects(pNotifyListeners);
        refreshPlugins(pNotifyListeners);
    }

    public void refreshProjects() {
        refreshProjects(true);
    }

    public void refreshProjects(final boolean pNotifyListeners) {
        parseProjects();
        if (pNotifyListeners)
            nodeStructureChanged(projectsNode);
    }

    public void refreshPlugins() {
        refreshPlugins(true);
    }

    public void refreshPlugins(final boolean pNotifyListeners) {
        parsePlugins();
        if (pNotifyListeners)
            nodeStructureChanged(pluginsNode);
    }

    public void pomAdded(final PomManagerEvent pEvent) {
        final VirtualFilePointer pointer = pEvent.getFilePointer();

        final PomNode node = createPomNode(pointer);
        projectsNode.insert(node, projectsNode.getChildCount());
        nodesWereInserted(projectsNode, new int[]{projectsNode.getIndex(node)});
    }

    public void pomRemoved(final PomManagerEvent pEvent) {
        final MutableTreeNode node = findPomNode(pEvent.getFilePointer());
        if (node != null) {
            final int index = projectsNode.getIndex(node);
            node.removeFromParent();
            nodesWereRemoved(projectsNode, new int[]{index}, new Object[]{node});
        }
    }

    public void pomValidityChanged(final PomManagerEvent pEvent) {
        final VirtualFilePointer filePointer = pEvent.getFilePointer();
        final PomNode node = findPomNode(filePointer);
        if (node != null) {
            if (filePointer.isValid()) {
                final int index = projectsNode.getIndex(node);

                node.removeFromParent();
                projectsNode.insert(createPomNode(filePointer), index);

                nodeStructureChanged(projectsNode);
            }
            else {
                node.removeAllChildren();
                nodeStructureChanged(node);
            }
        }
    }

    public void pomGoalsChanged(final PomManagerEvent pEvent) {
        final VirtualFilePointer filePointer = pEvent.getFilePointer();
        final PomNode node = findPomNode(filePointer);
        if (node != null) {
            final int index = projectsNode.getIndex(node);

            node.removeFromParent();
            projectsNode.insert(createPomNode(filePointer), index);

            nodeStructureChanged(projectsNode);
        }
    }

    public void pomJdkChanged(PomManagerEvent pEvent) {
    }

    public void propertyChange(final PropertyChangeEvent pEvent) {
        final Object src = pEvent.getSource();
        final String propertyName = pEvent.getPropertyName();

        if (src instanceof MavenManager && "mavenHome".equals(propertyName))
            refreshPlugins();
    }

    private void parseProjects() {
        projectsNode.removeAllChildren();
        final PomManager pomManager = PomManager.getInstance(project);
        final VirtualFilePointer[] pointers = pomManager.getPomPointers();

        //
        //sort the projects by file name (url)
        //
        Arrays.sort(pointers, new Comparator<VirtualFilePointer>() {
            public int compare(final VirtualFilePointer o1, final VirtualFilePointer o2) {
                return o1.getPresentableUrl().compareToIgnoreCase(o2.getPresentableUrl());
            }
        });

        //
        //create tree nodes
        //
        for (VirtualFilePointer pointer : pointers) {
            final PomNode pomNode = createPomNode(pointer);
            projectsNode.insert(pomNode, projectsNode.getChildCount());
        }
    }

    private PomNode createPomNode(final VirtualFilePointer pPointer) {
        final PomManager pomMgr = PomManager.getInstance(project);

        final PomNode pomNode = new PomNode(pPointer);
        final GoalInfo[] goals = pomMgr.getGoals(pPointer.getUrl());

        //
        //sort goals
        //
        Arrays.sort(goals, new Comparator<GoalInfo>() {
            public int compare(final GoalInfo o1, final GoalInfo o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        //
        //create nodes
        //
        for (GoalInfo goal : goals) {
            final GoalNode goalNode = new GoalNode(goal);
            pomNode.insert(goalNode, pomNode.getChildCount());
        }

        return pomNode;
    }

    private void parsePlugins() {
        pluginsNode.removeAllChildren();
        final MavenPluginsManager pluginsMgr = MavenPluginsManager.getInstance(project);
        final PluginInfo[] plugins = pluginsMgr.getPlugins();

        //
        //sort the plugins by name
        //
        Arrays.sort(plugins, new Comparator<PluginInfo>() {
            public int compare(final PluginInfo o1, final PluginInfo o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        //
        //create tree nodes
        //
        for (PluginInfo plugin : plugins) {
            final PluginNode pluginNode = createPluginNode(plugin);
            pluginsNode.insert(pluginNode, pluginsNode.getChildCount());
        }
    }

    private PluginNode createPluginNode(final PluginInfo plugin) {
        final PluginNode pluginNode = new PluginNode(plugin);
        final GoalInfo[] goals = plugin.getGoals();

        //
        //sort the plugins by name
        //
        Arrays.sort(goals, new Comparator<GoalInfo>() {
            public int compare(final GoalInfo o1, final GoalInfo o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        //
        //create tree nodes
        //
        for (GoalInfo goal : goals)
            pluginNode.insert(new GoalNode(goal), pluginNode.getChildCount());

        return pluginNode;
    }

    private PomNode findPomNode(final VirtualFilePointer pPointer) {
        //noinspection UNCHECKED_WARNING
        final Enumeration<PomNode> children = projectsNode.children();
        while (children.hasMoreElements()) {
            final PomNode node = children.nextElement();
            if (pPointer.equals(node.getUserObject()))
                return node;
        }

        return null;
    }
}
