/*
 * Copyright (c) 2006 Bryan Kate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package mavenreloaded.configuration;


import javax.swing.*;


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

        if (settingsPathTextField.getText() != null ? !settingsPathTextField.getText().equals(data.getSettingsPath()) : data.getSettingsPath() != null) {
            return true;
        }

        if (searchFilterTextField.getText() != null ? !searchFilterTextField.getText().equals(data.getSearchFilter()) : data.getSearchFilter() != null) {
            return true;
        }

        if (logLevelSlider.getValue() != data.getLogLevel()) {
            return true;
        }

        return false;
    }

}

