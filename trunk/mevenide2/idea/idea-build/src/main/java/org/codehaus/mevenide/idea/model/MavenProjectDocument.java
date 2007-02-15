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

import org.apache.maven.pom.x400.ProjectDocument;

import java.util.List;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public interface MavenProjectDocument extends Comparable {

    /**
     * Method description
     *
     * @return Document me!
     */
    public List<MavenPluginDocument> getPluginDocumentList();

    /**
     * Method description
     *
     * @return Document me!
     */
    public VirtualFile getPomFile();

    /**
     * Method description
     *
     * @return Document me!
     */
    public ProjectDocument getProjectDocument();

    /**
     * Method description
     *
     * @param pluginPathList Document me!
     */
    public void setPluginDocumentList(List<MavenPluginDocument> pluginPathList);

    /**
     * Method description
     *
     * @param pomFile Document me!
     */
    public void setPomFile(VirtualFile pomFile);

    /**
     * Method description
     *
     * @param projectDocument Document me!
     */
    public void setProjectDocument(ProjectDocument projectDocument);
}
