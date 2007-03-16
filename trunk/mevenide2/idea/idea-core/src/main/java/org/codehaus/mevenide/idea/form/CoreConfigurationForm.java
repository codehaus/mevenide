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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.codehaus.mevenide.idea.model.MavenConfiguration;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * This is the core configuration dialog container. It acts as a single point of configuration
 * for all Mevenide2 IDEA plugins.
 *
 * @author Ralf Quebbemann (ralfq@codehaus.org)
 */
public class CoreConfigurationForm {
    private JPanel panel;
    private JTabbedPane tabbedPane;
    private MavenCoreSetupDialog mavenCoreSetupDialog;

    public CoreConfigurationForm() {
        mavenCoreSetupDialog = new MavenCoreSetupDialog();
        tabbedPane.add("Core", mavenCoreSetupDialog.getRootComponent());
    }

    // Method returns the root component of the form
    public JComponent getRootComponent() {
        return panel;
    }


    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public void setData(MavenConfiguration data) {
        mavenCoreSetupDialog.setData(data);
    }

    public void getData(MavenConfiguration data) {
        mavenCoreSetupDialog.getData(data);
    }

    public boolean isModified(MavenConfiguration data) {
        return mavenCoreSetupDialog.isModified(data);
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
        panel.setLayout(new FormLayout("fill:d:grow", "center:d:grow"));
        tabbedPane = new JTabbedPane();
        CellConstraints cc = new CellConstraints();
        panel.add(tabbedPane, cc.xy(1, 1));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}