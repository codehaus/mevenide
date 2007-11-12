package org.codehaus.mevenide.idea.component;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractFileChangeAdapter extends PsiTreeChangeAdapter {
    private Set<VirtualFile> filesToUpdate = new HashSet<VirtualFile>();

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
        if (psiFile != null) {
            VirtualFile virtualFile = psiFile.getVirtualFile();
            if (virtualFile != null) {
                if (matches(virtualFile)) {
                    return virtualFile;
                }
            }
        }
        return null;
    }

    private void tryAdd(PsiElement element) {
        if (element instanceof PsiFile) {
            final VirtualFile virtualFile = getPomFile((PsiFile) element);
            if (virtualFile != null) {
                scheduleUpdate(virtualFile);
            }
        }
    }

    private void tryUpdate(PsiElement element) {
        if (element != null && !(element instanceof PsiFile)) {
            final VirtualFile virtualFile = getPomFile(element.getContainingFile());
            if (virtualFile != null) {
                scheduleUpdate(virtualFile);
            }
        }
    }

    private void tryRemove(PsiElement element) {
        if (element instanceof PsiFile) {
            VirtualFile virtualFile = getPomFile((PsiFile) element);
            if (virtualFile != null) {
                scheduleRemove(virtualFile);
            }
        }
    }

    private void scheduleUpdate(final VirtualFile virtualFile) {
        if ( filesToUpdate.isEmpty() ) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    doUpdate();
                }
            });
        }
        filesToUpdate.add(virtualFile);
    }

    private void scheduleRemove(VirtualFile virtualFile) {
        filesToUpdate.remove ( virtualFile );
        doRemove(virtualFile);
    }

    protected void doUpdate() {
        for ( VirtualFile virtualFile : filesToUpdate ) {
            doUpdate ( virtualFile);
        }
        filesToUpdate.clear();
    }

    protected abstract boolean matches(VirtualFile virtualFile);

    protected abstract void doRemove(VirtualFile virtualFile);

    protected abstract void doUpdate(VirtualFile virtualFile);
}
