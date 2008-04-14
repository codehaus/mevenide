/*
 * Copyright 2008 Mevenide Team
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.codehaus.mevenide.netbeans.configurations;

import javax.swing.JComponent;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.customizer.ModelHandle;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author mkleint
 */
public class ConfigurationsPanelProvider implements ProjectCustomizer.CompositeCategoryProvider {

    public ProjectCustomizer.Category createCategory(Lookup context) {
        return ProjectCustomizer.Category.create(
                ModelHandle.PANEL_MAPPING, 
                NbBundle.getMessage(ConfigurationsPanelProvider.class, "TIT_Configurations"), 
                null,
                (ProjectCustomizer.Category[])null);
    }
    
    public JComponent createComponent(ProjectCustomizer.Category category, Lookup context) {
        ModelHandle handle = context.lookup(ModelHandle.class);
        NbMavenProject project = context.lookup(NbMavenProject.class);
        return new ConfigurationsPanel(handle, project);
    }

}
