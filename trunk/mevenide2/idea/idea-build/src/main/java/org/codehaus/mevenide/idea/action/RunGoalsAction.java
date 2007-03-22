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
import org.codehaus.mevenide.idea.common.util.ErrorHandler;
import org.codehaus.mevenide.idea.helper.ActionContext;
import org.codehaus.mevenide.idea.util.GuiUtils;
import org.codehaus.mevenide.idea.util.IdeaMavenPluginException;
import org.codehaus.mevenide.idea.util.PluginConstants;
import org.codehaus.mevenide.idea.gui.PomTreeUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class RunGoalsAction extends AbstractBaseAction {

    /**
     * Constructs ...
     */
    public RunGoalsAction() {}

    /**
     * Constructs ...
     *
     * @param actionContext Document me!
     * @param text          Document me!
     * @param description   Document me!
     * @param icon          Document me!
     */
    public RunGoalsAction(ActionContext actionContext, String text, String description, Icon icon) {
        super(text, description, icon);
        this.actionContext = actionContext;
    }

    /**
     * Method description
     *
     * @param actionEvent Document me!
     */
    public void actionPerformed(AnActionEvent actionEvent) {
        String actionText = actionEvent.getPresentation().getText();

        if (actionText.equals(PluginConstants.ACTION_COMMAND_RUN_GOALS)) {
            try {
                ActionUtils
                    .runSelectedGoals(actionContext,
                                      GuiUtils.getSortedSelectedNodeObjects(PomTreeUtil.getPomTree(actionContext)));
            } catch (IdeaMavenPluginException e) {
                ErrorHandler.processAndShowError(actionContext.getPluginProject(), e, false);
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

        if ((actionContext != null) && (actionContext.getGuiContext().getMavenToolWindowForm() != null)) {
            List<DefaultMutableTreeNode> selectedNodeList =
                GuiUtils
                    .getSelectedNodeObjects(PomTreeUtil.getPomTree(actionContext));

            if (selectedNodeList.isEmpty()) {
                presentation.setEnabled(false);

                return;
            }

            if (PomTreeUtil.nodesAreExecutableMavenGoals(selectedNodeList)) {
                presentation.setEnabled(true);
            } else {
                presentation.setEnabled(false);
            }
        }
    }
}
