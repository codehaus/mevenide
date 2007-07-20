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

package org.codehaus.mevenide.netbeans.nodes;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;

/**
 *
 * @author mkleint
 */
public class OtherRootNodeFactory implements NodeFactory {
    
    private static final String KEY_OTHER = "otherRoots"; //NOI18N
    private static final String KEY_OTHER_TEST = "otherTestRoots"; //NOI18N
    private static final String MAIN = "src/main"; //NOI18N
    private static final String TEST = "src/test"; //NOI18N
    
    /** Creates a new instance of OtherRootNodeFactory */
    public OtherRootNodeFactory() {
    }
    
    public NodeList createNodes(Project project) {
        NbMavenProject prj = project.getLookup().lookup(NbMavenProject.class);
        return new NList(prj);
    }
    
    private static class NList extends AbstractMavenNodeList<String> implements PropertyChangeListener, FileChangeListener {
        private NbMavenProject project;
        NList(NbMavenProject prj) {
            project = prj;
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (NbMavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                fireChange();
            }
            if (NbMavenProject.PROP_RESOURCE.equals(evt.getPropertyName())) {
                if (MAIN.equals(evt.getNewValue()) || TEST.equals(evt.getNewValue())) { //NOI18N
                    fireChange();
                    checkFileObject((String)evt.getNewValue());
                }
            }
        }
        
        public List<String> keys() {
            List<String> list = new ArrayList<String>();
            if (project.getOtherRoots(false).length > 0) {
                list.add(KEY_OTHER);
            }
            if (project.getOtherRoots(true).length > 0) {
                list.add(KEY_OTHER_TEST);
            }
            return list;
        }
        
        public Node node(String key) {
            if (KEY_OTHER.equals(key)) {
                File[] fls = project.getOtherRoots(false);
                // the content of OtherRoots can change from keys() to node(String)
                if (fls.length > 0) {
                    FileObject fo = FileUtil.toFileObject(fls[0].getParentFile());
                    return new OthersRootNode(project, false, fo);
                }
                return null;
            } else if (KEY_OTHER_TEST.equals(key)) {
                File[] fls = project.getOtherRoots(false);
                // the content of OtherRoots can change from keys() to node(String)
                if (fls.length > 0) {
                    FileObject fo = FileUtil.toFileObject(fls[0].getParentFile());
                    return new OthersRootNode(project, true, fo);
                }
                return null;
            }
            assert false: "Wrong key for Dependencies NodeFactory: " + key; //NOI18N
            return null;
        }
        
        @Override
        public void addNotify() {
            ProjectURLWatcher watch = project.getLookup().lookup(ProjectURLWatcher.class);
            watch.addPropertyChangeListener(project, this);
            watch.addWatchedPath(MAIN); //NOI18N
            watch.addWatchedPath(TEST); //NOI18N    
            checkFileObject(MAIN);
            checkFileObject(TEST);
        }
        
        @Override
        public void removeNotify() {
            ProjectURLWatcher watch = project.getLookup().lookup(ProjectURLWatcher.class);
            watch.removePropertyChangeListener(project, this);
            watch.removeWatchedPath(MAIN); //NOI18N
            watch.removeWatchedPath(TEST); //NOI18N            
            FileObject fo = project.getProjectDirectory().getFileObject(MAIN);
            if (fo != null) {
                fo.removeFileChangeListener(this);
            }
            fo = project.getProjectDirectory().getFileObject(TEST);
            if (fo != null) {
                fo.removeFileChangeListener(this);
            }
        }
        
        private void checkFileObject(String path) {
            FileObject fo = project.getProjectDirectory().getFileObject(path);
            if (fo != null) {
                fo.removeFileChangeListener(this);
                fo.addFileChangeListener(this);
            }
        }

        public void fileFolderCreated(FileEvent arg0) {
            fireChange();
        }

        public void fileDataCreated(FileEvent arg0) {
        }

        public void fileChanged(FileEvent arg0) {
        }

        public void fileDeleted(FileEvent arg0) {
            fireChange();
            arg0.getFile().removeFileChangeListener(this);
        }

        public void fileRenamed(FileRenameEvent arg0) {
            fireChange();
        }

        public void fileAttributeChanged(FileAttributeEvent arg0) {
        }
    }
}
