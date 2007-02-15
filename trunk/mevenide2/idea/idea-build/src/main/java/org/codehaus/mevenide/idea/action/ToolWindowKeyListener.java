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



package org.codehaus.mevenide.idea.action;

import org.codehaus.mevenide.idea.gui.form.MavenProjectConfigurationForm;
import org.codehaus.mevenide.idea.helper.ActionContext;
import org.codehaus.mevenide.idea.util.PluginConstants;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class ToolWindowKeyListener implements KeyListener {

    /**
     * Field description
     */
    protected ActionContext context;

    public ToolWindowKeyListener(ActionContext context) {
        this.context = context;
    }

    public void keyTyped(KeyEvent keyEvent) {}

    public void keyPressed(KeyEvent keyEvent) {}

    public void keyReleased(KeyEvent keyEvent) {
        if (keyEvent.getSource() instanceof JTextField) {
            JTextField textfield = (JTextField) keyEvent.getSource();

            if (textfield.getName().equals(PluginConstants.ACTION_COMMAND_EDIT_MAVEN_COMMAND_LINE)) {
                if (this.context.getGuiContext().getProjectConfigurationForm() != null) {
                    this.context.getProjectPluginSettings().setMavenCommandLineParams(textfield.getText());
                    ((MavenProjectConfigurationForm) this.context.getGuiContext().getProjectConfigurationForm())
                        .setData(this.context.getProjectPluginSettings());
                }
            }
        }
    }
}
