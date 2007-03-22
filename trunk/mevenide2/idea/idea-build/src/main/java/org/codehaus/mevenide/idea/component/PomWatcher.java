package org.codehaus.mevenide.idea.component;

import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.util.xml.DomFileDescription;
import com.intellij.util.xml.DomManager;
import org.apache.log4j.Logger;
import org.codehaus.mevenide.idea.gui.PomTreeUtil;
import org.codehaus.mevenide.idea.helper.ActionContext;
import org.codehaus.mevenide.idea.model.MavenProjectDocument;
import org.codehaus.mevenide.idea.model.ModelUtils;
import org.codehaus.mevenide.idea.util.GuiUtils;
import org.codehaus.mevenide.idea.util.PluginConstants;
import org.codehaus.mevenide.idea.xml.ProjectDocument;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PomWatcher {
    private static final Logger LOG = Logger.getLogger(PomWatcher.class);

    private final ActionContext actionContext;

    public PomWatcher(ActionContext actionContext) {
        this.actionContext = actionContext;

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

    private class MyPsiTreeChangeAdapter extends PsiTreeChangeAdapter {

        public void beforeChildAddition(PsiTreeChangeEvent event) {
            super.beforeChildAddition(event);
        }

        public void beforeChildRemoval(PsiTreeChangeEvent event) {
            super.beforeChildRemoval(event);
        }

        public void beforeChildReplacement(PsiTreeChangeEvent event) {
            super.beforeChildReplacement(event);
            if (event.getParent() instanceof PsiDirectory) {
                tryRemove (event.getOldChild());
            }
        }

        public void beforeChildMovement(PsiTreeChangeEvent event) {
            super.beforeChildMovement(event);
        }

        public void beforeChildrenChange(PsiTreeChangeEvent event) {
            super.beforeChildrenChange(event);
        }

        public void beforePropertyChange(PsiTreeChangeEvent event) {
            super.beforePropertyChange(event);
            if (event.getPropertyName().equals(PsiTreeChangeEvent.PROP_FILE_NAME)) {
                tryRemove(event.getChild());
            }
        }

        public void childAdded(PsiTreeChangeEvent event) {
            super.childAdded(event);
            tryAdd(event.getChild());
            tryUpdate(event.getChild());
        }

        public void childRemoved(PsiTreeChangeEvent event) {
            super.childRemoved(event);
            if (event.getParent() instanceof PsiDirectory) {
                tryRemove(event.getChild());
            }
            tryUpdate(event.getParent());
        }

        public void childReplaced(PsiTreeChangeEvent event) {
            super.childReplaced(event);
            if (event.getParent() instanceof PsiDirectory) {
                tryAdd(event.getNewChild());
            }
            tryUpdate(event.getChild());
        }

        public void childMoved(PsiTreeChangeEvent event) {
            super.childMoved(event);
            tryUpdate(event.getChild());
        }

        public void childrenChanged(PsiTreeChangeEvent event) {
            super.childrenChanged(event);
        }

        public void propertyChanged(PsiTreeChangeEvent event) {
            super.propertyChanged(event);
            if (event.getPropertyName().equals(PsiTreeChangeEvent.PROP_FILE_NAME)) {
                tryAdd(event.getElement());
            }
        }

        private VirtualFile getPomFile(PsiFile psiFile) {
            VirtualFile virtualFile = psiFile.getVirtualFile();
            if (virtualFile != null) {
                if (isPomFile(virtualFile)) {
                    return virtualFile;
                }
            }
            return null;
        }

        private void tryAdd(PsiElement element) {
            if (element instanceof PsiFile) {
                final VirtualFile virtualFile = getPomFile((PsiFile) element);
                if (virtualFile != null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            doAdd(virtualFile);
                        }
                    });
                }
            }
        }

        private void doAdd(VirtualFile virtualFile) {
            List<MavenProjectDocument> documentList = actionContext.getPomDocumentList();
            MavenProjectDocument document = findMavenPluginDocument(documentList, virtualFile);
            if (document == null) {
                MavenProjectDocument newDocument = ModelUtils.loadMavenProjectDocument(actionContext, virtualFile);
                if (newDocument != null) {
                    PomTreeUtil.addSinglePomToTree(actionContext, newDocument);
                } else {
                    LOG.error("failed to load document: " + virtualFile.getPath());
                }
            } else {
                LOG.error("document is already loaded: " + virtualFile.getPath());
            }
        }

        private void tryRemove(PsiElement element) {
            if (element instanceof PsiFile) {
                VirtualFile virtualFile = getPomFile((PsiFile) element);
                if (virtualFile != null) {
                    doRemove(virtualFile);
                }
            }
        }

        private void doRemove(VirtualFile virtualFile) {
            List<MavenProjectDocument> documentList = actionContext.getPomDocumentList();
            MavenProjectDocument document = findMavenPluginDocument(documentList, virtualFile);
            if (document != null) {
                documentList.remove(document);
                DefaultMutableTreeNode node = PomTreeUtil.findMavenProjectDocumentNode(actionContext, document);
                GuiUtils.removeAndSelectParent(PomTreeUtil.getPomTree(actionContext), node);
            } else {
                LOG.error("Cannot find document for " + virtualFile.getPath());
            }
        }

        private void tryUpdate(PsiElement element) {
            if (element != null && !(element instanceof PsiFile)) {
                final VirtualFile virtualFile = getPomFile(element.getContainingFile());
                if (virtualFile != null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            doUpdate(virtualFile);
                        }
                    });
                }
            }
        }

        private void doUpdate(VirtualFile virtualFile) {
            List<MavenProjectDocument> documentList = actionContext.getPomDocumentList();
            MavenProjectDocument document = findMavenPluginDocument(documentList, virtualFile);
            if (document != null) {
                document.reparse();
                ModelUtils.loadPlugins(document, actionContext.getProjectPluginSettings().getMavenRepository());
                DefaultMutableTreeNode node = PomTreeUtil.findMavenProjectDocumentNode(actionContext, document);
                DefaultTreeModel treeModel = (DefaultTreeModel) PomTreeUtil.getPomTree(actionContext).getModel();
                PomTreeUtil.updatePluginNodes(document, actionContext, node);
                treeModel.nodeChanged(node);
            } else {
                LOG.error("Cannot find document for " + virtualFile.getPath());
            }
        }

        private MavenProjectDocument findMavenPluginDocument(List<MavenProjectDocument> pomDocumentList, VirtualFile file) {
            for (MavenProjectDocument document : pomDocumentList) {
                if (document.getPomFile().equals(file)) {
                    return document;
                }
            }
            return null;
        }
    }
}
