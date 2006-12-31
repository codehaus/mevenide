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
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.api.common.J2eeProjectConstants;
import org.netbeans.modules.j2ee.api.ejbjar.EjbJar;
import org.netbeans.modules.j2ee.dd.api.application.Application;
import org.netbeans.modules.j2ee.dd.api.application.DDProvider;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.spi.ejbjar.EarImplementation;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

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
        File guessed = guessEarDescriptor(project);
        return guessed == null ? null : FileUtil.toFileObject(guessed);
    }

    public String getJ2eePlatformVersion() {
        DDProvider prov = DDProvider.getDefault();
        FileObject dd = getDeploymentDescriptor();
        if (dd != null) {
            try {
                Application app = prov.getDDRoot(dd);
                String appVersion = app.getVersion().toString();
                return appVersion;
            } catch (IOException exc) {
                ErrorManager.getDefault().notify(exc);
            }
        }
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
     * return a best guess about the location of the ear descriptor
     */
    public static File guessEarDescriptor(MavenProject project) {
        if (generatingAppDescriptor(project)) {
           //generating the file
            // this property works like this in 1.7 and later.. in 1.5 it generates into  maven.ear.appxml
            // but back then the maven.ear.descriptordir prop should not be present
            File fo = FileUtilities.getFileForProperty("maven.ear.descriptordir", project.getPropertyResolver()); //NOI18N
            if (fo != null) {
                return new File(fo, "application.xml");
            }
        }
        // first iterate all possible locations for existing item, if not found return default..
        File fo = FileUtilities.getFileForProperty("maven.ear.appxml", project.getPropertyResolver()); //NOI18N
        if (fo != null && fo.exists()) {
            return fo;
        }
        Sources srcs = (Sources)project.getLookup().lookup(Sources.class);
        SourceGroup[] grps = srcs.getSourceGroups(MavenSourcesImpl.TYPE_RESOURCES);
        if (grps != null) {
            for (int i = 0; i < grps.length; i++) {
                fo = FileUtil.toFile(grps[0].getRootFolder());
                File toRet = new File(fo, J2eeModule.APP_XML); //NOI18N
                if (toRet.exists()) {
                    return toRet;
                }
            }
        }
        // a fall back.. nothing exists, just try the default.
        return  FileUtilities.getFileForProperty("maven.ear.appxml", project.getPropertyResolver()); //NOI18N
    }

     private boolean checkMultiProjectType() {
        String type = project.getPropertyResolver().getResolvedValue("maven.multiproject.type");
        if (type != null) {
            return "ear".equalsIgnoreCase(type.trim());
        }
        return false;
    }
   
    public boolean isValid() {
        return checkMultiProjectType() || getDeploymentDescriptor() != null || generatingAppDescriptor(project);
    }
    
    private static boolean generatingAppDescriptor(MavenProject project) {
        String val = project.getPropertyResolver().getResolvedValue("maven.ear.appxml.generate");
        return Boolean.valueOf(val).equals(Boolean.TRUE) || "yes".equalsIgnoreCase(val);
    }
}
