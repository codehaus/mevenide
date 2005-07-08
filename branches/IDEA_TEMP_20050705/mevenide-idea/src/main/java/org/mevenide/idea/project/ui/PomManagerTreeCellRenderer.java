package org.mevenide.idea.project.ui;

import com.intellij.openapi.project.Project;
import com.intellij.util.PathUtil;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.goals.Goal;
import org.mevenide.idea.project.goals.PluginGoalContainer;
import org.mevenide.idea.psi.PomModelManager;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class PomManagerTreeCellRenderer extends DefaultTreeCellRenderer {
    private final Project project;

    public PomManagerTreeCellRenderer(final Project pProject) {
        project = pProject;
    }

    @Override
    public Component getTreeCellRendererComponent(final JTree pTree,
                                                  final Object pValue,
                                                  final boolean pSelected,
                                                  final boolean pExpanded,
                                                  final boolean pLead,
                                                  final int pRow,
                                                  final boolean pHasFocus) {

        final Component c = super.getTreeCellRendererComponent(pTree,
                                                               pValue,
                                                               pSelected,
                                                               pExpanded,
                                                               pLead,
                                                               pRow,
                                                               pHasFocus);
        if (c instanceof JLabel) {
            final JLabel label = (JLabel) c;

            if (pValue instanceof PomNode) {
                final PomNode pomNode = (PomNode) pValue;

                label.setIcon(Icons.MAVEN);

                final PomManager pomMgr = PomManager.getInstance(project);
                final String url = pomNode.getUserObject();
                if (!pomMgr.isValid(url))
                    label.setForeground(Color.RED);

                final PomModelManager modelMgr = PomModelManager.getInstance(project);
                final PsiProject psi = modelMgr.getPsiProject(url);

                //TODO: show path relative to project root
                String text = PathUtil.toPresentableUrl(url);
                if (psi != null) {
                    final String name = psi.getName();
                    if (name == null || name.trim().length() == 0) {
                        final String groupId = psi.getGroupId();
                        final String artifactId = psi.getArtifactId();
                        if (groupId != null && groupId.trim().length() > 0 && artifactId != null && artifactId.trim().length() > 0)
                            text = groupId + ":" + artifactId;
                    }
                    else
                        text = name;
                }
                label.setText(text);
            }
            else if (pValue instanceof PluginNode) {
                final PluginNode node = (PluginNode) pValue;
                final PluginGoalContainer plugin = node.getUserObject();
                label.setText(plugin.getName() + " (" + plugin.getVersion() + ")");
                label.setIcon(Icons.PLUGIN);
            }
            else if (pValue instanceof GoalNode) {
                final GoalNode node = (GoalNode) pValue;
                final Goal goal = node.getUserObject();
                label.setText(goal.getName());
                label.setIcon(Icons.GOAL);
            }
        }

        return c;
    }
}
