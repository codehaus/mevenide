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

import org.apache.commons.lang.StringUtils;
import org.apache.maven.pom.x400.ProjectDocument;

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
    private VirtualFile pomFile;
    private ProjectDocument projectDocument;

    /**
     * Constructs ...
     *
     * @param projectDocument Document me!
     */
    public MavenProjectDocumentImpl(ProjectDocument projectDocument) {
        this.projectDocument = projectDocument;
    }

    /**
     * Constructs ...
     *
     * @param pomFile Document me!
     */
    public MavenProjectDocumentImpl(VirtualFile pomFile) {
        this.pomFile = pomFile;
    }

    /**
     * Constructs ...
     *
     * @param projectDocument Document me!
     * @param pomFile         Document me!
     */
    public MavenProjectDocumentImpl(ProjectDocument projectDocument, VirtualFile pomFile) {
        this.projectDocument = projectDocument;
        this.pomFile = pomFile;
    }

    /**
     * Method description
     *
     * @param o Document me!
     *
     * @return Document me!
     */
    public int compareTo(Object o) {
        return this.toString().compareToIgnoreCase(o.toString());
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public String toString() {
        if (StringUtils.isBlank(projectDocument.getProject().getName())) {
            return projectDocument.getProject().getArtifactId();
        }

        return projectDocument.getProject().getName();
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public List<MavenPluginDocument> getPluginDocumentList() {
        return pluginDocumentList;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public VirtualFile getPomFile() {
        return pomFile;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public ProjectDocument getProjectDocument() {
        return projectDocument;
    }

    /**
     * Method description
     *
     * @param pluginDocumentList Document me!
     */
    public void setPluginDocumentList(List<MavenPluginDocument> pluginDocumentList) {
        this.pluginDocumentList = pluginDocumentList;
    }

    /**
     * Method description
     *
     * @param pomFile Document me!
     */
    public void setPomFile(VirtualFile pomFile) {
        this.pomFile = pomFile;
    }

    /**
     * Method description
     *
     * @param projectDocument Document me!
     */
    public void setProjectDocument(ProjectDocument projectDocument) {
        this.projectDocument = projectDocument;
    }
}
