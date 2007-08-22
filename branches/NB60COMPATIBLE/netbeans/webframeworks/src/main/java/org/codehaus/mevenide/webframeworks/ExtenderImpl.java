/* ==========================================================================
 * Copyright 2007 Mevenide Team
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

package org.codehaus.mevenide.webframeworks;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.codehaus.mevenide.netbeans.api.archetype.Archetype;
import org.codehaus.mevenide.netbeans.spi.archetype.NewProjectWizardExtender;
import org.netbeans.api.project.Project;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebFrameworkProvider;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.FinishablePanel;

/**
 *
 * @author mkleint
 */
public class ExtenderImpl implements NewProjectWizardExtender {

    public FinishablePanel createPanel(Archetype selectedArchetype) {
        if (selectedArchetype != null) {
            if ("org.apache.maven.archetypes".equals(selectedArchetype.getGroupId()) && 
                "maven-archetype-webapp".equals(selectedArchetype.getArtifactId())) {
                return new PanelSupportedFrameworks();
            }
        }
        return null;
    }

    public Set instantiate(Project project, WizardDescriptor descriptor) {
        List lst = (List)descriptor.getProperty(WizardProperties.FRAMEWORKS);
        Set files = new HashSet();
        for (Object wfp : lst) {
            WebFrameworkProvider prov = (WebFrameworkProvider)wfp;
            files.addAll(prov.extend(WebModule.getWebModule(project.getProjectDirectory())));
        }
        return files;
    }

}
