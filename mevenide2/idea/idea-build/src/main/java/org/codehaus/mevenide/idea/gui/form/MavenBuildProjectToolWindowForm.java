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

import org.codehaus.mevenide.idea.gui.PomTree;
import org.codehaus.mevenide.idea.util.PluginConstants;

import java.awt.*;

import javax.swing.*;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenBuildProjectToolWindowForm extends AbstractForm {
    private JPanel panel;
    private JScrollPane scrollpane;
    private JTextField textFieldCmdLine;

    /**
     * Constructs ...
     */
    public MavenBuildProjectToolWindowForm() {
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        textFieldCmdLine = new JTextField();
        textFieldCmdLine.setName(PluginConstants.ACTION_COMMAND_EDIT_MAVEN_COMMAND_LINE);
        panel.add(textFieldCmdLine,
                  new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                      GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                      GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null));
        scrollpane = new JScrollPane();
        panel.add(scrollpane,
                  new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                      GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                                      GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                                      null, null, null));
    }

    public JTextField getTextFieldCmdLine() {
        return textFieldCmdLine;
    }

    public PomTree getPomTree() {
        return (PomTree) scrollpane.getViewport().getView();
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public JScrollPane getScrollpane() {
        return scrollpane;
    }

    public JComponent getRootComponent() {
        return panel;    // To change body of implemented methods use File | Settings | File Templates.
    }
}
