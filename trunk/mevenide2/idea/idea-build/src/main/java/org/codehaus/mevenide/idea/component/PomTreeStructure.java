package org.codehaus.mevenide.idea.component;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.SimpleTreeBuilder;
import com.intellij.ui.treeStructure.SimpleTreeStructure;
import com.intellij.util.ui.tree.TreeUtil;
import org.codehaus.mevenide.idea.gui.BuildBundle;
import org.codehaus.mevenide.idea.gui.PomTreeView;
import org.codehaus.mevenide.idea.model.MavenPluginDocument;
import org.codehaus.mevenide.idea.model.MavenProjectDocument;
import org.codehaus.mevenide.idea.model.ModelUtils;
import org.codehaus.mevenide.idea.model.PluginGoal;
import org.codehaus.mevenide.idea.util.PluginConstants;
import org.codehaus.mevenide.idea.xml.MavenDefaultsDocument;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PomTreeStructure extends SimpleTreeStructure {
    private final Project project;
    private final String mavenRepository;
    private final PomTreeView.Settings settings;
    private final RootNode root;

    private Icon iconFolderOpen = IconLoader.getIcon("/nodes/folderOpen.png");
    private Icon iconFolderClosed = IconLoader.getIcon("/nodes/folder.png");
    private Icon iconPlugin = IconLoader.getIcon("/nodes/plugin.png");

    private Icon iconPom = IconLoader.getIcon(PluginConstants.ICON_POM_SMALL);
    private Icon iconGoal = IconLoader.getIcon(PluginConstants.ICON_APPLICATION_SMALL);

    private Icon iconPhasesOpen = IconLoader.getIcon("/nodes/moduleGroupOpen.png");
    private Icon iconPhasesClosed = IconLoader.getIcon("/nodes/moduleGroupClosed.png");

    private Collection<String> standardPhases;
    private Iterable<? extends MavenDefaultsDocument.Goal> standardGoals;
    private SimpleTreeBuilder treeBuilder;
    private SimpleTree tree;

    private Map<VirtualFile, PomNode> fileToNode = new HashMap<VirtualFile,PomNode>();

    public PomTreeStructure(Project project, String mavenRepository, PomTreeView.Settings settings, Collection<String> standardPhases, Iterable<? extends MavenDefaultsDocument.Goal> standardGoals, SimpleTree tree) {
        this.tree = tree;
        this.standardGoals = standardGoals;
        this.standardPhases = standardPhases;
        this.project = project;
        this.mavenRepository = mavenRepository;
        this.settings = settings;
        this.root = new RootNode();
    }

    public Object getRootElement() {
        return root;
    }

    public void setBuilder(SimpleTreeBuilder builder) {
        this.treeBuilder = builder;
    }

    public void rebuild() {
        Map<VirtualFile, PomNode> oldFileToNode = fileToNode;
        fileToNode = new HashMap<VirtualFile, PomNode>();
        for (VirtualFile pomFile : collectPomFiles()) {
            PomNode pomNode = oldFileToNode.get(pomFile);
            if ( pomNode == null ){
                pomNode = new PomNode(pomFile);
            } else {
                pomNode.unlinkNested();
            }
            fileToNode.put(pomFile, pomNode);
        }
        root.rebuild();
    }

    private Collection<VirtualFile> collectPomFiles() {
        Collection<VirtualFile> pomFiles = new ArrayList<VirtualFile>();
        for ( VirtualFile dir : ProjectRootManager.getInstance(project).getContentRoots()){
            collectPomFiles(dir, pomFiles);
        }
        return pomFiles;
    }

    private void collectPomFiles(VirtualFile dir, Collection<VirtualFile> pomFiles) {
        for (VirtualFile child : dir.getChildren()) {
            if (child.isDirectory()) {
                collectPomFiles(child, pomFiles);
            } else if (child.getName().equals("pom.xml")) {
                pomFiles.add(child);
            }
        }
    }

    public void update(VirtualFile file) {
        final PomNode pomNode = fileToNode.get(file);
        if ( pomNode != null ) {
            pomNode.onFileUpdate();
        } else {
            final PomNode newNode = new PomNode(file);
            fileToNode.put( file, newNode);
            root.addToStructure(newNode);
        }
    }

    public void remove(VirtualFile file) {
        final PomNode pomNode = fileToNode.get(file);
        if (pomNode != null) {
            fileToNode.remove(file);
            pomNode.removeFromParent();
        }
    }

    private void updateTreeFrom(SimpleNode node) {
        final DefaultMutableTreeNode mutableTreeNode = TreeUtil.findNodeWithObject((DefaultMutableTreeNode) tree.getModel().getRoot(), node);
        if (mutableTreeNode != null) {
            treeBuilder.addSubtreeToUpdate(mutableTreeNode);
        } else {
            treeBuilder.updateFromRoot();
        }
    }

    static Comparator<SimpleNode> nodeComparator = new Comparator<SimpleNode>() {
        public int compare(SimpleNode o1, SimpleNode o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };

    private static <T extends SimpleNode> void insertSorted(List<T> list, T newObject) {
        int pos = Collections.binarySearch(list, newObject, nodeComparator);
        list.add (pos >= 0 ? pos : -pos - 1, newObject );
    }

    class CustomNode extends SimpleNode {
        private CustomNode structuralParent;

        public CustomNode(CustomNode parent) {
            super(project);
            setStructuralParent(parent);
        }

        public void setStructuralParent(CustomNode structuralParent) {
            this.structuralParent = structuralParent;
        }

        public SimpleNode[] getChildren() {
            return SimpleNode.NO_CHILDREN;
        }

        public <T extends CustomNode> T getParent(Class<T> aClass) {
            CustomNode node = this;
            while (true) {
                node = node.structuralParent;
                if (node == null || aClass.isInstance(node)) {
                    //noinspection unchecked
                    return (T) node;
                }
            }
        }

        protected boolean isVisible () {
            return true;
        }

        protected void updateSubTree() {
            updateTreeFrom(this);
        }
    }

    interface DisplayList {
        void add(Iterable<? extends CustomNode> nodes);
        void add(CustomNode node);
        void sort();
    }

    abstract class ListNode extends CustomNode implements DisplayList {

        List<SimpleNode> displayList = new ArrayList<SimpleNode>();

        public ListNode(CustomNode parent) {
            super(parent);
        }

        public SimpleNode[] getChildren() {
            displayList.clear();
            collect(this);
            return displayList.toArray(new SimpleNode[displayList.size()]);
        }

        public void add(Iterable<? extends CustomNode> nodes) {
            for (CustomNode node : nodes) {
                add(node);
            }
        }

        public void add(CustomNode node) {
            if (node.isVisible()){
                displayList.add(node);
            }
        }

        public void sort() {
            Collections.sort(displayList, nodeComparator);
        }

        protected abstract void collect(DisplayList displayList);
    }

    class RootNode extends ListNode {

        List<ModuleNode> moduleNodes = new ArrayList<ModuleNode>();

        public RootNode() {
            super(null);
            addPlainText(BuildBundle.message("node.root"));
        }

        protected void collect(DisplayList displayList) {
            if (settings.groupByModule) {
                displayList.add(moduleNodes);
            } else {
                for (ModuleNode moduleNode : moduleNodes) {
                    moduleNode.collect(displayList);
                }
                displayList.sort();
            }
        }

        private void rebuild() {
            moduleNodes.clear();
            for (PomNode pomNode : fileToNode.values()) {
                addToStructure(pomNode);
            }
        }

        private void addToStructure(PomNode pomNode) {
            findModuleNode(VfsUtil.getModuleForFile(project, pomNode.getFile())).addPom(pomNode);
        }

        private ModuleNode findModuleNode(Module module) {
            for (ModuleNode moduleNode : moduleNodes) {
                if (moduleNode.getModule() == module) {
                    return moduleNode;
                }
            }
            ModuleNode newNode = new ModuleNode(this, module);
            insertSorted(moduleNodes, newNode);
            updateSubTree();
            return newNode;
        }
    }

    abstract class PomGroupNode extends ListNode {

        List<PomNode> pomNodes = new ArrayList<PomNode>();

        public PomGroupNode(CustomNode parent) {
            super(parent);
        }

        protected void collectNested(DisplayList displayList) {
            displayList.add(pomNodes);
        }

        public void collectNestedRecursively(DisplayList nodes) {
            collectNested(nodes);
            for (PomNode pomNode : pomNodes) {
                pomNode.collectNestedRecursively(nodes);
            }
        }

        public void addPom(PomNode newNode) {
            Collection<PomNode> childrenOfNew = new ArrayList<PomNode>();
            for (PomNode node : pomNodes) {
                if ( node.isAncestor(newNode)) {
                    node.addNestedPom(newNode);
                    return;
                }
                if ( newNode.isAncestor (node)) {
                    childrenOfNew.add(node);
                }
            }

            pomNodes.removeAll(childrenOfNew);
            for (PomNode child : childrenOfNew) {
                newNode.addNestedPom(child);
            }

            newNode.setStructuralParent(this);
            insertSorted(pomNodes, newNode);
            updateSubTree();
        }

        public void remove(PomNode pomNode) {
            pomNodes.remove(pomNode);
        }

        public void reinsert(PomNode pomNode) {
            remove(pomNode);
            insertSorted(pomNodes, pomNode);
        }

        public void merge(PomGroupNode groupNode) {
            for (PomNode pomNode : groupNode.pomNodes) {
              addPom( pomNode);
            }
            groupNode.clear();
        }

        public void clear() {
            pomNodes.clear();
        }
    }

    class ModuleNode extends PomGroupNode {
        private final Module module;

        public ModuleNode(RootNode parent, Module module) {
            super(parent);
            this.module = module;
            addPlainText(module.getName());
            ModuleType moduleType = module.getModuleType();
            setIcons(moduleType.getNodeIcon(false), moduleType.getNodeIcon(true));
        }

        public Module getModule() {
            return module;
        }

        protected void collect(DisplayList displayList) {
            if (settings.groupByDirectory) {
                collectNested(displayList);
            } else {
                collectNestedRecursively(displayList);
                displayList.sort();
            }
        }
    }

    public class PomNode extends ListNode {

        MavenProjectDocument document;

        GoalGroupNode phasesNode;
        List<PluginNode> pomPluginNodes = new ArrayList<PluginNode>();
        List<ExtraPluginNode> extraPluginNodes = new ArrayList<ExtraPluginNode>();
        NestedPomsNode nestedPomsNode;

        String savedPath = "";

        public PomNode(VirtualFile pom) {
            super(null);
            setUniformIcon(iconPom);
            document = ModelUtils.loadMavenProjectDocument(project, pom);
            phasesNode = new StandardPhasesNode(this);
            nestedPomsNode = new NestedPomsNode(this);

            updateNode();
        }

        protected void collect(DisplayList displayList) {
            displayList.add(phasesNode);
            displayList.add(pomPluginNodes);
            displayList.add(extraPluginNodes);
            displayList.add(nestedPomsNode);
        }

        public MavenProjectDocument getDocument() {
            return document;
        }

        public String getSavedPath() {
            return savedPath;
        }

        private VirtualFile getFile() {
            return document.getPomFile();
        }

        private VirtualFile getDirectory() {
            return getFile().getParent();
        }

        public boolean isAncestor(PomNode that) {
            return VfsUtil.isAncestor(this.getDirectory(), that.getDirectory(), true);
        }

        private void updateNode() {
            document.reparse();
            ModelUtils.loadPlugins(document, mavenRepository);
            createPomPluginNodes();
            updateText();
        }

        private void createPomPluginNodes() {
            pomPluginNodes.clear();
            for (MavenPluginDocument plugin : document.getPlugins()) {
                if (plugin.getPluginGoalList().size() != 0) {
                    pomPluginNodes.add(new PluginNode(this, plugin));
                }
            }
        }

        private void updateText() {
            clearColoredText();
            addPlainText(document.getProject().getName().getStringValue());
            savedPath = getDirectory().getPath();
            addColoredFragment(" (" + savedPath + ")", SimpleTextAttributes.GRAYED_ATTRIBUTES);
        }

        public void addNestedPom(PomNode child) {
            nestedPomsNode.addPom(child);
        }

        private void updateFromVisibleParent() {
            updateTreeFrom(getParent(
                    settings.groupByDirectory ? NestedPomsNode.class :
                    settings.groupByModule ?    ModuleNode.class :
                                                RootNode.class));
        }

        public void collectNestedRecursively(DisplayList displayList) {
            nestedPomsNode.collectNestedRecursively(displayList);
        }

        void onFileUpdate() {
            final String oldName = getName();
            final String oldPath = getSavedPath();

            updateNode();

            if ( ! oldPath.equals (getSavedPath())){
                removeFromParent();
                root.addToStructure(this);
            } else if (!oldName.equals(getName())) {
                PomGroupNode groupNode = getParent(PomGroupNode.class);
                groupNode.reinsert(this);
                updateFromVisibleParent();
            } else {
                updateSubTree();
            }
        }

        void removeFromParent() {
            PomGroupNode groupNode = getParent(PomGroupNode.class);
            groupNode.remove(this);
            groupNode.merge(nestedPomsNode);
            updateFromVisibleParent();
        }

        public void attachPlugin(MavenPluginDocument mavenPluginDocument) {
            extraPluginNodes.add(new ExtraPluginNode(this, mavenPluginDocument));
            updateSubTree();
        }

        public void detachPlugin(ExtraPluginNode pluginNode) {
            extraPluginNodes.remove(pluginNode);
            updateSubTree();
        }

        public void unlinkNested() {
            nestedPomsNode.clear();
        }
    }

    class NestedPomsNode extends PomGroupNode {

        public NestedPomsNode(PomNode parent) {
            super(parent);
            addPlainText(BuildBundle.message("node.nested.poms"));
            setIcons(iconFolderClosed, iconFolderOpen);
        }

        protected void collect(DisplayList displayList) {
            collectNested(displayList);
        }


        protected boolean isVisible() {
            return settings.groupByDirectory && ! pomNodes.isEmpty();
        }
    }

    abstract class GoalGroupNode extends ListNode {

        List<CustomNode> goalNodes = new ArrayList<CustomNode>();

        public GoalGroupNode(PomNode parent) {
            super(parent);
        }

        protected void collect(DisplayList displayList) {
            displayList.add(goalNodes);
        }
    }

    public abstract class GoalNode extends CustomNode {
        private final String goal;

        public GoalNode(CustomNode parent, String goal) {
            super(parent);
            this.goal = goal;
            addPlainText(goal);
            setUniformIcon(iconGoal);
        }

        public String getGoal() {
            return goal;
        }
    }

    class StandardPhasesNode extends GoalGroupNode {

        public StandardPhasesNode(PomNode parent) {
            super(parent);
            addPlainText(BuildBundle.message("node.phases"));
            setIcons(iconPhasesClosed, iconPhasesOpen);

            for (MavenDefaultsDocument.Goal goal : standardGoals) {
                goalNodes.add(new StandardGoalNode(this, goal.getName().toString()));
            }
        }
    }

    class StandardGoalNode extends GoalNode {

        public StandardGoalNode(CustomNode parent, String goal) {
            super(parent, goal);
        }

        public boolean isVisible() {
            return ! settings.filterStandardPhases || standardPhases.contains(getName());
        }
    }

    public class PluginNode extends GoalGroupNode {
        public PluginNode(PomNode parent, MavenPluginDocument plugin) {
            super(parent);
            addPlainText(plugin.getPluginDocument().getPlugin().getGoalPrefix());
            setUniformIcon(iconPlugin);

            for (PluginGoal goal : plugin.getPluginGoalList()) {
                goalNodes.add(new PluginGoalNode(this, goal));
            }
        }
    }

    class PluginGoalNode extends GoalNode {
        public PluginGoalNode(PluginNode parent, PluginGoal goal) {
            super(parent, goal.getPluginPrefix() + ":" + goal.getGoal());
        }
    }

    public class ExtraPluginNode extends PluginNode {
        public ExtraPluginNode(PomNode parent, MavenPluginDocument plugin) {
            super(parent, plugin);
        }

        public void detach() {
            getParent(PomNode.class).detachPlugin(this);
        }
    }
}
