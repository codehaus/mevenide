/* ==========================================================================
 * Copyright 2006 Mevenide Team
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
package org.codehaus.mevenide.netbeans.embedder;

import java.io.File;
import org.openide.modules.ModuleInstall;

/**
 * Module install that checks if the new 6.5 support is installed.
 * @author mkleint
 */
public class ModInstall extends ModuleInstall {

    
    /** Creates a new instance of ModInstall */
    public ModInstall() {
    }
    
    @Override
    public void validate() throws IllegalStateException {
       super.validate();
       String prop = System.getProperty("netbeans.user");
       if (prop != null) {
           File file = new File(prop);
           File config = new File(file, "config");
           if (config.exists()) {
                File nex = new File(config, "Preferences" + File.separator +
                        "org" + File.separator +
                        "netbeans" + File.separator +
                        "modules" + File.separator +
                        "maven");
                if (nex.exists()) {
                    throw new IllegalStateException("Newer Maven support is installed in NetBeans 6.5 that might clash with this one. Aborting installation.");
                }
           }
       }
   }

}
