/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */

package org.mevenide.ui.eclipse.nature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.mevenide.context.IQueryContext;
import org.mevenide.project.dependency.DependencyUtil;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.ui.eclipse.Mevenide;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class MavenBuilder extends IncrementalProjectBuilder {
    public static final String BUILDER_ID = Mevenide.PLUGIN_ID + ".mavenbuilder"; //$NON-NLS-1$

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
        if (getProject() == null) {
            return new IProject[0];
        }

        IResourceDelta d = getDelta(getProject());
        if (d != null) {
            List actionDefinitions = Mevenide.getInstance().getActionDefinitionsManager().getDefinitions();
            ActionActivator activator = new ActionActivator(actionDefinitions, getProject());
            d.accept(activator);
        }

        return getMavenRequiredProjects();
    }

    private IProject[] getMavenRequiredProjects() {
        List projects = new ArrayList();
        try {
            IQueryContext context = Mevenide.getInstance().getPOMManager().getQueryContext(getProject());
            if (context != null) {
                Project currentPom = context.getPOMContext().getFinalProject();
                IProject[] wsProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
                for (int i = 0; i < wsProjects.length; i++) {
                    IProject wsProject = wsProjects[i];
                    if (isDependingUpon(currentPom, wsProject)) {
                        projects.add(wsProject);
                    }
                }
            }
        } catch (Exception e) {
            //try/catch in loop in order to be able to process other projects   
            String message = "Unable to check for compute dependent projects"; //$NON-NLS-1$
            Mevenide.displayError("Get Dependent Projects", message, e);
        }
        return (IProject[]) projects.toArray(new IProject[projects.size()]);
    }

    private boolean isDependingUpon(Project currentPom, IProject wsProject) {
        boolean explicitDependency = isExplicitelyDependingUpon(currentPom, wsProject);
        boolean implicitDependency = isImplicitelyDependingUpon(currentPom, wsProject);
        return explicitDependency || implicitDependency;
    }

    private boolean isImplicitelyDependingUpon(Project currentPom, IProject wsProject) {
        return false;
    }

    private boolean isExplicitelyDependingUpon(Project currentPom, IProject wsProject) {
        try {
            IFile file = wsProject.getFile("project.xml"); //$NON-NLS-1$
            if (file.exists()) {
                Dependency dependency = ProjectReader.getReader().extractDependency(file.getLocation().toFile());
                List dependencies = currentPom.getDependencies();
                if (dependencies != null) {
                    for (int i = 0; i < dependencies.size(); i++) {
                        if (DependencyUtil.areEquals((Dependency) dependencies.get(i), dependency)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            String message = "Unable to check project dependencies"; //$NON-NLS-1$
            Mevenide.displayError("Check Dependent Project", message, e);
        }
        return false;
    }

////////////////////////////////////////////////////////////////////////////////

    static final void addToProject(IProject project) throws CoreException {
        IProjectDescription projectDescription = project.getDescription();
        ICommand[] commands = project.getDescription().getBuildSpec();
        boolean addBuilder = true;
        for (int i = 0; i < commands.length; i++) {
            if (commands[i].getBuilderName().equals(BUILDER_ID)) {
                addBuilder = false;
            }
        }
        if (addBuilder) {
            ICommand[] newCommands = null;
            ICommand command = projectDescription.newCommand();
            command.setBuilderName(BUILDER_ID);
            newCommands = new ICommand[commands.length + 1];
            System.arraycopy(commands, 0, newCommands, 0, commands.length);
            newCommands[commands.length] = command;
            projectDescription.setBuildSpec(newCommands);
            project.setDescription(projectDescription, null);
        }
    }

    static final void removeFromProject(IProject project) throws CoreException {
        if (project != null) {
            IProjectDescription projectDescription = project.getDescription();
            ICommand[] commands = projectDescription.getBuildSpec();
            ICommand[] newCommands = null;
            int builderIndex = -1;
            for (int i = 0; i < commands.length; i++) {
                if (commands[i].getBuilderName().equals(BUILDER_ID)) {
                    builderIndex = i;
                }
            }
            if (builderIndex != -1) {
                newCommands = new ICommand[commands.length - 1];
                for (int i = 0; i < builderIndex; i++) {
                    newCommands[i] = commands[i];
                }
                for (int i = builderIndex + 1; i < commands.length; i++) {
                    newCommands[i - 1] = commands[i];
                }
                projectDescription.setBuildSpec(newCommands);
                project.setDescription(projectDescription, null);
            }
        }
    }

}
