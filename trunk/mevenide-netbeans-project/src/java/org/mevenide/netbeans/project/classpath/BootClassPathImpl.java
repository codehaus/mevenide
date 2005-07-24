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

package org.mevenide.netbeans.project.classpath;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.mevenide.netbeans.project.MavenProject;
import org.netbeans.spi.java.classpath.ClassPathImplementation;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;

import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.classpath.ClassPath;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
final class BootClassPathImpl implements ClassPathImplementation {

    private MavenProject project;
    private List resourcesCache;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    
    private String lastNonDefault = null;
    private String lastNonDefaultPlatform = null;

    public BootClassPathImpl (MavenProject proj) {
        project = proj;
        project.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String customCompile = project.getPropertyResolver().getResolvedValue("maven.compile.executable");
                if (customCompile != null && !customCompile.equals(lastNonDefault) 
                 || customCompile == null && lastNonDefault != null) {
                    List oldValue = resourcesCache;
                    resourcesCache = null;
                    List newValue = getResources();
                    support.firePropertyChange(ClassPathImplementation.PROP_RESOURCES, oldValue, newValue);
                }
                if (lastNonDefaultPlatform != null) {
                    JavaPlatform[] pms = JavaPlatformManager.getDefault().getPlatforms(lastNonDefaultPlatform, null);
                    // the platform in question was removed..
                    if (pms.length == 0) {
                        List oldValue = resourcesCache;
                        resourcesCache = null;
                        List newValue = getResources();
                        support.firePropertyChange(ClassPathImplementation.PROP_RESOURCES, oldValue, newValue);
                    }
                }
            }
        });
        
    }

    public synchronized java.util.List getResources() {
        if (this.resourcesCache == null) {
            JavaPlatform jp = findActivePlatform ();
            if (jp != null) {
                //TODO May also listen on CP, but from Platform it should be fixed.
                ClassPath cp = jp.getBootstrapLibraries();
                List entries = cp.entries();
                ArrayList result = new ArrayList (entries.size());
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
        JavaPlatformManager pm = JavaPlatformManager.getDefault();
        String customCompile = project.getPropertyResolver().getResolvedValue("maven.compile.executable");
        if (customCompile != null) {
            FileObject toolFO = FileUtil.toFileObject(new File(customCompile));
            if (toolFO != null) {
                String toolname = toolFO.getNameExt();
                JavaPlatform[] platforms = pm.getInstalledPlatforms();
                for (int i = 0; i < platforms.length; i++) {
                    FileObject fo = platforms[i].findTool(toolname);
                    if (fo != null && fo.equals(toolFO)) {
                        lastNonDefault = customCompile;
                        lastNonDefaultPlatform = platforms[i].getDisplayName();
                        return platforms[i];
                    }
                }
                // not found, platform not defined.
            }
        }
        lastNonDefault = null;
        lastNonDefaultPlatform = null;
        //Invalid platform ID or default platform
        return pm.getDefaultPlatform();
    }
    
}
