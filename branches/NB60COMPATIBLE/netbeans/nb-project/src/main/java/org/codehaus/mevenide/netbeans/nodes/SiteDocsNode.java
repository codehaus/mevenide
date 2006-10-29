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

import javax.swing.Action;
import org.codehaus.mevenide.netbeans.ActionProviderImpl;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.netbeans.api.queries.VisibilityQuery;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.loaders.DataObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Utilities;


/**
 * filter node for display of site sources
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
class SiteDocsNode extends FilterNode {
    private NbMavenProject project;
    private boolean isTopLevelNode = false;
    
    SiteDocsNode(NbMavenProject proj, Node orig) {
        this(proj, orig, true);
    }
    
    private SiteDocsNode(NbMavenProject proj, Node orig, boolean isTopLevel) {
        super(orig, new SiteFilterChildren(proj, orig));
        isTopLevelNode = isTopLevel;
        project = proj;
    }
    
    public String getDisplayName() {
        if (isTopLevelNode) {
            return "Project Site";
        }
        return super.getDisplayName();
        
    }
    
    public javax.swing.Action[] getActions(boolean param) {
        if (isTopLevelNode) {
            ActionProviderImpl impl = (ActionProviderImpl)project.getLookup().lookup(ActionProviderImpl.class);
            Action[] toReturn = new Action[4];
            toReturn[0] = CommonProjectActions.newFileAction();
            toReturn[1] = null;
            NetbeansActionMapping mapp = new NetbeansActionMapping();
            mapp.addGoal("site");
            toReturn[2] = impl.createCustomMavenAction("Generate Site", mapp);
            mapp = new NetbeansActionMapping();
            mapp.addGoal("site:deploy");
            toReturn[3] = impl.createCustomMavenAction("Deploy Site", mapp);
            return toReturn;
        } else {
            return super.getActions(param);
        }
    }    

    public java.awt.Image getIcon(int param) {
        java.awt.Image retValue = super.getIcon(param);
        if (isTopLevelNode) {
            retValue = Utilities.mergeImages(retValue, 
                                             Utilities.loadImage("org/codehaus/mevenide/netbeans/webPagesBadge.gif"), 
                                             8, 8);
        } 
        return retValue;
    }

    public java.awt.Image getOpenedIcon(int param) {
        java.awt.Image retValue = super.getOpenedIcon(param);
        if (isTopLevelNode) {
            retValue = Utilities.mergeImages(retValue, 
                                             Utilities.loadImage("org/codehaus/mevenide/netbeans/webPagesBadge.gif"), 
                                             8, 8);
        } 
        return retValue;
    }
    
    static class SiteFilterChildren extends FilterNode.Children {
        private NbMavenProject project;
        SiteFilterChildren(NbMavenProject proj, Node original) {
            super(original);
            project = proj;
        }
        
        protected Node[] createNodes(Node obj) {
            DataObject dobj = (DataObject)(obj).getLookup().lookup(DataObject.class);
        
            if (dobj != null) {
                if (!VisibilityQuery.getDefault().isVisible(dobj.getPrimaryFile())) {
                    return new Node[0];
                }
                Node n = new SiteDocsNode(project, obj, false);
                return new Node[] {n};
            }
            Node origos = (Node)obj;
            return new Node[] { origos.cloneNode() };
        }        
    }    
}

