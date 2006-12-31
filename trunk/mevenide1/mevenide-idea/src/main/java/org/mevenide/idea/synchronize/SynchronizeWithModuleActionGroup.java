package org.mevenide.idea.synchronize;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.ModuleListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import org.mevenide.idea.Res;
import org.mevenide.idea.project.ui.PomManagerPanel;
import org.mevenide.idea.synchronize.ui.SynchronizationResultsPanel;
import org.mevenide.idea.util.FileUtils;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.actions.AbstractAnActionGroup;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class SynchronizeWithModuleActionGroup extends AbstractAnActionGroup {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(SynchronizeWithModuleActionGroup.class);

    public SynchronizeWithModuleActionGroup(final Project pProject) {
        super("Synchronize POM with module", true);
        final ModuleManager moduleMgr = ModuleManager.getInstance(pProject);
        final Module[] modules = moduleMgr.getModules();
        for (Module module : modules)
            add(new SynchronizeWithModuleAction(module));

        moduleMgr.addModuleListener(new ModuleListener() {
            public void moduleAdded(Project project, Module module) {
                add(new SynchronizeWithModuleAction(module));
            }

            public void beforeModuleRemoved(Project project, Module module) {
            }

            public void moduleRemoved(Project project, Module module) {
                final AnAction[] actions = getChildren(null);
                for (AnAction action : actions) {
                    if (!(action instanceof SynchronizeWithModuleAction))
                        continue;

                    final SynchronizeWithModuleAction act = (SynchronizeWithModuleAction) action;
                    if (act.module == module)
                        remove(act);
                }
            }

            public void modulesRenamed(Project project, List<Module> modules) {
                for (Module module : modules) {
                    final AnAction[] actions = getChildren(null);
                    for (AnAction action : actions) {
                        if (!(action instanceof SynchronizeWithModuleAction))
                            continue;

                        final SynchronizeWithModuleAction act = (SynchronizeWithModuleAction) action;
                        if (act.module == module) {
                            act.getTemplatePresentation().setText(
                                    RES.get("sync.module.action.text", module.getName()));
                            act.getTemplatePresentation().setDescription(
                                    RES.get("sync.module.action.desc", module.getName()));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void update(final AnActionEvent pEvent) {
        final boolean enabled = getSelectedPomUrl(pEvent) != null;
        if (PomManagerPanel.PLACE.equalsIgnoreCase(pEvent.getPlace()))
            pEvent.getPresentation().setEnabled(enabled);
        else
            pEvent.getPresentation().setVisible(enabled);
    }

    /**
     * @param pEvent
     *
     * @return selected pom url (only if it is a pom)
     * @todo this is already implemented in {@link org.mevenide.idea.project.actions.AbstractPomAnAction}
     */
    private String getSelectedPomUrl(final AnActionEvent pEvent) {
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

    private class SynchronizeWithModuleAction extends AbstractAnAction {
        private final Module module;

        public SynchronizeWithModuleAction(final Module pModule) {
            super(RES.get("sync.module.action.text", pModule.getName()),
                  RES.get("sync.module.action.desc", pModule.getName()),
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

            final String url = getSelectedPomUrl(pEvent);
            if (url == null)
                return;

            tw.setAvailable(true, null);
            tw.show(new Runnable() {
                public void run() {
                    final SynchronizationResultsPanel ui;
                    ui = (SynchronizationResultsPanel) tw.getComponent();
                    final InspectionsManager mgr = InspectionsManager.getInstance(project);
                    final ProblemInfo[] problems = mgr.inspect(url, module);
                    ui.setProblems(problems);
                }
            });
        }
    }
}
