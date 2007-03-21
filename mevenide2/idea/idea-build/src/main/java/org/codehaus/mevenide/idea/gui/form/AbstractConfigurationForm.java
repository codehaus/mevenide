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


package org.codehaus.mevenide.idea.gui.form;

import org.codehaus.mevenide.idea.helper.ActionContext;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public abstract class AbstractConfigurationForm extends AbstractForm {
    protected ActionContext context;

    /**
     * Field description
     */
    protected JButton buttonMavenHomeDir;
    protected JButton buttonAlternativeSettingsFile;
    protected JCheckBox checkBoxUseMavenEmbedder;

    /**
     * Field description
     */
    protected JLabel labelMavenExecutable;

    /**
     * Field description
     */
    protected JLabel labelMavenCmdLineArgs;

    /**
     * Field description
     */
    protected JLabel labelVmOptions;

    /**
     * Field description
     */
    protected JLabel labelMavenOptions;

    /**
     * Field description
     */
    protected JTextField textFieldMavenHomeDir;

    /**
     * Field description
     */
    protected JTextField textFieldVmOptions;

    /**
     * Field description
     */
    protected JTextField textFieldMavenCmdLineArgs;

    /**
     * Field description
     */
    protected JTextField textFieldAlternateSettingsFile;

    /**
     * Method description
     *
     * @return Document me!
     */
    public JButton getButtonMavenHomeDir() {
        return buttonMavenHomeDir;
    }

    public JButton getButtonAlternativeSettingsFile() {
        return buttonAlternativeSettingsFile;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public JTextField getTextFieldMavenHomeDir() {
        return textFieldMavenHomeDir;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public JTextField getTextFieldAlternateSettingsFile() {
        return textFieldAlternateSettingsFile;
    }

    protected void getData(org.codehaus.mevenide.idea.build.IMavenBuildConfiguration data) {
        data.setMavenHome(textFieldMavenHomeDir.getText());
        data.setMavenSettingsFile(textFieldAlternateSettingsFile.getText());
        data.setMavenCommandLineParams(textFieldMavenCmdLineArgs.getText());
        data.setUseMavenEmbedder(checkBoxUseMavenEmbedder.isSelected());
        data.setVmOptions(textFieldVmOptions.getText());
    }

    protected void setData(org.codehaus.mevenide.idea.build.IMavenBuildConfiguration data) {
        textFieldMavenHomeDir.setText(data.getMavenHome());
        textFieldAlternateSettingsFile.setText(data.getMavenSettingsFile());
        textFieldMavenCmdLineArgs.setText(data.getMavenCommandLineParams());
        textFieldVmOptions.setText(data.getVmOptions());
        checkBoxUseMavenEmbedder.setSelected(data.isUseMavenEmbedder());

    }

    protected boolean isDataModified(org.codehaus.mevenide.idea.build.IMavenBuildConfiguration data) {
        if ((textFieldMavenHomeDir.getText() != null)
                ? !textFieldMavenHomeDir.getText().equals(data.getMavenHome())
                : data.getMavenHome() != null) {
            return true;
        }

        if ((textFieldAlternateSettingsFile.getText() != null)
                ? !textFieldAlternateSettingsFile.getText().equals(data.getMavenSettingsFile())
                : data.getMavenSettingsFile() != null) {
            return true;
        }

        if ((textFieldMavenCmdLineArgs.getText() != null)
                ? !textFieldMavenCmdLineArgs.getText().equals(data.getMavenCommandLineParams())
                : data.getMavenCommandLineParams() != null) {
            return true;
        }

        if ((textFieldVmOptions.getText() != null)
                ? !textFieldVmOptions.getText().equals(data.getVmOptions())
                : data.getVmOptions() != null) {
            return true;
        }

        if (checkBoxUseMavenEmbedder.isSelected() != data.isUseMavenEmbedder()) {
            return true;
        }
        return false;
    }
}
