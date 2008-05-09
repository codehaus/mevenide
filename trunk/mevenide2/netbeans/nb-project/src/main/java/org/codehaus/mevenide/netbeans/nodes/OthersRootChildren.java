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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.codehaus.mevenide.netbeans.MavenSourcesImpl;
import org.codehaus.mevenide.netbeans.NbMavenProjectImpl;
import org.codehaus.mevenide.netbeans.VisibilityQueryDataFilter;
import org.codehaus.mevenide.netbeans.api.NbMavenProject;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.Children;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
class OthersRootChildren extends Children.Keys {
    
    private NbMavenProjectImpl project;
    private PropertyChangeListener changeListener;
    private boolean test;
    public OthersRootChildren(NbMavenProjectImpl prj, boolean testResource) {
        this.project = prj;
        test = testResource;
        changeListener  = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (NbMavenProjectImpl.PROP_PROJECT.equals(evt.getPropertyName())) {
                    regenerateKeys();
                    refresh();
                }
            }
        };
    }
    
    @Override
    protected void addNotify() {
        super.addNotify();
        NbMavenProject.addPropertyChangeListener(project, changeListener);
        regenerateKeys();
    }
    
    @Override
    protected void removeNotify() {
        setKeys(Collections.EMPTY_SET);
        NbMavenProject.removePropertyChangeListener(project, changeListener);
        super.removeNotify();
        
    }
    
    private void regenerateKeys() {
        List<SourceGroup> list = new ArrayList<SourceGroup>();
        Sources srcs = project.getLookup().lookup(Sources.class);
        if (srcs == null) {
            throw new IllegalStateException("need Sources instance in lookup"); //NOI18N
        }
        SourceGroup[] resgroup = srcs.getSourceGroups(test ? MavenSourcesImpl.TYPE_TEST_OTHER  
                                                           : MavenSourcesImpl.TYPE_OTHER);
        Set<FileObject> files = new HashSet<FileObject>();
        for (int i = 0; i < resgroup.length; i++) {
            list.add(resgroup[i]);
            files.add(resgroup[i].getRootFolder());
            //TODO all direct subfolders that are contained in the SG?
        }
        setKeys(list);
        ((OthersRootNode)getNode()).setFiles(files);
    }
    
    
    protected Node[] createNodes(Object key) {
        SourceGroup grp = (SourceGroup)key;
        Node[] toReturn = new Node[1];
        DataFolder dobj = DataFolder.findFolder(grp.getRootFolder());
        Children childs = dobj.createNodeChildren(VisibilityQueryDataFilter.VISIBILITY_QUERY_FILTER);
        toReturn[0] = new FilterNode(dobj.getNodeDelegate().cloneNode(), childs);
        return toReturn;
    }
    

   
}
