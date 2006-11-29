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

package org.codehaus.mevenide.netbeans.operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.embedder.writer.WriterUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.DeleteOperationImplementation;
import org.openide.filesystems.FileObject;

/**
 *
 * @author mkleint
 */
public class DeleteOperationImpl implements DeleteOperationImplementation {
    private NbMavenProject project;
    
    /** Creates a new instance of DeleteOperationImpl */
    public DeleteOperationImpl(NbMavenProject prj) {
        project = prj;
    }
    
    private static void addFile(FileObject projectDirectory, String fileName, List<FileObject> result) {
        FileObject file = projectDirectory.getFileObject(fileName);
        if (file != null) {
            result.add(file);
        }
    }
    
    public void notifyDeleting() throws IOException {
        // do a project clean. and remove the 
        String prjLoc = project.getProjectDirectory().getNameExt();
        FileObject fo = project.getProjectDirectory().getParent();
        if (ProjectManager.getDefault().isProject(fo)) {
            Project possibleParent = ProjectManager.getDefault().findProject(fo);
            if (possibleParent != null) {
                NbMavenProject par = possibleParent.getLookup().lookup(NbMavenProject.class);
                if (par != null) {
                    MavenProject prj = par.getOriginalMavenProject();
                    if (prj.getModules() != null && prj.getModules().contains(prjLoc)) {
                        //delet module from parent..
                        FileObject pomFO = par.getProjectDirectory().getFileObject("pom.xml");
                        Model mdl = WriterUtils.loadModel(pomFO);
                        List lst = mdl.getModules();
                        // can still be null in case the stuff is written in a profile or parent pom?
                        if (lst != null) {
                            lst.remove(prjLoc);
                            WriterUtils.writePomModel(fo, mdl);
                        }
                    }
                }
            }
        }
    }
    
    public void notifyDeleted() throws IOException {
        
    }
    
    public List<FileObject> getMetadataFiles() {
        FileObject projectDirectory = project.getProjectDirectory();
        List<FileObject> files = new ArrayList<FileObject>();
        addFile(projectDirectory, "pom.xml", files); // NOI18N
        addFile(projectDirectory, "profiles.xml", files); // NOI18N
        addFile(projectDirectory, "nbactions.xml", files); //NOI18N
        
        return files;
    }
    
    public List<FileObject> getDataFiles() {
        List<FileObject> files = new ArrayList<FileObject>();
        addFile(project.getProjectDirectory(), "src", files);
        return files;
    }
    
}
