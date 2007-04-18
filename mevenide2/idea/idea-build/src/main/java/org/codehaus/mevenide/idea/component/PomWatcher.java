package org.codehaus.mevenide.idea.component;

import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.xml.DomFileDescription;
import com.intellij.util.xml.DomManager;
import org.apache.log4j.Logger;
import org.codehaus.mevenide.idea.gui.PomTreeUtil;
import org.codehaus.mevenide.idea.gui.PomTreeView;
import org.codehaus.mevenide.idea.helper.ActionContext;
import org.codehaus.mevenide.idea.model.MavenProjectDocument;
import org.codehaus.mevenide.idea.model.ModelUtils;
import org.codehaus.mevenide.idea.util.GuiUtils;
import org.codehaus.mevenide.idea.util.PluginConstants;
import org.codehaus.mevenide.idea.xml.ProjectDocument;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.LinkedHashSet;
import java.util.Set;

public class PomWatcher {
    private static final Logger LOG = Logger.getLogger(PomWatcher.class);

    private final ActionContext actionContext;
    private final PomTreeView pomTreeView;

    public PomWatcher(ActionContext actionContext, PomTreeView pomTreeView) {
        this.actionContext = actionContext;
        this.pomTreeView = pomTreeView;

        DomManager manager = DomManager.getDomManager(actionContext.getPluginProject());
        manager.registerFileDescription(new DomFileDescription<ProjectDocument.Project>(ProjectDocument.Project.class, "project") {
            protected void initializeFileDescription() {
            }
        });

        StartupManager.getInstance(actionContext.getPluginProject()).registerPostStartupActivity(new Runnable() {
            public void run() {
                loadExistingPoms();
                registerListeners();
            }
        });
    }

    private void loadExistingPoms() {
        actionContext.getPomDocumentList().clear();

        Set<VirtualFile> mavenPomList = getPomFilesOfProject();

        for (VirtualFile pomFile : mavenPomList) {
            ModelUtils.loadMavenProjectDocument(actionContext, pomFile);
        }

        fillPomTree();

        pomTreeView.rebuild();
    }

    private void registerListeners() {
        PsiManager.getInstance(actionContext.getPluginProject()).addPsiTreeChangeListener(new MyPsiTreeChangeAdapter());

        ProjectRootManager.getInstance(actionContext.getPluginProject()).addModuleRootListener(new ModuleRootListener() {
            public void beforeRootsChange(ModuleRootEvent event) {
            }

            public void rootsChanged(ModuleRootEvent event) {
                loadExistingPoms();
            }
        });
    }

    private Set<VirtualFile> getPomFilesOfProject() {
        Set<VirtualFile> pomFiles = new LinkedHashSet<VirtualFile>();

        for (VirtualFile contentRoot : ProjectRootManager.getInstance(actionContext.getPluginProject()).getContentRoots()) {
            readPomFiles(contentRoot, pomFiles);
        }

        return pomFiles;
    }

    private void fillPomTree() {
        PomTreeUtil.getPomTree(actionContext).clear();

        for (MavenProjectDocument mavenProjectDocument : actionContext.getPomDocumentList()) {
            PomTreeUtil.addSinglePomToTree(actionContext, mavenProjectDocument);
//            pomTreeView.addPom ( mavenProjectDocument );
        }
    }

    private void readPomFiles(VirtualFile root, Set<VirtualFile> pomFiles) {
        VirtualFile[] children = root.getChildren();

        if (children == null) {
            return;
        }

        for (VirtualFile child : children) {
            if (isPomFile(child)) {
                pomFiles.add(child);
            }

            readPomFiles(child, pomFiles);
        }
    }

    private boolean isPomFile(VirtualFile file) {
        return file.getName().equalsIgnoreCase(PluginConstants.POM_FILE_NAME);
    }

    private class MyPsiTreeChangeAdapter extends AbstractFileChangeAdapter {

        protected boolean matches(VirtualFile virtualFile) {
            return isPomFile(virtualFile);
        }

        protected void doUpdate(VirtualFile virtualFile) {
            MavenProjectDocument document = findMavenPluginDocument(actionContext, virtualFile);
            if (document == null) {
                MavenProjectDocument newDocument = ModelUtils.loadMavenProjectDocument(actionContext, virtualFile);
                if (newDocument != null) {
                    PomTreeUtil.addSinglePomToTree(actionContext, newDocument);
                } else {
                    LOG.error("failed to load document: " + virtualFile.getPath());
                }
            } else {
                document.reparse();
                ModelUtils.loadPlugins(document, actionContext.getProjectPluginSettings().getMavenRepository());
                DefaultMutableTreeNode node = PomTreeUtil.findMavenProjectDocumentNode(actionContext, document);
                DefaultTreeModel treeModel = (DefaultTreeModel) PomTreeUtil.getPomTree(actionContext).getModel();
                PomTreeUtil.updatePluginNodes(document, actionContext, node);
                treeModel.nodeChanged(node);
            }

            pomTreeView.update( virtualFile );
        }

        protected void doRemove(VirtualFile virtualFile) {
            MavenProjectDocument document = findMavenPluginDocument(actionContext, virtualFile);
            if (document != null) {
                actionContext.getPomDocumentList().remove(document);
                DefaultMutableTreeNode node = PomTreeUtil.findMavenProjectDocumentNode(actionContext, document);
                GuiUtils.removeAndSelectParent(PomTreeUtil.getPomTree(actionContext), node);
            } else {
                LOG.error("Cannot find document for " + virtualFile.getPath());
            }
            pomTreeView.remove ( virtualFile);
        }
    }

    private static MavenProjectDocument findMavenPluginDocument(ActionContext actionContext, VirtualFile file) {
        for (MavenProjectDocument document : actionContext.getPomDocumentList()) {
            if (document.getPomFile().equals(file)) {
                return document;
            }
        }
        return null;
    }
}
