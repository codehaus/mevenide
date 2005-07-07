package org.mevenide.idea.project.ui;

import com.intellij.ide.AutoScrollToSourceOptionProvider;
import com.intellij.ide.CommonActionsManager;
import com.intellij.ide.TreeExpander;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.Tree;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.mevenide.idea.Res;
import org.mevenide.idea.execute.MavenExecuteManager;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.actions.AddPluginGoalToPomAction;
import org.mevenide.idea.project.actions.ExecuteGoalAction;
import org.mevenide.idea.project.actions.RefreshPomToolWindowAction;
import org.mevenide.idea.project.actions.RemovePluginGoalFromPomAction;
import org.mevenide.idea.project.goals.Goal;
import org.mevenide.idea.project.goals.GoalContainer;
import org.mevenide.idea.project.goals.PluginGoal;
import org.mevenide.idea.project.goals.PluginGoalContainer;
import org.mevenide.idea.project.util.PomUtils;

/**
 * @author Arik
 */
public class PomManagerPanel extends JPanel
        implements Disposable, AutoScrollToSourceOptionProvider {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(PomManagerPanel.class);

    /**
     * Tool window name.
     */
    public static final String TITLE = RES.get("pom.manager.name");

    /**
     * The project this instance is registered for.
     */
    private final Project project;

    /**
     * The Maven tree model.
     */
    private final PomTreeModel model;

    /**
     * The POM tree.
     */
    private final JTree tree;

    /**
     * Used by the expand/collapse all actions.
     */
    private final TreeExpander treeExpanded = new PomTreeExpander();

    /**
     * Whether autoscroll to source is on or off.
     */
    private boolean autoScrollToSource;

    /**
     * Creates an instance for the given project.
     *
     * @param pProject the project this instance will be registered to
     */
    public PomManagerPanel(final Project pProject) {
        super(new BorderLayout());

        project = pProject;
        model = new PomTreeModel(project);

        //
        //create the tree
        //
        tree = new Tree(model);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new PomManagerTreeCellRenderer(project));
        tree.addTreeSelectionListener(new TreeSelectionHandler());
        tree.addMouseListener(new DblClickHandler());
        final TreeSelectionModel treeSelModel = tree.getSelectionModel();
        treeSelModel.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        add(ScrollPaneFactory.createScrollPane(tree), BorderLayout.CENTER);

        //
        //create the toolbar
        //
        final ActionManager actionMgr = ActionManager.getInstance();
        final CommonActionsManager cmnActionsMgr = CommonActionsManager.getInstance();
        final AnAction autoScrollAction =
                cmnActionsMgr.installAutoscrollToSourceHandler(project,
                                                               tree,
                                                               this);

        final DefaultActionGroup actionGrp = new DefaultActionGroup("POM Manager", false);
        actionGrp.add(new ExecuteGoalAction());
        actionGrp.add(new AddPluginGoalToPomAction());
        actionGrp.add(new RemovePluginGoalFromPomAction());
        actionGrp.addSeparator();
        actionGrp.add(new RefreshPomToolWindowAction());
        actionGrp.add(autoScrollAction);
        actionGrp.addSeparator();
        actionGrp.add(cmnActionsMgr.createCollapseAllAction(treeExpanded));
        actionGrp.add(cmnActionsMgr.createExpandAllAction(treeExpanded));

        final ActionToolbar toolbar = actionMgr.createActionToolbar(TITLE, actionGrp, true);
        add(toolbar.getComponent(), BorderLayout.PAGE_START);
    }

    public void refresh() {
        model.refresh();
    }

    public boolean isAutoScrollMode() {
        return autoScrollToSource;
    }

    public void setAutoScrollMode(final boolean pState) {
        autoScrollToSource = pState;
    }

    public String[] getPomsWithSelectedGoals(final boolean pIncludePluginGoals) {
        final TreePath[] selection = tree.getSelectionPaths();
        if (selection == null)
            return new String[0];

        final Set<String> projects = new HashSet<String>(3);
        for (TreePath path : selection) {
            final Object item = path.getLastPathComponent();
            if (item instanceof GoalNode) {
                final PomNode pomParent = model.getPomNode((GoalNode) item);
                if (pomParent != null || pIncludePluginGoals) {
                    final String url;
                    if (pomParent == null)
                        url = null;
                    else
                        url = pomParent.getUserObject();
                    projects.add(url);
                }
            }
        }

        final int size = projects.size();
        return projects.toArray(new String[size]);
    }

    public Goal[] getSelectedGoals() {
        final TreePath[] selection = tree.getSelectionPaths();
        if (selection == null)
            return new Goal[0];

        final Set<Goal> goals = new HashSet<Goal>(selection.length);
        for (TreePath path : selection) {
            final Object item = path.getLastPathComponent();
            if (!(item instanceof GoalNode))
                continue;

            final GoalNode node = (GoalNode) item;
            final Goal goal = node.getUserObject();
            goals.add(goal);
        }

        return goals.toArray(new PluginGoal[goals.size()]);
    }

    public Goal[] getSelectedGoalsForPom(final String pUrl) {
        return getSelectedGoalsForPom(pUrl, false);
    }

    public Goal[] getSelectedGoalsForPom(final String pUrl,
                                         final boolean pRecursePluginNodes) {
        final TreePath[] selection = tree.getSelectionPaths();
        if (selection == null)
            return new Goal[0];

        final Set<Goal> goals = new HashSet<Goal>(selection.length);
        for (TreePath path : selection) {
            final Object item = path.getLastPathComponent();
            if (item instanceof PluginNode && pRecursePluginNodes && pUrl == null) {
                final PluginNode node = (PluginNode) item;
                final PluginGoalContainer plugin = node.getUserObject();
                final Goal[] pluginGoals = plugin.getGoals();
                for (Goal goal : pluginGoals)
                    goals.add(goal);
            }
            else if (item instanceof GoalNode) {
                final GoalNode node = (GoalNode) item;
                final Goal goal = node.getUserObject();

                final PomNode pomNode = model.getPomNode(node);
                if (pomNode == null && pUrl == null)
                    goals.add(goal);
                else if (pomNode != null && pomNode.getUserObject().equals(pUrl))
                    goals.add(goal);
            }
        }

        return goals.toArray(new PluginGoal[goals.size()]);
    }

    private VirtualFile getGoalContainerFile(final GoalContainerNode pNode) {
        return getGoalContainerFile(pNode.getUserObject());
    }

    private void navigateToSource(final PomNode pPomNode) {
        final PomManager pomMgr = PomManager.getInstance(project);
        final String url = pPomNode.getUserObject();
        final VirtualFile pomFile = pomMgr.getFile(url);
        if (pomFile == null || !pomFile.isValid() || pomFile.isDirectory())
            return;

        final OpenFileDescriptor desc = new OpenFileDescriptor(project, pomFile);
        if (desc.canNavigateToSource())
            desc.navigate(true);
    }

    public void navigateToSource(final PluginNode pNode) {
        final VirtualFile script = getGoalContainerFile(pNode);
        if (script == null || !script.isValid())
            return;

        final OpenFileDescriptor desc = new OpenFileDescriptor(project, script);
        if (desc.canNavigateToSource())
            desc.navigate(true);
    }

    public void navigateToSource(final GoalNode pNode) {
        navigateToSource(pNode.getUserObject());
    }

    public void navigateToSource(final Goal pGoal) {
        final PsiDocumentManager psiMgr = PsiDocumentManager.getInstance(project);
        final FileEditorManager fileMgr = FileEditorManager.getInstance(project);

        final VirtualFile script = getGoalContainerFile(pGoal.getContainer());
        if (script == null)
            return;

        final OpenFileDescriptor desc = new OpenFileDescriptor(project, script);
        if (desc.canNavigateToSource()) {
            desc.navigate(true);
            final FileEditor fileEditor = fileMgr.getSelectedEditor(script);
            if (fileEditor instanceof TextEditor) {
                final Editor editor = ((TextEditor) fileEditor).getEditor();
                final Document document = editor.getDocument();
                final PsiFile psiFile = psiMgr.getPsiFile(document);
                if (!(psiFile instanceof XmlFile))
                    return;

                final XmlFile xmlFile = (XmlFile) psiFile;
                final XmlDocument xmlDoc = xmlFile.getDocument();
                if (xmlDoc == null)
                    return;

                final XmlTag projectTag = xmlDoc.getRootTag();
                if (projectTag == null)
                    return;

                final XmlTag[] goals = projectTag.findSubTags("goal");
                final String goalName = pGoal.getName();
                for (XmlTag goalTag : goals) {
                    if (goalName.equals(goalTag.getAttributeValue("name"))) {
                        final int offset = goalTag.getTextOffset();
                        editor.getCaretModel().moveToOffset(offset);
                        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
                        break;
                    }
                }
            }
        }
    }

    public void dispose() {
        model.dispose();
    }

    private VirtualFile getGoalContainerFile(final GoalContainer pContainer) {
        final VirtualFilePointer scriptFile = pContainer.getScriptFile();
        if (scriptFile == null || !scriptFile.isValid())
            return null;

        final VirtualFile file = scriptFile.getFile();
        if (file == null || !file.isValid())
            return null;

        return file;
    }

    private class DblClickHandler extends MouseAdapter {
        @Override
        public void mouseClicked(final MouseEvent pEvent) {
            if (pEvent.getClickCount() != 2)
                return;

            final int row = tree.getRowForLocation(pEvent.getX(), pEvent.getY());
            if (row < 0)
                return;

            final TreePath path = tree.getPathForRow(row);
            if (path == null)
                return;

            final PomManager pomMgr = PomManager.getInstance(project);

            final TreeNode node = (TreeNode) path.getLastPathComponent();
            if (!(node instanceof GoalNode))
                return;

            final VirtualFile pomFile;
            final PomNode pomNode = model.getPomNode(node);

            if (pomNode != null)
                pomFile = pomMgr.getFile(pomNode.getUserObject());
            else
                pomFile = pomMgr.getFile(PomUtils.selectPom(project));

            if (pomFile == null)
                return;

            final Goal goal = ((GoalNode) node).getUserObject();
            MavenExecuteManager.getInstance(project).execute(pomFile, goal);
        }
    }

    private class TreeSelectionHandler implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent e) {
            if (!autoScrollToSource)
                return;

            final TreePath selection = tree.getSelectionPath();
            if (selection == null)
                return;

            final TreeNode node = (TreeNode) selection.getLastPathComponent();
            if (node instanceof PluginNode)
                navigateToSource((PluginNode) node);
            else if (node instanceof GoalNode)
                navigateToSource((GoalNode) node);
            else if (node instanceof PomNode)
                navigateToSource((PomNode) node);
        }
    }

    private class PomTreeExpander implements TreeExpander {
        private boolean hasExpandedProjects() {
            final TreeNode projectsNode = model.getProjectsNode();
            //noinspection unchecked
            final Enumeration<TreeNode> projectNodes = projectsNode.children();
            while (projectNodes.hasMoreElements()) {
                final TreeNode node = projectNodes.nextElement();
                if (tree.isExpanded(new TreePath(model.getPathToRoot(node))))
                    return true;
            }

            return false;
        }

        private boolean isPluginsNodeExpanded() {
            final TreeNode pluginsNode = model.getPluginsNode();
            return tree.isExpanded(new TreePath(model.getPathToRoot(pluginsNode)));
        }

        public boolean canCollapse() {
            return hasExpandedProjects() || isPluginsNodeExpanded();
        }

        public boolean canExpand() {
            return !hasExpandedProjects() || !isPluginsNodeExpanded();
        }

        public void collapseAll() {
            final TreeNode projectsNode = model.getProjectsNode();
            //noinspection unchecked
            final Enumeration<TreeNode> projectNodes = projectsNode.children();
            while (projectNodes.hasMoreElements()) {
                final TreeNode node = projectNodes.nextElement();
                tree.collapsePath(new TreePath(model.getPathToRoot(node)));
            }

            final TreeNode pluginsNode = model.getPluginsNode();
            tree.collapsePath(new TreePath(model.getPathToRoot(pluginsNode)));
        }

        public void expandAll() {
            final TreeNode pluginsNode = model.getPluginsNode();
            tree.expandPath(new TreePath(model.getPathToRoot(pluginsNode)));

            final TreeNode projectsNode = model.getProjectsNode();
            //noinspection unchecked
            final Enumeration<TreeNode> projectNodes = projectsNode.children();
            while (projectNodes.hasMoreElements()) {
                final TreeNode node = projectNodes.nextElement();
                tree.expandPath(new TreePath(model.getPathToRoot(node)));
            }
        }
    }
}
