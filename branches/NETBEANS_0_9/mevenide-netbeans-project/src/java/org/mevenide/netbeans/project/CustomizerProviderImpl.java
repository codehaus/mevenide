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

package org.mevenide.netbeans.project;

import java.util.List;
import javax.swing.JButton;
import org.mevenide.netbeans.api.project.MavenProject;
import org.mevenide.netbeans.project.customizer.MavenCustomizer;
import org.mevenide.netbeans.project.writer.NbProjectWriter;
import org.netbeans.spi.project.ui.CustomizerProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;

/**
 * project customizer provider, shows the project customizer and writes down changes.
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
        MavenCustomizer customizer = new MavenCustomizer(project);
        DialogDescriptor dialogDescriptor = new DialogDescriptor(
            customizer,
            project.getDisplayName(),
            true, // is modal for now
            options,
            options[1],                     // initial value
            DialogDescriptor.BOTTOM_ALIGN,
            null,
            null);
        
        dialogDescriptor.setClosingOptions(new Object[] {options[1], options[2] });
        Object retValue = DialogDisplayer.getDefault().notify(dialogDescriptor);
        if (retValue == options[0] || retValue == options[1]) {
            List changes = customizer.getChanges();
            boolean wasError = false;
            try {
                NbProjectWriter writer = new NbProjectWriter(project);
                writer.applyChanges(changes);
            } catch (Exception exc) {
                ErrorManager.getDefault().notify(ErrorManager.USER, exc);
                wasError = true;
            }
        }
    }
    
    private boolean stringsEqual(String one, String two) {
        if (one == null && two == null) {
            return true;
        }
        if (one == null || two == null) {
            return false;
        }
        return (one.trim().equals(two.trim()));
    }
    
}
