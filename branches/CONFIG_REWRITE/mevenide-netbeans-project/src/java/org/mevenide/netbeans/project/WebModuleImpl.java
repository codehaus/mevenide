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

package org.mevenide.netbeans.project;

import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleImplementation;
import org.openide.filesystems.FileObject;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class WebModuleImpl implements WebModuleImplementation {
    private MavenProject project;
    public WebModuleImpl(MavenProject proj) {
        System.out.println("WebModuleImpl - constructor");
        project = proj;
    }

    public FileObject getWebInf() {
        System.out.println("getwebinf");
        FileObject fo = FileUtilities.getFileObjectForProperty("maven.war.src", project.getPropertyResolver()); //NOI18N
        if (fo != null) {
            return fo.getFileObject("WEB-INF"); //NOI18N
        }
        return fo;
    }

    public String getJ2eePlatformVersion() {
        System.out.println("getJ2eePlatformVersion");
        return WebModule.J2EE_13_LEVEL;
    }

    public FileObject getDocumentBase() {
        System.out.println("getDocumentBase" + FileUtilities.getFileObjectForProperty("maven.war.src", project.getPropertyResolver()));
        return FileUtilities.getFileObjectForProperty("maven.war.src", project.getPropertyResolver()); //NOI18N
    }

    public FileObject getDeploymentDescriptor() {
        System.out.println("getDeploymentDescriptor");
        return FileUtilities.getFileObjectForProperty("maven.war.webxml", project.getPropertyResolver()); //NOI18N
    }

    public String getContextPath() {
        System.out.println("getContextPath");
        return "";
    }
    

}
