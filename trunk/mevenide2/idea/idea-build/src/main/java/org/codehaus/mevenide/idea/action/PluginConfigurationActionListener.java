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



package org.codehaus.mevenide.idea.action;

import org.codehaus.mevenide.idea.gui.form.AbstractConfigurationForm;
import org.codehaus.mevenide.idea.util.PluginConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import javax.swing.*;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class PluginConfigurationActionListener implements ActionListener {
    private AbstractConfigurationForm projectConfigurationForm;

    /**
     * Constructs ...
     *
     * @param form Document me!
     */
    public PluginConfigurationActionListener(AbstractConfigurationForm form) {
        this.projectConfigurationForm = form;
    }

    /**
     * Method description
     *
     * @param actionEvent Document me!
     */
    public void actionPerformed(ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser();

        if (projectConfigurationForm != null) {
            if (actionEvent.getActionCommand().equals(PluginConstants.ACTION_COMMAND_SET_MAVEN_HOME)) {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                if ((projectConfigurationForm.getTextFieldMavenHomeDir() != null)
                        && (projectConfigurationForm.getTextFieldMavenHomeDir().getText().length() > 0)) {
                    fileChooser.setCurrentDirectory(
                        new File(projectConfigurationForm.getTextFieldMavenHomeDir().getText()));
                }

                int returnVal = fileChooser.showOpenDialog(projectConfigurationForm.getRootComponent());

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();

                    projectConfigurationForm.getTextFieldMavenHomeDir().setText(file.getPath());
                }
            } else if (actionEvent.getActionCommand().equals(PluginConstants.ACTION_COMMAND_SET_ALTERNATE_SETTINGS)) {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                if ((projectConfigurationForm.getTextFieldAlternateSettingsFile() != null)
                        && (projectConfigurationForm.getTextFieldAlternateSettingsFile().getText().length() > 0)) {
                    fileChooser.setCurrentDirectory(
                        new File(projectConfigurationForm.getTextFieldAlternateSettingsFile().getText()));
                }

                int returnVal = fileChooser.showOpenDialog(projectConfigurationForm.getRootComponent());

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();

                    projectConfigurationForm.getTextFieldAlternateSettingsFile().setText(file.getPath());
                }
            }
        }
    }
}
