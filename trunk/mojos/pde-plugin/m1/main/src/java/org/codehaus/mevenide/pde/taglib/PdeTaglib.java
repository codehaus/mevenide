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
package org.codehaus.mevenide.pde.taglib;

import org.apache.commons.jelly.TagLibrary;
import org.codehaus.mevenide.pde.artifact.PdeArtifactNameTag;
import org.codehaus.mevenide.pde.artifact.PdeEnabledTag;
import org.codehaus.mevenide.pde.artifact.PdeFeatureTag;
import org.codehaus.mevenide.pde.artifact.PdeInstallParametersTag;
import org.codehaus.mevenide.pde.artifact.PdeSiteTag;
import org.codehaus.mevenide.pde.artifact.PdeTypeTag;
import org.codehaus.mevenide.pde.classpath.PdeClasspathTag;
import org.codehaus.mevenide.pde.plugin.PdePluginTag;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PdeTaglib extends TagLibrary {
    public PdeTaglib() {
		//simple utility tags
		registerTag("enabled", PdeEnabledTag.class);
		registerTag("name", PdeArtifactNameTag.class);
		registerTag("type", PdeTypeTag.class);
		
		//artifact:install parameters
		registerTag("install-parameters", PdeInstallParametersTag.class);
		
		//core tags
		registerTag("plugin", PdePluginTag.class);
		registerTag("feature", PdeFeatureTag.class);
		registerTag("site", PdeSiteTag.class);
		
		//classpath resolver 
        registerTag("classpath", PdeClasspathTag.class);
    }
}

