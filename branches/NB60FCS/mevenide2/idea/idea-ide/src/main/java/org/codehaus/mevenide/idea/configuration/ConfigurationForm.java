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



package org.codehaus.mevenide.idea.configuration;

import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.Insets;
import java.awt.Dimension;

/**
 * An auto-generated class that goes along with the configuration GUI form.
 *
 * @author bkate
 */
public class ConfigurationForm {
    private JPanel rootComponent;
    private JTextField settingsPathTextField;
    private JTextField searchFilterTextField;
    private JCheckBox updateClasspathsCheckBox;
    private JCheckBox sortDependencyListCheckBox;
    private JCheckBox manageSourceRootsCheckBox;
    private JCheckBox manageModuleInterdependenciesCheckBox;
    private JCheckBox downloadJavadocCheckBox;
    private JCheckBox generateSourcesCheckBox;
    private JCheckBox downloadSourcesCheckBox;
    private JCheckBox enablePluginCheckBox;
    private JCheckBox removeDuplicateDependenciesCheckBox;
    private JCheckBox respondToPomChangesCheckBox;
    private JSlider logLevelSlider;

    /**
     * Default constructor.
     */
    public ConfigurationForm() {
    }

    /**
     * Gets the root GUI component of this form.
     *
     * @return The root JComponent that contains the form.
     */
    public JComponent getRootComponent() {
        return rootComponent;
    }

    public void setData(ConfigurationBean data) {
        enablePluginCheckBox.setSelected(data.isPluginEnabled());
        updateClasspathsCheckBox.setSelected(data.isUpdateClasspathsEnabled());
        sortDependencyListCheckBox.setSelected(data.isSortDependenciesEnabled());
        removeDuplicateDependenciesCheckBox.setSelected(data.isRemoveDuplicateDependenciesEnabled());
        respondToPomChangesCheckBox.setSelected(data.isRespondToPomChangesEnabled());
        manageSourceRootsCheckBox.setSelected(data.isManageSourceRootsEnabled());
        manageModuleInterdependenciesCheckBox.setSelected(data.isManageModuleInterdependenciesEnabled());
        downloadJavadocCheckBox.setSelected(data.isDownloadJavadocEnabled());
        downloadSourcesCheckBox.setSelected(data.isDownloadSourcesEnabled());
        generateSourcesCheckBox.setSelected(data.isGenerateSourcesEnabled());
        settingsPathTextField.setText(data.getSettingsPath());
        searchFilterTextField.setText(data.getSearchFilter());
        logLevelSlider.setValue(data.getLogLevel());
    }

    public void getData(ConfigurationBean data) {
        data.setPluginEnabled(enablePluginCheckBox.isSelected());
        data.setUpdateClasspathsEnabled(updateClasspathsCheckBox.isSelected());
        data.setSortDependenciesEnabled(sortDependencyListCheckBox.isSelected());
        data.setRemoveDuplicateDependenciesEnabled(removeDuplicateDependenciesCheckBox.isSelected());
        data.setRespondToPomChangesEnabled(respondToPomChangesCheckBox.isSelected());
        data.setManageSourceRootsEnabled(manageSourceRootsCheckBox.isSelected());
        data.setManageModuleInterdependenciesEnabled(manageModuleInterdependenciesCheckBox.isSelected());
        data.setDownloadJavadocEnabled(downloadJavadocCheckBox.isSelected());
        data.setDownloadSourcesEnabled(downloadSourcesCheckBox.isSelected());
        data.setGenerateSourcesEnabled(generateSourcesCheckBox.isSelected());
        data.setSettingsPath(settingsPathTextField.getText());
        data.setSearchFilter(searchFilterTextField.getText());
        data.setLogLevel(logLevelSlider.getValue());
    }

    public boolean isModified(ConfigurationBean data) {
        if (enablePluginCheckBox.isSelected() != data.isPluginEnabled()) {
            return true;
        }

        if (updateClasspathsCheckBox.isSelected() != data.isUpdateClasspathsEnabled()) {
            return true;
        }

        if (sortDependencyListCheckBox.isSelected() != data.isSortDependenciesEnabled()) {
            return true;
        }

        if (removeDuplicateDependenciesCheckBox.isSelected() != data.isRemoveDuplicateDependenciesEnabled()) {
            return true;
        }

        if (respondToPomChangesCheckBox.isSelected() != data.isRespondToPomChangesEnabled()) {
            return true;
        }

        if (manageSourceRootsCheckBox.isSelected() != data.isManageSourceRootsEnabled()) {
            return true;
        }

        if (manageModuleInterdependenciesCheckBox.isSelected() != data.isManageModuleInterdependenciesEnabled()) {
            return true;
        }

        if (downloadJavadocCheckBox.isSelected() != data.isDownloadJavadocEnabled()) {
            return true;
        }

        if (downloadSourcesCheckBox.isSelected() != data.isDownloadSourcesEnabled()) {
            return true;
        }

        if (generateSourcesCheckBox.isSelected() != data.isGenerateSourcesEnabled()) {
            return true;
        }

        if ((settingsPathTextField.getText() != null)
                ? !settingsPathTextField.getText().equals(data.getSettingsPath())
                : data.getSettingsPath() != null) {
            return true;
        }

        if ((searchFilterTextField.getText() != null)
                ? !searchFilterTextField.getText().equals(data.getSearchFilter())
                : data.getSearchFilter() != null) {
            return true;
        }

        if (logLevelSlider.getValue() != data.getLogLevel()) {
            return true;
        }

        return false;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootComponent = new JPanel();
        rootComponent.setLayout(new GridLayoutManager(5, 1, new Insets(10, 10, 10, 10), -1, -1));
        enablePluginCheckBox = new JCheckBox();
        enablePluginCheckBox.setEnabled(true);
        enablePluginCheckBox.setHorizontalTextPosition(10);
        enablePluginCheckBox.setSelected(false);
        enablePluginCheckBox.setText("Enable Plugin");
        enablePluginCheckBox.setToolTipText("Enable/Disable Maven Reloaded plugin");
        rootComponent.add(enablePluginCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        rootComponent.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10),
                new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(3, 3, 3, 3), -1, -1));
        rootComponent.add(panel1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                new Dimension(350, 175), null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder("POM Usage"));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, 5));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        updateClasspathsCheckBox = new JCheckBox();
        updateClasspathsCheckBox.setText("Update Classpaths");
        updateClasspathsCheckBox.setToolTipText("Use POMs to manage module classpaths.");
        panel2.add(updateClasspathsCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manageSourceRootsCheckBox = new JCheckBox();
        manageSourceRootsCheckBox.setText("Manage Source Roots");
        manageSourceRootsCheckBox.setToolTipText("Set module source and test source roots based on POM source roots.");
        panel2.add(manageSourceRootsCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manageModuleInterdependenciesCheckBox = new JCheckBox();
        manageModuleInterdependenciesCheckBox.setLabel("Manage Module Inter-Dependencies");
        manageModuleInterdependenciesCheckBox.setText("Manage Module Inter-Dependencies");
        manageModuleInterdependenciesCheckBox
                .setToolTipText("Use POMs to determine dependencies between project modules.");
        panel2.add(manageModuleInterdependenciesCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        generateSourcesCheckBox = new JCheckBox();
        generateSourcesCheckBox.setText("Generate Sources");
        generateSourcesCheckBox.setToolTipText(
                "Run generate-sources, generate-resources, generate-test-sources, and generate-test-resources on POM files before managing module source roots.");
        panel2.add(generateSourcesCheckBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sortDependencyListCheckBox = new JCheckBox();
        sortDependencyListCheckBox.setText("Sort Dependency List");
        sortDependencyListCheckBox.setToolTipText("Sort the list of dependencies for each module.");
        panel2.add(sortDependencyListCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeDuplicateDependenciesCheckBox = new JCheckBox();
        removeDuplicateDependenciesCheckBox.setSelected(false);
        removeDuplicateDependenciesCheckBox.setText("Remove Duplicate Dependencies");
        removeDuplicateDependenciesCheckBox.setToolTipText(
                "Attempt to remove multiple versions of dependencies in each module. May cause unexpected results from missing classpath dependencies.");
        removeDuplicateDependenciesCheckBox.setVerifyInputWhenFocusTarget(false);
        panel2.add(removeDuplicateDependenciesCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        downloadSourcesCheckBox = new JCheckBox();
        downloadSourcesCheckBox.setText("Download Sources");
        downloadSourcesCheckBox.setToolTipText("Download source jar files of POM dependencies.");
        downloadSourcesCheckBox.setVerifyInputWhenFocusTarget(false);
        panel2.add(downloadSourcesCheckBox, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        downloadJavadocCheckBox = new JCheckBox();
        downloadJavadocCheckBox.setSelected(false);
        downloadJavadocCheckBox.setText("Download Javadoc");
        downloadJavadocCheckBox.setToolTipText("Download javadoc jar files of POM dependencies.");
        panel2.add(downloadJavadocCheckBox, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        respondToPomChangesCheckBox = new JCheckBox();
        respondToPomChangesCheckBox.setSelected(false);
        respondToPomChangesCheckBox.setText("Respond to POM File Changes");
        respondToPomChangesCheckBox
                .setToolTipText("Determines if the plugin should update the project after a change to a POM file.");
        panel2.add(respondToPomChangesCheckBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 2, new Insets(3, 3, 3, 3), -1, 10));
        rootComponent.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                new Dimension(350, 150), null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder("General"));
        final JLabel label1 = new JLabel();
        label1.setText("Settings Path");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsPathTextField = new JTextField();
        settingsPathTextField.setText("");
        settingsPathTextField.setToolTipText("Absolute path to Maven 2 settings.xml file.");
        settingsPathTextField.setVerifyInputWhenFocusTarget(false);
        panel3.add(settingsPathTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Logging Level");
        panel3.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Search Filter");
        label3.setToolTipText("");
        panel3.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        searchFilterTextField = new JTextField();
        searchFilterTextField.setToolTipText(
                "A comma separated list of strings that will be tested against directory names while searching for POM files. If a the name is a case insensitive match, the directory will be skipped. No wildcards.");
        panel3.add(searchFilterTextField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                null, new Dimension(150, -1), null, 0, false));
        logLevelSlider = new JSlider();
        logLevelSlider.setInverted(true);
        logLevelSlider.setMajorTickSpacing(1);
        logLevelSlider.setMaximum(5);
        logLevelSlider.setMinimum(0);
        logLevelSlider.setPaintLabels(false);
        logLevelSlider.setPaintTicks(true);
        logLevelSlider.setSnapToTicks(true);
        logLevelSlider.setToolTipText("Change the logging level: OFF ->FATAL -> ERROR -> WARN -> INFO -> DEBUG");
        logLevelSlider.setValueIsAdjusting(false);
        panel3.add(logLevelSlider, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        rootComponent.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10),
                new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootComponent;
    }
}
