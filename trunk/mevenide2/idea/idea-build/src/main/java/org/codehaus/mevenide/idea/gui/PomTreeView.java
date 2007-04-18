package org.codehaus.mevenide.idea.gui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.SimpleTreeBuilder;
import com.intellij.util.containers.HashSet;
import org.codehaus.mevenide.idea.component.PomTreeStructure;
import org.codehaus.mevenide.idea.model.MavenProjectDocument;
import org.codehaus.mevenide.idea.xml.MavenDefaultsDocument;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class PomTreeView {
    public static class Settings {
        public boolean groupByModule = false;
        public boolean groupByDirectory = false;
        public boolean filterStandardPhases = false;
    }

    private Settings settings = new Settings();

    private Project myProject;

    Set<MavenProjectDocument> myDocuments = new HashSet<MavenProjectDocument>();

    private SimpleTree myTree;
    private SimpleTreeBuilder myBuilder;
    PomTreeStructure treeStructure;

    public PomTreeView(Project project, Iterable<? extends MavenDefaultsDocument.Goal> standardGoals, Collection<String> standardPhases, String mavenRepository) {
        this.myProject = project;
        myTree = new SimpleTree();
        myTree.setRootVisible(false);
        myTree.setShowsRootHandles(true);
        myTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        treeStructure = new PomTreeStructure(project, mavenRepository, settings, standardPhases, standardGoals, myTree);
        myBuilder = new SimpleTreeBuilder(myTree, (DefaultTreeModel) myTree.getModel(), treeStructure, null);
        myBuilder.initRoot();

        treeStructure.setBuilder ( myBuilder);

        myTree.addMouseListener(new PomTreeMouseAdapter(this));

        Disposer.register(myProject, myBuilder);
    }

    public Project getProject() {
        return myProject;
    }

    public Collection<SimpleNode> getSelectedNodes() {
        Collection<SimpleNode> nodes = new ArrayList<SimpleNode>();
        TreePath[] treePaths = myTree.getSelectionPaths();
        if (treePaths != null) {
            for (TreePath treePath : treePaths) {
                nodes.add(myTree.getNodeFor(treePath));
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

    public Settings getSettings() {
        return settings;
    }

    public SimpleTree getTree() {
        return myTree;
    }

    public void update(VirtualFile virtualFile) {
        treeStructure.update(virtualFile);
    }

    public void remove(VirtualFile virtualFile) {
        treeStructure.remove(virtualFile);
    }

    public void rebuild() {
        treeStructure.rebuild();
        updateStructure();
        myTree.expandPath(new TreePath(myTree.getModel().getRoot()));
    }

    public void updateStructure() {
        myBuilder.updateFromRoot(true);
    }
}
