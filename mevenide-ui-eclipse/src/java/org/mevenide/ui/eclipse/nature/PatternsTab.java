/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.ui.eclipse.nature;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PatternsTab extends AbstractLaunchConfigurationTab {

    public PatternsTab() {
        super();
    }

    public void createControl(Composite parent) {
        setControl(new Composite(parent, SWT.NULL));
    }

    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        // TODO Auto-generated method stub
    }

    public void initializeFrom(ILaunchConfiguration configuration) {
        // TODO Auto-generated method stub
    }

    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        // TODO Auto-generated method stub
    }

    public String getName() {
        return "Patterns";
    }
    
    
    /**
     * disallow user to run the config
     */
    public boolean isValid(ILaunchConfiguration launchConfig) {
        return false;
    }
}
