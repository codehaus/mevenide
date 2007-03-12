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

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import org.codehaus.mevenide.idea.build.IMavenConfiguration;
import org.codehaus.mevenide.idea.build.util.BuildConstants;
import org.codehaus.mevenide.idea.helper.ActionContext;

import java.awt.*;

import javax.swing.*;

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
    protected JCheckBox checkBoxStrictChecksums;
    protected JCheckBox checkBoxLaxChecksums;
    protected JCheckBox checkBoxFailFast;
    protected JCheckBox checkBoxFailAtEnd;
    protected JCheckBox checkBoxBatchMode;
    protected JCheckBox checkBoxFailNever;
    protected JCheckBox checkBoxUpdatePlugins;
    protected JCheckBox checkBoxUpdateSnapshots;
    protected JCheckBox checkBoxNonRecursive;
    protected JCheckBox checkBoxNoPluginRegistry;
    protected JCheckBox checkBoxCheckPluginUpdates;
    protected JCheckBox checkBoxNoPluginUpdates;
    protected JCheckBox checkBoxDebug;
    protected JCheckBox checkBoxErrors;
    protected JCheckBox checkBoxOffline;
    protected JCheckBox checkBoxReactor;
    protected JCheckBox checkBoxSkipTests;
    protected JCheckBox checkBoxUseMavenEmbedder;

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
    public JTextField getTextFieldMavenCmdLineArgs() {
        return textFieldMavenCmdLineArgs;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public JTextField getTextFieldAlternateSettingsFile() {
        return textFieldAlternateSettingsFile;
    }

    public JTextField getTextFieldVmOptions() {
        return textFieldVmOptions;
    }

    public void setTextFieldVmOptions(JTextField textFieldVmOptions) {
        this.textFieldVmOptions = textFieldVmOptions;
    }

    /**
     * Method description
     *
     * @param buttonMavenHomeDir Document me!
     */
    public void setButtonMavenHomeDir(JButton buttonMavenHomeDir) {
        this.buttonMavenHomeDir = buttonMavenHomeDir;
    }

    /**
     * Method description
     *
     * @param textFieldMavenHomeDir Document me!
     */
    public void setTextFieldMavenHomeDir(JTextField textFieldMavenHomeDir) {
        this.textFieldMavenHomeDir = textFieldMavenHomeDir;
    }

    /**
     * Method description
     *
     * @param textFieldMavenGlobalCmdLineArgs
     *         Document me!
     */
    public void setTextFieldMavenCmdLineArgs(JTextField textFieldMavenGlobalCmdLineArgs) {
        this.textFieldMavenCmdLineArgs = textFieldMavenGlobalCmdLineArgs;
    }

    /**
     * Method description
     *
     * @param textFieldMavenGlobalOptions Document me!
     */
    public void setTextFieldAlternateSettingsFile(JTextField textFieldMavenGlobalOptions) {
        this.textFieldAlternateSettingsFile = textFieldMavenGlobalOptions;
    }

    protected JPanel createMavenOptionsPanel() {
        final JPanel mavenOptionsPanel = new JPanel();

        mavenOptionsPanel.setLayout(new GridLayoutManager(10, 2, new Insets(0, 0, 0, 0), -1, -1));
        checkBoxStrictChecksums = new JCheckBox();
        checkBoxStrictChecksums.setText(BuildConstants.MAVEN_OPTION_STRICT_CHECKSUM);
        mavenOptionsPanel.add(checkBoxStrictChecksums,
                              new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                  GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                  GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        checkBoxLaxChecksums = new JCheckBox();
        checkBoxLaxChecksums.setText(BuildConstants.MAVEN_OPTION_LAX_CHECKSUM);
        mavenOptionsPanel.add(checkBoxLaxChecksums,
                              new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                  GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                  GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        checkBoxFailFast = new JCheckBox();
        checkBoxFailFast.setText(BuildConstants.MAVEN_OPTION_FAIL_FAST);
        mavenOptionsPanel.add(checkBoxFailFast,
                              new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                  GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                  GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        checkBoxBatchMode = new JCheckBox();
        checkBoxBatchMode.setText(BuildConstants.MAVEN_OPTION_BATCH_MODE);
        mavenOptionsPanel.add(checkBoxBatchMode,
                              new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                  GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                  GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        checkBoxCheckPluginUpdates = new JCheckBox();
        checkBoxCheckPluginUpdates.setText(BuildConstants.MAVEN_OPTION_CHECK_PLUGIN_UPDATES);
        mavenOptionsPanel.add(checkBoxCheckPluginUpdates,
                              new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                  GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                  GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        checkBoxDebug = new JCheckBox();
        checkBoxDebug.setText(BuildConstants.MAVEN_OPTION_DEBUG);
        mavenOptionsPanel.add(checkBoxDebug,
                              new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                  GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                  GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        checkBoxErrors = new JCheckBox();
        checkBoxErrors.setText(BuildConstants.MAVEN_OPTION_ERRORS);
        mavenOptionsPanel.add(checkBoxErrors,
                              new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                  GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                  GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        checkBoxFailAtEnd = new JCheckBox();
        checkBoxFailAtEnd.setText(BuildConstants.MAVEN_OPTION_FAIL_AT_END);
        mavenOptionsPanel.add(checkBoxFailAtEnd,
                              new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                  GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                  GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        checkBoxFailNever = new JCheckBox();
        checkBoxFailNever.setText(BuildConstants.MAVEN_OPTION_FAIL_NEVER);
        mavenOptionsPanel.add(checkBoxFailNever,
                              new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                  GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                  GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        checkBoxNonRecursive = new JCheckBox();
        checkBoxNonRecursive.setText(BuildConstants.MAVEN_OPTION_NON_RECURSIVE);
        mavenOptionsPanel.add(checkBoxNonRecursive,
                              new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                  GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                  GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        checkBoxNoPluginRegistry = new JCheckBox();
        checkBoxNoPluginRegistry.setText(BuildConstants.MAVEN_OPTION_NO_PLUGIN_REGISTRY);
        mavenOptionsPanel.add(checkBoxNoPluginRegistry,
                              new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                  GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                  GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        checkBoxNoPluginUpdates = new JCheckBox();
        checkBoxNoPluginUpdates.setText(BuildConstants.MAVEN_OPTION_NO_PLUGIN_UPDATES);
        mavenOptionsPanel.add(checkBoxNoPluginUpdates,
                              new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                  GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                  GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        checkBoxOffline = new JCheckBox();
        checkBoxOffline.setText(BuildConstants.MAVEN_OPTION_OFFLINE);
        mavenOptionsPanel.add(checkBoxOffline,
                              new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                  GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                  GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        checkBoxReactor = new JCheckBox();
        checkBoxReactor.setText(BuildConstants.MAVEN_OPTION_REACTOR);
        mavenOptionsPanel.add(checkBoxReactor,
                              new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                  GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                  GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        checkBoxUpdatePlugins = new JCheckBox();
        checkBoxUpdatePlugins.setText(BuildConstants.MAVEN_OPTION_UPDATE_PLUGINS);
        mavenOptionsPanel.add(checkBoxUpdatePlugins,
                              new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                  GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                  GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        checkBoxUpdateSnapshots = new JCheckBox();
        checkBoxUpdateSnapshots.setText(BuildConstants.MAVEN_OPTION_UPDATE_SNAPSHOTS);
        mavenOptionsPanel.add(checkBoxUpdateSnapshots,
                              new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                  GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                  GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        checkBoxSkipTests = new JCheckBox();
        checkBoxSkipTests.setText(BuildConstants.MAVEN_OPTION_SKIP_TESTS);
        mavenOptionsPanel.add(checkBoxSkipTests,
                              new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                  GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                  GridConstraints.SIZEPOLICY_FIXED, null, null, null));

        return mavenOptionsPanel;
    }

    protected void getData(IMavenConfiguration data) {
        data.setMavenHome(textFieldMavenHomeDir.getText());
        data.setMavenSettingsFile(textFieldAlternateSettingsFile.getText());
        data.setMavenCommandLineParams(textFieldMavenCmdLineArgs.getText());
        data.setUseMavenEmbedder(checkBoxUseMavenEmbedder.isSelected());
        data.setVmOptions(textFieldVmOptions.getText());
    }

    protected void setData(IMavenConfiguration data) {
        textFieldMavenHomeDir.setText(data.getMavenHome());
        textFieldAlternateSettingsFile.setText(data.getMavenSettingsFile());
        textFieldMavenCmdLineArgs.setText(data.getMavenCommandLineParams());
        textFieldVmOptions.setText(data.getVmOptions());
        checkBoxUseMavenEmbedder.setSelected(data.isUseMavenEmbedder());

    }

    protected boolean isDataModified(IMavenConfiguration data) {
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
