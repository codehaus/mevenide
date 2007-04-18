/* ==========================================================================
 * Copyright 2006 Mevenide Team
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



package org.codehaus.mevenide.idea.model;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang.StringUtils;
import org.codehaus.mevenide.idea.xml.ProjectDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenProjectDocumentImpl implements MavenProjectDocument {
    private List<MavenPluginDocument> pluginDocumentList = new ArrayList<MavenPluginDocument>();
    private PsiFile psiFile;
    private ProjectDocument projectDocument;

    public MavenProjectDocumentImpl(PsiFile psiFile) {
        this.psiFile = psiFile;
    }

    public int compareTo(Object o) {
        return this.toString().compareToIgnoreCase(o.toString());
    }

    public String toString() {
        if (!isValid()) {
            return "invalid";
        }

        String name = projectDocument.getProject().getName().getStringValue();
        if (!StringUtils.isBlank(name)) {
            return name;
        }

        String artifactId = projectDocument.getProject().getArtifactId().getStringValue();
        if (!StringUtils.isBlank(artifactId)) {
            return artifactId;
        }

        return "unnamed";
    }

    public Iterable<? extends MavenPluginDocument> getPlugins() {
        return pluginDocumentList;
    }

    public void addPlugin(MavenPluginDocument plugin) {
        pluginDocumentList.add(plugin);
    }

    public void removePlugin(MavenPluginDocument plugin) {
        pluginDocumentList.remove(plugin);
    }

    public void reparse() {
        projectDocument = ProjectDocument.Factory.parse(psiFile);
        pluginDocumentList.clear();
    }

    public VirtualFile getPomFile() {
        return psiFile.getVirtualFile();
    }

    public ProjectDocument.Project getProject() {
        return projectDocument.getProject();
    }

    public boolean isValid() {
        return projectDocument.isWellFormed();
    }

    public PsiFile getPsiFile() {
        return psiFile;
    }

}
