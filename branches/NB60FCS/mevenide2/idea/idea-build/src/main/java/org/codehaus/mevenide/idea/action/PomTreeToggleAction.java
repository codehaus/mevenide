package org.codehaus.mevenide.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import org.codehaus.mevenide.idea.component.MavenBuildProjectComponent;
import org.codehaus.mevenide.idea.gui.PomTreeStructure;

public abstract class PomTreeToggleAction extends ToggleAction {
    public void update(final AnActionEvent e) {
          super.update(e);
          e.getPresentation().setEnabled(getTreeStructure(e)!=null);
      }

    private PomTreeStructure getTreeStructure(AnActionEvent e) {
        Project project = (Project) e.getDataContext().getData(DataConstants.PROJECT);
        return project == null ? null : MavenBuildProjectComponent.getInstance(project).getPomTreeStructure();
    }

    public boolean isSelected(AnActionEvent e) {
        final PomTreeStructure structure = getTreeStructure(e);
        return structure != null && isSelected(structure);
    }

    public void setSelected(AnActionEvent e, boolean state) {
        final PomTreeStructure structure = getTreeStructure(e);
        if ( structure != null ) {
            setSelected(structure, state);
            structure.updateFromRoot(true);
        }
    }

    public abstract boolean isSelected(PomTreeStructure structure);

    public abstract void setSelected(PomTreeStructure structure, boolean state);
}
