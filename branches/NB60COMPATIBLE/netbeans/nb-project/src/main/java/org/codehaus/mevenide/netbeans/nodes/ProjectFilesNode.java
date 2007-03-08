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

package org.codehaus.mevenide.netbeans.nodes;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.embedder.MavenSettingsSingleton;
import org.openide.cookies.EditCookie;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 * maven project related aggregator node..
 * @author Milos Kleint
 */
public class ProjectFilesNode extends AbstractNode {
    
    private NbMavenProject project;
    /** Creates a new instance of ProjectFilesNode */
    public ProjectFilesNode(NbMavenProject project) {
        super(new ProjectFilesChildren(project), Lookups.fixed(project.getProjectDirectory()));
        setName("projectfiles"); //NOI18N
        setDisplayName("Project Files");
        setIconBaseWithExtension("org/codehaus/mevenide/netbeans/MavenFiles.gif");
        this.project = project;
    }
    
    public Action[] getActions(boolean context) {
        Collection col = new ArrayList();
        if (project.getProjectDirectory().getFileObject("profiles.xml") == null) {
            col.add(new AddProfileXmlAction());
        };
        if (! new File(MavenSettingsSingleton.getInstance().getM2UserDir(), "settings.xml").exists()) {
            col.add(new AddSettingsXmlAction());
        };
        return (Action[])col.toArray(new Action[col.size()]);
    }
    
    private static class ProjectFilesChildren extends Children.Keys implements PropertyChangeListener {
        private NbMavenProject project;
        private FileChangeAdapter fileChangeListener;
        
        public ProjectFilesChildren(NbMavenProject proj) {
            super();
            project = proj;
            fileChangeListener = new FileChangeAdapter() {
                public void fileDataCreated(FileEvent fe) {
                    regenerateKeys();
                    refresh();
                }
                public void fileDeleted(FileEvent fe) {
                    regenerateKeys();
                    refresh();
                }
            };
        }
        
        protected Node[] createNodes(Object obj) {
            File fil = (File)obj;
            FileObject fo = FileUtil.toFileObject(fil);
            if (fo != null) {
                try {
                    DataObject dobj = DataObject.find(fo);
                    FilterNode node = new FilterNode(dobj.getNodeDelegate().cloneNode());
                    return new Node[] { node };
                } catch (DataObjectNotFoundException e) {
                }
                
            }
            return new Node[0];
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (NbMavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
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
            ProjectURLWatcher.addPropertyChangeListener(project, this);
            project.getProjectDirectory().addFileChangeListener(fileChangeListener);
            regenerateKeys();
        }
        
        protected void removeNotify() {
            setKeys(Collections.EMPTY_SET);
            ProjectURLWatcher.removePropertyChangeListener(project, this);
            project.getProjectDirectory().removeFileChangeListener(fileChangeListener);
            super.removeNotify();
        }
        
        private void regenerateKeys() {
            Collection keys = new ArrayList();
            keys.add(new File(FileUtil.toFile(project.getProjectDirectory()), "pom.xml"));
            keys.add(new File(FileUtil.toFile(project.getProjectDirectory()), "profiles.xml"));
            keys.add(new File(MavenSettingsSingleton.getInstance().getM2UserDir(), "settings.xml"));
            setKeys(keys);
        }
    }
    
    private class AddProfileXmlAction extends AbstractAction {
        AddProfileXmlAction() {
            putValue(Action.NAME, "Create profiles.xml");
        }
        public void actionPerformed(ActionEvent e) {
            try {
                DataFolder folder = DataFolder.findFolder(project.getProjectDirectory());
                // path to template...
                FileObject temp = Repository.getDefault().getDefaultFileSystem().findResource("Maven2Templates/profiles.xml");
                DataObject dobj = DataObject.find(temp);
                DataObject newOne = dobj.createFromTemplate(folder);
                EditCookie cook = (EditCookie)newOne.getCookie(EditCookie.class);
                if (cook != null) {
                    cook.edit();
                }
                
            } catch (DataObjectNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
    }
    
    private class AddSettingsXmlAction extends AbstractAction {
        AddSettingsXmlAction() {
            putValue(Action.NAME, "Create settings.xml");
        }
        
        public void actionPerformed(ActionEvent e) {
            try {
                File fil = MavenSettingsSingleton.getInstance().getM2UserDir();
                if (!fil.exists()) {
                    fil.mkdirs();
                    FileObject fo = FileUtil.toFileObject(fil.getParentFile());
                    if (fo != null) {
                        fo.refresh();
                    }
                }
                DataFolder folder = DataFolder.findFolder(FileUtil.toFileObject(fil));
                // path to template...
                FileObject temp = Repository.getDefault().getDefaultFileSystem().findResource("Maven2Templates/settings.xml");
                DataObject dobj = DataObject.find(temp);
                DataObject newOne = dobj.createFromTemplate(folder);
                EditCookie cook = (EditCookie)newOne.getCookie(EditCookie.class);
                if (cook != null) {
                    cook.edit();
                }
                
            } catch (DataObjectNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
    }
    
    
}
