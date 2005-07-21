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

import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleFactory;
import org.netbeans.modules.web.spi.webmodule.WebModuleImplementation;
import org.netbeans.modules.web.spi.webmodule.WebModuleProvider;
import org.openide.filesystems.FileObject;
import org.mevenide.netbeans.project.*;


/**
 * web module provider implementation for maven project type.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class WebModuleProviderImpl implements WebModuleProvider {
    
    private MavenProject project;
    public WebModuleProviderImpl(MavenProject proj) {
        project = proj;
    }

    public WebModule findWebModule(FileObject fileObject) {
        // assuming here that we get asked only if the 
        WebModuleImpl impl = (WebModuleImpl)project.getLookup().lookup(WebModuleImpl.class);
        if (impl != null && impl.isValid()) {
            return WebModuleFactory.createWebModule(impl);
        }
        return null;
    }


}
