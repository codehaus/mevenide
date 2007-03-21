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

package org.codehaus.mevenide.idea.form;

import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.maven.execution.MavenExecutionRequest;
import org.codehaus.mevenide.idea.model.MavenConfiguration;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Insets;

/**
 * Todo: Describe what this class does!
 *
 * @author Ralf Quebbemann (ralfq@codehaus.org)
 */
public class MavenCoreConfigurationForm {
    public static final String ACTION_COMMAND_SET_LOCAL_REPOSITORY = "MavenCoreConfigurationForm.SetLocalRepository";
    private JCheckBox checkboxWorkOffline;
    private JPanel panel;
    private JComboBox comboboxOutputLevel;
    private JCheckBox checkboxProduceExceptionErrorMessages;
    private JComboBox comboboxChecksumPolicy;
    private JComboBox comboboxMultiprojectBuildFailPolicy;
    private JComboBox comboboxPluginUpdatePolicy;
    private JCheckBox checkboxUsePluginRegistry;
    private JTextField textFieldLocalRepository;
    private JButton buttonBrowseLocalRepository;
    private JLabel labelOutputLevel;
    private JLabel labelPluginUpdatePolicy;
    private JLabel labelMultiprojectBuildFailPolicy;
    private JLabel labelChecksumPolicy;
    private JLabel labelLocalRepository;
    private JCheckBox checkboxNonRecursive;
    private DefaultComboBoxModel comboboxModelOutputLevel = new DefaultComboBoxModel();
    private DefaultComboBoxModel comboboxModelChecksumPolicy = new DefaultComboBoxModel();
    private DefaultComboBoxModel comboboxModelMultiprojectBuildFailPolicy = new DefaultComboBoxModel();
    private DefaultComboBoxModel comboboxModelPluginUpdatePolicy = new DefaultComboBoxModel();


    public MavenCoreConfigurationForm() {
        fillComboboxOutputLevel();
        fillComboboxChecksumPolicy();
        fillComboboxFailureBehavior();
        fillComboboxPluginUpdatePolicy();
        buttonBrowseLocalRepository.setActionCommand(ACTION_COMMAND_SET_LOCAL_REPOSITORY);
    }

    private void fillComboboxFailureBehavior() {
        comboboxModelMultiprojectBuildFailPolicy
                .addElement(new CustomizingObject(MavenExecutionRequest.REACTOR_FAIL_FAST, "Stop at first failure"));
        comboboxModelMultiprojectBuildFailPolicy
                .addElement(new CustomizingObject(MavenExecutionRequest.REACTOR_FAIL_AT_END, "Fail at the end"));
        comboboxModelMultiprojectBuildFailPolicy
                .addElement(new CustomizingObject(MavenExecutionRequest.REACTOR_FAIL_NEVER, "Never fail"));
        comboboxMultiprojectBuildFailPolicy.setModel(comboboxModelMultiprojectBuildFailPolicy);
    }

    private void fillComboboxPluginUpdatePolicy() {
        comboboxModelPluginUpdatePolicy
                .addElement(new CustomizingObject("false", "No Global Policy"));
        comboboxModelPluginUpdatePolicy
                .addElement(new CustomizingObject("true", "Check For Updates"));
        comboboxModelPluginUpdatePolicy
                .addElement(new CustomizingObject("false", "Supress Checking"));
        comboboxPluginUpdatePolicy.setModel(comboboxModelPluginUpdatePolicy);
    }

    private void fillComboboxChecksumPolicy() {
        comboboxModelChecksumPolicy.addElement(new CustomizingObject("", "No Global Policy"));
        comboboxModelChecksumPolicy
                .addElement(new CustomizingObject(MavenExecutionRequest.CHECKSUM_POLICY_FAIL, "Strict (Fail)"));
        comboboxModelChecksumPolicy
                .addElement(new CustomizingObject(MavenExecutionRequest.CHECKSUM_POLICY_WARN, "Lax (Warn Only)"));
        comboboxChecksumPolicy.setModel(comboboxModelChecksumPolicy);
    }

    private void fillComboboxOutputLevel() {
        comboboxModelOutputLevel
                .addElement(new CustomizingObject(String.valueOf(MavenExecutionRequest.LOGGING_LEVEL_DEBUG), "Debug"));
        comboboxModelOutputLevel
                .addElement(new CustomizingObject(String.valueOf(MavenExecutionRequest.LOGGING_LEVEL_INFO), "Info"));
        comboboxModelOutputLevel
                .addElement(new CustomizingObject(String.valueOf(MavenExecutionRequest.LOGGING_LEVEL_WARN), "Warn"));
        comboboxModelOutputLevel
                .addElement(new CustomizingObject(String.valueOf(MavenExecutionRequest.LOGGING_LEVEL_ERROR), "Error"));
        comboboxModelOutputLevel
                .addElement(new CustomizingObject(String.valueOf(MavenExecutionRequest.LOGGING_LEVEL_FATAL), "Fatal"));
        comboboxModelOutputLevel.addElement(
                new CustomizingObject(String.valueOf(MavenExecutionRequest.LOGGING_LEVEL_DISABLED), "Disabled"));
        comboboxOutputLevel.setModel(comboboxModelOutputLevel);
    }

    public JComponent getRootComponent() {
        return panel;
    }

    public JButton getButtonBrowseLocalRepository() {
        return buttonBrowseLocalRepository;
    }

    public void setTextFieldLocalRepositoryData(String localRepository) {
        textFieldLocalRepository.setText(localRepository);
    }

    public void setData(MavenConfiguration data) {
        checkboxWorkOffline.setSelected(data.isWorkOffline());
        textFieldLocalRepository.setText(data.getLocalRepository());
        checkboxProduceExceptionErrorMessages.setSelected(data.isProduceExceptionErrorMessages());
        checkboxUsePluginRegistry.setSelected(data.isUsePluginRegistry());
        //       checkboxSkipTests.setSelected(data.isSkipTests());
        checkboxNonRecursive.setSelected(data.isNonRecursive());
        for (int i = 0; i < comboboxModelOutputLevel.getSize(); i++) {
            CustomizingObject customizingObject = (CustomizingObject) comboboxModelOutputLevel.getElementAt(i);
            if (customizingObject.getValue().equals(String.valueOf(data.getOutputLevel()))) {
                comboboxOutputLevel.setSelectedItem(customizingObject);
                break;
            }
        }
        for (int i = 0; i < comboboxModelChecksumPolicy.getSize(); i++) {
            CustomizingObject customizingObject = (CustomizingObject) comboboxModelChecksumPolicy.getElementAt(i);
            if (customizingObject.getValue().equals(data.getChecksumPolicy())) {
                comboboxChecksumPolicy.setSelectedItem(customizingObject);
                break;
            }
        }
        if (data.getChecksumPolicy() == null) {
            comboboxChecksumPolicy.setSelectedItem(comboboxChecksumPolicy.getModel().getElementAt(0));
            data.setChecksumPolicy(((CustomizingObject) comboboxChecksumPolicy.getSelectedItem()).getValue());
        }
        for (int i = 0; i < comboboxModelMultiprojectBuildFailPolicy.getSize(); i++) {
            CustomizingObject customizingObject =
                    (CustomizingObject) comboboxModelMultiprojectBuildFailPolicy.getElementAt(i);
            if (customizingObject.getValue().equals(data.getFailureBehavior())) {
                comboboxMultiprojectBuildFailPolicy.setSelectedItem(customizingObject);
                break;
            }
        }
        if (data.getFailureBehavior() == null) {
            comboboxModelMultiprojectBuildFailPolicy
                    .setSelectedItem(comboboxMultiprojectBuildFailPolicy.getModel().getElementAt(0));
            data.setFailureBehavior(
                    ((CustomizingObject) comboboxMultiprojectBuildFailPolicy.getSelectedItem()).getValue());
        }
        for (int i = 0; i < comboboxModelPluginUpdatePolicy.getSize(); i++) {
            CustomizingObject customizingObject =
                    (CustomizingObject) comboboxModelPluginUpdatePolicy.getElementAt(i);
            if (customizingObject.getValue().equals(String.valueOf(data.isPluginUpdatePolicy()))) {
                comboboxModelPluginUpdatePolicy.setSelectedItem(customizingObject);
                break;
            }
        }
        if (data.isPluginUpdatePolicy() == null) {
            comboboxModelPluginUpdatePolicy
                    .setSelectedItem(comboboxPluginUpdatePolicy.getModel().getElementAt(2));
            data.setPluginUpdatePolicy(Boolean.valueOf(
                    ((CustomizingObject) comboboxPluginUpdatePolicy.getSelectedItem()).getValue()));
        }
    }

    public void getData(MavenConfiguration data) {
        data.setWorkOffline(checkboxWorkOffline.isSelected());
        data.setLocalRepository(textFieldLocalRepository.getText());
        data.setProduceExceptionErrorMessages(checkboxProduceExceptionErrorMessages.isSelected());
        data.setUsePluginRegistry(checkboxUsePluginRegistry.isSelected());
        //       data.setSkipTests(checkboxSkipTests.isSelected());
        data.setNonRecursive(checkboxNonRecursive.isSelected());
        data.setOutputLevel(Integer.parseInt(((CustomizingObject) comboboxOutputLevel.getSelectedItem()).getValue()));
        data.setChecksumPolicy(((CustomizingObject) comboboxChecksumPolicy.getSelectedItem()).getValue());
        data.setFailureBehavior(((CustomizingObject) comboboxMultiprojectBuildFailPolicy.getSelectedItem()).getValue());
        data.setPluginUpdatePolicy(
                Boolean.valueOf(((CustomizingObject) comboboxPluginUpdatePolicy.getSelectedItem()).getValue()));
    }

    public boolean isModified(MavenConfiguration data) {
        if (checkboxWorkOffline.isSelected() != data.isWorkOffline()) return true;
        if (textFieldLocalRepository.getText() != null && data.getLocalRepository() != null ?
                !textFieldLocalRepository.getText().equals(data.getLocalRepository()) :
                data.getLocalRepository() != null) return true;
        if (checkboxProduceExceptionErrorMessages.isSelected() != data.isProduceExceptionErrorMessages()) return true;
        if (checkboxUsePluginRegistry.isSelected() != data.isUsePluginRegistry()) return true;
//        if (checkboxSkipTests.isSelected() != data.isSkipTests()) return true;
        if (checkboxNonRecursive.isSelected() != data.isNonRecursive()) return true;
        if (!((CustomizingObject) comboboxOutputLevel.getSelectedItem()).getValue()
                .equals(String.valueOf(data.getOutputLevel())))
            return true;
        if (!((CustomizingObject) comboboxChecksumPolicy.getSelectedItem()).getValue().equals(data.getChecksumPolicy()))
            return true;
        if (!((CustomizingObject) comboboxMultiprojectBuildFailPolicy.getSelectedItem()).getValue()
                .equals(data.getFailureBehavior()))
            return true;
        if (!((CustomizingObject) comboboxPluginUpdatePolicy.getSelectedItem()).getValue()
                .equals(String.valueOf(data.isPluginUpdatePolicy())))
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
        panel.setLayout(new FormLayout(
                "fill:d:grow,left:4dlu:noGrow,fill:300px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:d:grow",
                "center:d:noGrow,top:4dlu:noGrow,center:15px:noGrow,top:1dlu:noGrow,center:27px:noGrow,top:1dlu:noGrow,center:24px:noGrow,top:1dlu:noGrow,center:max(d;4px):noGrow,top:1dlu:noGrow,center:max(d;4px):noGrow,top:1dlu:noGrow,center:24px:noGrow,top:4dlu:noGrow,center:16px:noGrow,top:1dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow"));
        labelOutputLevel = new JLabel();
        labelOutputLevel.setText("Output Level");
        CellConstraints cc = new CellConstraints();
        panel.add(labelOutputLevel,
                new CellConstraints(3, 3, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT, new Insets(2, 5, 0, 0)));
        checkboxWorkOffline = new JCheckBox();
        checkboxWorkOffline.setText("Work Offline");
        panel.add(checkboxWorkOffline, cc.xy(3, 1));
        comboboxOutputLevel = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        comboboxOutputLevel.setModel(defaultComboBoxModel1);
        panel.add(comboboxOutputLevel,
                new CellConstraints(3, 5, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT, new Insets(0, 4, 2, 0)));
        textFieldLocalRepository = new JTextField();
        textFieldLocalRepository.setText("");
        panel.add(textFieldLocalRepository, new CellConstraints(3, 17, 1, 1, CellConstraints.FILL,
                CellConstraints.DEFAULT, new Insets(0, 3, 0, 0)));
        checkboxProduceExceptionErrorMessages = new JCheckBox();
        checkboxProduceExceptionErrorMessages.setText("Produce Exception Error Messages");
        panel.add(checkboxProduceExceptionErrorMessages, cc.xy(3, 7));
        buttonBrowseLocalRepository = new JButton();
        buttonBrowseLocalRepository.setText("Browse ...");
        panel.add(buttonBrowseLocalRepository, cc.xy(5, 17, CellConstraints.LEFT, CellConstraints.DEFAULT));
        comboboxPluginUpdatePolicy = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("No Global Policy");
        defaultComboBoxModel2.addElement("Check For Updates");
        defaultComboBoxModel2.addElement("Supress Checking");
        comboboxPluginUpdatePolicy.setModel(defaultComboBoxModel2);
        panel.add(comboboxPluginUpdatePolicy, cc.xy(5, 13, CellConstraints.LEFT, CellConstraints.CENTER));
        labelPluginUpdatePolicy = new JLabel();
        labelPluginUpdatePolicy.setText("Plugin Update Policy");
        panel.add(labelPluginUpdatePolicy, new CellConstraints(5, 11, 1, 1, CellConstraints.LEFT,
                CellConstraints.DEFAULT, new Insets(8, 1, 0, 0)));
        comboboxMultiprojectBuildFailPolicy = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("Stop at first failure");
        defaultComboBoxModel3.addElement("Fail at the end");
        defaultComboBoxModel3.addElement("Never fail");
        comboboxMultiprojectBuildFailPolicy.setModel(defaultComboBoxModel3);
        panel.add(comboboxMultiprojectBuildFailPolicy, cc.xy(5, 9, CellConstraints.LEFT, CellConstraints.DEFAULT));
        labelMultiprojectBuildFailPolicy = new JLabel();
        labelMultiprojectBuildFailPolicy.setText("Multiproject Build Fail Policy");
        panel.add(labelMultiprojectBuildFailPolicy,
                new CellConstraints(5, 7, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT, new Insets(8, 1, 0, 0)));
        comboboxChecksumPolicy = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("No Global Policy");
        defaultComboBoxModel4.addElement("Strict (Fail)");
        defaultComboBoxModel4.addElement("Lax (Warn Only)");
        comboboxChecksumPolicy.setModel(defaultComboBoxModel4);
        panel.add(comboboxChecksumPolicy, cc.xy(5, 5, CellConstraints.LEFT, CellConstraints.DEFAULT));
        labelChecksumPolicy = new JLabel();
        labelChecksumPolicy.setText("Checksum Policy");
        panel.add(labelChecksumPolicy,
                new CellConstraints(5, 3, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT, new Insets(2, 0, 0, 0)));
        labelLocalRepository = new JLabel();
        labelLocalRepository.setText("Local Repository");
        panel.add(labelLocalRepository, new CellConstraints(3, 15, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT,
                new Insets(2, 5, 0, 0)));
        final Spacer spacer1 = new Spacer();
        panel.add(spacer1, cc.xy(1, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        final Spacer spacer2 = new Spacer();
        panel.add(spacer2, cc.xy(7, 9, CellConstraints.FILL, CellConstraints.DEFAULT));
        final Spacer spacer3 = new Spacer();
        panel.add(spacer3, cc.xy(3, 19, CellConstraints.DEFAULT, CellConstraints.FILL));
        checkboxUsePluginRegistry = new JCheckBox();
        checkboxUsePluginRegistry.setText("Use Plugin Registry");
        panel.add(checkboxUsePluginRegistry, cc.xy(3, 9));
        checkboxNonRecursive = new JCheckBox();
        checkboxNonRecursive.setText("Non Recursive");
        panel.add(checkboxNonRecursive, cc.xy(3, 11));
        labelOutputLevel.setLabelFor(comboboxOutputLevel);
        labelPluginUpdatePolicy.setLabelFor(comboboxPluginUpdatePolicy);
        labelMultiprojectBuildFailPolicy.setLabelFor(comboboxMultiprojectBuildFailPolicy);
        labelChecksumPolicy.setLabelFor(comboboxChecksumPolicy);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
