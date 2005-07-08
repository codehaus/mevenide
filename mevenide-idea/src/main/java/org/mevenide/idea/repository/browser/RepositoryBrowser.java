package org.mevenide.idea.repository.browser;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.PopupHandler;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.TreePath;
import org.mevenide.idea.Res;
import org.mevenide.idea.repository.tree.RepoTree;
import org.mevenide.idea.repository.tree.model.RepoTreeNode;
import org.mevenide.idea.repository.tree.model.RepoTreeModel;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;

/**
 * @author Arik
 */
public class RepositoryBrowser extends JPanel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(RepositoryBrowser.class);

    /**
     * The tool window title.
     */
    protected static final String PLACE = RES.get("title");

    /**
     * The name of the empty repository.
     */
    public static final String EMPTY_TREE_NAME = "";

    /**
     * The project context.
     */
    protected final Project project;

    /**
     * The repository model.
     */
    protected final Map<IRepositoryReader, RepoTree> repos = new HashMap<IRepositoryReader, RepoTree>(10);

    /**
     * The card layout containing the trees. Used to display the selected repo.
     */
    protected final CardLayout treesCardLayout = new CardLayout();

    /**
     * The panel holding the registered repository trees.
     */
    protected final JPanel treesPanel = new JPanel(treesCardLayout);

    /**
     * Available actions for the tree.
     */
    private final ActionGroup actionGroup;

    /**
     * Creates
     * @param pProject
     */
    public RepositoryBrowser(final Project pProject) {
        super(new BorderLayout());
        project = pProject;

        //
        //create the action group to be used in the toolbar
        //
        actionGroup = createToolBarActionGroup();

        //
        //create the toolbar panel
        //
        add(createToolBar(actionGroup), BorderLayout.PAGE_START);

        //
        //add a panel containing the various trees
        //we need multiple JTree instances, since hidden models still update
        //the tree, and therefor send it events. If a hidden model sends events
        //to the tree while another model is set in the tree, exceptions will
        //occur. Therefor each repository has its own tree and model.
        //
        //Initialy, set an empty tree
        //
        final JTree initialTree = createRepoTree(null);
        treesPanel.add(ScrollPaneFactory.createScrollPane(initialTree), EMPTY_TREE_NAME);
        add(treesPanel, BorderLayout.CENTER);
    }

    protected ActionGroup createToolBarActionGroup() {
        final DefaultActionGroup actGroup = new DefaultActionGroup();
        actGroup.add(new DownloadArtifactsAction(this));
        actGroup.add(new RefreshRepoAction(this));
        return actGroup;
    }

    protected JComponent createToolBar(final ActionGroup pActions) {
        final JPanel toolbarPanel = new JPanel(new BorderLayout());

        //
        //add repository selection combo box
        //
        final JComboBox repoComboBox = new RepositoriesComboBox(project);
        toolbarPanel.add(repoComboBox, BorderLayout.CENTER);
        repoComboBox.setSelectedItem(null);
        repoComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JComboBox repoComboBox = (JComboBox) e.getSource();
                final Object selectedItem = repoComboBox.getSelectedItem();

                if (selectedItem == null || !(selectedItem instanceof IRepositoryReader))
                    treesCardLayout.show(treesPanel, EMPTY_TREE_NAME);
                else {
                    final IRepositoryReader repo = (IRepositoryReader) selectedItem;
                    showRepo(repo);
                }
            }
        });

        //
        //create the action group toolbar and add it to the toolbar panel
        //
        final ActionManager actMgr = ActionManager.getInstance();
        final ActionToolbar toolbar = actMgr.createActionToolbar(PLACE, pActions, true);
        toolbarPanel.add(toolbar.getComponent(), BorderLayout.LINE_START);

        //
        //return the toolbar panel
        //
        return toolbarPanel;
    }

    public Project getProject() {
        return project;
    }

    public void refresh(final IRepositoryReader pRepo) {
        if (repos.containsKey(pRepo)) {
            final String repoName = pRepo.getRootURI().toString();
            final RepoTreeModel repoModel = new RepoTreeModel(pRepo);
            final RepoTree repoTree = repos.get(pRepo);
            repoTree.setModel(repoModel);
            repoTree.fetchNode(repoModel.getRoot());
            treesCardLayout.show(treesPanel, repoName);
        }
        else
            addRepo(pRepo);
    }

    public void refreshSelectedRepo() {
        final Set<Map.Entry<IRepositoryReader, RepoTree>> entries = repos.entrySet();
        for (Map.Entry<IRepositoryReader, RepoTree> entry : entries) {
            final RepoTree tree = entry.getValue();
            if (tree.isVisible()) {
                refresh(entry.getKey());
                return;
            }
        }
    }

    public int getSelectedItemsCount() {
        final Collection<RepoTree> trees = repos.values();
        for (RepoTree tree : trees) {
            if (tree.isVisible()) {
                final TreePath[] selections = tree.getSelectionPaths();
                if (selections == null || selections.length == 0)
                    return 0;

                int count = 0;
                for (TreePath path : selections) {
                    final Object elt = path.getLastPathComponent();
                    if (elt instanceof RepoTreeNode)
                        count++;
                }

                return count;
            }
        }

        return 0;
    }

    public RepoPathElement[] getSelectedItems() {
        final Collection<RepoTree> trees = repos.values();
        for (RepoTree tree : trees) {
            if (tree.isVisible()) {
                final TreePath[] selections = tree.getSelectionPaths();
                if (selections == null || selections.length == 0)
                    return new RepoPathElement[0];

                final Set<RepoPathElement> elements = new HashSet<RepoPathElement>(selections.length);
                for (TreePath path : selections) {
                    final Object elt = path.getLastPathComponent();
                    if (elt instanceof RepoTreeNode) {
                        final RepoTreeNode node = (RepoTreeNode) elt;
                        elements.add(node.getPathElement());
                    }
                }

                return elements.toArray(new RepoPathElement[elements.size()]);
            }
        }

        return new RepoPathElement[0];
    }

    /**
     * Finds the tree associated with the specified repo and displays it. If the given repository is
     * {@code null}, the empty tree is displayed.
     *
     * @param pRepo the repository to display (may be {@code null})
     */
    protected void showRepo(final IRepositoryReader pRepo) {
        if (repos.containsKey(pRepo))
            treesCardLayout.show(treesPanel, pRepo.getRootURI().toString());
        else
            addRepo(pRepo);
    }

    protected RepoTree createRepoTree(final IRepositoryReader pRepo) {
        final RepoTree tree = new RepoTree();
        final ActionManager actionMgr = ActionManager.getInstance();
        PopupHandler.installPopupHandler(tree, actionGroup, PLACE, actionMgr);
        return tree;
    }

    /**
     * Adds the given repository to the list of registered repositories. A corresponding tree and
     * model are created for it, registered and added to the UI.
     *
     * @param pRepo the repository to add (may not be {@code null})
     */
    protected RepoTree addRepo(final IRepositoryReader pRepo) {
        final String repoName = pRepo.getRootURI().toString();

        //
        //create new tree and model for the repo
        //
        final RepoTreeModel repoModel = new RepoTreeModel(pRepo);
        final RepoTree repoTree = createRepoTree(pRepo);

        //
        //store the tree in the tree-cache, and add it to the layout
        //
        final JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(repoTree);
        treesPanel.add(scrollPane, repoName);
        repos.put(pRepo, repoTree);

        //
        //later on, start fetching the root node
        //
        repoTree.fetchNode(repoModel.getRoot());
        treesCardLayout.show(treesPanel, repoName);
        return repoTree;
    }
}
