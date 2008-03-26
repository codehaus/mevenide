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

package org.codehaus.mevenide.netbeans.classpath;

import java.beans.PropertyChangeEvent;
import org.codehaus.mevenide.netbeans.NbMavenProject;
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
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.netbeans.spi.java.classpath.FilteringPathResourceImplementation;
import org.netbeans.spi.java.classpath.PathResourceImplementation;


abstract class AbstractProjectClassPathImpl implements ClassPathImplementation {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private List resources;
    private NbMavenProject project;
    
    protected AbstractProjectClassPathImpl(NbMavenProject proj) {
        project = proj;
        //TODO make weak or remove the listeners as well??
        ProjectURLWatcher.addPropertyChangeListener(proj, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (NbMavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                    List newValues = getPath();
                    synchronized (AbstractProjectClassPathImpl.this) {
                        List oldvalue = resources;
//                        System.out.println("checking=" + AbstractProjectClassPathImpl.this.getClass());
                        if (hasChanged(oldvalue, newValues)) {
                            resources = newValues;
//                            System.out.println("old=" + oldvalue);
//                            System.out.println("new=" + newValues);
//                            System.out.println("firing change=" + AbstractProjectClassPathImpl.this.getClass());
                            support.firePropertyChange(ClassPathImplementation.PROP_RESOURCES, oldvalue, resources);
                        }
                    }
                }
            }
        });
    }
    
    private boolean hasChanged(List oldValues, List newValues) {
        if (oldValues == null) {
            return (newValues != null);
        }
        Iterator it = oldValues.iterator();
        ArrayList nl = new ArrayList();
        nl.addAll(newValues);
        while (it.hasNext()) {
            PathResourceImplementation res = (PathResourceImplementation)it.next();
            URL oldUrl = res.getRoots()[0];
            boolean found = false;
            if (nl.size() == 0) {
                return true;
            }
            Iterator inner = nl.iterator();
            while (inner.hasNext()) {
                PathResourceImplementation res2 = (PathResourceImplementation)inner.next();
                URL newUrl = res2.getRoots()[0];
                if (newUrl.equals(oldUrl)) {
                    inner.remove();
                    found = true;
                    break;
                }
            }
            if (!found) {
                return true;
            }
        }
        if (nl.size() != 0) {
            return true;
        }
        return false;
    }
    
    protected final NbMavenProject getMavenProject() {
        return project;
    }
    
    public synchronized List /*<PathResourceImplementation>*/ getResources() {
        if (resources == null) {
            resources = this.getPath();
        }
        return resources;
    }
    
    
    abstract URI[] createPath();
    
    //to be overriden by subclasses..
    protected FilteringPathResourceImplementation getFilteringResources() {
        return null;
    }
    
    private List getPath() {
        List<PathResourceImplementation> result = new ArrayList<PathResourceImplementation>();
        URI[] pieces = createPath();
        for (int i = 0; i < pieces.length; i++) {
            try {
                URL entry;
                
                // if file does not exist (e.g. build/classes folder
                // was not created yet) then corresponding File will
                // not be ended with slash. Fix that.
                
                //HACK the url is considered archive if the name contains a dot.
                // should be safe since the only folder classpath items are sources and target/classes
                // if this causes problems we need to move the url finetuning in the place which
                //creates the URIs (createPath())
                String lowecasePath = pieces[i].toString().toLowerCase();
                int lastdot = lowecasePath.lastIndexOf('.');
                int lastslash = lowecasePath.lastIndexOf('/');
                boolean isFile = (lastdot > 0 && lastdot > lastslash);
                
                if (isFile) {//NOI18N
                    entry = FileUtil.getArchiveRoot(pieces[i].toURL());
                } else {
                    entry = pieces[i].toURL();
                    if  (!entry.toExternalForm().endsWith("/")) { //NOI18N
                        entry = new URL(entry.toExternalForm() + "/"); //NOI18N
                    }
                }
                if (entry != null) {
                    result.add(ClassPathSupport.createResource(entry));
                }
            } catch (MalformedURLException mue) {
                ErrorManager.getDefault().notify(mue);
            }
        }
        FilteringPathResourceImplementation filtering = getFilteringResources();
        if (filtering != null) {
            result.add(filtering);
        }
        return Collections.unmodifiableList(result);
    }
    
    public void addPropertyChangeListener(java.beans.PropertyChangeListener propertyChangeListener) {
        synchronized (support) {
            support.addPropertyChangeListener(propertyChangeListener);
        }
    }
    
    public void removePropertyChangeListener(java.beans.PropertyChangeListener propertyChangeListener) {
        synchronized (support) {
            support.removePropertyChangeListener(propertyChangeListener);
        }
    }
    
}
