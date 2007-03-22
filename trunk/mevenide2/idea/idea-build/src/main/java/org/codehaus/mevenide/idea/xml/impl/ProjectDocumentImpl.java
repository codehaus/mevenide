package org.codehaus.mevenide.idea.xml.impl;

import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import org.codehaus.mevenide.idea.xml.ProjectDocument;

public class ProjectDocumentImpl implements ProjectDocument {

    DomFileElement<Project> fileElement;

    public ProjectDocumentImpl(PsiFile psiFile) {
        fileElement = DomManager.getDomManager(psiFile.getProject()).getFileElement((XmlFile) psiFile/*, Project.class*/);
    }

    public boolean isWellFormed() {
        return fileElement != null;
    }

    public Project getProject() {
        return fileElement != null ? fileElement.getRootElement() : null;
    }
}
