/* ==========================================================================
 * Copyright 2005 Mevenide Team
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
import org.codehaus.mevenide.netbeans.*;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;

/**
 * Interface that allows to put additional items into project's customizer and to provide custom
 * panel implementations.
 * Implementations should be registered in default lookup.
 * (Using META-INF/services/AdditionalM2CustomizerPanelProvider file in the module's jar.)
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public interface M2CustomizerPanelProvider {
    public static final String PANEL_RUN = "RUN";
    public static final String PANEL_BASIC = "BASIC";
    /**
     * Provide additional Lookup context for the given maven project.
     * @returns a Lookup instance or null.
     */
    JComponent createPanel(ModelHandle handle, NbMavenProject project, ProjectCustomizer.Category category);
    
    
    /**
     * If the panel returned from createPanel implements this interface, it will be notified before the saving of 
     * of the values, to allow last time checks and modifications.
     */
    public interface Panel {
        void applyChanges();
    }
}
