/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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

package org.mevenide.netbeans.project;

import java.awt.Dialog;
import javax.swing.JButton;
import org.mevenide.netbeans.project.customizer.MavenCustomizer;
import org.netbeans.spi.project.ui.CustomizerProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class CustomizerProviderImpl implements CustomizerProvider {
    private static final String COMMAND_APPLY = "APPLY";    // NOI18N
    private static final String COMMAND_OK = "OK";          // NOI18N
    private static final String COMMAND_CANCEL = "CANCEL";  // NOI18N
    
    private MavenProject project;
    /** Creates a new instance of CustomizerProviderImpl */
    public CustomizerProviderImpl(MavenProject projectToCustomize) {
        project = projectToCustomize;
    }
    
    public void showCustomizer() {
        // Create options
        JButton options[] = new JButton[] {
            new JButton("Apply"),
            new JButton("OK"),
            new JButton("Cancel")
        };
        
        // Set commands - doesn't do anything, add listener and processing later
        options[0].setActionCommand(COMMAND_APPLY);
        options[1].setActionCommand(COMMAND_OK);
        options[2].setActionCommand(COMMAND_CANCEL);
        
        DialogDescriptor dialogDescriptor = new DialogDescriptor(
            new MavenCustomizer( project ),
            project.getDisplayName(),
            true, // is modal for now
            options,
            options[1],                     // initial value
            DialogDescriptor.BOTTOM_ALIGN,
            null,
            null);
        
        dialogDescriptor.setClosingOptions(new Object[] {options[1], options[2] });
        Dialog dialog = DialogDisplayer.getDefault().createDialog( dialogDescriptor );
        dialog.show();
    }
    
}
