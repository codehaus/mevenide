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

package org.mevenide.netbeans.cargo;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class AddContainerAction extends AbstractAction {
    
    /** Creates a new instance of AddContainerAction */
    public AddContainerAction() {
        putValue(Action.NAME, "Add Container");
        putValue(Action.SHORT_DESCRIPTION, "Add Cargo Container");
    }

    public void actionPerformed(ActionEvent actionEvent) {
        ContainerPanel panel = new ContainerPanel();
        DialogDescriptor dd = new DialogDescriptor(panel, "Add Container");
        Object ret = DialogDisplayer.getDefault().notify(dd);
        if (ret == DialogDescriptor.OK_OPTION) {
            panel.createContainer();
        }
    }
    
}
