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

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import java.io.File;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.mevenide.idea.util.components.AbstractApplicationComponent;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * This component manages UI configuration for the {@link MavenManager} component. It displays a
 * window allowing the user to modify the Maven manager settings, and either discards them or
 * applies them based on user actions.
 *
 * @author Arik
 */
public class MavenManagerConfigurable extends AbstractApplicationComponent implements Configurable {
    /**
     * The user interface component to display to the user.
     */
    private MavenManagerPanel ui;

    public void initComponent() {
        ui = new MavenManagerPanel();
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
        final MavenManager mavenMgr = MavenManager.getInstance();
        try {
            mavenMgr.setMavenHome(ui.mavenHomeField.getText());
            mavenMgr.setMavenOptions(ui.mavenOptionsField.getText());
            mavenMgr.setOffline(ui.offlineCheckBox.isSelected());
        }
        catch (IllegalMavenHomeException e) {
            throw new ConfigurationException(e.getMessage());
        }
    }

    public JComponent createComponent() {
        return ui;
    }

    public void disposeUIResources() {
    }

    public boolean isModified() {
        final MavenManager mavenMgr = MavenManager.getInstance();

        //check offline mode
        if (ui.offlineCheckBox.isSelected() != mavenMgr.isOffline())
            return true;

        //check maven cmdline options
        if (!StringUtils.equals(mavenMgr.getMavenOptions(), ui.mavenOptionsField.getText()))
            return true;

        //check maven home
        final VirtualFile mavenHome = mavenMgr.getMavenHome();
        final VirtualFile selectedHome;
        final String selectedHomeValue = ui.mavenHomeField.getText();
        if (selectedHomeValue == null || selectedHomeValue.trim().length() == 0)
            selectedHome = null;
        else {
            final String selectedPath = selectedHomeValue.replace(File.separatorChar, '/');
            final String url = VirtualFileManager.constructUrl("file", selectedPath);
            selectedHome = VirtualFileManager.getInstance().findFileByUrl(url);
        }

        return selectedHome != mavenHome;
    }

    public void reset() {
        final MavenManager mavenMgr = MavenManager.getInstance();

        final VirtualFile mavenHome = mavenMgr.getMavenHome();
        ui.mavenHomeField.setText(mavenHome == null ? null : mavenHome.getPresentableUrl());
        ui.mavenOptionsField.setText(mavenMgr.getMavenOptions());
        ui.offlineCheckBox.setSelected(mavenMgr.isOffline());
    }

    private class MavenManagerPanel extends JPanel {
        private final TextFieldWithBrowseButton mavenHomeField = new TextFieldWithBrowseButton();
        private final JTextField mavenOptionsField = new JTextField();
        private final JCheckBox offlineCheckBox = new JCheckBox(RES.get("offline.mode.label"));
        private final JButton guessMavenHomeButton = new JButton("Guess");

        private MavenManagerPanel() {
            mavenHomeField.addBrowseFolderListener(
                    RES.get("choose.maven.home.dlg.title"),
                    RES.get("choose.maven.home.dlg.desc"),
                    null,
                    FileChooserDescriptorFactory.createSingleFolderDescriptor());


            guessMavenHomeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    final VirtualFile home = MavenManager.getInstance().guessMavenHome();
                    if(home != null && home.isValid() && home.isDirectory())
                        mavenHomeField.setText(home.getPresentableUrl());
                }
            });

            //layout components
            final String cols = "right:min, 2dlu, fill:min:grow, 2dlu, fill:min";
            final FormLayout layout = new FormLayout(cols);
            final DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
            builder.setComponentFactory(CustomFormsComponentFactory.getInstance());
            builder.append(RES.get("maven.home.label"), mavenHomeField, guessMavenHomeButton);
            builder.append(RES.get("maven.options.label"), mavenOptionsField, 3);
            builder.append(" ", offlineCheckBox, 3);
        }
    }
}
