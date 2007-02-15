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

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import org.codehaus.mevenide.idea.helper.BuildContext;
import org.codehaus.mevenide.idea.util.PluginConstants;

import javax.swing.*;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class CloseOutputPanelAction extends AbstractBuildAction {
    private ToolWindowManager manager;

    /**
     * Constructs ...
     */
    public CloseOutputPanelAction() {}

    /**
     * Constructs ...
     *
     * @param buildContext Document me!
     * @param text         Document me!
     * @param description  Document me!
     * @param icon         Document me!
     */
    public CloseOutputPanelAction(BuildContext buildContext, String text, String description, Icon icon) {
        super(text, description, icon);
        this.buildContext = buildContext;
        this.actionContext = buildContext.getActionContext();
        manager = ToolWindowManager.getInstance(buildContext.getActionContext().getPluginProject());
    }

    /**
     * Method description
     *
     * @param actionEvent Document me!
     */
    public void actionPerformed(AnActionEvent actionEvent) {
        String actionText = actionEvent.getPresentation().getText();

        if (actionText.equals(PluginConstants.ACTION_COMMAND_CLOSE_OUTPUT_PANEL)) {
            ToolWindow toolWindow = manager.getToolWindow(PluginConstants.OUTPUT_TOOL_WINDOW_ID);

            if (toolWindow != null) {
                manager.unregisterToolWindow(PluginConstants.OUTPUT_TOOL_WINDOW_ID);
            }
        }
    }

    /**
     * Method description
     *
     * @param e Document me!
     */
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();

        if (actionContext != null) {
            ToolWindow toolWindow = manager.getToolWindow(PluginConstants.OUTPUT_TOOL_WINDOW_ID);

            if (toolWindow != null) {
                presentation.setEnabled(true);
            } else {
                presentation.setEnabled(false);
            }
        }
    }
}
