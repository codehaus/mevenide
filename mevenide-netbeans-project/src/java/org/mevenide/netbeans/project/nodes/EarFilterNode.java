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
import java.io.File;
import javax.swing.Action;
import org.mevenide.netbeans.project.ActionProviderImpl;
import org.mevenide.netbeans.project.MavenProject;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Utilities;


/**
 * node for displaying maven.ear.src directory's content.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
class EarFilterNode extends FilterNode {
    private MavenProject project;
    private boolean isTopLevelNode = false;
    
    EarFilterNode(MavenProject proj, Node orig, File root) {
        this(proj, orig, root, true);
    }
    
    private EarFilterNode(MavenProject proj, Node orig, File root, boolean isTopLevel) {
        super(orig, new EarFilterChildren(proj, orig, root));
        isTopLevelNode = isTopLevel;
        project = proj;
    }
    
    public String getDisplayName() {
        if (isTopLevelNode) {
            return "Ear Sources";
        }
        return super.getDisplayName();
        
    }
    
    public Action[] getActions(boolean param) {
        if (isTopLevelNode) {
            Action[] toReturn = new Action[1];
            toReturn[0] = CommonProjectActions.newFileAction();
            return toReturn;
        } else {
            return super.getActions(param);
        }
    }    

    public Image getIcon(int param) {
        Image retValue = super.getIcon(param);
        if (isTopLevelNode) {
            retValue = Utilities.mergeImages(retValue, 
                                             Utilities.loadImage("org/mevenide/netbeans/project/resources/webPagesBadge.gif"), 
                                             8, 8);
        } 
        return retValue;
    }

    public java.awt.Image getOpenedIcon(int param) {
        java.awt.Image retValue = super.getOpenedIcon(param);
        if (isTopLevelNode) {
            retValue = Utilities.mergeImages(retValue, 
                                             Utilities.loadImage("org/mevenide/netbeans/project/resources/webPagesBadge.gif"), 
                                             8, 8);
        } 
        return retValue;
    }
    
    static class EarFilterChildren extends FilterNode.Children {
        private File root;
        private MavenProject project;
        EarFilterChildren(MavenProject proj, Node original, File rootpath) {
            super(original);
            root = rootpath;
            project = proj;
        }
        
        protected Node[] createNodes(Object obj) {
            DataObject dobj = (DataObject)((Node)obj).getLookup().lookup(DataObject.class);
        
            if (dobj != null) {
                File file = FileUtil.toFile(dobj.getPrimaryFile());
                if (file != null) {
                    if (/*SharabilityQuery.getSharability(file) == SharabilityQuery.NOT_SHARABLE || */
                        !DirScannerSubClass.checkVisible(file, root)) {
                        return new Node[0];
                    }
                }
                Node n = new EarFilterNode(project, (Node)obj, root, false);
                return new Node[] {n};
            }
            Node origos = (Node)obj;
            return new Node[] { origos.cloneNode() };
        }        
    }    
}

