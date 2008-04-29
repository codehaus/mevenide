package org.codehaus.mevenide.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.project.Project;
import org.codehaus.mevenide.idea.component.MavenBuildProjectComponent;
import org.codehaus.mevenide.idea.gui.PomTreeStructure;

public abstract class PomTreeAction extends AnAction {
    public void update(final AnActionEvent e) {
        e.getPresentation().setEnabled(isEnabled(e));
    }

    protected boolean isEnabled(AnActionEvent e) {
        return getTreeStructure(e) != null;
    }

    protected PomTreeStructure getTreeStructure(AnActionEvent e) {
        Project project = (Project) e.getDataContext().getData(DataConstants.PROJECT);
        return project == null ? null : MavenBuildProjectComponent.getInstance(project).getPomTreeStructure();
    }
}
