/* ==========================================================================
 * Copyright 2003-2005 Mevenide Team
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

package org.mevenide.netbeans.project.nodes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.mevenide.netbeans.api.project.MavenProject;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 *
 * @author Milos Kleint
 */
public class ProjectFilesNode extends AbstractNode {
    private static String[] labels = {
        "Project",
        "Project's parent",
        "Project's grandparent",
        "Project's grandgrandparent"
    };
    /** Creates a new instance of ProjectFilesNode */
    public ProjectFilesNode(MavenProject project) {
        super(new ProjectFilesChildren(project));
        setName("projectfiles"); //NOI18N
        setDisplayName("Project Files");
        setIconBase("org/mevenide/netbeans/project/resources/MavenFiles");
    }
    
   private static class ProjectFilesChildren extends Children.Keys implements PropertyChangeListener {
        private MavenProject project;
        public ProjectFilesChildren(MavenProject proj) {
            super();
            project = proj;
        }
        
        protected Node[] createNodes(Object obj) {
            FileWrapper wrap = (FileWrapper)obj;
            File fil = wrap.getFile();
            FileObject fo = FileUtil.toFileObject(fil);
            if (fo != null) {
                try {
                    DataObject dobj = DataObject.find(fo);
                    FilterNode node = new MyFilterNode(dobj.getNodeDelegate().cloneNode(), wrap.getText());
                    return new Node[] { node };
                } catch (DataObjectNotFoundException e) {
                }
                
            }
            return new Node[0];
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (MavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                regenerateKeys();
                refresh();
            }
        }
        
//        public void refreshChildren() {
//            Node[] nods = getNodes();
//            for (int i = 0; i < nods.length; i++) {
//                if (nods[i] instanceof DependencyNode) {
//                    ((DependencyNode)nods[i]).refreshNode();
//                }
//            }
//        }
        
        protected void addNotify() {
            super.addNotify();
            project.addPropertyChangeListener(this);
            regenerateKeys();
        }
        
        protected void removeNotify() {
            setKeys(Collections.EMPTY_SET);
            project.removePropertyChangeListener(this);
            super.removeNotify();
        }
        
        private void regenerateKeys() {
            Collection keys = new ArrayList();
            File[] projectFiles = project.getContext().getPOMContext().getProjectFiles();
            for (int i = 0; i < projectFiles.length; i++) {
                String prefix = i < labels.length ? labels[i] : "Some rather grand grandparent";
                
                File normalized = FileUtil.normalizeFile(projectFiles[i]);
                keys.add(new FileWrapper(prefix + " POM file", normalized));
                File parent = normalized.getParentFile();
                keys.add(new FileWrapper(prefix + " properties", new File(parent, "project.properties")));
                keys.add(new FileWrapper(prefix + " build propvierties", new File(parent, "build.properties")));
            }
            keys.add(new FileWrapper("User build properties", new File(project.getContext().getUserDirectory(), "build.properties")));
	    File mavenScript = new File(project.getContext().getProjectDirectory(), "maven.xml");
	    // As the best pratices recommands not to have a maven.xml file
	    // The link appear only if the file exist
	    if (mavenScript.exists()) {	    
		keys.add(new FileWrapper("Maven script file", new File(project.getContext().getProjectDirectory(), "maven.xml")));
	    }
            setKeys(keys);
        }
    }    
   
   private static class FileWrapper {
       private File loc;
       private String display;
       public FileWrapper(String displayName, File file) {
           display = displayName;
           loc = file;
       }

       public boolean equals(Object obj) {
           return loc.equals(obj);
       }

       public int hashCode() {
           return loc.hashCode();
       }
       
       public File getFile() {
           return loc;
       }
       
       public String getText() {
           return display;
       }
       
   }
   
   private static class MyFilterNode extends FilterNode {
       public MyFilterNode(Node original, String dn) {
           super(original);
           disableDelegation(DELEGATE_GET_DISPLAY_NAME | DELEGATE_SET_DISPLAY_NAME |
                             DELEGATE_DESTROY);
           setDisplayName(dn);
       }
   }
   
    
}
