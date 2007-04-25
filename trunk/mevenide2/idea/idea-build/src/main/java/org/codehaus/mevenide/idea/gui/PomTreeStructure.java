package org.codehaus.mevenide.idea.gui;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.SimpleTreeBuilder;
import com.intellij.ui.treeStructure.SimpleTreeStructure;
import com.intellij.util.ui.tree.TreeUtil;
import org.codehaus.mevenide.idea.build.MavenRunner;
import org.codehaus.mevenide.idea.common.MavenBuildPluginSettings;
import org.codehaus.mevenide.idea.common.util.ErrorHandler;
import org.codehaus.mevenide.idea.model.MavenPluginDocument;
import org.codehaus.mevenide.idea.model.MavenProjectDocument;
import org.codehaus.mevenide.idea.model.ModelUtils;
import org.codehaus.mevenide.idea.util.PluginConstants;
import org.codehaus.mevenide.idea.xml.PluginDocument;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.util.*;

public class PomTreeStructure extends SimpleTreeStructure {
    public static class Settings {
        public boolean groupByModule = false;
        public boolean groupByDirectory = false;
        public boolean filterStandardPhases = false;
    }

    private final Settings settings = new Settings();

    private final Project project;
    private final String mavenRepository;

    private final RootNode root;

    private Icon iconFolderOpen = IconLoader.getIcon("/nodes/folderOpen.png");
    private Icon iconFolderClosed = IconLoader.getIcon("/nodes/folder.png");
    private Icon iconPlugin = IconLoader.getIcon("/nodes/plugin.png");

    private Icon iconPom = IconLoader.getIcon(PluginConstants.ICON_POM_SMALL);
    private Icon iconGoal = IconLoader.getIcon(PluginConstants.ICON_APPLICATION_SMALL);

    private Icon iconPhasesOpen = IconLoader.getIcon("/nodes/moduleGroupOpen.png");
    private Icon iconPhasesClosed = IconLoader.getIcon("/nodes/moduleGroupClosed.png");

    private Collection<String> standardPhases;
    private Collection<String> standardGoals;
    private SimpleTreeBuilder treeBuilder;
    private SimpleTree tree;

    private Map<VirtualFile, PomNode> fileToNode = new HashMap<VirtualFile,PomNode>();
    private Element savedConfigElement;

    @NonNls private static final String GROUP_ID_ATTR = "groupId";
    @NonNls private static final String ARTIFACT_ID_ATTR = "artifactId";
    @NonNls private static final String VERSION_ATTR = "version";
    @NonNls private static final String PLUGIN_TAG = "plugin";
    @NonNls private static final String POM_TAG = "pom";
    @NonNls private static final String POM_OPTIONS_TAG = "pom-options";
    @NonNls private static final String ID_ATTR = "id";

    Map<String,Integer> standardGoalOrder;

    public PomTreeStructure(Project project, MavenBuildPluginSettings pluginSettings) {
        this.project = project;

        tree = new SimpleTree();
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        tree.addMouseListener(new PomTreeMouseAdapter(this));

        standardGoals = pluginSettings.getStandardGoalsList();
        standardPhases = pluginSettings.getStandardPhasesList();
        mavenRepository = pluginSettings.getMavenRepository();

        root = new RootNode();
        SimpleTreeBuilder myBuilder = new SimpleTreeBuilder(tree, (DefaultTreeModel) tree.getModel(), this, null);
        myBuilder.initRoot();
        setBuilder (myBuilder);
        Disposer.register(project, myBuilder);
    }

    public Settings getSettings() {
        return settings;
    }

    public SimpleTree getTree() {
        return tree;
    }

    public Project getProject() {
        return project;
    }

    public Object getRootElement() {
        return root;
    }

    public void setBuilder(SimpleTreeBuilder builder) {
        this.treeBuilder = builder;
    }

    public void rebuild(Iterable<? extends VirtualFile> files){
        final Map<VirtualFile, PomNode> oldFileToNode = fileToNode;
        fileToNode = new HashMap<VirtualFile, PomNode>();
        for (VirtualFile pomFile : files) {
            PomNode pomNode = oldFileToNode.get(pomFile);
            if ( pomNode == null ){
                pomNode = new PomNode(pomFile);
            } else {
                pomNode.unlinkNested();
            }
            fileToNode.put(pomFile, pomNode);
        }

        if ( savedConfigElement != null) {
            restorePluginState(savedConfigElement);
            savedConfigElement = null;
        }
        
        root.rebuild();

        updateFromRoot(true);
        tree.expandPath(new TreePath(tree.getModel().getRoot()));
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
            updateFromRoot(false);
        }
    }

    public void updateFromRoot(boolean rebuild) {
        treeBuilder.updateFromRoot(rebuild);
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

    public static PomNode getCommonParent (Collection<GoalNode> goalNodes) {
        PomNode parent = null;
        for (GoalNode goalNode : goalNodes) {
            PomNode nextParent = goalNode.getParent(PomNode.class);
            if ( parent == null ) {
                parent = nextParent;
            } else if ( parent != nextParent ) {
                return null;
            }
        }
        return parent;
    }

    private int getStandardGoalOrder (String goal){
        if ( standardGoalOrder == null ) {
            standardGoalOrder = new HashMap<String, Integer>();
            int i = 0;
            for ( String aGoal : standardGoals){
                standardGoalOrder.put (aGoal, i++);
            }
        }
        Integer order = standardGoalOrder.get(goal);
        return order != null ? order : standardGoalOrder.size();
    }

    public List<String> getSortedGoalList(Collection<GoalNode> goalNodes) {
        List<String> goalList = new ArrayList<String>();
        for (GoalNode node : goalNodes) {
            goalList.add(node.getName());
        }
        Collections.sort(goalList, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return getStandardGoalOrder(o1) - getStandardGoalOrder(o2);
            }
        });
        return goalList;
    }

    public void runGoals(Collection<PomTreeStructure.GoalNode> goalNodes){
        PomTreeStructure.PomNode pomNode = PomTreeStructure.getCommonParent(goalNodes);
        if (pomNode != null) {
            MavenRunner.setupBuildContext(project, pomNode.getFile(), getSortedGoalList(goalNodes));
            MavenRunner.run(project);
        }
    }

    private PomNode findPomById(String id) {
        for ( PomNode pomNode : fileToNode.values()) {
            if ( pomNode.getId().equals(id)) {
                return pomNode;
            }
        }
        return null;
    }

    public void readExternal(Element root) {
        Element element = root.getChild("pom-tree");
        if ( element != null ) {
            settings.groupByModule = Boolean.valueOf(JDOMExternalizerUtil.readField(element, "groupByModule"));
            settings.groupByDirectory = Boolean.valueOf(JDOMExternalizerUtil.readField(element, "groupByDirectory"));
            settings.filterStandardPhases = Boolean.valueOf(JDOMExternalizerUtil.readField(element, "filterStandardPhases"));
        }
        savedConfigElement = root;
    }

    public void writeExternal(Element root) {
        Element element = new Element("pom-tree");
        root.addContent(element);
        JDOMExternalizerUtil.writeField(element, "groupByModule", Boolean.toString(settings.groupByModule));
        JDOMExternalizerUtil.writeField(element, "groupByDirectory", Boolean.toString(settings.groupByDirectory));
        JDOMExternalizerUtil.writeField(element, "filterStandardPhases", Boolean.toString(settings.filterStandardPhases));

        savePluginsState(root);
    }

    private void restorePluginState(Element root) {
        Element pomOptionsElement = root.getChild(POM_OPTIONS_TAG);
        if (pomOptionsElement != null) {
            for (Object pom : pomOptionsElement.getChildren(POM_TAG)) {
                Element pomElement = (Element) pom;
                PomNode node = findPomById(pomElement.getAttributeValue(ID_ATTR));
                if (node != null) {
                    for (Object plugin : pomElement.getChildren(PLUGIN_TAG)) {
                        Element pluginElement = (Element) plugin;
                        String path = ModelUtils.findPluginPath(mavenRepository,
                                pluginElement.getAttributeValue(GROUP_ID_ATTR),
                                pluginElement.getAttributeValue(ARTIFACT_ID_ATTR),
                                pluginElement.getAttributeValue(VERSION_ATTR));
                        if ( path != null ) {
                            node.attachPlugin(path);
                        }
                    }
                }
            }
        }
    }

    private void savePluginsState(Element root) {
        Element pomOptionsElement = null;
        for ( PomNode pomNode : fileToNode.values()) {
            Element pomElement = null;
            for ( ExtraPluginNode plugin : pomNode.getExtraPluginNodes()){
                if ( pomElement == null ){
                    if ( pomOptionsElement == null ){
                        pomOptionsElement = new Element(POM_OPTIONS_TAG);
                        root.addContent(pomOptionsElement);
                    }
                    pomElement = new Element (POM_TAG);
                    pomElement.setAttribute(ID_ATTR, pomNode.getId());
                    pomOptionsElement.addContent(pomElement);
                }
                Element pluginElement = new Element(PLUGIN_TAG);
                PluginDocument.Plugin pluginModel = plugin.getDocument().getPlugin();
                pluginElement.setAttribute(GROUP_ID_ATTR, pluginModel.getGroupId());
                pluginElement.setAttribute(ARTIFACT_ID_ATTR, pluginModel.getArtifactId());
                pluginElement.setAttribute(VERSION_ATTR, pluginModel.getVersion());
                pomElement.addContent(pluginElement);
            }
        }
    }

    public Collection<SimpleNode> getSelectedNodes() {
        Collection<SimpleNode> nodes = new ArrayList<SimpleNode>();
        TreePath[] treePaths = tree.getSelectionPaths();
        if (treePaths != null) {
            for (TreePath treePath : treePaths) {
                nodes.add(tree.getNodeFor(treePath));
            }
        }
        return nodes;
    }

    public <T extends SimpleNode> Collection<T> getSelectedNodes(Class<T> aClass, boolean strict) {
        return filterNodes ( getSelectedNodes(), aClass, strict );
    }

    public <T extends SimpleNode> Collection<T> filterNodes(Collection<SimpleNode> nodes, Class<T> aClass, boolean strict) {
        Collection<T> filtered = new ArrayList<T>();
        for (SimpleNode node : nodes) {
            if ((aClass != null) && (!aClass.isInstance(node) || (strict && aClass != node.getClass()))) {
                filtered.clear();
                break;
            }
            //noinspection unchecked
            filtered.add((T) node);
        }
        return filtered;
    }

    public Navigatable[] getNavigatables() {
        Collection<PomTreeStructure.PomNode> selectedNodes = getSelectedNodes(PomTreeStructure.PomNode.class, true);
        if (selectedNodes.isEmpty()) {
            return null;
        } else {
            final ArrayList<Navigatable> navigatables = new ArrayList<Navigatable>();
            for (PomTreeStructure.PomNode pomNode : selectedNodes) {
                navigatables.add ( pomNode.getDocument().getPsiFile());
            }
            return navigatables.toArray(new Navigatable[navigatables.size()]);
        }
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

        public String getId () {
            return document.toString();
        }

        public List<ExtraPluginNode> getExtraPluginNodes() {
            return extraPluginNodes;
        }

        public String getSavedPath() {
            return savedPath;
        }

        public VirtualFile getFile() {
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

        public void attachPlugin(String path) {
            try {
                MavenPluginDocument mavenPluginDocument = ModelUtils.createMavenPluginDocument(path);
                if (mavenPluginDocument != null) {
                   attachPlugin(mavenPluginDocument);
                }
            } catch (Exception e) {
                ErrorHandler.processAndShowError(getProject(), e);
            }
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

            for (String goal : standardGoals) {
                goalNodes.add(new StandardGoalNode(this, goal));
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
        protected final MavenPluginDocument plugin;

        public PluginNode(PomNode parent, MavenPluginDocument plugin) {
            super(parent);
            this.plugin = plugin;
            String prefix = plugin.getPluginDocument().getPlugin().getGoalPrefix();
            addPlainText(prefix);
            setUniformIcon(iconPlugin);

            for (String goal : plugin.getPluginGoalList()) {
                goalNodes.add(new PluginGoalNode(this, prefix, goal));
            }
        }
    }

    class PluginGoalNode extends GoalNode {
        public PluginGoalNode(PluginNode parent, String pluginPrefix, String goal) {
            super(parent, pluginPrefix + ":" + goal);
        }
    }

    public class ExtraPluginNode extends PluginNode {
        public ExtraPluginNode(PomNode parent, MavenPluginDocument plugin) {
            super(parent, plugin);
        }

        public void detach() {
            getParent(PomNode.class).detachPlugin(this);
        }

        public PluginDocument getDocument() {
            return plugin.getPluginDocument();
        }
    }
}
