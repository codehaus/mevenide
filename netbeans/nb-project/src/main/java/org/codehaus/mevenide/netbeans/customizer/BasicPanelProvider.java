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

package org.codehaus.mevenide.netbeans.customizer;
import org.codehaus.mevenide.netbeans.api.customizer.ModelHandle;
import javax.swing.JComponent;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class BasicPanelProvider implements ProjectCustomizer.CompositeCategoryProvider {
    
    /** Creates a new instance of BasicPanelProvider */
    public BasicPanelProvider() {
    }
    
    public Category createCategory(Lookup context) {
        return ProjectCustomizer.Category.create(
                ModelHandle.PANEL_BASIC, 
                NbBundle.getMessage(BasicPanelProvider.class, "TIT_Basic"), 
                null,
                (ProjectCustomizer.Category[])null);
    }
    
    public JComponent createComponent(Category category, Lookup context) {
        ModelHandle handle = context.lookup(ModelHandle.class);
        return new BasicInfoPanel(handle);
    }
    
}
