/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.nodes;
import org.netbeans.modules.maven.spi.nodes.AbstractMavenNodeList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.api.NbMavenProject;
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
        NbMavenProjectImpl prj = project.getLookup().lookup(NbMavenProjectImpl.class);
        return new NList(prj);
    }
    
    private static class NList extends AbstractMavenNodeList<String> implements PropertyChangeListener, FileChangeListener {
        private NbMavenProjectImpl project;
        NList(NbMavenProjectImpl prj) {
            project = prj;
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (NbMavenProjectImpl.PROP_PROJECT.equals(evt.getPropertyName())) {
                fireChange();
            }
            if (NbMavenProjectImpl.PROP_RESOURCE.equals(evt.getPropertyName())) {
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
            NbMavenProject watch = project.getLookup().lookup(NbMavenProject.class);
            watch.addPropertyChangeListener(project, this);
            watch.addWatchedPath(MAIN); //NOI18N
            watch.addWatchedPath(TEST); //NOI18N    
            checkFileObject(MAIN);
            checkFileObject(TEST);
        }
        
        @Override
        public void removeNotify() {
            NbMavenProject watch = project.getLookup().lookup(NbMavenProject.class);
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
