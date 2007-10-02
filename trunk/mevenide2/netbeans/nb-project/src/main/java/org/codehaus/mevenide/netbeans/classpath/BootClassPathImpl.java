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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.Constants;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.netbeans.spi.java.classpath.ClassPathImplementation;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;

import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.platform.Specification;
import org.netbeans.spi.java.classpath.PathResourceImplementation;
import org.openide.util.WeakListeners;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public final class BootClassPathImpl implements ClassPathImplementation, PropertyChangeListener {

    private List<? extends PathResourceImplementation> resourcesCache;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    private NbMavenProject project;
    private String lastHintValue = null;
    private boolean activePlatformValid = true;
    private JavaPlatformManager platformManager;
    
//    private String lastNonDefault = null;
//    private String lastNonDefaultPlatform = null;


    BootClassPathImpl(NbMavenProject project) {
        this.project = project;
    }

    public synchronized List<? extends PathResourceImplementation> getResources() {
        if (this.resourcesCache == null) {
            JavaPlatform jp = findActivePlatform ();
            if (jp != null) {
                //TODO May also listen on CP, but from Platform it should be fixed.
                ClassPath cp = jp.getBootstrapLibraries();
                List entries = cp.entries();
                ArrayList<PathResourceImplementation> result = new ArrayList<PathResourceImplementation> (entries.size());
                for (Iterator it = entries.iterator(); it.hasNext();) {
                    ClassPath.Entry entry = (ClassPath.Entry) it.next();
                    result.add (ClassPathSupport.createResource(entry.getURL()));
                }
                resourcesCache = Collections.unmodifiableList (result);
            }
        }
        return this.resourcesCache;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener (listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener (listener);
    }

    private JavaPlatform findActivePlatform () {
        activePlatformValid = true;
        if (platformManager == null) {
            platformManager = JavaPlatformManager.getDefault();
            platformManager.addPropertyChangeListener(WeakListeners.propertyChange(this, platformManager));
            ProjectURLWatcher watch = project.getLookup().lookup(ProjectURLWatcher.class);
            watch.addPropertyChangeListener(this);
            
        }                
        String val = project.getOriginalMavenProject().getProperties().getProperty(Constants.HINT_JDK_PLATFORM);
        lastHintValue = val;
        JavaPlatform plat = getActivePlatform(val);
        if (plat == null) {
            //TODO report how?
            plat = platformManager.getDefaultPlatform();
            activePlatformValid = false;
        }
        //TODO how to find custom compiler in m2?
        //Invalid platform ID or default platform
        return plat;
    }
    
    /**
     * Returns the active platform used by the project or null if the active
     * project platform is broken.
     * @param activePlatformId the name of platform used by Ant script or null
     * for default platform.
     * @return active {@link JavaPlatform} or null if the project's platform
     * is broken
     */
    public static JavaPlatform getActivePlatform (final String activePlatformId) {
        final JavaPlatformManager pm = JavaPlatformManager.getDefault();
        if (activePlatformId == null) {
            return pm.getDefaultPlatform();
        }
        else {
            JavaPlatform[] installedPlatforms = pm.getPlatforms(null, new Specification ("j2se",null));   //NOI18N
            for (int i=0; i<installedPlatforms.length; i++) {
                String antName = (String) installedPlatforms[i].getProperties().get("platform.ant.name");        //NOI18N
                if (antName != null && antName.equals(activePlatformId)) {
                    return installedPlatforms[i];
                }
            }
            return null;
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String newVal = project.getOriginalMavenProject().getProperties().getProperty(Constants.HINT_JDK_PLATFORM);
        if (evt.getSource() == project && evt.getPropertyName().equals(NbMavenProject.PROP_PROJECT)) {
            //Active platform was changed
            if ( (newVal == null && lastHintValue != null) || (newVal != null && !newVal.equals(lastHintValue))) {
                resetCache ();
            }
        }
        else if (evt.getSource() == platformManager && 
                JavaPlatformManager.PROP_INSTALLED_PLATFORMS.equals(evt.getPropertyName()) 
                && lastHintValue != null) {
            lastHintValue = newVal;
            //Platform definitions were changed, check if the platform was not resolved or deleted
            if (activePlatformValid) {
                if (getActivePlatform (lastHintValue) == null) {
                    //the platform was removed
                    resetCache();
                }
            }
            else {
                if (getActivePlatform (lastHintValue) != null) {
                    //platform was added
                    resetCache();
                }
            }
        }
    }
    
    /**
     * Resets the cache and firesPropertyChange
     */
    private void resetCache () {
        synchronized (this) {
            resourcesCache = null;
        }
        support.firePropertyChange(PROP_RESOURCES, null, null);
    }
    
    
}
