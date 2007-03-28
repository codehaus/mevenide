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

import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.codehaus.mevenide.idea.common.MavenBuildPluginSettings;
import org.codehaus.mevenide.idea.form.CustomizingObject;
import org.codehaus.mevenide.idea.helper.IForm;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;

/**
 * Todo: Describe what this class does!
 *
 * @author Ralf Quebbemann (ralfq@codehaus.org)
 */
public class MavenBuildConfigurationForm implements IForm {
    /**
     * Field description
     */
    public static final String ACTION_COMMAND_SET_MAVEN_HOME = "Set Maven Home";

    /**
     * Field description
     */
    public static final String ACTION_COMMAND_SET_JDK_HOME = "Set JDK Home";

    private JCheckBox checkBoxUseEmbeddedMaven;
    private JPanel panel;
    private JTextField textFieldMavenHomeDirectory;
    private JButton buttonBrowseMavenHomeDirectory;
    private JTextField textFieldVMParameters;
    private JCheckBox checkBoxSkipTests;
    private JTextField textFieldAdditionalProperties;
    private JLabel labelBrowseMavenHomeDirectory;
    private JLabel labelJdkHomeDirectory;
    private JLabel labelVMParameters;
    private JComboBox comboBoxChooseJDK;
    private DefaultComboBoxModel comboboxModelModelChooseJdk = new DefaultComboBoxModel();


    public MavenBuildConfigurationForm() {
        buttonBrowseMavenHomeDirectory.setActionCommand(ACTION_COMMAND_SET_MAVEN_HOME);
    }

    private void fillComboboxJdk() {
        ProjectJdkTable jdkTable = ProjectJdkTable.getInstance();
        ProjectJdk[] projectJdks = jdkTable.getAllJdks();
        comboboxModelModelChooseJdk.removeAllElements();
        for (ProjectJdk projectJdk : projectJdks) {
            comboboxModelModelChooseJdk
                    .addElement(
                            new CustomizingObject(projectJdk.getVMExecutablePath(), projectJdk.getName()));
        }
        comboBoxChooseJDK.setModel(comboboxModelModelChooseJdk);
    }

    public JTextField getTextFieldMavenHomeDirectory() {
        return textFieldMavenHomeDirectory;
    }

    public JButton getButtonBrowseMavenHomeDirectory() {
        return buttonBrowseMavenHomeDirectory;
    }

    public JComponent getRootComponent() {
        return panel;
    }

    public void setData(MavenBuildPluginSettings data) {
        fillComboboxJdk();
        for (int i = 0; i < comboboxModelModelChooseJdk.getSize(); i++) {
            CustomizingObject customizingObject = (CustomizingObject) comboboxModelModelChooseJdk.getElementAt(i);
            if (customizingObject.getValue().equals(data.getJdkPath())) {
                comboBoxChooseJDK.setSelectedItem(customizingObject);
                break;
            }
        }

        checkBoxUseEmbeddedMaven.setSelected(data.isUseMavenEmbedder());
        textFieldMavenHomeDirectory.setText(data.getMavenHome());
        textFieldVMParameters.setText(data.getVmOptions());
        checkBoxSkipTests.setSelected(data.isSkipTests());
        textFieldAdditionalProperties.setText(data.getAdditionalOptions());
    }

    public void getData(MavenBuildPluginSettings data) {
        data.setUseMavenEmbedder(checkBoxUseEmbeddedMaven.isSelected());
        data.setMavenHome(textFieldMavenHomeDirectory.getText());
        data.setVmOptions(textFieldVMParameters.getText());
        data.setSkipTests(checkBoxSkipTests.isSelected());
        data.setAdditionalOptions(textFieldAdditionalProperties.getText());
        data.setJdkPath(((CustomizingObject) comboBoxChooseJDK.getSelectedItem()).getValue());

    }

    public boolean isModified(MavenBuildPluginSettings data) {
        if (!checkBoxUseEmbeddedMaven.isSelected()) {
            textFieldMavenHomeDirectory.setEnabled(true);
            textFieldVMParameters.setEnabled(true);
            buttonBrowseMavenHomeDirectory.setEnabled(true);
            labelBrowseMavenHomeDirectory.setEnabled(true);
            labelJdkHomeDirectory.setEnabled(true);
            labelVMParameters.setEnabled(true);
            comboBoxChooseJDK.setEnabled(true);
        } else {
            textFieldMavenHomeDirectory.setEnabled(false);
            textFieldVMParameters.setEnabled(false);
            buttonBrowseMavenHomeDirectory.setEnabled(false);
            labelBrowseMavenHomeDirectory.setEnabled(false);
            labelJdkHomeDirectory.setEnabled(false);
            labelVMParameters.setEnabled(false);
            comboBoxChooseJDK.setEnabled(false);
        }
        if (checkBoxUseEmbeddedMaven.isSelected() != data.isUseMavenEmbedder()) return true;
        if (textFieldMavenHomeDirectory.getText() != null ?
                !textFieldMavenHomeDirectory.getText().equals(data.getMavenHome()) : data.getMavenHome() != null)
            return true;
        if (textFieldVMParameters.getText() != null ? !textFieldVMParameters.getText().equals(data.getVmOptions()) :
                data.getVmOptions() != null) return true;
        if (checkBoxSkipTests.isSelected() != data.isSkipTests()) return true;
        if (textFieldAdditionalProperties.getText() != null ?
                !textFieldAdditionalProperties.getText().equals(data.getAdditionalOptions()) :
                data.getAdditionalOptions() != null) return true;
        if (!((CustomizingObject) comboBoxChooseJDK.getSelectedItem()).getValue()
                .equals(data.getJdkPath()))
            return true;

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
        panel = new JPanel();
        panel.setLayout(new FormLayout("fill:d:grow",
                "center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow",
                "center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,top:max(d;4px):noGrow"));
        CellConstraints cc = new CellConstraints();
        panel.add(panel1, cc.xy(1, 1, CellConstraints.FILL, CellConstraints.FILL));
        panel1.setBorder(BorderFactory.createTitledBorder("General Maven Setup"));
        checkBoxUseEmbeddedMaven = new JCheckBox();
        checkBoxUseEmbeddedMaven.setSelected(true);
        checkBoxUseEmbeddedMaven.setText("Use Embedded Maven");
        panel1.add(checkBoxUseEmbeddedMaven, cc.xy(1, 1));
        textFieldMavenHomeDirectory = new JTextField();
        textFieldMavenHomeDirectory.setColumns(25);
        textFieldMavenHomeDirectory.setEnabled(false);
        textFieldMavenHomeDirectory.setText("");
        panel1.add(textFieldMavenHomeDirectory, cc.xy(1, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        labelBrowseMavenHomeDirectory = new JLabel();
        labelBrowseMavenHomeDirectory.setEnabled(false);
        labelBrowseMavenHomeDirectory.setText("Maven2 Home Directory");
        panel1.add(labelBrowseMavenHomeDirectory, cc.xy(1, 3));
        buttonBrowseMavenHomeDirectory = new JButton();
        buttonBrowseMavenHomeDirectory.setEnabled(false);
        buttonBrowseMavenHomeDirectory.setText("Browse ...");
        panel1.add(buttonBrowseMavenHomeDirectory, cc.xy(3, 5, CellConstraints.LEFT, CellConstraints.DEFAULT));
        labelJdkHomeDirectory = new JLabel();
        labelJdkHomeDirectory.setEnabled(false);
        labelJdkHomeDirectory.setText("Choose JDK");
        panel1.add(labelJdkHomeDirectory, cc.xy(1, 7));
        labelVMParameters = new JLabel();
        labelVMParameters.setEnabled(false);
        labelVMParameters.setText("VM Parameters");
        panel1.add(labelVMParameters, cc.xy(1, 11));
        textFieldVMParameters = new JTextField();
        textFieldVMParameters.setEnabled(false);
        panel1.add(textFieldVMParameters, cc.xy(1, 13, CellConstraints.FILL, CellConstraints.DEFAULT));
        comboBoxChooseJDK = new JComboBox();
        comboBoxChooseJDK.setPreferredSize(new Dimension(150, 24));
        panel1.add(comboBoxChooseJDK, cc.xy(1, 9, CellConstraints.LEFT, CellConstraints.DEFAULT));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FormLayout("fill:d:noGrow",
                "top:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        panel.add(panel2, cc.xy(1, 3, CellConstraints.FILL, CellConstraints.FILL));
        panel2.setBorder(BorderFactory.createTitledBorder("Options"));
        final JLabel label1 = new JLabel();
        label1.setText("Additional Properties");
        label1.setToolTipText("Properties to pass to Maven, e.g. username=johndoe");
        panel2.add(label1, cc.xy(1, 3));
        textFieldAdditionalProperties = new JTextField();
        textFieldAdditionalProperties.setColumns(34);
        panel2.add(textFieldAdditionalProperties, cc.xy(1, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        checkBoxSkipTests = new JCheckBox();
        checkBoxSkipTests.setText("Skip Tests");
        panel2.add(checkBoxSkipTests, cc.xy(1, 1));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
