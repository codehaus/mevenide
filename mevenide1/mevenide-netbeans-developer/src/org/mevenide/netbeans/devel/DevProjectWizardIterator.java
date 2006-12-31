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
package org.mevenide.netbeans.devel;

import org.mevenide.netbeans.project.wizards.CreateProjectPanel;
import org.mevenide.netbeans.project.wizards.MavenNewWizardIterator;
import org.openide.WizardDescriptor;


/**
 * Wizard for creating maven projects.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class DevProjectWizardIterator extends MavenNewWizardIterator {

    private static final long serialVersionUID = 13334234343323432L;
    
    /** Create a new wizard iterator. */
    public DevProjectWizardIterator() {
    }
    
    protected WizardDescriptor.Panel[] createPanels () {
        return new WizardDescriptor.Panel[] {
                new CreateProjectPanel(false),
                new GenerateCodePanel()
            };
    }
    
    protected String[] createSteps() {
            return new String[] {
                "Setup Maven Project",
                "Generate Code"
            };
    }
    
}
