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

import org.codehaus.mevenide.idea.CorePlugin;

import javax.swing.*;

/**
 * Todo: Describe what this class does!
 *
 * @author Ralf Quebbemann (ralfq@codehaus.org)
 */
public class CoreConfigurationForm {
    private JPanel panel;
    private JTabbedPane tabbedPane;
    private JCheckBox checkboxScanForExistingPoms;

    // Method returns the root component of the form
    public JComponent getRootComponent() {
        return panel;
    }


    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public void setData(CorePlugin data) {
        checkboxScanForExistingPoms.setSelected(data.isScanForExistingPoms());
    }

    public void getData(CorePlugin data) {
        data.setScanForExistingPoms(checkboxScanForExistingPoms.isSelected());
    }

    public boolean isModified(CorePlugin data) {
        if (checkboxScanForExistingPoms.isSelected() != data.isScanForExistingPoms()) return true;
        return false;
    }
}
