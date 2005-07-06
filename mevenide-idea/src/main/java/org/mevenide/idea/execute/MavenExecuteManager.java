package org.mevenide.idea.execute;

import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.filters.ExceptionFilter;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.RegexpFilter;
import com.intellij.ide.DataManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.vfs.VirtualFile;
import org.mevenide.idea.MavenHomeUndefinedException;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.model.GoalInfo;
import org.mevenide.idea.util.components.AbstractProjectComponent;
import org.mevenide.idea.util.ui.UIUtils;

/**
 * @author Arik
 */
public class MavenExecuteManager extends AbstractProjectComponent {
    public MavenExecuteManager(final Project pProject) {
        super(pProject);
    }

    public void execute(final VirtualFile pPomFile, final GoalInfo... pGoals) {
        final VirtualFile dir = pPomFile.getParent();
        if (dir == null)
            return;

        //
        //always save all modified files before invoking maven
        //
        ApplicationManager.getApplication().saveAll();

        try {
            //
            //create the process descriptor
            //
            final ProjectJdk jdk = PomManager.getInstance(project).getJdk(pPomFile);
            if (jdk == null)
                throw CantRunException.noJdkConfigured();
            MavenJavaParameters p = new MavenJavaParameters(dir, jdk, pGoals);

            //
            //provide filters which allow linking compilation errors to source files
            //
            Filter[] filters = new Filter[]{
                    new ExceptionFilter(project),
                    new RegexpFilter(project, MavenJavaParameters.COMPILE_REGEXP)
            };

            //
            //executes the process, creating a console window for it
            //
            final StringBuilder contentNameBuf = new StringBuilder();
            for (GoalInfo goal : pGoals) {
                if (contentNameBuf.length() > 0)
                    contentNameBuf.append(' ');
                contentNameBuf.append(goal.getName());
            }
            final String contentName = contentNameBuf.toString();
            ExecutionManager.getInstance(project).execute(p,
                                                          contentName,
                                                          DataManager.getInstance().getDataContext(),
                                                          filters);
        }
        catch (MavenHomeUndefinedException e) {
            UIUtils.showError(project, e);
            LOG.trace(e.getMessage(), e);
        }
        catch (CantRunException e) {
            UIUtils.showError(project, e);
            LOG.trace(e.getMessage(), e);
        }
        catch (ExecutionException e) {
            UIUtils.showError(project, e);
            LOG.trace(e.getMessage(), e);
        }
    }

    public static MavenExecuteManager getInstance(final Project pProject) {
        return pProject.getComponent(MavenExecuteManager.class);
    }
}
