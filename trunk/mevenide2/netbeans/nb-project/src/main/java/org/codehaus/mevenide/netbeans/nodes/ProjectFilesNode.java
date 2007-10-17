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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 * maven project related aggregator node..
 * @author Milos Kleint
 */
public class ProjectFilesNode extends AnnotatedAbstractNode {
    
    private NbMavenProject project;
    /** Creates a new instance of ProjectFilesNode */
    public ProjectFilesNode(NbMavenProject project) {
        super(new ProjectFilesChildren(project), Lookups.fixed(project.getProjectDirectory()));
        setName("projectfiles"); //NOI18N
        setDisplayName(org.openide.util.NbBundle.getMessage(ProjectFilesNode.class, "LBL_Project_Files"));
        this.project = project;
        setMyFiles();
    }
    
    @Override
    public Action[] getActions(boolean context) {
        Collection<Action> col = new ArrayList<Action>();
        if (project.getProjectDirectory().getFileObject("profiles.xml") == null) { //NOI18N
            col.add(new AddProfileXmlAction());
        }
        if (! new File(MavenSettingsSingleton.getInstance().getM2UserDir(), "settings.xml").exists()) { //NOI18N
            col.add(new AddSettingsXmlAction());
        }
        return col.toArray(new Action[col.size()]);
    }
    
    private Image getIcon(boolean opened) {
        Image badge = Utilities.loadImage("org/codehaus/mevenide/netbeans/projectfiles-badge.png", true); //NOI18N
        Image img = Utilities.mergeImages(NodeUtils.getTreeFolderIcon(opened), badge, 8, 8);
        return img;
    }
    
    @Override
    protected Image getIconImpl(int param) {
        return getIcon(false);
    }

    @Override
    protected Image getOpenedIconImpl(int param) {
        return getIcon(true);
    }
    
    private void setMyFiles() {
        Set<FileObject> fobs = new HashSet<FileObject>();
        FileObject fo = project.getProjectDirectory().getFileObject("pom.xml"); //NOI18N
        if (fo != null) {
            //#119134 for some unknown reason, the pom.xml might be missing from the project directory in some cases.
            // prevent passing null to the list that causes problems down the stream.
            fobs.add(fo);
        }
        FileObject fo2 = project.getProjectDirectory().getFileObject("profiles.xml"); //NOI18N
        if (fo2 != null) {
            fobs.add(fo2);
        }
        setFiles(fobs);
    }
    
    private static class ProjectFilesChildren extends Children.Keys<File> implements PropertyChangeListener {
        private NbMavenProject project;
        private FileChangeAdapter fileChangeListener;
        
        public ProjectFilesChildren(NbMavenProject proj) {
            super();
            project = proj;
            fileChangeListener = new FileChangeAdapter() {
                @Override
                public void fileDataCreated(FileEvent fe) {
                    regenerateKeys(true);
                }
                @Override
                public void fileDeleted(FileEvent fe) {
                    regenerateKeys(true);
                }
            };
        }
        
        protected Node[] createNodes(File fil) {
            FileObject fo = FileUtil.toFileObject(fil);
            if (fo != null) {
                try {
                    DataObject dobj = DataObject.find(fo);
                    FilterNode node = new FilterNode(dobj.getNodeDelegate().cloneNode());
                    return new Node[] { node };
                } catch (DataObjectNotFoundException e) {
                    //NOPMD
                }
                
            }
            return new Node[0];
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (NbMavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                regenerateKeys(true);
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
        
        @Override
        protected void addNotify() {
            super.addNotify();
            ProjectURLWatcher.addPropertyChangeListener(project, this);
            project.getProjectDirectory().addFileChangeListener(fileChangeListener);
            regenerateKeys(false);
        }
        
        @Override
        protected void removeNotify() {
            setKeys(Collections.EMPTY_SET);
            ProjectURLWatcher.removePropertyChangeListener(project, this);
            project.getProjectDirectory().removeFileChangeListener(fileChangeListener);
            super.removeNotify();
        }
        
        private void regenerateKeys(boolean refresh) {
            Collection<File> keys = new ArrayList<File>();
            keys.add(new File(FileUtil.toFile(project.getProjectDirectory()), "pom.xml")); //NOI18N
            keys.add(new File(FileUtil.toFile(project.getProjectDirectory()), "profiles.xml")); //NOI18N
            keys.add(new File(MavenSettingsSingleton.getInstance().getM2UserDir(), "settings.xml")); //NOI18N
            setKeys(keys);
            ((ProjectFilesNode)getNode()).setMyFiles();
            if (refresh) {
                for (File key : keys) {
                    refreshKey(key);
                }
            }
        }
    }

    private class AddProfileXmlAction extends AbstractAction {
        AddProfileXmlAction() {
            putValue(Action.NAME, org.openide.util.NbBundle.getMessage(ProjectFilesNode.class, "BTN_Create_profile_xml"));
        }
        public void actionPerformed(ActionEvent e) {
            try {
                DataFolder folder = DataFolder.findFolder(project.getProjectDirectory());
                // path to template...
                FileObject temp = Repository.getDefault().getDefaultFileSystem().findResource("Maven2Templates/profiles.xml"); //NOI18N
                DataObject dobj = DataObject.find(temp);
                DataObject newOne = dobj.createFromTemplate(folder);
                EditCookie cook = newOne.getCookie(EditCookie.class);
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
            putValue(Action.NAME, NbBundle.getMessage(ProjectFilesNode.class, "BTN_Create_settings_xml"));
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
                FileObject temp = Repository.getDefault().getDefaultFileSystem().findResource("Maven2Templates/settings.xml"); //NOI18N
                DataObject dobj = DataObject.find(temp);
                DataObject newOne = dobj.createFromTemplate(folder);
                EditCookie cook = newOne.getCookie(EditCookie.class);
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
