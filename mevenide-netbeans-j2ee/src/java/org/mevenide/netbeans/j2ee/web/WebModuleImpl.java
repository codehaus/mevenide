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

package org.mevenide.netbeans.j2ee.web;

import java.io.File;
import java.io.IOException;
import org.mevenide.properties.IPropertyLocator;
import org.netbeans.modules.j2ee.dd.api.web.DDProvider;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleImplementation;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.mevenide.netbeans.project.*;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.openide.filesystems.FileUtil;


/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class WebModuleImpl implements WebModuleImplementation {
    private MavenProject project;
    public WebModuleImpl(MavenProject proj) {
        project = proj;
    }

    public FileObject getWebInf() {
        FileObject fo = FileUtilities.getFileObjectForProperty("maven.war.src", project.getPropertyResolver()); //NOI18N
        if (fo != null) {
            return fo.getFileObject("WEB-INF"); //NOI18N
        }
        return fo;
    }

    public String getJ2eePlatformVersion() {
        DDProvider prov = DDProvider.getDefault();
        FileObject dd = getDeploymentDescriptor();
        if (dd != null) {
            try {
                WebApp wa = prov.getDDRoot(dd);
                String waVersion = wa.getVersion() ;

                if(WebApp.VERSION_2_4.equals(waVersion)) {
                    return WebModule.J2EE_14_LEVEL;
                }
            } catch (IOException exc) {
                ErrorManager.getDefault().notify(exc);
            }
        }
        return WebModule.J2EE_13_LEVEL;
    }

    public FileObject getDocumentBase() {
        FileObject docbase = FileUtilities.getFileObjectForProperty("maven.war.src", project.getPropertyResolver()); //NOI18N
        return docbase;
    }

    public FileObject getDeploymentDescriptor() {
        return FileUtilities.getFileObjectForProperty("maven.war.webxml", project.getPropertyResolver()); //NOI18N
    }

    public String getContextPath() {
        return project.getPropertyResolver().getResolvedValue("maven.war.final.name");
    }
    
    public boolean isValid() {
        boolean hasWarSrc =  null != FileUtilities.getFileObjectForProperty("maven.war.src", project.getPropertyResolver()); //NOI18N        
        boolean redefinedDescLocation = project.getPropertyLocator().getPropertyLocation("maven.war.webxml") != IPropertyLocator.LOCATION_DEFAULTS;
        boolean hasDescriptor = getDeploymentDescriptor() != null;
        // more or less heuristics to support generated web.xml files.
        return hasDescriptor || redefinedDescLocation || hasWarSrc;
    }
    
    /**
     * return a best guess about the location of the web descriptor
     */
    public static File guessWebDescriptor(MavenProject project) {
        // first iterate all possible locations for existing item, if not found return default..
        File fo = FileUtilities.getFileForProperty("maven.war.webxml", project.getPropertyResolver()); //NOI18N
        if (fo != null && fo.exists()) {
            return fo;
        }
        Sources srcs = project.getSources();
        SourceGroup[] grps = srcs.getSourceGroups(MavenSourcesImpl.TYPE_RESOURCES);
        if (grps != null) {
            for (int i = 0; i < grps.length; i++) {
                fo = FileUtil.toFile(grps[0].getRootFolder());
                File toRet = new File(fo, J2eeModule.WEB_XML); //NOI18N
                if (toRet.exists()) {
                    return toRet;
                }
            }
        }
        // a fall back.. nothing exists, just try the default.
        return  FileUtilities.getFileForProperty("maven.war.webxml", project.getPropertyResolver()); //NOI18N
    }


}
