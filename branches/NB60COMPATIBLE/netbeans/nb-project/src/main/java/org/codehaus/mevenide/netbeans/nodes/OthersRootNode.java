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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.DataFolder;
import org.openide.nodes.AbstractNode;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
class OthersRootNode extends AbstractNode {
    private NbMavenProject project;
    private FileObject file;
    
    OthersRootNode(NbMavenProject mavproject, boolean testResource, FileObject fo) {
        super(new OthersRootChildren(mavproject, testResource), Lookups.fixed(fo, DataFolder.findFolder(fo)));
        setName(testResource ? "OtherTestRoots" : "OtherRoots"); //NOI18N
        setDisplayName(testResource ? "Other Test Sources" : "Other Sources");
        // can do so, since we depend on it..
//        setIconBase("org/mevenide/netbeans/project/resources/defaultFolder"); //NOI18N
        project = mavproject;
        file = fo;
    }
    
    public Action[] getActions(boolean context) {
            List supers = Arrays.asList(super.getActions(context));
            List lst = new ArrayList(supers.size() + 5);
            lst.addAll(supers);
            Action[] retValue = new Action[lst.size()];
            retValue = (Action[])lst.toArray(retValue);
            return retValue;

    }
    
    public java.awt.Image getIcon(int param) {
        java.awt.Image retValue = super.getIcon(param);
//        retValue = Utilities.mergeImages(retValue,
//                        Utilities.loadImage("org/mevenide/netbeans/project/resources/resources.gif"),
//                        0, 0);
        return retValue;
    }
    
    public java.awt.Image getOpenedIcon(int param) {
        java.awt.Image retValue = super.getOpenedIcon(param);
//        retValue = Utilities.mergeImages(retValue,
//                      Utilities.loadImage("org/mevenide/netbeans/project/resources/resources.gif"),
//                      0, 0);
        return retValue;
    }
    
    public String getDisplayName () {
        String s = super.getDisplayName ();

        try {            
            s = file.getFileSystem ().getStatus ().annotateName (s, Collections.singleton(file));
        } catch (FileStateInvalidException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }

        return s;
    }

    public String getHtmlDisplayName() {
         try {
             FileSystem.Status stat = file.getFileSystem().getStatus();
             if (stat instanceof FileSystem.HtmlStatus) {
                 FileSystem.HtmlStatus hstat = (FileSystem.HtmlStatus) stat;

                 String result = hstat.annotateNameHtml (
                     super.getDisplayName(), Collections.singleton(file));

                 //Make sure the super string was really modified
                 if (!super.getDisplayName().equals(result)) {
                     return result;
                 }
             }
         } catch (FileStateInvalidException e) {
             ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
         }
         return super.getHtmlDisplayName();
    }    
    
}

