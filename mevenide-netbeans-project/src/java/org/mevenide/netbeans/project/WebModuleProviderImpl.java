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

package org.mevenide.netbeans.project;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleFactory;
import org.netbeans.modules.web.spi.webmodule.WebModuleImplementation;
import org.netbeans.modules.web.spi.webmodule.WebModuleProvider;
import org.openide.filesystems.FileObject;


/**
 * web module provider implementation for maven project type.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class WebModuleProviderImpl implements WebModuleProvider {
    
    private static final Log logger = LogFactory.getLog(WebModuleProvider.class);
    private MavenProject project;
    public WebModuleProviderImpl(MavenProject proj) {
        project = proj;
    }

    public WebModule findWebModule(FileObject fileObject) {
        // assuming here that we get asked only if the 
        WebModuleImplementation impl = (WebModuleImplementation)project.getLookup().lookup(WebModuleImplementation.class);
        if (impl != null) {
            return WebModuleFactory.createWebModule(impl);
        }
        return null;
    }


}
