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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.apache.tools.ant.DirectoryScanner;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.MavenSourcesImpl;

import org.netbeans.api.project.SourceGroup;

import org.netbeans.api.project.Sources;
import org.netbeans.spi.java.project.support.ui.PackageView;


import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;
import org.openide.nodes.Children;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
class ResourcesRootChildren extends Children.Keys {
    private static Log logger = LogFactory.getLog(ResourcesRootChildren.class);
    
    private MavenProject project;
    private PropertyChangeListener changeListener;
    public ResourcesRootChildren(MavenProject project) {
        this.project = project;
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
        SourceGroup[] resgroup = srcs.getSourceGroups(MavenSourcesImpl.TYPE_RESOURCES);
        for (int i = 0; i < resgroup.length; i++) {
            list.add(resgroup[i]);
        }
        setKeys(list);
    }
    
    
    protected Node[] createNodes(Object key) {
        SourceGroup grp = (SourceGroup)key;
        Node[] toReturn = new Node[1];
        toReturn[0] = PackageView.createPackageView(grp);
        return toReturn;
    }
}
