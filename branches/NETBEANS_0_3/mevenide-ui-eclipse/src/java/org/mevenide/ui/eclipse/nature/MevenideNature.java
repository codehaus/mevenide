/*
 * ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * =========================================================================
 */
package org.mevenide.ui.eclipse.nature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.view.SynchronizationView;
import org.mevenide.ui.eclipse.util.FileUtils;

/**
 * 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 *  
 */
public class MevenideNature implements IProjectNature {

    private static Log log = LogFactory.getLog(MevenideNature.class);
    private IProject project;
    //@todo add a preference to control the behaviour
    private boolean createPomOnActivation = true;

    public void configure() throws CoreException {
        try {
            addMavenBuilder(project);
            synchronizeProject(project);
        }
        catch (Exception e) {
            log.debug("Unable to add MevenideNature to project '" + project.getName(), e); //$NON-NLS-1$
            //throw new CoreException(new Status(IStatus.ERROR, "mevenide", 1, e.getMessage(), e)); //$NON-NLS-1$
        }
    }

    public void deconfigure() throws CoreException {
        removeMavenBuilder(project);
    }

    public static void configureProject(IProject project) throws Exception {
        addMavenNature(project);
    }

    private static void addMavenNature(IProject project) {
        try {
            IProjectDescription projectDescription = project.getDescription();
            String[] natures = projectDescription.getNatureIds();
            String[] newNatures = new String[natures.length + 1];
            System.arraycopy(natures, 0, newNatures, 0, natures.length);
            newNatures[natures.length] = Mevenide.NATURE_ID;
            projectDescription.setNatureIds(newNatures);
            project.setDescription(projectDescription, null);
        }
        catch (Throwable e) {
            log.debug("Unable to set project description", e); //$NON-NLS-1$
        }
    }

    private void addMavenBuilder(IProject project) throws Exception {
        IProjectDescription projectDescription = project.getDescription();
        ICommand[] commands = project.getDescription().getBuildSpec();
        boolean addBuilder = true;
        for (int i = 0; i < commands.length; i++) {
            if ( commands[i].getBuilderName().equals(MavenBuilder.BUILDER_ID) ) {
                addBuilder = false;
            }
        }
        if ( addBuilder ) {
            ICommand[] newCommands = null;
            ICommand command = projectDescription.newCommand();
            command.setBuilderName(MavenBuilder.BUILDER_ID);
            newCommands = new ICommand[commands.length + 1];
            System.arraycopy(commands, 0, newCommands, 0, commands.length);
            newCommands[commands.length] = command;
            projectDescription.setBuildSpec(newCommands);
            project.setDescription(projectDescription, null);
        }
    }

    private void synchronizeProject(IProject project) throws Exception {
        if ( createPomOnActivation && FileUtils.getPom(project) != null && !FileUtils.getPom(project).exists() ) {
            FileUtils.createPom(project);
        }
        SynchronizationView view = (SynchronizationView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .showView(Mevenide.SYNCHRONIZE_VIEW_ID);
        view.setInput(project);
    }

    public static void deconfigureProject(IProject project) throws CoreException {
        removeMavenNature(project);
    }

    private void removeMavenBuilder(IProject project) throws CoreException {
        if ( project != null ) {
            IProjectDescription projectDescription = project.getDescription();
            ICommand[] commands = projectDescription.getBuildSpec();
            ICommand[] newCommands = null;
            int builderIndex = -1;
            for (int i = 0; i < commands.length; i++) {
                if ( commands[i].getBuilderName().equals(MavenBuilder.BUILDER_ID) ) {
                    builderIndex = i;
                }
            }
            if ( builderIndex != -1 ) {
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

    private static void removeMavenNature(IProject project) throws CoreException {
        if ( project != null ) {
            IProjectDescription description = project.getDescription();
            String[] prevNatures = description.getNatureIds();
            List newNatures = new ArrayList(Arrays.asList(prevNatures));
            if ( newNatures.contains(Mevenide.NATURE_ID) ) {
                newNatures.remove(Mevenide.NATURE_ID);
                String[] setNatures = new String[newNatures.size()];
                for (int i = 0; i < setNatures.length; i++) {
                    setNatures[i] = (String) newNatures.get(i);
                }
                description.setNatureIds(setNatures);
                project.setDescription(description, null);
            }
        }
    }

    public IProject getProject() {
        return project;
    }

    public void setProject(IProject project) {
        this.project = project;
    }
}