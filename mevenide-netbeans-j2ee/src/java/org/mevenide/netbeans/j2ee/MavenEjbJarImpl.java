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

import java.io.IOException;
import java.math.BigDecimal;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.MavenSourcesImpl;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.api.common.J2eeProjectConstants;
import org.netbeans.modules.j2ee.dd.api.ejb.DDProvider;
import org.netbeans.modules.j2ee.dd.api.ejb.EjbJar;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarImplementation;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class MavenEjbJarImpl implements EjbJarImplementation {
    private MavenProject project;
    /** Creates a new instance of MavenEjbJarImpl */
    public MavenEjbJarImpl(MavenProject proj) {
        project = proj;
    }
    
    /**
     */
    public FileObject getDeploymentDescriptor() {
        FileObject fo = getMetaInf();
        if (fo != null) {
            fo = fo.getFileObject("ejb-jar.xml");
            return fo;
        }
        // WHAT to return here?
        return null;
    }
    
    public String getJ2eePlatformVersion() {
        DDProvider prov = DDProvider.getDefault();
        FileObject dd = getDeploymentDescriptor();
        if (dd != null) {
            try {
                EjbJar ejb = prov.getDDRoot(dd);
                String ejbVersion = ejb.getVersion().toString();
                return ejbVersion;
            } catch (IOException exc) {
                ErrorManager.getDefault().notify(exc);
            }
        }
        // hardwire?
        return EjbJar.VERSION_2_0;
    }
    
    public FileObject getMetaInf() {
        FileObject fo = FileUtilities.getFileObjectForProperty("maven.ejb.src", project.getPropertyResolver()); //NOI18N
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
    
    public FileObject[] getJavaSources() {
        Sources srcs = (Sources)project.getLookup().lookup(Sources.class);
        SourceGroup[] grps = srcs.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        FileObject[] toRet = null;
        if (grps != null) {
            toRet = new FileObject[grps.length];
            for (int i = 0; i < grps.length; i++) {
                toRet[i] = grps[i].getRootFolder();
            }
        }
        return toRet;
    }
    
    public boolean isValid() {
        return getDeploymentDescriptor() != null;
    }
    
}
