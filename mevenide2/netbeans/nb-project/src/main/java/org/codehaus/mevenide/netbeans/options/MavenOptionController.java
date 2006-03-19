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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.SettingsUtils;
import org.apache.maven.settings.io.xpp3.SettingsXpp3Writer;
import org.codehaus.mevenide.netbeans.embedder.MavenSettingsSingleton;
import org.codehaus.plexus.util.IOUtil;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
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
        SettingsXpp3Writer swriter = new SettingsXpp3Writer();
        Writer wr = null;
        FileLock lock = null;
        try {
            File userDir = MavenSettingsSingleton.getInstance().getM2UserDir();
            File settingsFile = new File(userDir, "settings.xml");
            if (!settingsFile.exists()) {
                settingsFile.getParentFile().mkdirs();
                settingsFile.createNewFile();
            }
            FileObject fo = FileUtil.toFileObject(settingsFile);
            OutputStream str;
            if (fo != null) {
                lock = fo.lock();
                str = fo.getOutputStream(lock);
            } else {
                str = new FileOutputStream(settingsFile);
            }
            wr = new OutputStreamWriter(str);
            swriter.write(wr, setts);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            IOUtil.close(wr);
            if (lock != null) {
                lock.releaseLock();
            }
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
