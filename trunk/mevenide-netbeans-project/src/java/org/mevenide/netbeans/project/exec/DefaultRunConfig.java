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

package org.mevenide.netbeans.project.exec;

import org.mevenide.netbeans.project.MavenSettings;

/**
 * default impl of RunConfig, delegates to MavenSettings for command line switches.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class DefaultRunConfig implements RunConfig {
    
    /** Creates a new instance of DefaultRunConfig */
    public DefaultRunConfig() {
    }

    public String getMavenHome() {
        return null;
    }

    public String getMavenLocalHome() {
        return null;
    }

    public boolean isDebug() {
        return MavenSettings.getDefault().isDebug();
    }

    public boolean isExceptions() {
        return MavenSettings.getDefault().isExceptions();
    }

    public boolean isNoBanner() {
        return MavenSettings.getDefault().isNoBanner();
    }

    public boolean isNonverbose() {
        return MavenSettings.getDefault().isNonverbose();
    }

    public boolean isOffline() {
        return MavenSettings.getDefault().isOffline();
    }
    
}
