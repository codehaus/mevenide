/* ==========================================================================
 * Copyright 2005 Mevenide Team
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

package org.codehaus.mevenide.continuum.options;

import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.codehaus.mevenide.continuum.ContinuumSettings;
import org.netbeans.spi.options.OptionsCategory;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 * controller for contuinuum advanced settings
 * @author Milos Kleint (mkleint@codehaus.org)
 */
class ContinuumOptionController extends OptionsCategory.PanelController {
    private SettingsPanel panel;
    /** Creates a new instance of ContinuumOptionController */
    ContinuumOptionController() {
    }

    public void update() {
        getPanel().setServers(ContinuumSettings.getDefault().getServers());
    }

    public void applyChanges() {
        ContinuumSettings.getDefault().setServers(getPanel().getServers());
    }

    public void cancel() {
        getPanel().setServers(ContinuumSettings.getDefault().getServers());
    }

    public boolean isValid() {
        return true;
    }

    public boolean isChanged() {
        return true;
    }

    public JComponent getComponent(Lookup lookup) {
        return getPanel();
    }

    private SettingsPanel getPanel() {
        if (panel == null) {
            panel = new SettingsPanel();
        }
        return panel;
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    }
    
}
