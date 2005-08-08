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

/**
 * Command line switches provider for maven executor
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public interface RunConfig {
    public boolean isOffline();
    public boolean isDebug();
    public boolean isExceptions();
    public boolean isNoBanner();
    public boolean isNonverbose();
    public String getMavenHome();
    public String getMavenLocalHome();
}
