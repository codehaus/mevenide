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
package org.mevenide.netbeans.grammar;

import java.util.logging.Logger;
import org.mevenide.environment.SysEnvLocationFinder;
import org.openide.modules.ModuleInstall;



/** Manages a module's lifecycle.
 * Remember that an installer is optional and often not needed at all.
 *
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenGrammarModule extends ModuleInstall
{
    private static final Logger LOGGER = Logger.getLogger(MavenGrammarModule.class.getName());
 
    private static final long serialVersionUID = -485754848837354747L;
    
    public void restored()
    {
        // By default, do nothing.
        // Put your startup code here.
        
        SysEnvLocationFinder.setDefaultSysEnvProvider(new NbSysEnvProvider());
    }
    
}
