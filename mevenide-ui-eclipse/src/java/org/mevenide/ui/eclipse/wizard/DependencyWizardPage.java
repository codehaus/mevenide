/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
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

package org.mevenide.ui.eclipse.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * A base class for all wizard pages that manipulate a Dependency.
 */
public abstract class DependencyWizardPage extends WizardPage {

    public DependencyWizardPage() {
        super(Mevenide.getResourceString("NewDependencyWizardPage.page.pageName")); //$NON-NLS-1$
        setTitle(Mevenide.getResourceString("NewDependencyWizardPage.page.title")); //$NON-NLS-1$
        setDescription(Mevenide.getResourceString("NewDependencyWizardPage.page.description")); //$NON-NLS-1$
    }

    /**
     * @return the groupid
     */
    public String getGroupId() {
        return "";
    }

    /**
     * @return the type
     */
    public String getType() {
        return "";
    }

    /**
     * @return the artifactId
     */
    public String getArtifactId() {
        return "";
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return "";
    }

    /**
     * @return the jar
     */
    public String getJar() {
        return "";
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return "";
    }

}