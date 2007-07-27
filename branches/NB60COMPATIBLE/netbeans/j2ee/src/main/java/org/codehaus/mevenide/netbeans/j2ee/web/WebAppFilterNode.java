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
package org.codehaus.mevenide.netbeans.j2ee.web;

import java.io.File;
import java.util.Collections;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.api.queries.VisibilityQuery;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;


/**
 * filter node for display of web sources
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
class WebAppFilterNode extends FilterNode {
    private boolean isTopLevelNode = false;
    private FileObject file;
    
    WebAppFilterNode(NbMavenProject proj, Node orig, File root) {
        this(proj, orig, root, true);
    }
    
    private WebAppFilterNode(NbMavenProject proj, Node orig, File root, boolean isTopLevel) {
        super(orig, new WebAppFilterChildren(proj, orig, root));
        isTopLevelNode = isTopLevel;
        if (isTopLevel) {
            file = FileUtil.toFileObject(root);
        }
    }
    
    @Override
    public String getDisplayName() {
        if (isTopLevelNode) {
            String s = NbBundle.getMessage(WebAppFilterNode.class, "LBL_Web_Pages");
            
            try {
                s = file.getFileSystem().getStatus().annotateName(s, Collections.singleton(file));
            } catch (FileStateInvalidException e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
            
            return s;
        }
        return getOriginal().getDisplayName();
        
    }

    @Override
    public String getHtmlDisplayName() {
        if (!isTopLevelNode) {
            return getOriginal().getHtmlDisplayName();
        }
         try {
             FileSystem.Status stat = file.getFileSystem().getStatus();
             if (stat instanceof FileSystem.HtmlStatus) {
                 FileSystem.HtmlStatus hstat = (FileSystem.HtmlStatus) stat;

                String s = NbBundle.getMessage(WebAppFilterNode.class, "LBL_Web_Pages");
                 String result = hstat.annotateNameHtml (
                     s, Collections.singleton(file));

                 //Make sure the super string was really modified
                 if (!s.equals(result)) {
                     return result;
                 }
             }
         } catch (FileStateInvalidException e) {
             ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
         }
         return super.getHtmlDisplayName();
    }    
    
    
    @Override
    public javax.swing.Action[] getActions(boolean param) {
//        if (isTopLevelNode) {
//            Action[] toReturn = new Action[1];
//            toReturn[0] = CommonProjectActions.newFileAction();
//            return toReturn;
//        } else {
            return super.getActions(param);
//        }
    }    

    @Override
    public java.awt.Image getIcon(int param) {
        java.awt.Image retValue = super.getIcon(param);
        if (isTopLevelNode) {
            retValue = Utilities.mergeImages(retValue, 
                                             Utilities.loadImage("org/codehaus/mevenide/netbeans/j2ee/web/webPagesBadge.png"), //NOI18N
                                             8, 8);
        } 
        return retValue;
    }

    @Override
    public java.awt.Image getOpenedIcon(int param) {
        java.awt.Image retValue = super.getOpenedIcon(param);
        if (isTopLevelNode) {
            retValue = Utilities.mergeImages(retValue, 
                                             Utilities.loadImage("org/codehaus/mevenide/netbeans/j2ee/web/webPagesBadge.png"), //NOI18N
                                             8, 8);
        } 
        return retValue;
    }
    
    static class WebAppFilterChildren extends FilterNode.Children {
        private File root;
        private NbMavenProject project;
        WebAppFilterChildren(NbMavenProject proj, Node original, File rootpath) {
            super(original);
            root = rootpath;
            project = proj;
        }
        
        @Override
        protected Node[] createNodes(Node obj) {
            DataObject dobj = obj.getLookup().lookup(DataObject.class);
        
            if (dobj != null) {
                if (!VisibilityQuery.getDefault().isVisible(dobj.getPrimaryFile())) {
                    return new Node[0];
                }
                Node n = new WebAppFilterNode(project, obj, root, false);
                return new Node[] {n};
            }
            Node origos = obj;
            return new Node[] { origos.cloneNode() };
        }        
    }    
}

