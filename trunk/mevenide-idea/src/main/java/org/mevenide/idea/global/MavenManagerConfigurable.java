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

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ui.images.Icons;
import org.mevenide.idea.util.ui.UIUtils;

import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * This component manages UI configuration for the {@link MavenManager} component. It displays a window
 * allowing the user to modify the Maven manager settings, and either discards them or applies them based on
 * user actions.
 *
 * @author Arik
 */
public class MavenManagerConfigurable implements ApplicationComponent, Configurable {
    /**
     * Component name.
     */
    private static final String NAME = MavenManagerConfigurable.class.getName();

    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(MavenManagerConfigurable.class);

    /**
     * Resources.
     */
    private static final Res RES = Res.getInstance(MavenManagerConfigurable.class);

    /**
     * The user interface component to display to the user.
     */
    private MavenManagerPanel ui;

    public String getComponentName() {
        return NAME;
    }

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
            MavenManager.getInstance().setMavenHome(ui.getMavenHome());
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
        final File selectedHome = ui.getMavenHome();
        final File mavenHome = MavenManager.getInstance().getMavenHome();

        if (selectedHome == mavenHome)
            return false;

        if (selectedHome == null || mavenHome == null)
            return true;

        return !selectedHome.equals(mavenHome);
    }

    public void reset() {
        ui.setMavenHome(MavenManager.getInstance().getMavenHome());
    }

    /**
     * The user interface panel.
     */
    private class MavenManagerPanel extends JPanel {
        /**
         * The text field for selecting (or browsing) the Maven home.
         */
        private final TextFieldWithBrowseButton mavenHomeField = new TextFieldWithBrowseButton();

        /**
         * Creates an instance.
         */
        public MavenManagerPanel() {
            init();
        }

        /**
         * Creates an instance using (or not using) double buffering.
         *
         * @param isDoubleBuffered whether to use double buffering or not
         */
        public MavenManagerPanel(boolean isDoubleBuffered) {
            super(isDoubleBuffered);
            init();
        }

        /**
         * Initializes the panel by creating the required components and laying them out on the panel.
         */
        private void init() {
            setLayout(new GridBagLayout());
            GridBagConstraints c;

            //Add maven home label
            c = new GridBagConstraints();
            c.insets = new Insets(5, 5, 5, 5);
            c.fill = GridBagConstraints.BOTH;
            add(new JLabel(RES.get("maven.home.label")), c);

            //add maven home field
            mavenHomeField.addBrowseFolderListener(RES.get("choose.maven.home"),
                                                   RES.get("choose.maven.home.desc"),
                                                   null,
                                                   new MavenHomeFileChooser());
            c = new GridBagConstraints();
            c.gridx = 1;
            c.fill = GridBagConstraints.BOTH;
            c.insets = new Insets(5, 5, 5, 5);
            c.weightx = 1;
            add(mavenHomeField, c);
        }

        public void setMavenHome(final File pMavenHome) {
            mavenHomeField.setText(pMavenHome == null ? null : pMavenHome.getAbsolutePath());
        }

        public File getMavenHome() {
            final String text = mavenHomeField.getText();
            if (text == null)
                return null;
            else if (text.trim().length() == 0)
                return null;
            else
                return new File(text).getAbsoluteFile();
        }

        private class MavenHomeFileChooser extends FileChooserDescriptor {
            public MavenHomeFileChooser() {
                super(false,   //prevent file-selection
                      true,    //allow folder-selection
                      false,   //prevent jar selection
                      false,   //prevent jar file selection
                      false,   //prevent jar content selection
                      false    //prevent multiple selection
                );
            }
        }
    }
}
