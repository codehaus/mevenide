/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenProjectFactory implements ProjectFactory
{
    
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
        return project.isFile();
    }
    
    public Project loadProject(FileObject fileObject, ProjectState projectState) throws IOException
    {
        if (FileUtil.toFile(fileObject) == null) {
            return null;
        }
        FileObject projectFile = fileObject.getFileObject("project.xml");
        if (projectFile == null || !projectFile.isData()) {
            return null;
        }
        File projectDiskFile = FileUtil.toFile(projectFile);
        if (projectDiskFile == null) return null;
        try {
            Project proj =  new MavenProject(projectFile, projectDiskFile);
            return proj;
        } catch (Exception exc) {
            return null;
        }
//        Document projectXml;
//        try {
//            projectXml = XMLUtil.parse(new InputSource(projectDiskFile.toURI().toString()), false, true, /*XXX need error handler*/null, null);
//        } catch (SAXException e) {
//            throw (IOException)new IOException(e.toString()).initCause(e);
//        }
//        Element projectEl = projectXml.getDocumentElement();
//        if (!"project".equals(projectEl.getLocalName()) || !PROJECT_NS.equals(projectEl.getNamespaceURI())) { // NOI18N
//            return null;
//        }
//        Element typeEl = Util.findElement(projectEl, "type", PROJECT_NS); // NOI18N
//        if (typeEl == null) {
//            return null;
//        }
//        String type = Util.findText(typeEl);
//        if (type == null) {
//            return null;
//        }
//        AntBasedProjectType provider = findAntBasedProjectType(type);
//        if (provider == null) {
//            return null;
//        }
//        AntProjectHelper helper = HELPER_CALLBACK.createHelper(projectDirectory, projectXml, state, provider);
//        Project project = provider.createProject(helper);
//        project2Helper.put(project, helper);
//        helper2Project.put(helper, project);
//        return project;
        
    }
    
    public void saveProject(Project project) throws IOException, java.lang.ClassCastException
    {
    }
    
}
