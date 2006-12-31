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

package org.mevenide.netbeans.api.customizer;

import javax.swing.JComponent;
import org.mevenide.netbeans.api.project.MavenProject;
import org.mevenide.plugins.IPluginInfo;

/**
 * Interface that allows to put custom UI for some maven plugins in the
 * project's customizer dialog.  
 * Implementations should be registered in default lookup.
 * (Using META-INF/services/CustomPluginPanelProvider file in the module's jar.)
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public interface CustomPluginPanelProvider {
    /**
     * factory method for creating custom UI for a plugin designated by parameter plugin.
     * If implementation doesn't provide one, return null.
     * @return a JComponent that is an instance of ProjectPanel at the same time.
     * @param project the project that the panel is being created for.
     * @param plugin the information about the plugin that is used by the project and needs visualization
     */
    JComponent createPanel(MavenProject project, IPluginInfo plugin);
    
}
