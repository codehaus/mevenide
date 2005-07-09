package org.mevenide.idea.synchronize;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.mevenide.idea.Res;
import org.mevenide.idea.project.ui.PomManagerPanel;
import org.mevenide.idea.synchronize.ui.SynchronizationResultsPanel;
import org.mevenide.idea.psi.PomModelManager;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.actions.AbstractAnActionGroup;
import org.mevenide.idea.util.ui.images.Icons;
import javax.swing.*;
import java.awt.*;

/**
 * @author Arik
 */
public class SynchronizeWithModuleActionGroup extends AbstractAnActionGroup {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(SynchronizeWithModuleActionGroup.class);

    public AnAction[] getChildren(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if (project == null)
            return new AnAction[0];

        final String pomUrl = getSelectedPomUrl(pEvent);
        if(pomUrl == null || pomUrl.trim().length() == 0)
            return new AnAction[0];

        final Module[] modules = ModuleManager.getInstance(project).getModules();
        final AnAction[] actions = new AnAction[modules.length];
        for (int i = 0; i < modules.length; i++)
            actions[i] = new SynchronizeWithModuleAction(pomUrl, modules[i]);

        return actions;
    }

    @Override
    public void update(final AnActionEvent pEvent) {
        pEvent.getPresentation().setEnabled(getSelectedPomUrl(pEvent) != null);
    }

    private String getSelectedPomUrl(final AnActionEvent pEvent) {
        if (PomManagerPanel.PLACE.equals(pEvent.getPlace())) {
            final Component comp = pEvent.getInputEvent().getComponent();
            final PomManagerPanel pomPanel = (PomManagerPanel) SwingUtilities.getAncestorOfClass(
                    PomManagerPanel.class,
                    comp);
            return pomPanel.getSelectedPomUrl();
        }
        else
            return null;
    }

    private class SynchronizeWithModuleAction extends AbstractAnAction {
        private final Module module;

        public SynchronizeWithModuleAction(final String pPomUrl,
                                           final Module pModule) {
            super(RES.get("sync.module.action.text", pModule.getName()),
                  RES.get("sync.module.action.desc",
                          getPomName(pModule.getProject(), pPomUrl),
                          pModule.getName()),
                  Icons.SYNC);

            module = pModule;
        }

        public void actionPerformed(final AnActionEvent pEvent) {
            final Project project = getProject(pEvent);
            if (project == null)
                return;

            final ToolWindow tw = SynchronizationResultsPanel.getInstance(project);
            if (tw == null)
                return;

            tw.setAvailable(true, null);
            tw.show(new Runnable() {
                public void run() {
                    final SynchronizationResultsPanel ui;
                    ui = (SynchronizationResultsPanel) tw.getComponent();

                    //TODO: inspect only our module!
                    final InspectionsManager mgr = InspectionsManager.getInstance(project);
                    final ProblemInfo[] problems = mgr.inspect();
                    ui.setProblems(problems);
                }
            });
        }
    }

    private static String getPomName(final Project pProject, final String pPomUrl) {
        final PsiProject psi = PomModelManager.getInstance(pProject).getPsiProject(pPomUrl);
        if (psi == null)
            return null;

        return psi.getName();
    }
}
