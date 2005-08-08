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


package org.mevenide.netbeans.project;

import java.io.File;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenProjectFactory implements ProjectFactory
{
    private static final Log logger = LogFactory.getLog(MavenProjectFactory.class);
    
    /** Creates a new instance of MavenProjectFactory */
    public MavenProjectFactory()
    {
    }
    
    public boolean isProject(FileObject fileObject)
    {
        File projectDir = FileUtil.toFile(fileObject);
        if (projectDir == null) {
            return false;
        }
        File project = new File(projectDir, "project.xml"); // NOI18N
        return project.isFile() &&  !"nbproject".equalsIgnoreCase(projectDir.getName()); //NOI18N
    }
    
    public Project loadProject(FileObject fileObject, ProjectState projectState) throws IOException
    {
        if (FileUtil.toFile(fileObject) == null) {
            return null;
        }
        if ("nbproject".equalsIgnoreCase(fileObject.getName())) {
            return null;
        }
        FileObject projectFile = fileObject.getFileObject("project.xml"); //NOI18N
        if (projectFile == null || !projectFile.isData()) {
            return null;
        }
        File projectDiskFile = FileUtil.toFile(projectFile);
        if (projectDiskFile == null)  {
            return null;
        }
        try {
            MavenProject proj =  new MavenProject(projectFile, projectDiskFile);
            return proj;
        } catch (Exception exc) {
            logger.error("Cannot load project=" + projectDiskFile, exc);
            ErrorManager.getDefault().getInstance(MavenProjectFactory.class.getName()).notify(ErrorManager.INFORMATIONAL, exc);
            return null;
        }
    }
    
    public void saveProject(Project project) throws IOException {
        // what to do here??
    }
    
}
