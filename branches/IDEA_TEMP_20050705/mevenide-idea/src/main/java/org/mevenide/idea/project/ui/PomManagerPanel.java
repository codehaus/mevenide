package org.mevenide.idea.project.ui;

import com.intellij.ide.AutoScrollToSourceOptionProvider;
import com.intellij.ide.CommonActionsManager;
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
import com.intellij.openapi.ui.SelectFromListDialog;
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
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.mevenide.idea.Res;
import org.mevenide.idea.execute.MavenExecuteManager;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.actions.AddGoalToPomAction;
import org.mevenide.idea.project.actions.ExecuteGoalAction;
import org.mevenide.idea.project.actions.RefreshPomToolWindowAction;
import org.mevenide.idea.project.actions.RemoveGoalFromPomAction;
import org.mevenide.idea.project.model.GoalInfo;
import org.mevenide.idea.project.model.PluginInfo;

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
        tree = new Tree(model);

        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new PomManagerTreeCellRenderer());
        tree.addTreeSelectionListener(new TreeSelectionListener() {
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
            }
        });
        tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(final MouseEvent pEvent) {
                if (pEvent.getClickCount() == 2) {
                    final int row = tree.getRowForLocation(pEvent.getX(),
                                                           pEvent.getY());
                    if (row < 0)
                        return;

                    final TreePath path = tree.getPathForRow(row);
                    if (path == null)
                        return;

                    final TreeNode node = (TreeNode) path.getLastPathComponent();
                    if (node instanceof GoalNode) {
                        final VirtualFile pomFile;
                        final PomNode pomNode = model.getPomNode(node);
                        if (pomNode != null)
                            pomFile = pomNode.getUserObject().getFile();
                        else {
                            final SelectFromListDialog dlg = new SelectFromListDialog(
                                    project,
                                    PomManager.getInstance(project).getPomPointers(),
                                    new SelectFromListDialog.ToStringAspect() {
                                        public String getToStirng(Object obj) {
                                            final VirtualFilePointer p = (VirtualFilePointer) obj;
                                            return p.getPresentableUrl();
                                        }
                                    },
                                    "Select POM to execute goal for",
                                    ListSelectionModel.SINGLE_SELECTION);
                            dlg.setModal(true);
                            dlg.setResizable(true);
                            dlg.show();

                            if (!dlg.isOK())
                                return;

                            pomFile = ((VirtualFilePointer) dlg.getSelection()[0]).getFile();
                        }

                        final GoalInfo goal = ((GoalNode) node).getUserObject();
                        MavenExecuteManager.getInstance(project).execute(pomFile, goal);
                    }
                }
            }
        });
        add(ScrollPaneFactory.createScrollPane(tree), BorderLayout.CENTER);

        final CommonActionsManager cmnActionsMgr = CommonActionsManager.getInstance();
        final AnAction autoScrollAction =
                cmnActionsMgr.installAutoscrollToSourceHandler(project, tree, this);

        final DefaultActionGroup actionGrp = new DefaultActionGroup("POM Manager", false);
        actionGrp.add(new ExecuteGoalAction());
        actionGrp.add(new AddGoalToPomAction());
        actionGrp.add(new RemoveGoalFromPomAction());
        actionGrp.add(new RefreshPomToolWindowAction());
        actionGrp.add(autoScrollAction);
        final ActionManager actionMgr = ActionManager.getInstance();
        final ActionToolbar toolbar = actionMgr.createActionToolbar(TITLE, actionGrp, true);
        add(toolbar.getComponent(), BorderLayout.PAGE_START);
    }

    public Project getProject() {
        return project;
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

    public VirtualFile[] getSelectedProjects() {
        return getSelectedProjects(true);
    }

    public VirtualFile[] getSelectedProjects(final boolean pStrict) {
        final TreePath[] selection = tree.getSelectionPaths();
        if (selection == null)
            return new VirtualFile[0];

        final Set<PomNode> projects = new HashSet<PomNode>(3);
        for (TreePath path : selection) {
            final Object item = path.getLastPathComponent();
            if (item instanceof PomNode)
                projects.add(((PomNode) item));
            else if (!pStrict && item instanceof GoalNode) {
                final GoalNode node = (GoalNode) item;
                final PomNode pomParent = model.getPomNode(node);
                if (pomParent != null)
                    projects.add(pomParent);
            }
        }

        final int size = projects.size();
        final PomNode[] nodes = projects.toArray(new PomNode[size]);
        final VirtualFile[] files = new VirtualFile[projects.size()];
        for (int i = 0; i < nodes.length; i++)
            files[i] = nodes[i].getUserObject().getFile();

        return files;
    }

    public GoalInfo[] getSelectedGoals(final VirtualFile pPomFile) {
        final TreePath[] selection = tree.getSelectionPaths();
        if (selection == null)
            return new GoalInfo[0];

        final Set<GoalInfo> goals = new HashSet<GoalInfo>(selection.length);
        for (TreePath path : selection) {
            final Object item = path.getLastPathComponent();
            if (!(item instanceof GoalNode))
                continue;

            final GoalNode node = (GoalNode) item;
            final GoalInfo goal = node.getUserObject();
            final PomNode pomNode = model.getPomNode(node);

            if (pomNode == null && pPomFile == null)
                goals.add(goal);

            else if (pPomFile != null && pomNode != null &&
                    pPomFile.equals(pomNode.getUserObject().getFile()))
                goals.add(goal);
        }

        return goals.toArray(new GoalInfo[goals.size()]);
    }

    public void navigateToSource(final PluginNode pNode) {
        final PluginInfo plugin = pNode.getUserObject();
        final VirtualFile scriptFile = plugin.getScriptFile();
        if (scriptFile == null)
            return;

        final OpenFileDescriptor desc = new OpenFileDescriptor(project,
                                                               scriptFile);
        if (desc.canNavigateToSource())
            desc.navigate(true);
    }

    public void navigateToSource(final GoalNode pNode) {
        final PsiDocumentManager psiMgr = PsiDocumentManager.getInstance(project);
        final FileEditorManager fileMgr = FileEditorManager.getInstance(project);

        final GoalInfo goal = pNode.getUserObject();
        final PluginNode pluginNode = model.getPluginNode(pNode);
        if (pluginNode == null)
            return;

        final PluginInfo plugin = pluginNode.getUserObject();
        final VirtualFile scriptFile = plugin.getScriptFile();
        if (scriptFile == null)
            return;

        final OpenFileDescriptor desc = new OpenFileDescriptor(project,
                                                               scriptFile);
        if (desc.canNavigateToSource()) {
            desc.navigate(true);
            final FileEditor fileEditor = fileMgr.getSelectedEditor(scriptFile);
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
                for (XmlTag goalTag : goals) {
                    if (goal.getName().equals(goalTag.getAttributeValue("name"))) {
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
}
