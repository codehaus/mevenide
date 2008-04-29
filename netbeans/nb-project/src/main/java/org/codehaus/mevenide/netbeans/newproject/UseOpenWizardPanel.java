/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.newproject;

import java.awt.Component;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Panel just asking for basic info.
 */
public class UseOpenWizardPanel implements WizardDescriptor.Panel {
    
    private UseOpenPanel component;
    
    /** Creates a new instance of templateWizardPanel */
    public UseOpenWizardPanel() {
    }
    
    public Component getComponent() {
        if (component == null) {
            component = new UseOpenPanel();
            component.setName(NbBundle.getMessage(ChooseWizardPanel.class, "TIT_UseOpenProjectStep"));
        }
        return component;
    }
    
    public HelpCtx getHelp() {
        return new HelpCtx(UseOpenWizardPanel.class);
    }
    
    public boolean isValid() {
        return true;
    }
    
    public final void addChangeListener(ChangeListener l) {}
    public final void removeChangeListener(ChangeListener l) {}
    
    public void readSettings(Object settings) {}
    
    public void storeSettings(Object settings) {}
    
    public boolean isFinishPanel() {
        return true;
    }
    
}
