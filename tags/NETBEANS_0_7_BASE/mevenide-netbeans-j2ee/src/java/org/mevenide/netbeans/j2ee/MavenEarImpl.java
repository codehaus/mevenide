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

package org.mevenide.netbeans.j2ee;

import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.MavenSourcesImpl;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.api.common.J2eeProjectConstants;
import org.netbeans.modules.j2ee.api.ejbjar.EjbJar;
import org.netbeans.modules.j2ee.spi.ejbjar.EarImplementation;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.filesystems.FileObject;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class MavenEarImpl implements EarImplementation {
    
    private MavenProject project;
    /** Creates a new instance of MavenEjbImp */
    public MavenEarImpl(MavenProject proj) {
        project = proj;
    }

    public void addEjbJarModule(EjbJar module) {
        throw new IllegalStateException("Not implemented yet for Maven projects");
    }

    public void addWebModule(WebModule module) {
        throw new IllegalStateException("Not implemented yet for Maven projects");
    }

    public FileObject getDeploymentDescriptor() {
        FileObject fo = FileUtilities.getFileObjectForProperty("maven.ear.appxml", project.getPropertyResolver()); //NOI18N
        if (fo != null) {
            return fo;
        }
        fo = getMetaInf();
        if (fo != null) {
            fo = fo.getFileObject("application.xml");
            return fo;
        }
        return null;
    }

    public String getJ2eePlatformVersion() {
        // hardwire?
        return J2eeProjectConstants.J2EE_13_LEVEL;
//        J2eeProjectConstants.J2EE_14_LEVEL;        
    }

    public FileObject getMetaInf() {
        FileObject fo = FileUtilities.getFileObjectForProperty("maven.ear.src", project.getPropertyResolver()); //NOI18N
        if (fo != null) {
            FileObject toRet = fo.getFileObject("META-INF"); //NOI18N
            if (toRet != null) {
                return toRet;
            }
        }
        Sources srcs = project.getSources();
        SourceGroup[] grps = srcs.getSourceGroups(MavenSourcesImpl.TYPE_RESOURCES);
        if (grps != null) {
            for (int i = 0; i < grps.length; i++) {
                fo = grps[0].getRootFolder().getFileObject("META-INF");
                if (fo != null) {
                    return fo;
                }
            }
        }
        return null;
                
    }
    
    public boolean isValid() {
        return getDeploymentDescriptor() != null;
    }
}
