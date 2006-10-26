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

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;


/**
 * factory of maven projects
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class NbMavenProjectFactory implements ProjectFactory
{
    
    /** Creates a new instance of NbMavenProjectFactory */
    public NbMavenProjectFactory()
    {
    }
    
    public boolean isProject(FileObject fileObject)
    {
        File projectDir = FileUtil.toFile(fileObject);
        if (projectDir == null) {
            return false;
        }
        
        File newproject = new File(projectDir, "pom.xml.temp"); // NOI18N
        if (newproject.isFile()) {
            return true;
        }
        
        File project = new File(projectDir, "pom.xml"); // NOI18N
        if (project.isFile() && 
            "archetype-resources".equalsIgnoreCase(projectDir.getName()) &&
            "resources".equalsIgnoreCase(projectDir.getParentFile().getName())) {
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
        if ("nbproject".equalsIgnoreCase(fileObject.getName())) {
            return null;
        }
//        if (fileObject.getFileObject("project.xml") != null) {
//            // for now maven 1 projects have precedence
//            return null;
//        }
        FileObject projectFile = fileObject.getFileObject("pom.xml"); //NOI18N
        if (projectFile == null || !projectFile.isData()) {
            if (fileObject.getFileObject("pom.xml.temp") != null) {
                return new TempProject(fileObject);
            }
            return null;
            
        }
        File projectDiskFile = FileUtil.normalizeFile(FileUtil.toFile(projectFile));
        if (projectDiskFile == null)  {
            return null;
        }
        if (projectDiskFile.isFile() && 
            "archetype-resources".equalsIgnoreCase(fileObject.getName()) &&
            "resources".equalsIgnoreCase(fileObject.getParent().getName())) {
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
    
    
    private static class TempProject implements Project, ProjectInformation, LogicalViewProvider {

        private Lookup look;
        private FileObject dir;
        public TempProject(FileObject fo) {
            dir = fo;
        }
        public FileObject getProjectDirectory() {
            return dir;
        }

        public Lookup getLookup() {
            if (look == null) {
                look = Lookups.fixed(new Object[] {
                    this, 
                    new NullActionProvider()
                });
            }
            return look;
        }

        public String getName() {
            return "temp-project";
        }

        public String getDisplayName() {
            return "Creating Maven Project from Archetype...";
        }

        public Icon getIcon() {
            return new ImageIcon(Utilities.loadImage("org/codehaus/mevenide/netbeans/Maven2Icon.gif"));
        }

        public Project getProject() {
            return this;
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
        }

        public Node createLogicalView() {
            AbstractNode nd = new AbstractNode(Children.LEAF, Lookups.singleton(this)) {
                public String getHtmlDisplayName() {
                    return "<i>" + getDisplayName() + "</i>";
                }
            };
            nd.setName("temp-project");
            nd.setDisplayName("Creating Maven Project from Archetype...");
            nd.setIconBaseWithExtension("org/codehaus/mevenide/netbeans/Maven2TempIcon.png");
            return nd;
        }

        public Node findPath(Node root, Object target) {
            return null;
        }
        
    }
    
    private static class NullActionProvider implements ActionProvider {
        public String[] getSupportedActions() {
            return new String[0];
        }

        public void invokeAction(String command, Lookup context) throws IllegalArgumentException {
        }

        public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
            return false;
        }
        
    }
}
