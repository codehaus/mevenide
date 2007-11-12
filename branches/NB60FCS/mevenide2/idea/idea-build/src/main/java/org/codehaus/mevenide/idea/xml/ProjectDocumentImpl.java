package org.codehaus.mevenide.idea.xml;

import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;

public class ProjectDocumentImpl implements ProjectDocument {

    DomFileElement<Project> fileElement;

    public ProjectDocumentImpl(PsiFile psiFile) {
        if ( psiFile == null ) {
            fileElement = null;
        } else {
            fileElement = DomManager.getDomManager(psiFile.getProject()).getFileElement((XmlFile) psiFile, Project.class);
        }
    }

    public boolean isWellFormed() {
        return fileElement != null;
    }

    public Project getProject() {
        return fileElement != null ? fileElement.getRootElement() : null;
    }
}
