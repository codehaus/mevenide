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

import org.codehaus.mevenide.idea.common.util.ErrorHandler;
import org.codehaus.mevenide.idea.gui.PomTree;
import org.codehaus.mevenide.idea.gui.form.MavenBuildProjectToolWindowForm;
import org.codehaus.mevenide.idea.helper.ActionContext;
import org.codehaus.mevenide.idea.util.GuiUtils;
import org.codehaus.mevenide.idea.util.PluginConstants;

import java.util.Enumeration;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class FilterAction extends AbstractBaseAction {
    public FilterAction() {}

    public FilterAction(ActionContext context, String text, String description, Icon icon) {
        super(text, description, icon);
        this.actionContext = context;
    }

    /**
     * Method description
     *
     * @param actionEvent Document me!
     */
    public void actionPerformed(AnActionEvent actionEvent) {
        String actionText = actionEvent.getPresentation().getText();

        if (actionText.equals(PluginConstants.ACTION_COMMAND_FILTER)
                || actionText.equals(PluginConstants.ACTION_COMMAND_FILTER_RELEASE)) {
            try {
                if (!actionContext.getProjectPluginSettings().isUseFilter()) {
                    toggleFilter(actionContext, true);
                    actionContext.getProjectPluginSettings().setUseFilter(true);
                    actionEvent.getPresentation().setIcon(
                        GuiUtils.createImageIcon(PluginConstants.ICON_FILTER_APPLIED));
                    actionEvent.getPresentation().setText(PluginConstants.ACTION_COMMAND_FILTER_RELEASE);
                } else {
                    toggleFilter(actionContext, false);
                    actionContext.getProjectPluginSettings().setUseFilter(false);
                    actionEvent.getPresentation().setIcon(GuiUtils.createImageIcon(PluginConstants.ICON_FILTER));
                    actionEvent.getPresentation().setText(PluginConstants.ACTION_COMMAND_FILTER);
                }
            } catch (Exception e) {
                ErrorHandler.processAndShowError(actionContext.getPluginProject(), e);
            }
        }
    }

    /**
     * Method description
     *
     * @param context  Document me!
     * @param doFilter true in case the filter should be applied, false otherwise.
     */
    private void toggleFilter(ActionContext context, boolean doFilter) {
        PomTree pomTree =
            ((MavenBuildProjectToolWindowForm) context.getGuiContext().getMavenToolWindowForm()).getPomTree();
        TreePath[] selectedPaths = pomTree.getSelectionPaths();
        Enumeration expandedPaths = pomTree.getExpandedDescendants(
                                        new TreePath(
                                            ((DefaultMutableTreeNode) pomTree.getModel().getRoot()).getPath()));

        if (doFilter) {
            ActionUtils.filterStandardPhasesInNodes(context, (DefaultMutableTreeNode) pomTree.getModel().getRoot());
        } else {
            ActionUtils.unfilterStandardPhasesInNodes(context, (DefaultMutableTreeNode) pomTree.getModel().getRoot());
        }

        reloadTree(pomTree, expandedPaths, selectedPaths);
    }

    private void reloadTree(PomTree pomTree, Enumeration expandedPaths, TreePath[] selectedPaths) {
        DefaultTreeModel model = (DefaultTreeModel) pomTree.getModel();

        model.reload();

        if (expandedPaths != null) {
            while (expandedPaths.hasMoreElements()) {
                pomTree.expandPath((TreePath) expandedPaths.nextElement());
            }
        }

        if (selectedPaths != null) {
            pomTree.setSelectionPaths(selectedPaths);
        }
    }
}
