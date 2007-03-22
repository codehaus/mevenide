package org.codehaus.mevenide.idea.gui;

import org.codehaus.mevenide.idea.gui.form.MavenBuildProjectToolWindowForm;
import org.codehaus.mevenide.idea.helper.ActionContext;
import org.codehaus.mevenide.idea.model.MavenPluginDocument;
import org.codehaus.mevenide.idea.model.MavenProjectDocument;
import org.codehaus.mevenide.idea.model.PluginGoal;
import org.codehaus.mevenide.idea.util.GuiUtils;
import org.codehaus.mevenide.idea.util.PluginConstants;
import org.codehaus.mevenide.idea.xml.MavenDefaultsDocument;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public class PomTreeUtil {

    public static PomTree getPomTree(ActionContext context) {
        return ((MavenBuildProjectToolWindowForm) context.getGuiContext().getMavenToolWindowForm()).getPomTree();
    }

    /**
     * Method description
     *
     * @param context              Document me!
     * @param mavenProjectDocument Document me!
     *
     * @return Document me!
     *
     * @throws java.io.IOException
     * @throws org.codehaus.mevenide.idea.util.IdeaMavenPluginException
     *
     */
    public static DefaultMutableTreeNode addSinglePomToTree(ActionContext context,
                                                            MavenProjectDocument mavenProjectDocument){
        PomTree pomTree = getPomTree(context);

        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) pomTree.getModel().getRoot();
        DefaultMutableTreeNode childNode = pomTree.addObject(rootNode, mavenProjectDocument);

        addStandardPhasesToPomTree(pomTree, context, childNode);

        createPluginNodes(mavenProjectDocument, pomTree, childNode);

        return childNode;
    }

    private static void createPluginNodes(MavenProjectDocument mavenProjectDocument, PomTree pomTree, DefaultMutableTreeNode childNode) {
        for (MavenPluginDocument mavenPluginDocument : mavenProjectDocument.getPlugins()) {
            addPluginToPomTree(pomTree, childNode, mavenPluginDocument);
        }
    }

    public static void updatePluginNodes(MavenProjectDocument mavenProjectDocument, ActionContext context, DefaultMutableTreeNode node) {
        PomTree pomTree = getPomTree(context);
        DefaultTreeModel treeModel = (DefaultTreeModel) pomTree.getModel();
        while ( node.getChildCount() > 1 ) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(1);
            treeModel.removeNodeFromParent(child);
        }
        createPluginNodes(mavenProjectDocument, pomTree, node);
        treeModel.nodeChanged(node);
    }

    /**
     * Method description
     *
     * @param pomTree
     * @param context      Document me!
     * @param treeRootNode Document me!
     */
    private static void addStandardPhasesToPomTree(PomTree pomTree, ActionContext context, DefaultMutableTreeNode treeRootNode) {
        List<MavenDefaultsDocument.Goal> standardGoals = context.getStandardGoals();

        DefaultMutableTreeNode node = pomTree.addObject(treeRootNode, PluginConstants.NODE_POMTREE_PHASES);

        for (MavenDefaultsDocument.Goal goal : standardGoals) {
            node.add(GuiUtils.createDefaultTreeNode(goal.getName()));
        }

        if (context.getProjectPluginSettings().isUseFilter()) {
            filterStandardPhasesInNodes(context, node);
        }
    }

    /**
     * Method description
     *
     * @param pomTree
     * @param treeRootNode        Document me!
     * @param mavenPluginDocument Document me!
     */
    public static void addPluginToPomTree(PomTree pomTree, DefaultMutableTreeNode treeRootNode,
                                   MavenPluginDocument mavenPluginDocument) {
        Set<PluginGoal> pluginGoalList = mavenPluginDocument.getPluginGoalList();

        if (pluginGoalList.size() > 0) {
            DefaultMutableTreeNode node = pomTree.addObject(treeRootNode, mavenPluginDocument);

            for (PluginGoal goal : pluginGoalList) {
                node.add(GuiUtils.createDefaultTreeNode(goal));
            }
        }
    }

    /**
     * Method description
     *
     * @param selectedNodeList Document me!
     *
     * @return Document me!
     */
    public static boolean nodesAreExecutableMavenGoals(List<DefaultMutableTreeNode> selectedNodeList) {
        for (DefaultMutableTreeNode node : selectedNodeList) {
            Object nodeInfo = node.getUserObject();
            if ((nodeInfo == null) || !isExecutableGoal(nodeInfo)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isExecutableGoal(Object nodeInfo) {
        return isProjectGoal(nodeInfo) || isPluginGoal(nodeInfo);
    }

    public static boolean isPluginGoal(Object nodeInfo) {
        return nodeInfo instanceof PluginGoal;
    }

    public static boolean isProjectGoal(Object nodeInfo) {
        return nodeInfo instanceof MavenDefaultsDocument.Name;
    }

    public static MavenProjectDocument getMavenProjectDocument( DefaultMutableTreeNode node ) {
        Object nodeInfo = node.getUserObject();

        if (!node.isRoot()) {
            if ((nodeInfo != null) && (nodeInfo instanceof MavenProjectDocument)) {
                return (MavenProjectDocument) nodeInfo;
            }
        }
        return null;
    }

    /**
     * Filters standard child nodes of the Phases node. After applying this toggleFilter, only the
     * standard phases as listed below are child nodes of the Phases node.
     * <p/>
     * <ul> <li>clean</li> <li>compile</li> <li>test</li> <li>package</li> <li>install</li> </ul>
     *
     * @param startNode     of tree.
     * @param actionContext The action context.
     *
     * @return the start node.
     */
    public static DefaultMutableTreeNode filterStandardPhasesInNodes(ActionContext actionContext,
            DefaultMutableTreeNode startNode) {
        if (startNode != null) {
            List<String> standardPhasesList = actionContext.getProjectPluginSettings().getStandardPhasesList();

            // traverse the whole tree in case the start node is not a Phases node.
            if (!startNode.getUserObject().toString().equals(PluginConstants.NODE_POMTREE_PHASES)) {
                Enumeration enumeration = startNode.postorderEnumeration();

                while (enumeration.hasMoreElements()) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();

                    if (node.getUserObject().toString().equals(PluginConstants.NODE_POMTREE_PHASES)) {
                        filterPhases(node, standardPhasesList);
                    }
                }

                // only traverse the child nodes below the given Phases start node.
            } else {
                filterPhases(startNode, standardPhasesList);
            }
        }

        return startNode;
    }

    static void filterPhases(DefaultMutableTreeNode startNode, List<String> standardPhasesList) {
        for (int j = 0; j < startNode.getChildCount(); j++) {
            DefaultMutableTreeNode phaseNode = (DefaultMutableTreeNode) startNode.getChildAt(j);
            String phaseName = phaseNode.getUserObject().toString();

            if (!standardPhasesList.contains(phaseName)) {
                startNode.remove(phaseNode);
                j--;
            }
        }
    }

    /**
     * Unfilters standard child nodes of the Phases node. After applying this toggleFilter, all maven
     * phases are listed as child nodes of the phases node.
     *
     * @param actionContext The action context.
     * @param startNode     of tree.
     *
     * @return the start node.
     */
    public static DefaultMutableTreeNode unfilterStandardPhasesInNodes(ActionContext actionContext,
            DefaultMutableTreeNode startNode) {
        if (startNode != null) {

            List<MavenDefaultsDocument.Goal> standardGoalsList = actionContext.getStandardGoals();
            // traverse the whole tree in case the start node is not a Phases node.
            if (!startNode.getUserObject().toString().equals(PluginConstants.NODE_POMTREE_PHASES)) {
                Enumeration enumeration = startNode.postorderEnumeration();

                while (enumeration.hasMoreElements()) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();

                    if (node.getUserObject().toString().equals(PluginConstants.NODE_POMTREE_PHASES)) {
                        unfilterPhases(node, standardGoalsList);
                    }
                }

                // only traverse the child nodes below the given Phases start node.
            } else {
                unfilterPhases(startNode, standardGoalsList);
            }
        }

        return startNode;
    }

    static void unfilterPhases(DefaultMutableTreeNode startNode, List<MavenDefaultsDocument.Goal> standardGoals) {
        startNode.removeAllChildren();

        for (MavenDefaultsDocument.Goal goal : standardGoals) {
            startNode.add(GuiUtils.createDefaultTreeNode(goal.getName()));
        }
    }

    public static DefaultMutableTreeNode findMavenProjectDocumentNode(ActionContext context, Object object) {
        return GuiUtils.findNodeByObject((DefaultMutableTreeNode) getPomTree(context).getModel().getRoot(), object);
    }
}
