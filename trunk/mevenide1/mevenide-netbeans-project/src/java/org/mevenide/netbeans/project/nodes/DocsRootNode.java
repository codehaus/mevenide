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
import java.util.Collections;
import javax.swing.Action;
import org.mevenide.netbeans.project.ActionProviderImpl;
import org.mevenide.netbeans.api.project.MavenProject;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.ErrorManager;
import org.openide.actions.FileSystemAction;
import org.openide.actions.FindAction;
import org.openide.actions.ToolsAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.loaders.DataFolder;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
class DocsRootNode extends AbstractNode {
    private SourceGroup group;
    private MavenProject project;
    
    DocsRootNode(SourceGroup grp, MavenProject proj) {
        super(Children.LEAF, Lookups.fixed( new Object[] {DataFolder.findFolder(grp.getRootFolder()), 
                                                          proj}));
        project = proj;
        setName("XDocumentation"); //NOI18N
        setDisplayName("Documentation");
        // can do so, since we depend on it..
        setIconBase("org/openide/loaders/defaultFolder"); //NOI18N
        
        group = grp;
        DataFolder dobj = DataFolder.findFolder(group.getRootFolder());
        if (dobj != null) {
            final Children childs = dobj.createNodeChildren(new VisibilityQueryDataFilter());
            // kind of hack..
            Children.MUTEX.postWriteRequest(new Runnable() {
               public void run() {
                   setChildren(childs);
               }
            });
        }
        //TODO listening on project changes and change root when docs move??
        
    }
    
    public Image getOpenedIcon(int param) {
        Image img = super.getOpenedIcon(param);
        FileObject fo = group.getRootFolder();
        try {
            img = fo.getFileSystem().getStatus().annotateIcon(img, param, Collections.singleton(fo));
        } catch (FileStateInvalidException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
        return img;
    }
    
    public Image getIcon(int param) {
        Image img = super.getOpenedIcon(param);
        FileObject fo = group.getRootFolder();
        try {
            img = fo.getFileSystem().getStatus().annotateIcon(img, param, Collections.singleton(fo));
        } catch (FileStateInvalidException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
        return img;
    }
    
    public javax.swing.Action[] getActions(boolean param) {
        Action[] toReturn = new Action[10];
        ActionProviderImpl provider = (ActionProviderImpl)project.getLookup().lookup(ActionProviderImpl.class);
        toReturn[0] = CommonProjectActions.newFileAction();
        toReturn[1] = null;
        toReturn[2] = provider.createCustomMavenAction("Generate Site", "site:generate");
        String method = project.getPropertyResolver().getResolvedValue("maven.site.deploy.method"); //NOI18N
        String deployName = "Deploy site using ";
        if (method != null && method.equalsIgnoreCase("fs")) {
            deployName = deployName + "filesystem";
        } else {
            deployName = deployName + "ssh";
        }
        toReturn[3] = provider.createCustomMavenAction(deployName, "site:deploy"); //NOI18N
        toReturn[4] = null;
        toReturn[5] = SystemAction.get(FindAction.class);
        toReturn[6] = null;
        toReturn[7] = SystemAction.get(FileSystemAction.class);
        toReturn[8] = null;
        toReturn[9] = SystemAction.get(ToolsAction.class);
        
        return toReturn;
    }    
}

