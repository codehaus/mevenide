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

import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.mevenide.netbeans.project.ActionProviderImpl;
import org.mevenide.netbeans.project.MavenProject;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.queries.VisibilityQuery;
import org.netbeans.spi.project.ui.support.LogicalViews;
import org.openide.filesystems.FileObject;
import org.openide.loaders.ChangeableDataFilter;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
class DocsRootNode extends AbstractNode {
    private SourceGroup group;
    private MavenProject project;
    
    DocsRootNode(SourceGroup grp, MavenProject proj) {
        super(Children.LEAF, Lookups.fixed( new Object[] {DataFolder.findFolder(grp.getRootFolder()), proj}));
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
    
    public javax.swing.Action[] getActions(boolean param) {
        Action[] toReturn = new Action[3];
        ActionProviderImpl provider = (ActionProviderImpl)project.getLookup().lookup(ActionProviderImpl.class);
        toReturn[0] = LogicalViews.newFileAction();
        toReturn[1] = null;
        toReturn[2] = provider.createCustomMavenAction("Generate Site", "site:generate");
        return toReturn;
    }    
    
    /**
     * filter for xdocs folder.. this one is not package oriented but rather simple folder structure
     * need to filter out the unimportant files, just like the Files tab does.
     */
    static final class VisibilityQueryDataFilter implements ChangeListener, ChangeableDataFilter {
        
        EventListenerList ell = new EventListenerList();
        
        public VisibilityQueryDataFilter() {
            VisibilityQuery.getDefault().addChangeListener( this );
        }
        
        public void addChangeListener( ChangeListener listener ) {
            ell.add( ChangeListener.class, listener );
        }
        
        public void removeChangeListener( ChangeListener listener ) {
            ell.remove( ChangeListener.class, listener );
        }
        
        public boolean acceptDataObject(DataObject obj) {
            FileObject fo = obj.getPrimaryFile();
            return VisibilityQuery.getDefault().isVisible( fo );
        }
        
        public void stateChanged( ChangeEvent e) {
            Object[] listeners = ell.getListenerList();
            ChangeEvent event = null;
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (listeners[i] == ChangeListener.class) {
                    if ( event == null) {
                        event = new ChangeEvent( this );
                    }
                    ((ChangeListener)listeners[i+1]).stateChanged( event );
                }
            }
        }
    }
}

