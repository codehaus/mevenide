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

import org.apache.maven.util.DownloadMeter;
import org.openide.awt.StatusDisplayer;

/**
 * DownloadMeter that puts events into status bar. To be replaced when netbeans 
 * gets a proper progress bar.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
class StatusBarDownloadMeter implements DownloadMeter {
        private String path;
        public StatusBarDownloadMeter(String path) {
            this.path = path;
        }
        public void finish(final int total) {
            StatusDisplayer.getDefault().setStatusText(path + " downloaded.");
        }

        public void update(final int param, final int param1) {
            StatusDisplayer.getDefault().setStatusText(path + " downloaded " + param + " of " + param1);
        }
        
    }    