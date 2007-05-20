/* ==========================================================================
 * Copyright 2005-2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.options;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JComponent;
import org.apache.maven.settings.Settings;
import org.codehaus.mevenide.netbeans.embedder.MavenSettingsSingleton;
import org.codehaus.mevenide.netbeans.embedder.writer.WriterUtils;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 * controller for maven2 settings in the options dialog.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
class MavenOptionController extends OptionsPanelController {
    private SettingsPanel panel;
    private Settings setts;
    /**
     * Creates a new instance of MavenOptionController
     */
    MavenOptionController() {
    }
    
    public void update() {
        if (setts == null) {
            setts = MavenSettingsSingleton.getInstance().createUserSettingsModel();
        }
        getPanel().setValues(setts);
    }
    
    public void applyChanges() {
        if (setts == null) {
            setts = MavenSettingsSingleton.getInstance().createUserSettingsModel();
        }
        getPanel().applyValues(setts);
        try {
            File userDir = MavenSettingsSingleton.getInstance().getM2UserDir();
            WriterUtils.writeSettingsModel(FileUtil.toFileObject(userDir), setts);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void cancel() {
        setts = null;
    }
    
    public boolean isValid() {
        return getPanel().hasValidValues();
    }
    
    public boolean isChanged() {
        return getPanel().hasChangedValues();
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
