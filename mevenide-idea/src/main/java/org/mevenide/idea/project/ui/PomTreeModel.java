package org.mevenide.idea.project.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.mevenide.idea.global.MavenPluginsManager;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.PomManagerEvent;
import org.mevenide.idea.project.PomManagerListener;
import org.mevenide.idea.project.goals.*;

/**
 * @author Arik
 */
public class PomTreeModel extends DefaultTreeModel implements Disposable,
                                                              PomManagerListener,
                                                              PomPluginGoalsListener {
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
     * Comparator for sorting {@link Goal goals}.
     */
    private static final Comparator<Goal> GOAL_COMPARATOR = new Comparator<Goal>() {
        public int compare(final Goal o1, final Goal o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    /**
     * Comparator for sorting {@link GoalContainer goal containers}.
     */
    private static final Comparator<GoalContainer> GOAL_CONTAINER_COMPARATOR = new Comparator<GoalContainer>() {
        public int compare(final GoalContainer o1, final GoalContainer o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };

    private final PropertyChangeListener MAVEN_PLUGINS_LISTENER = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if ("plugins".equals(evt.getPropertyName()))
                refreshPlugins();
        }
    };

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
        PomManager.getInstance(project).addPomManagerListener(this);
        PomPluginGoalsManager.getInstance(project).addPomPluginGoalsListener(this);
        MavenPluginsManager.getInstance(project).addPropertyChangeListener(
                "plugins", MAVEN_PLUGINS_LISTENER);

        //
        //build model
        //
        final DefaultMutableTreeNode root = (DefaultMutableTreeNode) super.root;
        root.insert(projectsNode, root.getChildCount());
        root.insert(pluginsNode, root.getChildCount());
        refresh(false);
    }

    public DefaultMutableTreeNode getPluginsNode() {
        return pluginsNode;
    }

    public DefaultMutableTreeNode getProjectsNode() {
        return projectsNode;
    }

    public void dispose() {
        PomManager.getInstance(project).removePomManagerListener(this);
        PomPluginGoalsManager.getInstance(project).removePomPluginGoalsListener(this);
        MavenPluginsManager.getInstance(project).removePropertyChangeListener("plugins",
                                                                              MAVEN_PLUGINS_LISTENER);
    }

    public void refresh() {
        refresh(true);
    }

    public void refreshProjects() {
        refreshProjects(true);
    }

    public void refreshPlugins() {
        refreshPlugins(true);
    }

    /**
     * Returns the ancestor {@link PomNode} for the given node.
     *
     * @param pNode the node
     *
     * @return a {@link PomNode} up the given node's hierarchy, or {@code null}
     */
    public PomNode getPomNode(final TreeNode pNode) {
        TreeNode parent = pNode;
        while (parent != null && !(parent instanceof PomNode)) parent = parent.getParent();

        return (PomNode) parent;
    }

    public void pomAdded(final PomManagerEvent pEvent) {
        final PomNode node = createPomNode(pEvent.getUrl());
        projectsNode.insert(node, projectsNode.getChildCount());
        nodesWereInserted(projectsNode, new int[]{projectsNode.getIndex(node)});
    }

    public void pomRemoved(final PomManagerEvent pEvent) {
        final MutableTreeNode node = findPomNode(pEvent.getUrl());
        if (node != null) {
            final int index = projectsNode.getIndex(node);
            node.removeFromParent();
            nodesWereRemoved(projectsNode, new int[]{index}, new Object[]{node});
        }
    }

    public void pomValidityChanged(final PomManagerEvent pEvent) {
        final PomManager pomMgr = PomManager.getInstance(project);
        final String url = pEvent.getUrl();
        final PomNode node = findPomNode(url);
        if (node != null) {
            if (pomMgr.isValid(url)) {
                final int index = projectsNode.getIndex(node);

                node.removeFromParent();
                projectsNode.insert(createPomNode(url), index);

                nodeStructureChanged(projectsNode);
            }
            else {
                node.removeAllChildren();
                nodeStructureChanged(node);
            }
        }
    }

    public void pomPluginGoalAdded(final PomPluginGoalEvent pEvent) {
        final PluginGoal goal = pEvent.getAddedGoal();
        final PluginGoalNode goalNode = new PluginGoalNode(goal);
        final PomNode pomNode = findPomNode(pEvent.getPomUrl());
        pomNode.insert(goalNode, pomNode.getChildCount());

        final int[] childIndices = new int[]{pomNode.getIndex(goalNode)};
        nodesWereInserted(pomNode, childIndices);
    }

    public void pomPluginGoalRemoved(final PomPluginGoalEvent pEvent) {
        final PluginGoal goal = pEvent.getRemovedGoal();
        final PomNode pomNode = findPomNode(pEvent.getPomUrl());

        //noinspection unchecked
        final Enumeration<TreeNode> children = pomNode.children();
        while (children.hasMoreElements()) {
            final TreeNode node = children.nextElement();
            if (node instanceof PluginGoalNode) {
                final PluginGoalNode goalNode = (PluginGoalNode) node;
                if (goalNode.getGoal().equals(goal)) {

                    final int index = pomNode.getIndex(goalNode);
                    final int[] childIndices = new int[]{index};
                    final Object[] removedChildren = new Object[]{goalNode};

                    goalNode.removeFromParent();
                    nodesWereRemoved(pomNode, childIndices, removedChildren);
                    return;
                }
            }
        }
    }

    private void parseProjects() {
        projectsNode.removeAllChildren();
        final PomManager pomManager = PomManager.getInstance(project);
        final String[] urls = pomManager.getFileUrls();

        //
        //sort the projects by file name (url)
        //
        Arrays.sort(urls);

        //
        //create tree nodes
        //
        for (String url : urls) {
            final PomNode pomNode = createPomNode(url);
            projectsNode.insert(pomNode, projectsNode.getChildCount());
        }
    }

    private PomNode createPomNode(final String pPomUrl) {
        final PomPluginGoalsManager plgMgr = PomPluginGoalsManager.getInstance(project);

        final PomNode pomNode = new PomNode(pPomUrl);
        final PluginGoal[] goals = plgMgr.getPluginGoals(pPomUrl);
        Arrays.sort(goals, GOAL_COMPARATOR);

        //
        //create nodes
        //
        for (PluginGoal goal : goals) {
            final PluginGoalNode goalNode = new PluginGoalNode(goal);
            pomNode.insert(goalNode, pomNode.getChildCount());
        }

        return pomNode;
    }

    private void parsePlugins() {
        pluginsNode.removeAllChildren();
        final MavenPluginsManager pluginsMgr = MavenPluginsManager.getInstance(project);
        final PluginGoalContainer[] plugins = pluginsMgr.getPlugins();

        //
        //sort the plugins by name
        //
        Arrays.sort(plugins, GOAL_CONTAINER_COMPARATOR);

        //
        //create tree nodes
        //
        for (PluginGoalContainer plugin : plugins) {
            final PluginNode pluginNode = createPluginNode(plugin);
            pluginsNode.insert(pluginNode, pluginsNode.getChildCount());
        }
    }

    private PluginNode createPluginNode(final PluginGoalContainer plugin) {
        final PluginNode pluginNode = new PluginNode(plugin);
        final PluginGoal[] goals = plugin.getGoals();

        //
        //sort the plugins by name
        //
        Arrays.sort(goals, GOAL_COMPARATOR);

        //
        //create tree nodes
        //
        for (PluginGoal goal : goals) {
            final PluginGoalNode node = new PluginGoalNode(goal);
            pluginNode.insert(node, pluginNode.getChildCount());
        }

        return pluginNode;
    }

    private PomNode findPomNode(final String pPomUrl) {
        //noinspection unchecked
        final Enumeration<PomNode> children = projectsNode.children();
        while (children.hasMoreElements()) {
            final PomNode node = children.nextElement();
            if (pPomUrl.equalsIgnoreCase(node.getUserObject()))
                return node;
        }

        return null;
    }

    private void refresh(final boolean pNotifyListeners) {
        refreshProjects(pNotifyListeners);
        refreshPlugins(pNotifyListeners);
    }

    private void refreshProjects(final boolean pNotifyListeners) {
        parseProjects();
        if (pNotifyListeners)
            nodeStructureChanged(projectsNode);
    }

    private void refreshPlugins(final boolean pNotifyListeners) {
        parsePlugins();
        if (pNotifyListeners)
            nodeStructureChanged(pluginsNode);
    }
}
