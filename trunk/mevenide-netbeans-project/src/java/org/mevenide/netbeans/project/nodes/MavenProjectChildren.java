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
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.MavenSourcesImpl;
import org.netbeans.api.java.project.JavaProjectConstants;

import org.netbeans.api.project.SourceGroup;

import org.netbeans.api.project.Sources;
import org.netbeans.api.queries.VisibilityQuery;
import org.netbeans.spi.java.project.support.ui.PackageView;


import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.ChangeableDataFilter;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.nodes.Children;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
class MavenProjectChildren extends Children.Keys {
    private static final Log logger = LogFactory.getLog(MavenProjectChildren.class);
    
    private static final Object KEY_JELLY_SCRIPT = "jellyScript"; //NOI18N
    private static final Object KEY_RESOURCES = "resources"; //NOI18N
    
    private MavenProject project;
    private PropertyChangeListener changeListener;
    
    public MavenProjectChildren(MavenProject project) {
        this.project = project;
        changeListener  = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (MavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                    logger.debug("regenerating project children keys");
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
        SourceGroup[] javagroup = srcs.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        for (int i = 0; i < javagroup.length; i++) {
            list.add(javagroup[i]);
        }
        Project proj = project.getOriginalMavenProject();
        Build build = proj.getBuild();
        if (build != null) {
            List reso = build.getResources();
            if (reso != null && reso.size() > 0) {
                list.add(KEY_RESOURCES);
            }
        }
        FileObject fo = project.getProjectDirectory().getFileObject("plugin", "jelly");
        if (fo != null) {
            list.add(KEY_JELLY_SCRIPT);
        }
        SourceGroup[] xdocsgroup = srcs.getSourceGroups(MavenSourcesImpl.TYPE_XDOCS);
        for (int i = 0; i < xdocsgroup.length; i++) {
            list.add(xdocsgroup[i]);
        }
        setKeys(list);
    }
    
    
    protected Node[] createNodes(Object key) {
        Node n = null;
        if (key instanceof SourceGroup) {
            SourceGroup grp = (SourceGroup)key;
            if (MavenSourcesImpl.NAME_XDOCS.equals(grp.getName())) {
                n = new DocsRootNode(grp, project);
            } else {
                n = PackageView.createPackageView(grp);
            }
        }
        else if (key == KEY_JELLY_SCRIPT) {
            n = new PluginScriptNode(project.getProjectDirectory());
        }
        else if (key == KEY_RESOURCES) {
            n = new ResourcesRootNode(project);
        }
        return n == null ? new Node[0] : new Node[] {n};
    }
    
    private DataFolder getFolder(String relPath) {
        FileObject folder = FileUtilities.findFolder(project.getProjectDirectory(), relPath);
        if (folder != null) {
            return DataFolder.findFolder(folder);
        }
        //TODO - create the folder if it doesn't exist? and do it here? I'd rather do it when opening project..
        return null;
    }
    
    private DataFolder getFolder(URI path) {
        try {
            FileObject folder = URLMapper.findFileObject(path.toURL());
            if (folder != null) {
                return DataFolder.findFolder(folder);
            }
        } catch (MalformedURLException exc) {
            logger.warn("malformed URI=" + path, exc);
        }
        //TODO - create the folder if it doesn't exist? and do it here? I'd rather do it when opening project..
        return null;
    }
    
    
 
    
    
}
