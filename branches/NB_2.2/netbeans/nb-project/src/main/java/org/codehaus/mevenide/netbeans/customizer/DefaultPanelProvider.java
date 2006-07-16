/* ==========================================================================
 * Copyright 2005-2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.customizer;

import javax.swing.JComponent;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;

/**
 * the default implementation for panels, is a fallback implementation with ordering position of
 * 666, if you want to override the defaults in your impl, just place it to a lower position.
 * @author mkleint
 */
public class DefaultPanelProvider implements M2CustomizerPanelProvider {
    
    /** Creates a new instance of DefaultPanelProvider */
    public DefaultPanelProvider() {
    }

    public JComponent createPanel(ModelHandle handle, NbMavenProject project, ProjectCustomizer.Category category) {
        if (M2CustomizerPanelProvider.PANEL_BASIC.equals(category.getName())) {
            return new BasicInfoPanel(handle, project);
        }
        if (M2CustomizerPanelProvider.PANEL_RUN.equals(category.getName())) {
            if ("jar".equalsIgnoreCase(project.getOriginalMavenProject().getPackaging())) {
                return new RunJarPanel(handle, project);
            }
        }
        return null;
    }
    
}
