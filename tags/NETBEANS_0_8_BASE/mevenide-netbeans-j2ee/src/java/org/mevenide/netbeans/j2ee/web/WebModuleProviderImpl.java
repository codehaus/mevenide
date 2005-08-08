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

import org.mevenide.netbeans.project.MavenProject;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleFactory;
import org.netbeans.modules.web.spi.webmodule.WebModuleProvider;
import org.openide.filesystems.FileObject;


/**
 * web module provider implementation for maven project type.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class WebModuleProviderImpl implements WebModuleProvider {
    
    private MavenProject project;
    private WebModuleImpl implementation;
    public WebModuleProviderImpl(MavenProject proj, WebModuleImpl impl) {
        project = proj;
        implementation = impl;
    }
    
    public WebModuleImpl getWebImpl() {
        return implementation;
    }

    public WebModule findWebModule(FileObject fileObject) {
        // assuming here that we get asked only if the 
        if (implementation != null && implementation.isValid()) {
            return WebModuleFactory.createWebModule(implementation);
        }
        return null;
    }


}
