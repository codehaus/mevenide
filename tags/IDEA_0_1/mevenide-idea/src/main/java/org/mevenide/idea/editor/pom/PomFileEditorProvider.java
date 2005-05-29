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

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.Disposable;
import org.jdom.Element;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.idea.util.components.AbstractApplicationComponent;

import java.io.File;

/**
 * @author Arik
 */
public class PomFileEditorProvider extends AbstractApplicationComponent implements FileEditorProvider {

    public boolean accept(final Project pProject, final VirtualFile pFile) {
        final String extension = pFile.getExtension();
        if(extension == null || extension.trim().length() == 0)
            return false;

        if(!pFile.getName().equalsIgnoreCase("project.xml"))
            return false;

        final File file = VfsUtil.virtualToIoFile(pFile).getAbsoluteFile();

        final Module[] modules = ModuleManager.getInstance(pProject).getModules();
        for(final Module module : modules) {
            final ModuleSettings settings = ModuleSettings.getInstance(module);
            final File pomFile = settings.getPomFile();
            if(pomFile != null && pomFile.equals(file))
                return true;
        }

        return false;
    }

    public FileEditor createEditor(final Project pProject, final VirtualFile pFile) {
        return new PomFileEditor(pProject, pFile);
    }

    public void disposeEditor(final FileEditor pEditor) {
        if(pEditor instanceof Disposable)
            ((Disposable)pEditor).dispose();
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
