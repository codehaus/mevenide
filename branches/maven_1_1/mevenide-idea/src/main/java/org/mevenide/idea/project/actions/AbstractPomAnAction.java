package org.mevenide.idea.project.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import java.awt.*;
import javax.swing.*;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.ui.PomManagerPanel;
import org.mevenide.idea.util.FileUtils;
import org.mevenide.idea.util.actions.AbstractAnAction;

/**
 * @author Arik Kfir
 */
public abstract class AbstractPomAnAction extends AbstractAnAction {
    protected AbstractPomAnAction() {
    }

    protected AbstractPomAnAction(final String pText) {
        super(pText);
    }

    protected AbstractPomAnAction(final String pText, final String pDescription, final Icon pIcon) {
        super(pText, pDescription, pIcon);
    }

    @Override
    public void update(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if (project == null) {
            pEvent.getPresentation().setEnabled(false);
            return;
        }

        final VirtualFile file = getVirtualFile(pEvent);
        final PomManager pomMgr = PomManager.getInstance(project);

        final boolean enabled = file != null && !pomMgr.contains(file.getUrl());
        if (pEvent.getPlace().equalsIgnoreCase(PomManagerPanel.PLACE))
            pEvent.getPresentation().setEnabled(enabled);
        else
            pEvent.getPresentation().setVisible(enabled);
    }

    protected String getSelectedPomUrl(final AnActionEvent pEvent) {
        if (PomManagerPanel.PLACE.equals(pEvent.getPlace())) {
            final Component comp = pEvent.getInputEvent().getComponent();
            final PomManagerPanel pomPanel = (PomManagerPanel) SwingUtilities.getAncestorOfClass(
                    PomManagerPanel.class,
                    comp);
            return pomPanel.getSelectedPomUrl();
        }
        else {
            final VirtualFile file = getVirtualFile(pEvent);
            if (file == null || !file.isValid() || !FileUtils.exists(file))
                return null;
            else
                return file.getUrl();
        }
    }
}
