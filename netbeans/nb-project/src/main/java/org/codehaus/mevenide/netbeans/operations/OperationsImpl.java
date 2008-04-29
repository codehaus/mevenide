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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.api.execute.RunUtils;
import org.codehaus.mevenide.netbeans.embedder.writer.WriterUtils;
import org.codehaus.mevenide.netbeans.execute.BeanRunConfig;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.CopyOperationImplementation;
import org.netbeans.spi.project.DeleteOperationImplementation;
import org.netbeans.spi.project.MoveOperationImplementation;
import org.netbeans.spi.project.ProjectState;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 * Implementation of IDE's idea how to move/delete/copy a project.
 * makes sure the project is removed from the possible module section of the parent..
 * @author mkleint@codehaus.org
 */
public class OperationsImpl implements DeleteOperationImplementation, MoveOperationImplementation, CopyOperationImplementation {
    protected NbMavenProject project;
    private ProjectState state;
    /** Creates a new instance of AbstractOperation */
    public OperationsImpl(NbMavenProject proj, ProjectState state) {
        project = proj;
        this.state = state;
    }
    
    
    protected static void addFile(FileObject projectDirectory, String fileName, List<FileObject> result) {
        FileObject file = projectDirectory.getFileObject(fileName);
        if (file != null) {
            result.add(file);
        }
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
        addFile(project.getProjectDirectory(), "src", files); //NOI18N
        //TODO is there more?
        return files;
    }
    
    public void notifyDeleting() throws IOException {
        // cannot run ActionProvider.CLEAN because that one doesn't stop thi thread.
        //TODO shall I get hold of the actual mapping for the clean action?
        BeanRunConfig config = new BeanRunConfig();
        config.setExecutionDirectory(FileUtil.toFile(project.getProjectDirectory()));
        //config.setOffline(true);
        config.setGoals(Collections.singletonList("clean")); //NOI18N
        config.setRecursive(false);
        config.setProject(project);
        config.setExecutionName(NbBundle.getMessage(OperationsImpl.class, "NotifyDeleting.execute"));
        config.setUpdateSnapshots(false);
        config.setTaskDisplayName(NbBundle.getMessage(OperationsImpl.class, "NotifyDeleting.execute"));
        ExecutorTask task = RunUtils.executeMaven(config);
        task.result();
        checkParentProject(project.getProjectDirectory(), true, null, null);
        config.setProject(null);
    }
    
    
    
    public void notifyDeleted() throws IOException {
        state.notifyDeleted();
    }
    
    public void notifyMoving() throws IOException {
        notifyDeleting();
    }
    
    public void notifyMoved(Project original, File originalLoc, String newName) throws IOException {
        if (original == null) {
            //old project call..
            state.notifyDeleted();
            return;
        } else {
            if (original.getProjectDirectory().equals(project.getProjectDirectory())) {
                // oh well, just change the name in the pom when rename is invoked.
                FileObject pomFO = project.getProjectDirectory().getFileObject("pom.xml"); //NOI18N
                Model mdl = WriterUtils.loadModel(pomFO);
                mdl.setName(newName);
                WriterUtils.writePomModel(pomFO, mdl);
                ProjectURLWatcher.fireMavenProjectReload(project);
            }
            checkParentProject(project.getProjectDirectory(), false, newName, originalLoc.getName());
        }
    }
    
    public void notifyCopying() throws IOException {
        
    }
    
    public void notifyCopied(Project original, File originalLoc, String newName) throws IOException {
        if (original == null) {
            //old project call..
        } else {
            checkParentProject(project.getProjectDirectory(), false, newName, originalLoc.getName());
        }
    }
    
    private void checkParentProject(FileObject projectDir, boolean delete, String newName, String oldName) throws IOException {
        String prjLoc = projectDir.getNameExt();
        FileObject fo = projectDir.getParent();
        Project possibleParent = ProjectManager.getDefault().findProject(fo);
        if (possibleParent != null) {
            NbMavenProject par = possibleParent.getLookup().lookup(NbMavenProject.class);
            if (par != null) {
                FileObject pomFO = par.getProjectDirectory().getFileObject("pom.xml"); //NOI18N
                Model mdl = WriterUtils.loadModel(pomFO);
                MavenProject prj = par.getOriginalMavenProject();
                if ((prj.getModules() != null && prj.getModules().contains(prjLoc)) == delete) {
                    //delete/add module from/to parent..
                    if (delete) {
                        mdl.removeModule(prjLoc);
                    } else {
                        mdl.addModule(prjLoc);
                    }
                }
                if (newName != null && oldName != null) {
                    if (oldName.equals(mdl.getArtifactId())) {
                        // is this condition necessary.. why not just overwrite the artifactID always..
                        mdl.setArtifactId(newName);
                    }
                }
                WriterUtils.writePomModel(pomFO, mdl);
            }
        }
        
    }
    
}
