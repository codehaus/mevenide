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
package org.mevenide.idea.editor.pom;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.xml.XmlFile;
import org.jdom.Element;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.idea.psi.project.impl.DefaultPsiProject;
import org.mevenide.idea.psi.util.PsiUtils;
import org.mevenide.idea.util.components.AbstractApplicationComponent;

/**
 * @author Arik
 */
public class PomFileEditorProvider extends AbstractApplicationComponent
        implements FileEditorProvider {
    public boolean accept(final Project pProject, final VirtualFile pFile) {
        final String extension = pFile.getExtension();
        if (extension == null || extension.trim().length() == 0)
            return false;

        final Module[] modules = ModuleManager.getInstance(pProject).getModules();
        for (final Module module : modules) {
            final ModuleSettings settings = ModuleSettings.getInstance(module);
            final VirtualFile pomFile = settings.getPomVirtualFile();
            if (pomFile != null && pomFile.equals(pFile))
                return true;
        }

        return false;
    }

    public FileEditor createEditor(final Project pProject, final VirtualFile pPomFile) {
        final FileDocumentManager filedocMgr = FileDocumentManager.getInstance();
        final Document document = filedocMgr.getDocument(pPomFile);
        final XmlFile xmlFile = PsiUtils.findXmlFile(pProject, document);

        return new PomFileEditor(new DefaultPsiProject(xmlFile));
    }

    public void disposeEditor(final FileEditor pEditor) {
        if (pEditor instanceof Disposable)
            ((Disposable) pEditor).dispose();
    }

    public String getEditorTypeId() {
        return "pomEditor";
    }

    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }

    public FileEditorState readState(final Element pSourceElement,
                                     final Project pProject,
                                     final VirtualFile pFile) {
        return new PomFileEditorState();
    }

    public void writeState(final FileEditorState pState,
                           final Project pProject,
                           final Element pTargetElement) {
    }
}
