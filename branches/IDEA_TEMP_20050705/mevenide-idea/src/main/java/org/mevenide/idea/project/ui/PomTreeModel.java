package org.mevenide.idea.project.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import org.mevenide.idea.global.MavenPluginsManager;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.PomManagerEvent;
import org.mevenide.idea.project.PomManagerListener;
import org.mevenide.idea.project.model.GoalInfo;
import org.mevenide.idea.project.model.PluginInfo;

/**
 * @author Arik
 */
public class PomTreeModel extends DefaultTreeModel implements Disposable, PomManagerListener {
    /**
     * The project this tree is created for.
     */
    private final Project project;

    /**
     * The node containing all available Maven plugins.
     */
    private MutableTreeNode pluginsNode = new DefaultMutableTreeNode("Plugins");

    /**
     * Creates an instance for the given project.
     *
     * @param pProject the project
     */
    public PomTreeModel(final Project pProject) {
        super(new DefaultMutableTreeNode(), true);

        project = pProject;
        final PomManager pomManager = PomManager.getInstance(project);
        pomManager.addPomManagerListener(this);

        //
        //create a node for each available POM in the project
        //
        final MutableTreeNode root = getRoot();
        final VirtualFilePointer[] pointers = pomManager.getPomPointers();
        for (VirtualFilePointer pointer : pointers) {
            final PomNode node = createPomNode(pointer);
            root.insert(node, root.getChildCount());
        }

        //
        //create the plugin nodes
        //
        root.insert(pluginsNode, root.getChildCount());
        final MavenPluginsManager pluginsMgr = MavenPluginsManager.getInstance(pProject);
        final PluginInfo[] plugins = pluginsMgr.getPlugins();
        for (PluginInfo plugin : plugins)
            pluginsNode.insert(createPluginNode(plugin),
                               pluginsNode.getChildCount());
    }

    public MutableTreeNode getRoot() {
        return (MutableTreeNode) super.getRoot();
    }

    public void dispose() {
        final PomManager pomManager = PomManager.getInstance(project);
        pomManager.removePomManagerListener(this);
    }

    public void pomAdded(final PomManagerEvent pEvent) {
        final MutableTreeNode root = getRoot();
        final VirtualFilePointer pointer = pEvent.getFilePointer();

        final PomNode node = createPomNode(pointer);
        root.insert(node, root.getIndex(pluginsNode));
        nodesWereInserted(root, new int[]{root.getIndex(node)});
    }

    public void pomRemoved(final PomManagerEvent pEvent) {
        final MutableTreeNode node = findPomNode(pEvent.getFilePointer());
        if (node != null) {
            final MutableTreeNode root = getRoot();
            final int index = root.getIndex(node);
            node.removeFromParent();
            nodesWereRemoved(root, new int[]{index}, new Object[]{node});
        }
    }

    public void pomValidityChanged(final PomManagerEvent pEvent) {
        final PomNode node = findPomNode(pEvent.getFilePointer());
        if (node != null) {
            node.removeAllChildren();
            nodeStructureChanged(node);
        }
    }

    private PomNode createPomNode(final VirtualFilePointer pPointer) {
        return new PomNode(pPointer);
    }

    private PluginNode createPluginNode(final PluginInfo plugin) {
        final PluginNode pluginNode = new PluginNode(plugin);
        final GoalInfo[] goals = plugin.getGoals();
        for (GoalInfo goal : goals)
            pluginNode.insert(new GoalNode(goal), pluginNode.getChildCount());

        return pluginNode;
    }

    private PomNode findPomNode(final VirtualFilePointer pPointer) {
        final MutableTreeNode root = getRoot();

        //noinspection UNCHECKED_WARNING
        final Enumeration<PomNode> children = root.children();
        while (children.hasMoreElements()) {
            final PomNode node = children.nextElement();
            if (pPointer.equals(node.getUserObject()))
                return node;
        }

        return null;
    }
}
