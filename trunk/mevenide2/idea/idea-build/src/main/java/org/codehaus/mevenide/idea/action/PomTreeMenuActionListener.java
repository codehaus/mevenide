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

import org.apache.log4j.Logger;
import org.codehaus.mevenide.idea.common.util.ErrorHandler;
import org.codehaus.mevenide.idea.helper.ActionContext;
import org.codehaus.mevenide.idea.util.PluginConstants;
import org.codehaus.mevenide.idea.gui.PomTreeUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class PomTreeMenuActionListener extends AbstractBaseActionListener implements ActionListener {
    private static final Logger LOG = Logger.getLogger(PomTreeMenuActionListener.class);

    /**
     * Constructs ...
     *
     * @param context Document me!
     */
    public PomTreeMenuActionListener(ActionContext context) {
        this.context = context;
    }

    /**
     * Method description
     *
     * @param actionEvent Document me!
     */
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();

        if (source instanceof JMenuItem) {
            String menuText = ((JMenuItem) source).getText();

            LOG.debug("Selected menu item: " + menuText);

            try {
                if (menuText.equals(PluginConstants.ACTION_COMMAND_ADD_POM)) {
                    ActionUtils.chooseAndAddPomToTree(context);
                } else if (menuText.equals(PluginConstants.ACTION_COMMAND_OPEN_POM)) {
                    ActionUtils.openPom(context);
                } else if (menuText.equals(PluginConstants.ACTION_COMMAND_REMOVE_POM)) {
                    ActionUtils.removePomFromTree(context);
                } else if (menuText.equals(PluginConstants.ACTION_COMMAND_ADD_PLUGIN)) {
                    ActionUtils.chooseAndAddPluginToPom(context, PomTreeUtil.getPomTree(context));
                } else if (menuText.equals(PluginConstants.ACTION_COMMAND_REMOVE_PLUGIN)) {
                    ActionUtils.removeSelectedPluginsFromPom(context);
                } else if (menuText.equals(PluginConstants.ACTION_COMMAND_RUN_GOALS)) {
                    ActionUtils.runSelectedGoals(context, null);
                }
            } catch (Exception e) {
                ErrorHandler.processAndShowError(context.getPluginProject(), e, false);
            }
        }
    }
}
