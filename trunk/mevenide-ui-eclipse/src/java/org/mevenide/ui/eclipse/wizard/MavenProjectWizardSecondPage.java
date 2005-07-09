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
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
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
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.mevenide.project.io.CarefulProjectMarshaller;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.nature.MevenideNature;

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
    /**
     * Called from the wizard on finish.
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

                IClasspathEntry[] entries = null;
                IPath outputLocation = null;
                IPath srcPath = new Path("main/src/java"); //$NON-NLS-1$
                IPath srcResourcesPath = new Path("main/src/resources"); //$NON-NLS-1$
                IPath srcTestPath = new Path("main/test/java"); //$NON-NLS-1$
                IPath testResourcesPath = new Path("main/test/resources"); //$NON-NLS-1$
                IPath binPath = new Path("target/classes"); //$NON-NLS-1$

                if (srcPath.segmentCount() > 0) {
                    IFolder folder = fCurrProject.getFolder(srcPath);
                    createFolder(folder, true, true, null);
                }

                if (srcResourcesPath.segmentCount() > 0) {
                    IFolder folder = fCurrProject.getFolder(srcResourcesPath);
                    createFolder(folder, true, true, null);
                }

                if (srcTestPath.segmentCount() > 0) {
                    IFolder folder = fCurrProject.getFolder(srcTestPath);
                    createFolder(folder, true, true, null);
                }

                if (testResourcesPath.segmentCount() > 0) {
                    IFolder folder = fCurrProject.getFolder(testResourcesPath);
                    createFolder(folder, true, true, null);
                }

                if (binPath.segmentCount() > 0 && !binPath.equals(srcPath)) {
                    IFolder folder = fCurrProject.getFolder(binPath);
                    createFolder(folder, true, true, null);
                }

                final IPath projectPath = fCurrProject.getFullPath();

                // configure the classpath entries, including the default jre library.
                List cpEntries = new ArrayList();
                cpEntries.add(JavaCore.newSourceEntry(projectPath.append(srcPath)));
                cpEntries.add(JavaCore.newSourceEntry(projectPath.append(srcTestPath)));
                cpEntries.add(JavaCore.newSourceEntry(projectPath.append(srcResourcesPath)));
                cpEntries.add(JavaCore.newSourceEntry(projectPath.append(testResourcesPath)));

                cpEntries.addAll(Arrays.asList(PreferenceConstants.getDefaultJRELibrary()));
                entries = (IClasspathEntry[]) cpEntries.toArray(new IClasspathEntry[cpEntries.size()]);
                outputLocation = projectPath.append(binPath);

                init(JavaCore.create(fCurrProject), outputLocation, entries, false);

                //set maven repo if not set 
                IPath mavenRepoVar = JavaCore.getClasspathVariable("MAVEN_REPO"); //$NON-NLS-1$
                if (mavenRepoVar == null) {
                    final String mavenRepo = Mevenide.getInstance().getPOMManager().getDefaultLocationFinder().getMavenLocalRepository();
                    JavaCore.setClasspathVariable("MAVEN_REPO", new Path(mavenRepo), null); //$NON-NLS-1$
                }

            }

            //@TODO might make use of templates sometime in the future
            IFile propertiesFile = fCurrProject.getFile("project.properties"); //$NON-NLS-1$
            propertiesFile.create(new ByteArrayInputStream((Mevenide.getResourceString("MavenProjectWizardSecondPage.PropertyHeader", fCurrProject.getName())).getBytes()), false, null); //$NON-NLS-1$

            MavenProjectWizard wizard = (MavenProjectWizard) getWizard();
            StringWriter strWriter = new StringWriter();
            new CarefulProjectMarshaller().marshall(strWriter, wizard.getProjectObjectModel());

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

    /**
     * Creates a folder and all parent folders if not existing.
     * Project must exist.
     * <code> org.eclipse.ui.dialogs.ContainerGenerator</code> is too heavy
     * (creates a runnable)
     */
    private static void createFolder(IFolder folder, boolean force, boolean local, IProgressMonitor monitor) throws CoreException {
        if (!folder.exists()) {
            IContainer parent= folder.getParent();
            if (parent instanceof IFolder) {
                createFolder((IFolder)parent, force, local, null);
            }
            folder.create(force, local, monitor);
        }
    }
}