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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.mevenide.ui.eclipse.Mevenide;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ManageActionDefinitionsAction extends Action {
    
    public ManageActionDefinitionsAction() {
        setText("Manage Configuration...");
        setToolTipText("Manage Configuration");
    }
    
    
    public void run() {
        openDialog();
    }
    
    
    private void openDialog() {
        Dialog d = new CustomLaunchConfigurationDialog(Mevenide.getInstance().getWorkbenchWindow().getShell());
        d.open();    
    }
}
