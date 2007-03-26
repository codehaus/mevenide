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

import org.apache.commons.lang.StringUtils;
import org.codehaus.mevenide.idea.gui.form.MavenBuildConfigurationForm;

import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class PluginConfigurationActionListener implements ActionListener {
    private MavenBuildConfigurationForm projectConfigurationForm;

    /**
     * Constructs ...
     *
     * @param form Document me!
     */
    public PluginConfigurationActionListener(MavenBuildConfigurationForm form) {
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
            if (actionEvent.getActionCommand().equals(MavenBuildConfigurationForm.ACTION_COMMAND_SET_MAVEN_HOME)) {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                if (!StringUtils.isEmpty(projectConfigurationForm.getTextFieldMavenHomeDirectory().getText())) {
                    fileChooser.setCurrentDirectory(
                            new File(projectConfigurationForm.getTextFieldMavenHomeDirectory().getText()));
                }

                int returnVal = fileChooser.showOpenDialog(projectConfigurationForm.getRootComponent());

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();

                    projectConfigurationForm.getTextFieldMavenHomeDirectory().setText(file.getPath());
                }
            }
        }
    }
}
