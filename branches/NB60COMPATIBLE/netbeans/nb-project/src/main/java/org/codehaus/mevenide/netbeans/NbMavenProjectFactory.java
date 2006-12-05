/* ==========================================================================
 * Copyright 2005 Mevenide Team
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


package org.codehaus.mevenide.netbeans;

import java.io.File;
import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;


/**
 * factory of maven projects
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class NbMavenProjectFactory implements ProjectFactory {
    
    /** Creates a new instance of NbMavenProjectFactory */
    public NbMavenProjectFactory() {
    }
    
    public boolean isProject(FileObject fileObject)
    {
        File projectDir = FileUtil.toFile(fileObject);
        if (projectDir == null) {
            return false;
        }
        
        File project = new File(projectDir, "pom.xml"); // NOI18N
        if (project.isFile() && 
            "archetype-resources".equalsIgnoreCase(projectDir.getName()) && //NOI18N
            "resources".equalsIgnoreCase(projectDir.getParentFile().getName())) { //NOI18N
            //this is an archetype resource, happily ignore..
            return false;
        }
        return project.isFile() &&  !"nbproject".equalsIgnoreCase(projectDir.getName()); //NOI18N
    }
    
    public Project loadProject(FileObject fileObject, ProjectState projectState) throws IOException
    {
        if (FileUtil.toFile(fileObject) == null) {
            return null;
        }
        if ("nbproject".equalsIgnoreCase(fileObject.getName())) { //NOI18N
            return null;
        }
        FileObject projectFile = fileObject.getFileObject("pom.xml"); //NOI18N
        if (projectFile == null || !projectFile.isData()) {
            return null;
            
        }
        File projectDiskFile = FileUtil.normalizeFile(FileUtil.toFile(projectFile));
        if (projectDiskFile == null)  {
            return null;
        }
        if (projectDiskFile.isFile() && 
            "archetype-resources".equalsIgnoreCase(fileObject.getName()) && //NOI18N
            "resources".equalsIgnoreCase(fileObject.getParent().getName())) { //NOI18N
            //this is an archetype resource, happily ignore..
            return null;
        }
        try {
            NbMavenProject proj =  new NbMavenProject(projectFile, projectDiskFile);
            return proj;
        } catch (Exception exc) {
            ErrorManager.getDefault().getInstance(NbMavenProjectFactory.class.getName()).notify(ErrorManager.INFORMATIONAL, exc);
            return null;
        }
    }
    
    public void saveProject(Project project) throws IOException {
        // what to do here??
    }
    
    
}
