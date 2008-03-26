/*
 *  Copyright 2008 mkleint.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.codehaus.mevenide.netbeans.classpath;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.Resource;
import org.codehaus.mevenide.netbeans.FileUtilities;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.netbeans.spi.java.classpath.ClassPathImplementation;
import org.netbeans.spi.java.classpath.FilteringPathResourceImplementation;
import org.netbeans.spi.java.classpath.support.PathResourceBase;
import org.netbeans.spi.project.support.ant.PathMatcher;
import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;
import org.openide.util.WeakListeners;

/**
 *
 * @author mkleint
 */
public class ExcludingResourceImpl extends PathResourceBase 
       implements FilteringPathResourceImplementation, PropertyChangeListener {

    private NbMavenProject project;
    private URL[] cachedRoots;
    private HashMap<URL, PathMatcher> matchers;
    private boolean test;

    //for tests only..
    protected ExcludingResourceImpl(boolean test) {
        this.test = test;
        matchers = new HashMap<URL, PathMatcher>();
    }
    
    public ExcludingResourceImpl(NbMavenProject project, boolean test) {
        this(test);
        this.project = project;
        ProjectURLWatcher watch = project.getLookup().lookup(ProjectURLWatcher.class);
        watch.addPropertyChangeListener(WeakListeners.propertyChange(this, watch));
    }
    
    public synchronized URL[] getRoots() {
        if (cachedRoots != null) {
            return cachedRoots;
        }
        URL[] urls = calculateRoots();
        cachedRoots = urls;
        return urls;
    }

    public ClassPathImplementation getContent() {
        return null;
    }

    public synchronized boolean includes(URL root, String resource) {
        PathMatcher match = matchers.get(root);
        assert match != null : "No PathMatcher for " + root;
        return match.matches(resource, true);
    }
    
    //protected for tests usage
    protected List<Resource> getResources(boolean istest) {
        return istest ? project.getOriginalMavenProject().getTestResources() : 
                          project.getOriginalMavenProject().getResources();
    }
    
    //protected for tests usage
    protected File getBase() {
        return FileUtil.toFile(project.getProjectDirectory());
    }

    private URL[] calculateRoots() {
        assert Thread.holdsLock(this);
        List<URL> newurls = new ArrayList<URL>();
        Map<URL, String> includes = new HashMap<URL, String>();
        Map<URL, String> excludes = new HashMap<URL, String>();
        List<Resource> lst = getResources(test);
        for (Resource res : lst) {
            URI uri = FileUtilities.getDirURI(getBase(), res.getDirectory());
            try {
                URL entry;
                //TODO what are all the extensions that get into classpath??
                // resources should be primarily non-jar anyway..
                if (uri.toString().toLowerCase().endsWith(".jar")  //NOI18N
                 || uri.toString().toLowerCase().endsWith(".ejb3")) {//NOI18N
                    entry = FileUtil.getArchiveRoot(uri.toURL());
                } else {
                    entry = uri.toURL();
                    if  (!entry.toExternalForm().endsWith("/")) { //NOI18N
                        entry = new URL(entry.toExternalForm() + "/"); //NOI18N
                    }
                }
                if (entry != null) {
                    if (!newurls.contains(entry)) {
                        newurls.add(entry);
                    }
                    processInEx(includes, entry, res.getIncludes());
                    processInEx(excludes, entry, res.getExcludes());
                }
            } catch (MalformedURLException mue) {
                ErrorManager.getDefault().notify(mue);
            }
        }
        matchers.clear();
        for (URL u : newurls) {
            String in = includes.get(u);
            String ex = excludes.get(u);
            matchers.put(u, new PathMatcher(in, ex, new File(u.toExternalForm())));
        }
        cachedRoots = newurls.toArray(new URL[0]);
        return cachedRoots;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (NbMavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
            //TODO optimize somehow? it's just too much work to figure if something changed..
             firePropertyChange(PROP_ROOTS, null, null);
//             super.firePropertyChange(this.PROP_INCLUDES, null, null);
        }
    }

    private void processInEx(Map<URL, String> cludes, URL entry, List res) {
        String clude = cludes.get(entry);
        if (clude == null) {
            clude = "";
        } else {
            clude = clude + ","; // PathMatcher assumes this as delimiter
        }
        if (res != null && res.size() > 0) {
            for (Object incl : res) {
                clude = clude + incl + ",";
            }
            if (clude.endsWith(",")) {
                clude.substring(0, clude.length() - 1);
            }
        } else {
//            clude = clude + "**";
        }
        if (clude.length() == 0) {
            clude = null;
        }
        cludes.put(entry, clude);
    }

}
