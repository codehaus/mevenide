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
import java.util.Iterator;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.api.project.AdditionalActionsProvider;
import org.mevenide.properties.IPropertyLocator;
import org.mevenide.netbeans.project.ActionProviderImpl;
import org.mevenide.netbeans.api.project.MavenProject;
import org.mevenide.netbeans.project.exec.RunGoalsAction;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;


/** A node to represent this object.
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class MavenProjectNode extends AbstractNode {
     private static Log log = LogFactory.getLog(MavenProjectNode.class);
     
     
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
        //HACK
        return ((ImageIcon)info.getIcon()).getImage();
    }
    
    public Image getOpenedIcon(int param) {
        //HACK
        return ((ImageIcon)info.getIcon()).getImage();
    }
    
    public javax.swing.Action[] getActions(boolean param) {
//       javax.swing.Action[] spr = super.getActions(param);
        boolean hasCompile = project.getPropertyResolver().getResolvedValue("maven.netbeans.exec.compile.single") != null;
        boolean isMultiproject = (project.getPropertyLocator().getPropertyLocation("maven.multiproject.includes") 
                                    > IPropertyLocator.LOCATION_DEFAULTS); //NOI18N
        ArrayList lst = new ArrayList();
        ActionProviderImpl provider = (ActionProviderImpl)project.getLookup().lookup(ActionProviderImpl.class);
        lst.add(CommonProjectActions.newFileAction());
        lst.add(null);
        lst.add(provider.createBasicMavenAction("Build", ActionProvider.COMMAND_BUILD));
        lst.add(provider.createBasicMavenAction("Rebuild", ActionProvider.COMMAND_REBUILD));
        lst.add(provider.createBasicMavenAction("Clean", ActionProvider.COMMAND_CLEAN));
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
        lst.add(CommonProjectActions.customizeProjectAction());
        
        return (Action[])lst.toArray(new Action[lst.size()]);
    }

    public String getShortDescription() {
        return project.getShortDescription();
    }


}
