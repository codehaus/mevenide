/*
 *  Copyright 2008 Anuradha.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.codehaus.mevenide.netbeans.actions;

import java.io.File;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.extension.ExtensionScanningException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.openide.util.Exceptions;

/**
 *
 * @author Anuradha G (anuradha@codehaus.org)
 */
public class ActionsUtil {

    
    
    public static MavenProject readMavenProject(Artifact artifact) {
        MavenProject mavenProject = null;

        String absolutePath = artifact.getFile().getAbsolutePath();
        String extension = artifact.getArtifactHandler().getExtension();

        String pomPath = absolutePath.substring(0, absolutePath.length() - extension.length());
        pomPath += "pom";//NOI18N
        File file = new File(pomPath);
        if (file.exists()) {
            try {

                mavenProject = EmbedderFactory.getProjectEmbedder().
                        readProject(file);

            } catch (ProjectBuildingException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ExtensionScanningException ex) {
                Exceptions.printStackTrace(ex);
            }

        }

        return mavenProject;
    }
}
