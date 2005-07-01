package org.mevenide.idea.toolwindows.repository;

import com.intellij.ide.CommonActionsManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.ModuleListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SelectFromListDialog;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.ScrollPaneFactory;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.tree.TreePath;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.idea.Res;
import org.mevenide.idea.module.ModuleLocationFinder;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.idea.repository.ArtifactDownloadManager;
import org.mevenide.idea.repository.RepositoryTree;
import org.mevenide.idea.repository.RepositoryTreeExpander;
import org.mevenide.idea.repository.RepositoryUtils;
import org.mevenide.idea.repository.model.RepoTreeNode;
import org.mevenide.idea.repository.model.RepositoryTreeModel;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.actions.AbstractToggleAnAction;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.images.Icons;
import org.mevenide.repository.RepoPathElement;

/**
 * @author Arik
 */
public class RepositoryToolWindow extends JPanel
    implements PropertyChangeListener, ModuleListener {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(RepositoryToolWindow.class);

    /**
     * The tool window title.
     */
    public static final String NAME = RES.get("title");

    /**
     * The project context.
     */
    private final Project project;

    /**
     * The repository tree UI component.
     */
    private final JTree tree = new RepositoryTree();

    /**
     * The repository model.
     */
    private RepositoryTreeModel model;

    /**
     * Creates an instance for the given project.
     */
    public RepositoryToolWindow(final Project pProject) {
        project = pProject;
        refreshModel();

        setName(NAME);

        setLayout(new BorderLayout());
        add(ScrollPaneFactory.createScrollPane(tree), BorderLayout.CENTER);

        //
        // create the action toolbar
        //
        final RepositoryTreeExpander expander = new RepositoryTreeExpander(tree);
        final DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new RefreshRepoTreeAction());
        actionGroup.add(new DownloadArtifactAction());
        actionGroup.addSeparator();
        actionGroup.add(new ShowLocalRepoAction());
        actionGroup.add(new ShowRemoteRepoAction());
        actionGroup.add(CommonActionsManager.getInstance().createCollapseAllAction(
            expander));
        final ActionToolbar toolbar =
            ActionManager.getInstance().createActionToolbar(NAME, actionGroup, true);
        add(toolbar.getComponent(), BorderLayout.PAGE_START);
    }

    public void refreshModel() {
        final ModuleManager moduleMgr = ModuleManager.getInstance(project);
        final Module[] modules = moduleMgr.getModules();
        for (Module module : modules) {
            final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
            moduleSettings.addPropertyChangeListener("queryContext", this);
        }

        model = new RepositoryTreeModel(RepositoryUtils.createRepoReaders(project));
        tree.setModel(model);
    }

    public JTree getTree() {
        return tree;
    }

    public RepoTreeNode[] getSelectedElements() {
        final TreePath[] selections = tree.getSelectionPaths();
        if (selections == null || selections.length == 0)
            return new RepoTreeNode[0];

        final Set<RepoTreeNode> elements = new HashSet<RepoTreeNode>(selections.length);
        for (TreePath path : selections) {
            final Object elt = path.getLastPathComponent();
            if (elt instanceof RepoTreeNode)
                elements.add((RepoTreeNode) elt);
        }

        return elements.toArray(new RepoTreeNode[elements.size()]);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        final String property = evt.getPropertyName();
        final Object source = evt.getSource();

        if (source instanceof ModuleSettings && property.equals("queryContext"))
            refreshModel();
    }

    public void moduleAdded(Project project, Module module) {
        refreshModel();
    }

    public void beforeModuleRemoved(Project project, Module module) {
    }

    public void moduleRemoved(Project project, Module module) {
        refreshModel();
    }

    public void modulesRenamed(Project project, List<Module> modules) {
    }

    public static void register(final Project pProject) {
        final ToolWindowManager toolMgr = ToolWindowManager.getInstance(pProject);
        final RepositoryToolWindow toolWindowComp = new RepositoryToolWindow(pProject);
        toolMgr.registerToolWindow(NAME, toolWindowComp, ToolWindowAnchor.RIGHT);
        final ToolWindow toolWindow = toolMgr.getToolWindow(NAME);
        toolWindow.setIcon(Icons.REPOSITORY);
    }

    public static void unregister(final Project project) {
        final ToolWindowManager toolMgr = ToolWindowManager.getInstance(project);
        toolMgr.unregisterToolWindow(NAME);
    }

    public static RepositoryToolWindow getInstance(final Project pProject) {
        final ToolWindowManager mgr = ToolWindowManager.getInstance(pProject);
        final ToolWindow tw = mgr.getToolWindow(NAME);
        return (RepositoryToolWindow) tw.getComponent();
    }

    private class DownloadArtifactAction extends AbstractAnAction {
        public DownloadArtifactAction() {
            super(RES.get("download.action.text"),
                  RES.get("download.action.desc"),
                  Icons.DOWNLOAD);
        }

        @Override
        public boolean displayTextInToolbar() {
            return true;
        }

        public void actionPerformed(final AnActionEvent pEvent) {
            final Module selectedModule = getModuleForDownload();
            if (selectedModule == null)
                return;

            //
            //prepare list of path elements to download
            //
            final RepoTreeNode[] selectedElements = getSelectedElements();
            final List<RepoPathElement> pathElements = new ArrayList<RepoPathElement>(
                selectedElements.length);
            for (RepoTreeNode node : selectedElements) {
                final RepoPathElement[] nodeElements = node.getPathElements();
                for (RepoPathElement pathElement : nodeElements)
                    pathElements.add(pathElement);
            }

            final Runnable downloader = new Runnable() {
                public void run() {
                    final ArtifactDownloadManager downloadMgr = ArtifactDownloadManager.getInstance();
                    for (RepoPathElement element : pathElements) {
                        try {
                            downloadMgr.downloadArtifact(selectedModule, element);
                        }
                        catch (IOException e) {
                            UIUtils.showError(selectedModule, e);
                        }
                    }
                }
            };

            final Application app = ApplicationManager.getApplication();
            app.runProcessWithProgressSynchronously(downloader,
                                                    "Downloading...",
                                                    true,
                                                    project);
        }

        @Override
        public void update(final AnActionEvent pEvent) {
            pEvent.getPresentation().setEnabled(getSelectedElements().length > 0);
        }

        private Module getModuleForDownload() {
            final Module selectedModule;
            final Module[] modules = ModuleManager.getInstance(project).getModules();
            if (modules.length == 1)
                selectedModule = modules[0];
            else {
                final Set<Module> localRepos = new HashSet<Module>(modules.length);
                for (Module module : modules) {
                    final ModuleSettings settings = ModuleSettings.getInstance(module);
                    final IQueryContext context = settings.getQueryContext();
                    if (context == null)
                        continue;

                    localRepos.add(module);
                }

                if (localRepos.size() > 1) {
                    final SelectFromListDialog dlg = new SelectFromListDialog(
                        project,
                        localRepos.toArray(),
                        new SelectFromListDialog.ToStringAspect() {
                            public String getToStirng(Object obj) {
                                final Module module = (Module) obj;
                                final ILocationFinder finder = new ModuleLocationFinder(
                                    module);
                                final String repo = finder.getMavenLocalRepository();
                                return module.getName() + " - Local repository at " + repo;
                            }
                        },
                        "Please select a local repository",
                        ListSelectionModel.SINGLE_SELECTION);

                    dlg.show();
                    if (!dlg.isOK())
                        return null;

                    final Object[] selection = dlg.getSelection();
                    if (selection.length == 0)
                        return null;

                    selectedModule = (Module) selection[0];
                }
                else
                    selectedModule = modules[0];
            }

            return selectedModule;
        }
    }

    private class ShowLocalRepoAction extends AbstractToggleAnAction {
        public ShowLocalRepoAction() {
            super(RES.get("toggle.local.repo.text"),
                  RES.get("toggle.local.repo.desc"),
                  Icons.REPO_FILTER_LOCAL);
        }

        public boolean isSelected(AnActionEvent e) {
            return model.isShowLocal();
        }

        public void setSelected(AnActionEvent e, boolean state) {
            model.setShowLocal(state);
        }
    }

    private class ShowRemoteRepoAction extends AbstractToggleAnAction {
        public ShowRemoteRepoAction() {
            super(RES.get("toggle.remote.repo.text"),
                  RES.get("toggle.remote.repo.desc"),
                  Icons.REPO_FILTER_REMOTE);
        }

        public boolean isSelected(AnActionEvent e) {
            return model.isShowRemote();
        }

        public void setSelected(AnActionEvent e, boolean state) {
            model.setShowRemote(state);
        }
    }
}
