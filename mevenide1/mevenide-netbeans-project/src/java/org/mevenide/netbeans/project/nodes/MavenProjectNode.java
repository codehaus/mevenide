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
package org.mevenide.netbeans.project.nodes;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;

import org.mevenide.netbeans.api.project.AdditionalActionsProvider;
import org.mevenide.properties.IPropertyLocator;
import org.mevenide.netbeans.project.ActionProviderImpl;
import org.mevenide.netbeans.api.project.MavenProject;
import org.mevenide.netbeans.project.exec.RunGoalsAction;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.ErrorManager;
import org.openide.actions.FindAction;
import org.openide.actions.ToolsAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.FolderLookup;
import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;


/** A node to represent this object.
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class MavenProjectNode extends AbstractNode {
     private static final Logger LOGGER = Logger.getLogger(MavenProjectNode.class.getName());
     
     
     private MavenProject project;
     private ProjectInformation info;
     private Image icon;
     public MavenProjectNode( Lookup lookup, MavenProject proj) {
        super(new MavenProjectChildren(proj), lookup);
        this.project = proj;
        info = (ProjectInformation)project.getLookup().lookup(ProjectInformation.class);
        project.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                fireDisplayNameChange(null, getDisplayName());
                fireIconChange();
            }
        });
//        setIconBase("org/mevenide/netbeans/projects/resources/MavenIcon");
    }
    
    
    public String getDisplayName() {
        return project.getDisplayName();
    }
    
    public Image getIcon(int param) {
        //HACK - 1. getImage call
        // 2. assume project root folder, should be Generic Sources root (but is the same)
        Image img = ((ImageIcon)info.getIcon()).getImage();
        FileObject fo = project.getProjectDirectory();
        try {
            img = fo.getFileSystem().getStatus().annotateIcon(img, param, Collections.singleton(fo));
        } catch (FileStateInvalidException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
        return img;
    }
    
    public Image getOpenedIcon(int param) {
        //HACK - 1. getImage call
        // 2. assume project root folder, should be Generic Sources root (but is the same)
        Image img = ((ImageIcon)info.getIcon()).getImage();
        FileObject fo = project.getProjectDirectory();
        try {
            img = fo.getFileSystem().getStatus().annotateIcon(img, param, Collections.singleton(fo));
        } catch (FileStateInvalidException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
        return img;
    }
    
    public javax.swing.Action[] getActions(boolean param) {
//       javax.swing.Action[] spr = super.getActions(param);
        boolean hasCompile = project.getPropertyResolver().getResolvedValue("maven.netbeans.exec.compile.single") != null;
        boolean isMultiproject = (project.getPropertyLocator().getPropertyLocation("maven.multiproject.includes") 
                                    > IPropertyLocator.LOCATION_DEFAULTS); //NOI18N
        boolean isRunnableProject = (project.getPropertyResolver().getResolvedValue("maven.jar.mainclass") != null) 
                                    && (project.getPropertyResolver().getResolvedValue("maven.jar.mainclass").length() > 0);
        ArrayList lst = new ArrayList();
        ActionProviderImpl provider = (ActionProviderImpl)project.getLookup().lookup(ActionProviderImpl.class);
        lst.add(CommonProjectActions.newFileAction());
        lst.add(null);
        lst.add(provider.createBasicMavenAction("Build", ActionProvider.COMMAND_BUILD));
        lst.add(provider.createBasicMavenAction("Rebuild", ActionProvider.COMMAND_REBUILD));
        lst.add(provider.createBasicMavenAction("Clean", ActionProvider.COMMAND_CLEAN));
        lst.add(provider.createBasicMavenAction("Test", ActionProvider.COMMAND_TEST));
        lst.add(null);
        
        if (isRunnableProject) {
            lst.add(provider.createBasicMavenAction("Run", ActionProvider.COMMAND_RUN));
            lst.add(provider.createBasicMavenAction("Debug", ActionProvider.COMMAND_DEBUG));
        }
        lst.add(provider.createBasicMavenAction("Generate Javadoc", "javadoc"));
        if (isMultiproject) {
            lst.add(provider.createBasicMavenAction("Build (multiproject)", ActionProviderImpl.COMMAND_MULTIPROJECTBUILD));
            lst.add(provider.createBasicMavenAction("Clean (multiproject)", ActionProviderImpl.COMMAND_MULTIPROJECTCLEAN));
        }
        Lookup.Result res = Lookup.getDefault().lookup(new Lookup.Template(AdditionalActionsProvider.class));
        Iterator it = res.allInstances().iterator();
        while (it.hasNext()) {
            AdditionalActionsProvider act = (AdditionalActionsProvider)it.next();
            Action[] acts = act.createPopupActions(project);
            lst.addAll(Arrays.asList(acts));
        }
        lst.add(new RunGoalsAction(project));
        // separator
        lst.add(null);
        lst.add(project.createRefreshAction());
        lst.add(CommonProjectActions.setAsMainProjectAction());
        lst.add(CommonProjectActions.openSubprojectsAction());
        lst.add(CommonProjectActions.closeProjectAction());
        lst.add(null);
        lst.add(SystemAction.get(FindAction.class));
            
        try {
            FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("Projects/Actions"); // NOI18N
            if (fo != null) {
                DataObject dobj = DataObject.find(fo);
                FolderLookup actionRegistry = new FolderLookup((DataFolder)dobj);
                Lookup.Template query = new Lookup.Template(Object.class);
                Lookup lookup = actionRegistry.getLookup();
                Iterator it2 = lookup.lookup(query).allInstances().iterator();
                if (it2.hasNext()) {
                    lst.add(null);
                }
                while (it2.hasNext()) {
                    Object next = it2.next();
                    if (next instanceof Action) {
                        lst.add(next);
                    } else if (next instanceof JSeparator) {
                        lst.add(null);
                    }
                }
            }
        } catch (DataObjectNotFoundException ex) {
            // data folder for existing fileobject expected
            ErrorManager.getDefault().notify(ex);
        }
        lst.add(null);
        lst.add(SystemAction.get(ToolsAction.class));
        lst.add(null);
        
        lst.add(CommonProjectActions.customizeProjectAction());
        
        return (Action[])lst.toArray(new Action[lst.size()]);
    }

    public String getShortDescription() {
        return project.getShortDescription();
    }


}
