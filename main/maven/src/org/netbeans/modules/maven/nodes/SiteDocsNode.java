/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.maven.nodes;

import java.util.Collections;
import javax.swing.Action;
import org.netbeans.modules.maven.ActionProviderImpl;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.api.queries.VisibilityQuery;
import org.netbeans.modules.maven.execute.model.NetbeansActionMapping;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.DataObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;


/**
 * filter node for display of site sources
 * @author  Milos Kleint
 */
class SiteDocsNode extends FilterNode {
    private NbMavenProjectImpl project;
    private boolean isTopLevelNode = false;
    
    SiteDocsNode(NbMavenProjectImpl proj, Node orig) {
        this(proj, orig, true);
    }
    
    private SiteDocsNode(NbMavenProjectImpl proj, Node orig, boolean isTopLevel) {
        //#142744 if orig child is leaf, put leave as well.
        super(orig, orig.getChildren() == Children.LEAF ? Children.LEAF : new SiteFilterChildren(proj, orig));
        isTopLevelNode = isTopLevel;
        project = proj;
    }
    
   
    @Override
    public String getDisplayName() {
        if (isTopLevelNode) {
            String s = NbBundle.getMessage(SiteDocsNode.class, "LBL_Site_Pages");
            DataObject dob = getOriginal().getLookup().lookup(DataObject.class);
            FileObject file = dob.getPrimaryFile();
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
            DataObject dob = getOriginal().getLookup().lookup(DataObject.class);
            FileObject file = dob.getPrimaryFile();
             FileSystem.Status stat = file.getFileSystem().getStatus();
             if (stat instanceof FileSystem.HtmlStatus) {
                 FileSystem.HtmlStatus hstat = (FileSystem.HtmlStatus) stat;

                String s = NbBundle.getMessage(SiteDocsNode.class, "LBL_Site_Pages");
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
    
    public javax.swing.Action[] getActions(boolean param) {
        if (isTopLevelNode) {
            ActionProviderImpl impl = project.getLookup().lookup(ActionProviderImpl.class);
            Action[] toReturn = new Action[4];
            toReturn[0] = CommonProjectActions.newFileAction();
            toReturn[1] = null;
            NetbeansActionMapping mapp = new NetbeansActionMapping();
            mapp.addGoal("site"); //NOI18N
            toReturn[2] = impl.createCustomMavenAction(org.openide.util.NbBundle.getMessage(SiteDocsNode.class, "BTN_Generate_Site"), mapp);
            mapp = new NetbeansActionMapping();
            mapp.addGoal("site:deploy"); //NOI18N
            toReturn[3] = impl.createCustomMavenAction(org.openide.util.NbBundle.getMessage(SiteDocsNode.class, "BTN_Deploy_Site"), mapp, false);
            return toReturn;
        } else {
            return super.getActions(param);
        }
    }    

    @Override
    public java.awt.Image getIcon(int param) {
        java.awt.Image retValue = super.getIcon(param);
        if (isTopLevelNode) {
            retValue = Utilities.mergeImages(retValue, 
                                             Utilities.loadImage("org/netbeans/modules/maven/projectsite-badge.png"), //NOI18N
                                             8, 8);
        } 
        return retValue;
    }

    @Override
    public java.awt.Image getOpenedIcon(int param) {
        java.awt.Image retValue = super.getOpenedIcon(param);
        if (isTopLevelNode) {
            retValue = Utilities.mergeImages(retValue, 
                                             Utilities.loadImage("org/netbeans/modules/maven/projectsite-badge.png"), //NOI18N
                                             8, 8);
        } 
        return retValue;
    }
    
    static class SiteFilterChildren extends FilterNode.Children {
        private NbMavenProjectImpl project;
        SiteFilterChildren(NbMavenProjectImpl proj, Node original) {
            super(original);
            project = proj;
        }
        
        @Override
        protected Node[] createNodes(Node obj) {
            DataObject dobj = (obj).getLookup().lookup(DataObject.class);
        
            if (dobj != null) {
                if (!VisibilityQuery.getDefault().isVisible(dobj.getPrimaryFile())) {
                    return new Node[0];
                }
                Node n = new SiteDocsNode(project, obj, false);
                return new Node[] {n};
            }
            Node origos = obj;
            return new Node[] { origos.cloneNode() };
        }        
    }    
}

