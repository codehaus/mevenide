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
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.properties.IPropertyLocator;
import org.mevenide.netbeans.project.ActionProviderImpl;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.exec.RunGoalsAction;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;
import org.openide.util.Utilities;


/** A node to represent this object.
 *
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenProjectNode extends AbstractNode {
     private static Log log = LogFactory.getLog(MavenProjectNode.class);
     
     
     private MavenProject project;
     private Image icon;
     public MavenProjectNode( Lookup lookup, MavenProject proj) {
        super(new MavenProjectChildren(proj), lookup);
        this.project = proj;
        project.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                fireDisplayNameChange(null, getDisplayName());
                fireIconChange();
            }
        });
//        setIconBase("org/mevenide/netbeans/projects/resources/MavenIcon");
    }
    
    
    public String getDisplayName()
    {
        return project.getDisplayName();
    }
    
    public Image getIcon(int param) {
        //HACK
        return ((ImageIcon)project.getProjectInfo().getIcon()).getImage();
    }
    
    public Image getOpenedIcon(int param) {
        //HACK
        return ((ImageIcon)project.getProjectInfo().getIcon()).getImage();
    }
    
    public javax.swing.Action[] getActions(boolean param) {
//       javax.swing.Action[] spr = super.getActions(param);
        boolean isMultiproject = (project.getPropertyLocator().getPropertyLocation("maven.multiproject.includes") 
                                    > IPropertyLocator.LOCATION_DEFAULTS); //NOI18N
        int slip = (isMultiproject ? 2 : 0);
        Action[] toReturn = new Action[14 + slip];
        ActionProviderImpl provider = (ActionProviderImpl)project.getLookup().lookup(ActionProviderImpl.class);
        toReturn[0] = CommonProjectActions.newFileAction();
        toReturn[1] = null;
        toReturn[2] = provider.createBasicMavenAction("Build", ActionProvider.COMMAND_BUILD);
        toReturn[3] = provider.createBasicMavenAction("Clean", ActionProvider.COMMAND_CLEAN);
        toReturn[4] = provider.createBasicMavenAction("Rebuild", ActionProvider.COMMAND_REBUILD);
        toReturn[5] = provider.createBasicMavenAction("Generate Javadoc", "javadoc");
        if (isMultiproject) {
            toReturn[6] = provider.createBasicMavenAction("Build (multiproject)", ActionProviderImpl.COMMAND_MULTIPROJECTBUILD);
            toReturn[7] = provider.createBasicMavenAction("Clean (multiproject)", ActionProviderImpl.COMMAND_MULTIPROJECTCLEAN);
        }
        toReturn[6 + slip] = new RunGoalsAction(project);
        // separator
        toReturn[7 + slip] = null;
        toReturn[8 + slip] = project.createRefreshAction();
        toReturn[9 + slip] = CommonProjectActions.setAsMainProjectAction();
        toReturn[10 + slip] = CommonProjectActions.openSubprojectsAction();
        toReturn[11 + slip] = CommonProjectActions.closeProjectAction();
        toReturn[12 + slip] = null;
        toReturn[13 + slip] = CommonProjectActions.customizeProjectAction();
//        for (int i = 0; i < spr.length; i++) {
//            toReturn[i + 6] = spr[i];
//        }
        return toReturn;
    }

    public String getShortDescription() {
        return project.getShortDescription();
    }


}
