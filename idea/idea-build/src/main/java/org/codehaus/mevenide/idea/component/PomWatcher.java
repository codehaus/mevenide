package org.codehaus.mevenide.idea.component;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.xml.DomFileDescription;
import com.intellij.util.xml.DomManager;
import org.codehaus.mevenide.idea.gui.PomTreeStructure;
import org.codehaus.mevenide.idea.util.PluginConstants;
import org.codehaus.mevenide.idea.xml.ProjectDocument;

import java.util.ArrayList;
import java.util.Collection;

public class PomWatcher {
    private final Project project;
    private final PomTreeStructure pomTreeStructure;

    public PomWatcher(Project project, PomTreeStructure structure) {
        this.project = project;
        this.pomTreeStructure = structure;

        DomManager manager = DomManager.getDomManager(project);
        manager.registerFileDescription(new DomFileDescription<ProjectDocument.Project>(ProjectDocument.Project.class, "project") {
            protected void initializeFileDescription() {
            }
        });

        StartupManager.getInstance(project).registerPostStartupActivity(new Runnable() {
            public void run() {
                rebuildTree();
                registerListeners();
            }
        });
    }

    private static boolean isPomFile(VirtualFile virtualFile) {
        return virtualFile.getName().equalsIgnoreCase(PluginConstants.POM_FILE_NAME);
    }

    private void rebuildTree() {
        pomTreeStructure.rebuild(collectPomFiles(project));
    }

    private static Collection<VirtualFile> collectPomFiles(Project project) {
        Collection<VirtualFile> pomFiles = new ArrayList<VirtualFile>();
        for ( VirtualFile dir : ProjectRootManager.getInstance(project).getContentRoots()){
            collectPomFiles(dir, pomFiles);
        }
        return pomFiles;
    }

    private static void collectPomFiles(VirtualFile dir, Collection<VirtualFile> pomFiles) {
        for (VirtualFile child : dir.getChildren()) {
            if (child.isDirectory()) {
                collectPomFiles(child, pomFiles);
            } else if (isPomFile(child)) {
                pomFiles.add(child);
            }
        }
    }

    private void registerListeners() {
        PsiManager.getInstance(project).addPsiTreeChangeListener(new MyPsiTreeChangeAdapter());

        ProjectRootManager.getInstance(project).addModuleRootListener(new ModuleRootListener() {
            public void beforeRootsChange(ModuleRootEvent event) {
            }

            public void rootsChanged(ModuleRootEvent event) {
                rebuildTree();
            }
        });
    }

    private class MyPsiTreeChangeAdapter extends AbstractFileChangeAdapter {

        protected boolean matches(VirtualFile virtualFile) {
            return isPomFile(virtualFile);
        }

        protected void doUpdate(VirtualFile virtualFile) {
            pomTreeStructure.update( virtualFile );
        }

        protected void doRemove(VirtualFile virtualFile) {
            pomTreeStructure.remove ( virtualFile);
        }
    }
}
