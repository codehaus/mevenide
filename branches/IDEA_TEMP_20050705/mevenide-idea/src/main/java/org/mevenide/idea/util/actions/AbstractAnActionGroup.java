package org.mevenide.idea.util.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author Arik Kfir
 */
public abstract class AbstractAnActionGroup extends DefaultActionGroup {
    protected AbstractAnActionGroup() {
    }

    protected AbstractAnActionGroup(String shortName, boolean popup) {
        super(shortName, popup);
    }

    protected Project getProject(final AnActionEvent pEvent) {
        return (Project) pEvent.getDataContext().getData(DataConstants.PROJECT);
    }

    @Override
    public void update(final AnActionEvent pEvent) {
        super.update(pEvent);
    }

    protected VirtualFile getVirtualFile(final AnActionEvent pEvent) {
        final DataContext dc = pEvent.getDataContext();
        return (VirtualFile) dc.getData(DataConstants.VIRTUAL_FILE);
    }
}
