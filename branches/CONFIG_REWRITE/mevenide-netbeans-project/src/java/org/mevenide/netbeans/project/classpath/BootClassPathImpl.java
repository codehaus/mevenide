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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.mevenide.netbeans.project.MavenProject;
import org.netbeans.spi.java.classpath.ClassPathImplementation;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;

import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.platform.Specification;
import org.netbeans.api.java.classpath.ClassPath;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
final class BootClassPathImpl implements ClassPathImplementation {

    private static final String PLATFORM_ACTIVE = "platform.active";        //NOI18N
    private static final String J2SE = "j2se";                              //NOI18N


    private MavenProject project;
    private List resourcesCache;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    public BootClassPathImpl (MavenProject proj) {
        project = proj;
    }

    public synchronized java.util.List getResources() {
        if (this.resourcesCache == null) {
            JavaPlatform jp = findActivePlatform ();
            if (jp != null) {
                //TODO: May also listen on CP, but from Platform it should be fixed.
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
//        String platformName = this.helper.evaluate (PLATFORM_ACTIVE);
//        if (platformName!=null) {
//            JavaPlatform[] installedPlatforms = pm.getInstalledPlatforms();
//            for (int i = 0; i< installedPlatforms.length; i++) {
//                Specification spec = installedPlatforms[i].getSpecification();
//                String antName = (String) installedPlatforms[i].getProperties().get (ANT_NAME);
//                if (J2SE.equalsIgnoreCase(spec.getName())
//                    && platformName.equals(antName)) {
//                    return installedPlatforms[i];
//                }
//            }
//        }
        //Invalid platform ID or default platform
        return pm.getDefaultPlatform();
    }
}
