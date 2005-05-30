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
package org.mevenide.idea.global;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.mevenide.idea.util.components.AbstractApplicationComponent;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.images.Icons;
import org.mevenide.idea.util.FileUtils;

import javax.swing.Icon;
import javax.swing.JComponent;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * This component manages UI configuration for the {@link MavenManager} component. It displays a window
 * allowing the user to modify the Maven manager settings, and either discards them or applies them based on
 * user actions.
 *
 * @author Arik
 */
public class MavenManagerConfigurable extends AbstractApplicationComponent implements Configurable {

    /**
     * The user interface component to display to the user.
     */
    private MavenManagerPanel ui;

    public void initComponent() {
        ui = new MavenManagerPanel(true);
    }

    public void disposeComponent() {
        ui = null;
    }

    public Icon getIcon() {
        return Icons.MAVEN_SETTINGS;
    }

    public String getHelpTopic() {
        return null;
    }

    public String getDisplayName() {
        return RES.get("configurable.display.name");
    }

    public void apply() throws ConfigurationException {
        try {
            final MavenManager mavenMgr = MavenManager.getInstance();
            mavenMgr.setMavenHome(ui.getMavenHome());
            mavenMgr.setMavenOptions(ui.getMavenOptions());
            mavenMgr.setOffline(ui.isOffline());
        }
        catch (FileNotFoundException e) {
            UIUtils.showError(e);
            LOG.trace(e.getMessage(), e);
        }
    }

    public JComponent createComponent() {
        return ui;
    }

    public void disposeUIResources() {
    }

    public boolean isModified() {
        final MavenManager mavenMgr = MavenManager.getInstance();

        final File mavenHome = mavenMgr.getMavenHome();
        final String mavenOptions = mavenMgr.getMavenOptions();
        final boolean offlineMode = mavenMgr.isOffline();

        final File selectedHome = ui.getMavenHome();
        final String selectedOptions = ui.getMavenOptions();
        final boolean selectedOfflineMode = ui.isOffline();

        final boolean offlineModeModified = selectedOfflineMode != offlineMode;
        final boolean jvmOptionsModified = !StringUtils.equals(mavenOptions, selectedOptions);
        final boolean homeModified = FileUtils.equals(selectedHome, mavenHome);

        return homeModified || offlineModeModified || jvmOptionsModified;
    }

    public void reset() {
        ui.readOptions(MavenManager.getInstance());
    }
}
