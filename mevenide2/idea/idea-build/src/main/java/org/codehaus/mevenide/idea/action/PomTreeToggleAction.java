package org.codehaus.mevenide.idea.action;

import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.project.Project;
import org.codehaus.mevenide.idea.gui.PomTreeView;
import org.codehaus.mevenide.idea.component.MavenBuildProjectComponent;

public abstract class PomTreeToggleAction extends ToggleAction {
    public void update(final AnActionEvent e) {
          super.update(e);
          e.getPresentation().setEnabled(getView(e)!=null);
      }

    private PomTreeView getView(AnActionEvent e) {
        Project project = (Project) e.getDataContext().getData(DataConstants.PROJECT);
        return project == null ? null : MavenBuildProjectComponent.getInstance(project).getPomTreeView();
    }

    public boolean isSelected(AnActionEvent e) {
        final PomTreeView view = getView(e);
        return view != null && isSelected(view);
    }

    public void setSelected(AnActionEvent e, boolean state) {
        final PomTreeView view = getView(e);
        if ( view != null ) {
            setSelected(view, state);
            view.updateStructure();
        }
    }

    public abstract boolean isSelected(PomTreeView view);

    public abstract void setSelected(PomTreeView view, boolean state);
}
