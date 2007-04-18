package org.codehaus.mevenide.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.project.Project;
import org.codehaus.mevenide.idea.component.MavenBuildProjectComponent;
import org.codehaus.mevenide.idea.gui.PomTreeView;
import org.codehaus.mevenide.idea.helper.ActionContext;

public abstract class PomTreeAction extends AnAction {
    public void update(final AnActionEvent e) {
        super.update(e);
        e.getPresentation().setEnabled(isEnabled(e));
    }

    protected boolean isEnabled(AnActionEvent e) {
        return getView(e) != null;
    }

    protected PomTreeView getView(AnActionEvent e) {
        Project project = (Project) e.getDataContext().getData(DataConstants.PROJECT);
        return project == null ? null : MavenBuildProjectComponent.getInstance(project).getPomTreeView();
    }

    protected ActionContext getActionContext(AnActionEvent e) {
        Project project = (Project) e.getDataContext().getData(DataConstants.PROJECT);
        return project == null ? null : MavenBuildProjectComponent.getInstance(project).getActionContext();
    }
}
