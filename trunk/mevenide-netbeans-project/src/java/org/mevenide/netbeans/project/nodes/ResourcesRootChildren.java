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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.MavenSourcesImpl;

import org.netbeans.api.project.SourceGroup;

import org.netbeans.api.project.Sources;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.Utilities;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
class ResourcesRootChildren extends Children.Keys {
    private static Log logger = LogFactory.getLog(ResourcesRootChildren.class);
    
    private MavenProject project;
    private PropertyChangeListener changeListener;
    private boolean test;
    public ResourcesRootChildren(MavenProject prj, boolean testResource) {
        this.project = prj;
        test = testResource;
        changeListener  = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (MavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                    regenerateKeys();
                    refresh();
                }
            }
        };
    }
    
    protected void addNotify() {
        super.addNotify();
        project.addPropertyChangeListener(changeListener);
        regenerateKeys();
    }
    
    protected void removeNotify() {
        setKeys(Collections.EMPTY_SET);
        project.removePropertyChangeListener(changeListener);
        super.removeNotify();
        
    }
    
    private void regenerateKeys() {
        List list = new ArrayList();
        Sources srcs = (Sources)project.getLookup().lookup(Sources.class);
        if (srcs == null) {
            throw new IllegalStateException("need Sources instance in lookup");
        }
        SourceGroup[] resgroup = srcs.getSourceGroups(test ? MavenSourcesImpl.TYPE_TEST_RESOURCES : 
                                                             MavenSourcesImpl.TYPE_RESOURCES);
        for (int i = 0; i < resgroup.length; i++) {
            list.add(resgroup[i]);
        }
        setKeys(list);
    }
    
    
    protected Node[] createNodes(Object key) {
        MavenSourcesImpl.ResourceGroup grp = (MavenSourcesImpl.ResourceGroup)key;
        Node[] toReturn = new Node[1];
        try  {
            DataObject dobj = DataObject.find(grp.getRootFolder());
            Node original = dobj.getNodeDelegate().cloneNode();
            toReturn[0] = new MyFilterNode(original, 
                                new ResourceFilterNode.ResFilterChildren(original, 
                                           grp.getRootFolderFile(), grp.getResource()),
                                grp);
            
        } catch (DataObjectNotFoundException exc) {
            toReturn = new Node[0];
        }
        return toReturn;
    }
    
    private class MyFilterNode extends FilterNode {
        private MavenSourcesImpl.ResourceGroup group;
        MyFilterNode(Node original, Children children, MavenSourcesImpl.ResourceGroup grp) {
            super(original, children);
            group = grp;
        }
        
        public String getDisplayName() {
            String toReturn = group.getResource().getDirectory();
            return toReturn;
        }
        
        public String getHtmlDisplayName() {
            String toReturn = getDisplayName();
            if (group.getResource().getTargetPath() != null) {
                toReturn = toReturn + " -> <I>" + group.getResource().getTargetPath() + "</I>";
            }
            return toReturn;
        }
        
        public java.awt.Image getIcon(int param) {
            java.awt.Image retValue = super.getIcon(param);
            retValue = Utilities.mergeImages(retValue,
                            Utilities.loadImage("org/mevenide/netbeans/project/resources/resourceBadge.gif"),
                            8, 8);
            return retValue;
        }
        
        public java.awt.Image getOpenedIcon(int param) {
            java.awt.Image retValue = super.getOpenedIcon(param);
            retValue = Utilities.mergeImages(retValue,
                            Utilities.loadImage("org/mevenide/netbeans/project/resources/resourceBadge.gif"),
                            8, 8);
            return retValue;
        }

        public Action[] getActions(boolean context) {
            List supers = Arrays.asList(super.getActions(context));
            List lst = new ArrayList(supers.size() + 5);
            lst.add(ShowAllResourcesAction.getInstance());
            lst.add(null);
            lst.addAll(supers);
            Action[] retValue = new Action[lst.size()];
            retValue = (Action[])lst.toArray(retValue);
            return retValue;
        }
        
    }
}
