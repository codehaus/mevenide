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

package org.codehaus.mevenide.netbeans.spi.archetype;

import java.util.Set;
import org.codehaus.mevenide.netbeans.api.archetype.Archetype;
import org.netbeans.api.project.Project;
import org.openide.WizardDescriptor;

/**
 *
 * @author mkleint
 */
public interface NewProjectWizardExtender {

    /**
     * either null or a panel that ought to be shown as the last panel.
     */ 
    WizardDescriptor.FinishablePanel createPanel(Archetype selectedArchetype);
    
    Set instantiate(Project project, WizardDescriptor descriptor);
}
