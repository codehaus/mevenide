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

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.codehaus.mevenide.idea.common.MavenBuildPluginSettings;
import org.codehaus.mevenide.idea.component.MavenBuildProjectComponent;
import org.codehaus.mevenide.idea.gui.PomTreeStructure;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
    private Project myProject;

    class ProjectPanel extends JPanel implements DataProvider {

        @Nullable
        public Object getData(@NonNls String dataId) {
            if ( dataId.equals(DataConstants.PROJECT)) {
                return myProject;
            }
            if ( dataId.equals(DataConstants.NAVIGATABLE_ARRAY)){
                PomTreeStructure treeStructure = MavenBuildProjectComponent.getInstance(myProject).getPomTreeStructure();
                return treeStructure.getNavigatables();
            }
            return null;
        }
    }

    public MavenBuildProjectToolWindowForm(Project project, final MavenBuildPluginSettings settings) {
        this.myProject = project;
        panel = new ProjectPanel();
        panel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("New Maven Toolbar",
                (ActionGroup) ActionManager.getInstance().getAction("org.codehaus.mevenide.idea.action.PomTreeToolbar"), true);
        panel.add(actionToolbar.getComponent(),
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null));

        textFieldCmdLine = new JTextField();
        textFieldCmdLine.setText(settings.getMavenCommandLineParams());
        textFieldCmdLine.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
                settings.setMavenCommandLineParams(textFieldCmdLine.getText());
            }
        });
//        textFieldCmdLine.setName(PluginConstants.ACTION_COMMAND_EDIT_MAVEN_COMMAND_LINE);

        panel.add(textFieldCmdLine,
                  new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                      GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                      GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null));
        scrollpane = new JScrollPane();
        panel.add(scrollpane,
                  new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_BOTH,
                                      GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                                      GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                                      null, null, null));
    }

    public JTextField getTextFieldCmdLine() {
        return textFieldCmdLine;
    }

    public JScrollPane getScrollpane() {
        return scrollpane;
    }

    public JComponent getRootComponent() {
        return panel;
    }
}
