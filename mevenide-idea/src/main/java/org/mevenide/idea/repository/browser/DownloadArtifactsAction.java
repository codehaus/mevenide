package org.mevenide.idea.repository.browser;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SelectFromListDialog;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.idea.Res;
import org.mevenide.idea.toolwindows.repository.RepoToolWindow;
import org.mevenide.idea.module.ModuleLocationFinder;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.idea.repository.download.ArtifactDownloadManager;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.images.Icons;
import org.mevenide.repository.RepoPathElement;

/**
 * @author Arik
 */
public class DownloadArtifactsAction extends AbstractAnAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(DownloadArtifactsAction.class);
    private static final ModuleToStringAspect MODULE_TO_STRING_ASPECT = new ModuleToStringAspect();

    public DownloadArtifactsAction() {
        super(RES.get("download.action.text"),
              RES.get("download.action.desc"),
              Icons.DOWNLOAD);
    }

    @Override
    public boolean displayTextInToolbar() {
        return true;
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final Module selectedModule = getModuleForDownload(pEvent);
        if (selectedModule == null)
            return;

        //
        //prepare list of path elements to download
        //
        final Project project = getProject(pEvent);
        final RepoToolWindow tw = RepoToolWindow.getInstance(project);
        final RepoPathElement[] pathElements = tw.getSelectedItems();

        final Runnable downloader = new Runnable() {
            public void run() {
                final ArtifactDownloadManager downloadMgr = ArtifactDownloadManager.getInstance();
                for (RepoPathElement element : pathElements) {
                    try {
                        downloadMgr.downloadArtifact(selectedModule, element);
                    }
                    catch (IOException e) {
                        //TODO: accumulate errors and display once
                        UIUtils.showError(selectedModule, e);
                    }
                }
            }
        };

        final Application app = ApplicationManager.getApplication();
        app.runProcessWithProgressSynchronously(downloader,
                                                "Downloading...",
                                                true,
                                                project);
    }

    @Override
    public void update(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if (project == null)
            pEvent.getPresentation().setEnabled(false);
        else {
            final RepoToolWindow tw = RepoToolWindow.getInstance(project);
            final int selectedItemsCount = tw.getSelectedItemsCount();
            pEvent.getPresentation().setEnabled(selectedItemsCount > 0);
        }
    }

    private Module getModuleForDownload(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if(project == null)
            return null;

        final Module[] modules = ModuleManager.getInstance(project).getModules();

        final Set<Module> localRepos = getLocalRepos(modules);
        if (localRepos.size() == 0)
            return null;
        else if (localRepos.size() == 1)
            return modules[0];
        else {
            final Module[] buffer = new Module[localRepos.size()];
            return selectModule(project, localRepos.toArray(buffer));
        }
    }

    private Module selectModule(final Project pProject,
                                final Module... pModules) {
        if (pProject == null)
            return null;

        final SelectFromListDialog dlg = new SelectFromListDialog(
                pProject,
                pModules,
                MODULE_TO_STRING_ASPECT,
                "Please select a local repository",
                ListSelectionModel.SINGLE_SELECTION);

        dlg.show();
        if (!dlg.isOK())
            return null;

        final Object[] selection = dlg.getSelection();
        if (selection.length == 0)
            return null;

        return (Module) selection[0];
    }

    private Set<Module> getLocalRepos(final Module[] pModules) {
        final Set<Module> localRepos = new HashSet<Module>(pModules.length);
        for (Module module : pModules) {
            final ModuleSettings settings = ModuleSettings.getInstance(module);
            final IQueryContext context = settings.getQueryContext();
            if (context == null)
                continue;

            localRepos.add(module);
        }
        return localRepos;
    }

    private static class ModuleToStringAspect implements SelectFromListDialog.ToStringAspect {
        public String getToStirng(Object obj) {
            final Module module = (Module) obj;
            final ILocationFinder finder = new ModuleLocationFinder(module);
            final String repo = finder.getMavenLocalRepository();
            return module.getName() + " - Local repository at " + repo;
        }
    }
}
