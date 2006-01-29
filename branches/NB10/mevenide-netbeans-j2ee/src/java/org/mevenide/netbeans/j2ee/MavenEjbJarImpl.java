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

import java.io.File;
import java.io.IOException;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.api.project.MavenProject;
import org.mevenide.netbeans.project.MavenSourcesImpl;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.dd.api.ejb.DDProvider;
import org.netbeans.modules.j2ee.dd.api.ejb.EjbJar;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarImplementation;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

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
        File fl = guessEjbJarDescriptor(project);
        if (fl != null) {
            return FileUtil.toFileObject(fl);
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
        Sources srcs = (Sources)project.getLookup().lookup(Sources.class);
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
    
    /** 
     * return a best guess about the location of the ejb descriptor
     */
    public static File guessEjbJarDescriptor(MavenProject project) {
        // first iterate all possible locations for existing item, if not found return default..
        File fo = FileUtilities.getFileForProperty("maven.ejb.src", project.getPropertyResolver()); //NOI18N
        if (fo != null && fo.exists()) {
            File toRet = new File(fo, J2eeModule.EJBJAR_XML); //NOI18N
            if (toRet.exists()) {
                return toRet;
            }
        }
        Sources srcs = (Sources)project.getLookup().lookup(Sources.class);
        SourceGroup[] grps = srcs.getSourceGroups(MavenSourcesImpl.TYPE_RESOURCES);
        if (grps != null) {
            for (int i = 0; i < grps.length; i++) {
                fo = FileUtil.toFile(grps[0].getRootFolder());
                File toRet = new File(fo, J2eeModule.EJBJAR_XML); //NOI18N
                if (toRet.exists()) {
                    return toRet;
                }
            }
        }
        // a fall back.. nothing exists, just try the default.
        fo = FileUtilities.getFileForProperty("maven.ejb.src", project.getPropertyResolver()); //NOI18N
        if (fo != null) {
            return new File(fo, J2eeModule.EJBJAR_XML); //NOI18N
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
    
    private boolean checkMultiProjectType() {
        String type = project.getPropertyResolver().getResolvedValue("maven.multiproject.type");
        if (type != null) {
            return "ejb".equalsIgnoreCase(type.trim());
        }
        return false;
    }
    
    public boolean isValid() {
        return checkMultiProjectType() || getDeploymentDescriptor() != null;
    }
    
}
