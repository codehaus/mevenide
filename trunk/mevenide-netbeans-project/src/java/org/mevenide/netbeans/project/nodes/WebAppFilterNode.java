/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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

import java.io.File;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.mevenide.netbeans.project.ActionProviderImpl;
import org.mevenide.netbeans.project.MavenProject;
import org.netbeans.api.queries.VisibilityQuery;
import org.netbeans.spi.project.ui.support.LogicalViews;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.ChangeableDataFilter;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Utilities;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
class WebAppFilterNode extends FilterNode {
    private MavenProject project;
    private boolean isTopLevelNode = false;
    
    WebAppFilterNode(MavenProject proj, Node orig, File root) {
        this(proj, orig, root, true);
    }
    
    private WebAppFilterNode(MavenProject proj, Node orig, File root, boolean isTopLevel) {
        super(orig, new WebAppFilterChildren(proj, orig, root));
        isTopLevelNode = isTopLevel;
        project = proj;
    }
    
    public String getDisplayName() {
        if (isTopLevelNode) {
            return "WebApp Sources";
        }
        return super.getDisplayName();
        
    }
    
    public javax.swing.Action[] getActions(boolean param) {
        if (isTopLevelNode) {
            Action[] toReturn = new Action[1];
            ActionProviderImpl provider = (ActionProviderImpl)project.getLookup().lookup(ActionProviderImpl.class);
            toReturn[0] = LogicalViews.newFileAction();
            return toReturn;
        } else {
            return super.getActions(param);
        }
    }    

    public java.awt.Image getIcon(int param) {
        java.awt.Image retValue;
        if (isTopLevelNode) {
            retValue = Utilities.loadImage("org/mevenide/netbeans/project/resources/webmodule.gif");
        } else {
            retValue = super.getIcon(param);
        }
        return retValue;
    }

    public java.awt.Image getOpenedIcon(int param) {
        java.awt.Image retValue;
        if (isTopLevelNode) {
            retValue = Utilities.loadImage("org/mevenide/netbeans/project/resources/webmodule.gif");
        } else {
            retValue = super.getOpenedIcon(param);
        }
        return retValue;
    }
    
    static class WebAppFilterChildren extends FilterNode.Children {
        private File root;
        private MavenProject project;
        WebAppFilterChildren(MavenProject proj, Node original, File rootpath) {
            super(original);
            root = rootpath;
            project = proj;
        }
        
        protected Node[] createNodes(Object obj) {
            DataObject dobj = (DataObject)((Node)obj).getLookup().lookup(DataObject.class);
        
            if (dobj != null) {
                File file = FileUtil.toFile(dobj.getPrimaryFile());
                if (/*SharabilityQuery.getSharability(file) == SharabilityQuery.NOT_SHARABLE || */
                    !DirScannerSubClass.checkVisible(file, root)) {
                    return new Node[0];
                }
            }
            Node n = new WebAppFilterNode(project, (Node)obj, root, false);
            return new Node[] {n};
        }        
    }    
}

