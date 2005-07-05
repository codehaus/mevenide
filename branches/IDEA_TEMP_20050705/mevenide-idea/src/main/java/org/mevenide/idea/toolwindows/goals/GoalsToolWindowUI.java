/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.idea.toolwindows.goals;

import com.intellij.ide.AutoScrollToSourceOptionProvider;
import com.intellij.ide.CommonActionsManager;
import com.intellij.ide.DataManager;
import com.intellij.ide.TreeExpander;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableGroup;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
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
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.commons.lang.StringUtils;
import org.mevenide.context.IQueryContext;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.idea.Res;
import org.mevenide.idea.execute.MavenRunner;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.idea.module.ModuleSettingsConfigurable;
import org.mevenide.idea.util.ConfigurableWrapper;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.goals.GoalsHelper;
import org.mevenide.idea.util.ui.images.Icons;
import org.mevenide.idea.util.ui.tree.GoalTreeNode;
import org.mevenide.idea.util.ui.tree.GoalsTreeCellRenderer;
import org.mevenide.idea.util.ui.tree.ModuleTreeNode;
import org.mevenide.idea.util.ui.tree.PluginTreeNode;
import org.mevenide.plugins.IPluginInfo;
import org.mevenide.plugins.PluginInfoFactory;
import org.mevenide.plugins.PluginInfoManager;
import org.mevenide.properties.IPropertyResolver;

/**
 * @author Arik
 */
public class GoalsToolWindowUI extends JPanel
    implements AutoScrollToSourceOptionProvider {
    /**
     * Resources.
     */
    private static final Res RES = Res.getInstance(GoalsToolWindowUI.class);

    /**
     * The tool window title.
     */
    private static final String NAME = RES.get("title");

    /**
     * The name of the property that points to the maven cache of expanded plugins.
     */
    private static final String PROP_PLUGIN_CACHE_DIR = "maven.plugin.unpacked.dir";

    /**
     * The project in which the tool window resides.
     */
    private final Project project;

    /**
     * The goals tree. Used by {@link #getSelectedModule()} to find out to which module
     * the selected goal(s) belong.
     */
    private final Tree goalsTree = new Tree();

    /**
     * The tree model representing available goals and plugins.
     */
    private final GoalsToolWindowTreeModel model;

    /**
     * The auto-scroll to source code flag.
     */
    private boolean autoScrollToSource = false;


    public GoalsToolWindowUI(final Project pProject) {
        project = pProject;
        model = new GoalsToolWindowTreeModel(project);
        init();
    }

    public GoalsToolWindowUI(final Project pProject, boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        project = pProject;
        model = new GoalsToolWindowTreeModel(project);
        init();
    }

    private void init() {
        final CommonActionsManager cmnActionsMgr = CommonActionsManager.getInstance();

        setLayout(new GridBagLayout());
        GridBagConstraints c;

        //
        // create the goals tree
        //
        goalsTree.setModel(model);
        goalsTree.setRootVisible(false);
        goalsTree.setShowsRootHandles(true);
        goalsTree.setCellRenderer(new GoalsTreeCellRenderer());
        goalsTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(final MouseEvent pEvent) {
                if (pEvent.getClickCount() == 2) {
                    final int row = goalsTree.getRowForLocation(pEvent.getX(),
                                                                pEvent.getY());
                    if (row < 0)
                        return;

                    final TreePath path = goalsTree.getPathForRow(row);
                    if (path == null)
                        return;

                    final TreeNode node = (TreeNode) path.getLastPathComponent();
                    if (node instanceof GoalTreeNode) {
                        final PluginTreeNode pluginNode = (PluginTreeNode) node.getParent();
                        final String plugin = pluginNode.getPlugin();
                        final String goal = ((GoalTreeNode) node).getGoal();
                        final String fqGoalName = GoalsHelper.buildFullyQualifiedName(
                            plugin,
                            goal);
                        final Component component = (Component) pEvent.getSource();
                        final DataContext dataContext = DataManager.getInstance().getDataContext(
                            component,
                            pEvent.getX(),
                            pEvent.getY());
                        MavenRunner.execute(getSelectedModule(),
                                            new String[]{fqGoalName},
                                            dataContext);
                    }
                }
            }
        });
        goalsTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                if (!autoScrollToSource)
                    return;

                final TreePath selection = goalsTree.getSelectionPath();
                if (selection == null)
                    return;

                final TreeNode node = (TreeNode) selection.getLastPathComponent();
                if (!(node instanceof GoalTreeNode))
                    return;

                final GoalTreeNode goalNode = (GoalTreeNode) node;
                final PluginTreeNode pluginNode = (PluginTreeNode) node.getParent();
                final String plugin = pluginNode.getPlugin();
                final String goal = goalNode.getGoal();

                selectGoalInEditor(plugin, goal);
            }
        });

        //
        // create the action toolbar
        //
        final AnAction autoScrollAction = cmnActionsMgr.installAutoscrollToSourceHandler(
            project, goalsTree, this);

        final GoalsTreeExpander expander = new GoalsTreeExpander();
        final DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new AttainSelectedGoalsAction());
        actionGroup.add(new ShowModuleSettingsAction());
        actionGroup.addSeparator();
        actionGroup.add(autoScrollAction);
        actionGroup.add(cmnActionsMgr.createCollapseAllAction(expander));
        actionGroup.add(cmnActionsMgr.createExpandAllAction(expander));
        final ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(
            NAME,
            actionGroup,
            true);

        //
        // add components to layout
        //
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        add(toolbar.getComponent(), c);

        c = new GridBagConstraints();
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        add(ScrollPaneFactory.createScrollPane(goalsTree), c);
    }

    /**
     * @param pModule
     * @param pPluginName
     * @param pGoalName
     *
     * @todo navigate to the specified plugin and goal
     */
    private void openProjectGoalInEditor(final Module pModule,
                                         final String pPluginName,
                                         final String pGoalName) {
        final ModuleSettings moduleSettings = ModuleSettings.getInstance(pModule);
        final VirtualFile pomFile = moduleSettings.getPomVirtualFile();
        final VirtualFile pomDir = pomFile.getParent();
        final VirtualFile mavenFile = pomDir.findChild("maven.xml");
        if (mavenFile == null || !mavenFile.isValid())
            return;

        FileEditorManager.getInstance(project).openFile(mavenFile, true);
    }

    private IPluginInfo getPlugin(final IQueryContext pQueryContext,
                                  final String pPluginName) {
        final PluginInfoFactory pluginInfoFactory = PluginInfoFactory.getInstance();
        final PluginInfoManager mgr = pluginInfoFactory.createManager(pQueryContext);
        final IPluginInfo[] plugins = mgr.getCurrentPlugins();

        for (IPluginInfo pluginInfo : plugins) {
            String name = pluginInfo.getName();
            if (name.startsWith("maven-"))
                name = name.substring(6);
            if (name.endsWith("-plugin"))
                name = name.substring(0, name.length() - 7);

            if (name.equals(pPluginName))
                return pluginInfo;
        }

        return null;
    }

    private VirtualFile getPluginScriptFile(final IQueryContext pQueryContext,
                                            final String pPluginName) {
        final IPluginInfo pluginInfo = getPlugin(pQueryContext, pPluginName);
        if (pluginInfo == null)
            return null;

        final IPropertyResolver resolver = pQueryContext.getResolver();
        final String cacheDirPath = resolver.getResolvedValue(PROP_PLUGIN_CACHE_DIR);
        if (cacheDirPath == null || cacheDirPath.trim().length() == 0)
            return null;

        final VirtualFileManager vfMgr = VirtualFileManager.getInstance();
        final String cacheDirUrl = VfsUtil.pathToUrl(cacheDirPath).replace('\\', '/');
        final VirtualFile cacheDir = vfMgr.findFileByUrl(cacheDirUrl);
        if (cacheDir == null)
            return null;

        final VirtualFile pluginDir = cacheDir.findChild(pluginInfo.getArtifactId());
        if (pluginDir == null)
            return null;

        return pluginDir.findChild("plugin.jelly");
    }

    private void navigateToGoal(final String pFullyQualifiedGoal,
                                final TextEditor pTextEditor) {

        final PsiDocumentManager psiDocMgr = PsiDocumentManager.getInstance(project);
        final Editor editor = pTextEditor.getEditor();
        final Document document = editor.getDocument();
        final PsiFile psiFile = psiDocMgr.getPsiFile(document);
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
            if (pFullyQualifiedGoal.equals(goalTag.getAttributeValue("name"))) {
                final int offset = goalTag.getTextOffset();
                editor.getCaretModel().moveToOffset(offset);
                editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
                break;
            }
        }
    }

    private void openGlobalGoalInEditor(final Module pModule,
                                        final String pPluginName,
                                        final String pGoalName) {
        final FileEditorManager fileEditorMgr = FileEditorManager.getInstance(project);
        final ModuleSettings moduleSettings = ModuleSettings.getInstance(pModule);

        final IQueryContext queryContext = moduleSettings.getQueryContext();
        if (queryContext == null)
            return;

        final VirtualFile jellyFile = getPluginScriptFile(queryContext, pPluginName);
        if (jellyFile == null)
            return;

        final OpenFileDescriptor desc = new OpenFileDescriptor(project, jellyFile);
        if (desc.canNavigateToSource())
            desc.navigate(true);
        else
            return;

        final FileEditor fileEditor = fileEditorMgr.getSelectedEditor(jellyFile);
        if (fileEditor instanceof TextEditor)
            navigateToGoal(
                GoalsHelper.buildFullyQualifiedName(pPluginName, pGoalName),
                (TextEditor) fileEditor);
    }

    public void selectGoalInEditor(final String pPluginName, final String pGoalName) {
        final String fqName = GoalsHelper.buildFullyQualifiedName(pPluginName,
                                                                  pGoalName);

        final ModuleSettings moduleSettings = ModuleSettings.getInstance(getSelectedModule());
        final IGoalsGrabber projectGrabber = moduleSettings.getProjectGoalsGrabber();
        final IGoalsGrabber globalsGrabber = moduleSettings.getGlobalGoalsGrabber();
        final String projectOrigin = projectGrabber.getOrigin(fqName);
        final String globalOrigin = globalsGrabber.getOrigin(fqName);

        if (projectOrigin != null)
            openProjectGoalInEditor(getSelectedModule(), pPluginName, pGoalName);
        else if (globalOrigin != null)
            openGlobalGoalInEditor(getSelectedModule(), pPluginName, pGoalName);
    }

    public Module getSelectedModule() {
        final TreePath selection = goalsTree.getSelectionPath();
        if (selection == null)
            return null;

        final TreeNode moduleNode = (TreeNode) selection.getPathComponent(1);
        if (moduleNode instanceof ModuleTreeNode)
            return ((ModuleTreeNode) moduleNode).getModule();
        else
            return null;
    }

    public String[] getSelectedGoals() {
        final TreePath[] selections = goalsTree.getSelectionPaths();
        if (selections == null || selections.length == 0)
            return new String[0];

        final List<String> goalList = new ArrayList<String>(selections.length);

        for (final TreePath selection : selections) {
            final TreeNode node = (TreeNode) selection.getLastPathComponent();
            if (node instanceof GoalTreeNode) {
                final PluginTreeNode pluginNode = (PluginTreeNode) node.getParent();
                final String plugin = pluginNode.getPlugin();
                final String goal = ((GoalTreeNode) node).getGoal();
                goalList.add(GoalsHelper.buildFullyQualifiedName(plugin, goal));
            }
        }

        return goalList.toArray(new String[goalList.size()]);
    }

    public boolean isAutoScrollMode() {
        return autoScrollToSource;
    }

    public void setAutoScrollMode(final boolean pAutoScrollToSourceMode) {
        autoScrollToSource = pAutoScrollToSourceMode;
    }

    public static void register(final Project pProject) {
        final ToolWindowManager toolMgr = ToolWindowManager.getInstance(pProject);
        final GoalsToolWindowUI toolWin = new GoalsToolWindowUI(pProject);
        toolMgr.registerToolWindow(NAME, toolWin, ToolWindowAnchor.RIGHT);
        final ToolWindow goalsTw = toolMgr.getToolWindow(NAME);
        goalsTw.setIcon(Icons.MAVEN);
    }

    public static void unregister(final Project project) {
        final ToolWindowManager toolMgr = ToolWindowManager.getInstance(project);
        toolMgr.unregisterToolWindow(NAME);
    }

    public static GoalsToolWindowUI getInstance(final Project pProject) {
        final ToolWindowManager mgr = ToolWindowManager.getInstance(pProject);
        final ToolWindow tw = mgr.getToolWindow(NAME);
        return (GoalsToolWindowUI) tw.getComponent();
    }

    private class GoalsTreeExpander implements TreeExpander {
        public boolean canCollapse() {
            final TreeNode root = (TreeNode) model.getRoot();
            if (goalsTree.isCollapsed(new TreePath(root)))
                return false;

            final Object[] nodes = new Object[2];
            nodes[0] = root;

            final int moduleCount = root.getChildCount();
            for (int i = 0; i < moduleCount; i++) {
                nodes[1] = root.getChildAt(i);
                if (goalsTree.isExpanded(new TreePath(nodes)))
                    return true;
            }

            return false;
        }

        public boolean canExpand() {
            final TreeNode root = (TreeNode) model.getRoot();
            if (goalsTree.isCollapsed(new TreePath(root)))
                return true;

            final Object[] nodes = new Object[2];
            nodes[0] = root;

            final int moduleCount = root.getChildCount();
            for (int i = 0; i < moduleCount; i++) {
                nodes[1] = root.getChildAt(i);
                if (goalsTree.isCollapsed(new TreePath(nodes)))
                    return true;
            }

            return false;
        }

        public void collapseAll() {
            final TreeNode root = (TreeNode) model.getRoot();
            final Object[] nodes = new Object[2];
            nodes[0] = root;

            final int moduleCount = root.getChildCount();
            for (int i = 0; i < moduleCount; i++) {
                nodes[1] = root.getChildAt(i);
                goalsTree.collapsePath(new TreePath(nodes));
            }
        }

        public void expandAll() {
            final TreeNode root = (TreeNode) model.getRoot();
            final Object[] nodes = new Object[2];
            nodes[0] = root;

            final int moduleCount = root.getChildCount();
            for (int i = 0; i < moduleCount; i++) {
                nodes[1] = root.getChildAt(i);
                goalsTree.expandPath(new TreePath(nodes));
            }
        }
    }

    private class AttainSelectedGoalsAction extends AbstractAnAction {
        public AttainSelectedGoalsAction() {
            super(RES.get("attain.goal.action.text"),
                  RES.get("attain.goal.action.desc"),
                  Icons.EXECUTE);
        }

        public void update(AnActionEvent e) {
            final String[] goals = getSelectedGoals();
            e.getPresentation().setEnabled(goals != null && goals.length > 0);
        }

        public boolean displayTextInToolbar() {
            return true;
        }

        public void actionPerformed(final AnActionEvent pEvent) {
            MavenRunner.execute(getSelectedModule(),
                                getSelectedGoals(),
                                pEvent.getDataContext());
        }
    }

    private class ShowModuleSettingsAction extends AbstractAnAction {
        public ShowModuleSettingsAction() {
            super(RES.get("show.settings.action.text"),
                  RES.get("show.settings.action.desc"),
                  Icons.MAVEN_SETTINGS_SMALL);
        }

        public boolean displayTextInToolbar() {
            return true;
        }

        public void actionPerformed(final AnActionEvent pEvent) {
            final Project project = getProject(pEvent);

            final ConfigurableGroup[] configurableGroup =
                new ConfigurableGroup[]{new ModulesConfigurableGroup(project)};
            ShowSettingsUtil.getInstance().showSettingsDialog(project, configurableGroup);
        }
    }

    private static class ModulesConfigurableGroup implements ConfigurableGroup {
        private final Configurable[] configurables;
        private static final String SETTINGS_MODULES_TITLE = RES.get("modules.title");

        public ModulesConfigurableGroup(final Project pProject) {
            final Module[] modules = ModuleManager.getInstance(pProject).getModules();
            configurables = new Configurable[modules.length];

            for (int i = 0; i < modules.length; i++) {

                final Module module = modules[i];
                final ModuleSettingsConfigurable configurable =
                    new ModuleSettingsConfigurable(module);

                final ConfigurableWrapper wrapper = new ConfigurableWrapper(configurable);
                wrapper.setCustomDisplayName(StringUtils.capitalize(module.getName()));

                if (module.getModuleType().equals(ModuleType.WEB))
                    wrapper.setCustomIcon(com.intellij.util.Icons.WEB_ICON);
                else if (module.getModuleType().equals(ModuleType.EJB))
                    wrapper.setCustomIcon(com.intellij.util.Icons.EJB_ICON);
                else if (module.getModuleType().equals(ModuleType.J2EE_APPLICATION))
                    wrapper.setCustomIcon(com.intellij.util.Icons.PACKAGE_ICON);
                else if (module.getModuleType().equals(ModuleType.JAVA))
                    wrapper.setCustomIcon(com.intellij.util.Icons.CLASS_ICON);

                configurables[i] = wrapper;
            }
        }

        public String getDisplayName() {
            return SETTINGS_MODULES_TITLE;
        }

        public String getShortName() {
            return SETTINGS_MODULES_TITLE;
        }

        public Configurable[] getConfigurables() {
            return configurables;
        }
    }
}
