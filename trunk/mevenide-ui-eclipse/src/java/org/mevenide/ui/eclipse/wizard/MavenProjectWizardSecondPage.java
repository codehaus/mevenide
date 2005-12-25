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

package org.mevenide.ui.eclipse.wizard;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.mevenide.project.io.CarefulProjectMarshaller;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.classpath.MavenClasspathContainer;
import org.mevenide.ui.eclipse.nature.MevenideNature;
import org.mevenide.ui.eclipse.preferences.MevenidePreferenceKeys;

/**
 * @author	<a href="mailto:jens@iostream.net">Jens Andersen</a>, Last updated by $Author$
 * @version $Id$
 */
public class MavenProjectWizardSecondPage extends JavaCapabilityConfigurationPage {
    private static final Log log = LogFactory.getLog(MavenProjectWizardSecondPage.class);

    private final MavenProjectWizardBasicSettingsPage fFirstPage;
    protected IPath fCurrProjectLocation;
    protected IProject fCurrProject;
    protected boolean fKeepContent;

    /**
     * @param fFirstPage
     */
    public MavenProjectWizardSecondPage(MavenProjectWizardBasicSettingsPage fFirstPage) {
        super();
        fCurrProjectLocation = null;
        fCurrProject = null;
        fKeepContent = true;
        this.fFirstPage = fFirstPage;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
     */
    public void setVisible(boolean visible) {
        if (visible) {
            changeToNewProject();
        } else {
            removeProject();
        }
        super.setVisible(visible);
    }

    private void changeToNewProject() {
        final IProject newProjectHandle = fFirstPage.getProjectHandle();
        final IPath newProjectLocation = fFirstPage.getLocationPath();

        fKeepContent = fFirstPage.getDetect();

        final boolean initialize = !(newProjectHandle.equals(fCurrProject) && newProjectLocation.equals(fCurrProjectLocation));

        final IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException, InterruptedException {
                try {
                    updateProject(initialize, monitor);
                } catch (OperationCanceledException e) {
                    throw new InterruptedException();
                }
            }
        };

        try {
            getContainer().run(true, false, new WorkspaceModifyDelegatingOperation(op));
        } catch (InvocationTargetException e) {
            final String title = JDTWizardMessages.JavaProjectWizardSecondPage_error_title;
            final String message = JDTWizardMessages.JavaProjectWizardSecondPage_error_message;
            Mevenide.displayError(title, message, e);
        } catch (InterruptedException e) {
            // cancel pressed
        }
    }

    /**
     * This method is called from the wizard on finish.
     * @param monitor
     * @throws CoreException
     * @throws InterruptedException
     */
    public void performFinish(IProgressMonitor monitor) throws CoreException, InterruptedException {
        try {
            monitor.beginTask(Mevenide.getResourceString("MavenProjectWizardSecondPage.operation.create"), 3); //$NON-NLS-1$
            if (fCurrProject == null) {
                updateProject(true, new SubProgressMonitor(monitor, 1));
            }
            configureJavaProject(new SubProgressMonitor(monitor, 2));
        } finally {
            monitor.done();
            fCurrProject = null;
        }
    }

    private void removeProject() {
        if (fCurrProject == null || !fCurrProject.exists()) {
            return;
        }

        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException {

                final boolean noProgressMonitor = Platform.getLocation().equals(fCurrProjectLocation);

                if (monitor == null || noProgressMonitor) {
                    monitor = new NullProgressMonitor();
                }

                monitor.beginTask(Mevenide.getResourceString("MavenProjectWizardSecondPage.operation.remove"), 3); //$NON-NLS-1$

                try {
                    boolean removeContent = !fKeepContent && fCurrProject.isSynchronized(IResource.DEPTH_INFINITE);
                    fCurrProject.delete(removeContent, false, monitor);
                } catch (CoreException e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                    fCurrProject = null;
                    fKeepContent = false;
                }
            }
        };

        try {
            getContainer().run(true, true, new WorkspaceModifyDelegatingOperation(op));
        } catch (InvocationTargetException e) {
            final String title = Mevenide.getResourceString("MavenProjectWizardSecondPage.error.remove.title"); //$NON-NLS-1$
            final String message = Mevenide.getResourceString("MavenProjectWizardSecondPage.error.remove.message"); //$NON-NLS-1$
            Mevenide.displayError(title, message, e);
        } catch (InterruptedException e) {
            // cancel pressed
        }
    }

    /**
     * Called from the wizard on cancel.
     */
    public void performCancel() {
        removeProject();
    }

    protected void updateProject(boolean initialize, IProgressMonitor monitor) {
        fCurrProject = fFirstPage.getProjectHandle();
        fCurrProjectLocation = fFirstPage.getLocationPath();

        final boolean noProgressMonitor = !initialize && !fFirstPage.getDetect();

        if (monitor == null || noProgressMonitor) {
            monitor = new NullProgressMonitor();
        }
        try {
            monitor.beginTask(Mevenide.getResourceString("MavenProjectWizardSecondPage.operation.initialize"), 2); //$NON-NLS-1$

            createProject(fCurrProject, fCurrProjectLocation, new SubProgressMonitor(monitor, 1));
			
			if (initialize) {
			    IPreferenceStore preferenceStore = Mevenide.getInstance().getPreferenceStore();
				boolean autosyncEnabled = preferenceStore.getBoolean(MevenidePreferenceKeys.AUTOSYNC_ENABLED);
				
				final IPath projectPath          = fCurrProject.getFullPath();
				final IPath pathSrcMainJava      = projectPath.append("src/main/java");       //$NON-NLS-1$
				final IPath pathSrcMainResources = projectPath.append("src/main/resources");  //$NON-NLS-1$
				final IPath pathSrcTestJava      = projectPath.append("src/test/java");       //$NON-NLS-1$
				final IPath pathSrcTestResources = projectPath.append("src/test/resources");  //$NON-NLS-1$
				final IPath pathOutputDefault    = projectPath.append("target/classes");      //$NON-NLS-1$
				final IPath pathOutputTest       = projectPath.append("target/test-classes"); //$NON-NLS-1$

				final IPath[] patternJavaSource = new IPath[] { new Path("**/*.java") };

				// configure the classpath entries, including the default jre library.
				List cpEntries = new ArrayList();
				cpEntries.add(JavaCore.newSourceEntry(pathSrcMainJava, patternJavaSource, ClasspathEntry.EXCLUDE_NONE, null /* use default output path */));
				cpEntries.add(JavaCore.newSourceEntry(pathSrcMainResources, ClasspathEntry.INCLUDE_ALL, patternJavaSource, null /* use default output path */));
				cpEntries.add(JavaCore.newSourceEntry(pathSrcTestJava, patternJavaSource, ClasspathEntry.EXCLUDE_NONE, pathOutputTest));
				cpEntries.add(JavaCore.newSourceEntry(pathSrcTestResources, ClasspathEntry.INCLUDE_ALL, patternJavaSource, pathOutputTest));
				
				if (autosyncEnabled) {
					cpEntries.add(JavaCore.newContainerEntry(new Path(MavenClasspathContainer.ID)));
				}
				
				cpEntries.addAll(Arrays.asList(PreferenceConstants.getDefaultJRELibrary()));
				
				//set JUnit location 
				cpEntries.add(JavaCore.newVariableEntry(new Path("JUNIT_HOME/junit.jar"), null, null)); //$NON-NLS-1$
				
				IClasspathEntry[] entries = (IClasspathEntry[]) cpEntries.toArray(new IClasspathEntry[cpEntries.size()]);
				IPath outputLocation = projectPath.append(pathOutputDefault);
				
				//set maven repo if not set 
				IPath mavenRepoVar = JavaCore.getClasspathVariable("MAVEN_REPO"); //$NON-NLS-1$
				if (mavenRepoVar == null) {
				    final String mavenRepo = Mevenide.getInstance().getPOMManager().getDefaultLocationFinder().getMavenLocalRepository();
				    JavaCore.setClasspathVariable("MAVEN_REPO", new Path(mavenRepo), null); //$NON-NLS-1$
				}
				
				init(JavaCore.create(fCurrProject), outputLocation, entries, false);
			}
			
			//@TODO might make use of templates sometime in the future
			IFile propertiesFile = fCurrProject.getFile("project.properties"); //$NON-NLS-1$
			propertiesFile.create(new ByteArrayInputStream((Mevenide.getResourceString("MavenProjectWizardSecondPage.PropertyHeader", fCurrProject.getName())).getBytes()), false, null); //$NON-NLS-1$
			
			StringWriter strWriter = new StringWriter();
			new CarefulProjectMarshaller().marshall(strWriter, ((MavenProjectWizard) getWizard()).getProjectObjectModel());
			
			IFile referencedProjectFile = fCurrProject.getFile("project.xml"); //$NON-NLS-1$
			referencedProjectFile.create(new ByteArrayInputStream(strWriter.toString().getBytes()), false, null);
			
			//add maven nature
			MevenideNature.addToProject(fCurrProject);

            monitor.worked(1);
        } catch (Exception e) {
            String message = "Unable to create project"; //$NON-NLS-1$
            log.error(message, e);
        } finally {
            monitor.done();
        }
    }
}