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

package org.mevenide.netbeans.project.classpath;

import java.beans.PropertyChangeEvent;
import org.netbeans.spi.java.classpath.ClassPathImplementation;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.MalformedURLException;
import java.io.File;
import java.net.URI;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.project.io.JarOverrideReader2;


abstract class AbstractProjectClassPathImpl implements ClassPathImplementation {
    private static final Log logger = LogFactory.getLog(AbstractProjectClassPathImpl.class);    
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    private List resources;
    private MavenProject project;
    
    protected AbstractProjectClassPathImpl(MavenProject proj) {
        project = proj;
        project.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                logger.warn("fire PROP_RSOURCES");
                resources = null;
                support.firePropertyChange(ClassPathImplementation.PROP_RESOURCES, null, null);
            }
        });
        
    }
    protected final MavenProject getMavenProject() {
        return project;
    }
    
    public synchronized List /*<PathResourceImplementation>*/ getResources() {
        logger.debug("getresources");
        if (resources == null) {
            resources = this.getPath();
        }
        return resources;
    }
    
    
    abstract URI[] createPath();
    
    private List getPath() {
        List result = new ArrayList();
        URI[] pieces = createPath();
        for (int i = 0; i < pieces.length; i++) {
            try {
                URL entry;
                
                // if file does not exist (e.g. build/classes folder
                // was not created yet) then corresponding File will
                // not be ended with slash. Fix that.
                if (pieces[i].toString().toLowerCase().endsWith(".jar")) {
                    entry = FileUtil.getArchiveRoot(pieces[i].toURL());
                } else {
                    entry = pieces[i].toURL();
                    if  (entry.toExternalForm().endsWith("/")) {
                        entry = new URL(entry.toExternalForm() + "/");
                    }
                }
                if (entry != null) {
                    result.add(ClassPathSupport.createResource(entry));
                }
            } catch (MalformedURLException mue) {
                ErrorManager.getDefault().notify(mue);
            }
        }
        return Collections.unmodifiableList(result);
    }
    
    public void addPropertyChangeListener(java.beans.PropertyChangeListener propertyChangeListener) {
        synchronized (support) {
            logger.warn("project=" + project.getDisplayName() + " adding propchange=" + propertyChangeListener.getClass());
            support.addPropertyChangeListener(propertyChangeListener);
        }
    }
    
    public void removePropertyChangeListener(java.beans.PropertyChangeListener propertyChangeListener) {
        synchronized (support) {
            logger.warn("removing propchange=" + propertyChangeListener.getClass());
            support.removePropertyChangeListener(propertyChangeListener);
        }
    }
    
    
    protected URI checkOneDependency(Dependency dep) {
        if (dep.isAddedToClasspath()) {
            // check override first
            URI uri;
            String path = JarOverrideReader2.getInstance().processOverride(dep,
                    getMavenProject().getPropertyResolver(),
                    getMavenProject().getLocFinder());
            if (path != null) {
                File file = new File(path);
                uri = file.toURI();
            } else {
                uri = FileUtilities.getDependencyURI(dep, getMavenProject());
            }
            logger.debug("dep uri=" + uri);
            if (uri != null) {
                return uri;
            }
        }
        return null;
    }    
}
