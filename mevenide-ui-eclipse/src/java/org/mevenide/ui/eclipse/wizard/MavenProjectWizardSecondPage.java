/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.project.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.project.io.PomSkeletonBuilder;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenidePreferenceKeys;
import org.mevenide.ui.eclipse.preferences.PreferencesManager;
import org.mevenide.ui.eclipse.util.FileUtils;

/**
 * @author	<a href="mailto:jens@iostream.net">Jens Andersen</a>, Last updated by $Author$
 * @version $Id$
 */
public class MavenProjectWizardSecondPage extends JavaCapabilityConfigurationPage{
	private final MavenProjectWizardFirstPage fFirstPage;

	protected IPath fCurrProjectLocation;
	protected IProject fCurrProject;
	
	protected boolean fKeepContent;
	
	/**
	 * 
	 * @param fFirstPage
	 */
	public MavenProjectWizardSecondPage (MavenProjectWizardFirstPage fFirstPage)
	{
		this.fFirstPage = fFirstPage;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		if (visible) {
			changeToNewProject();
		} else {
			performCancel();
		}
		super.setVisible(visible);
	}
	/*
	 * 
	 */
	private void changeToNewProject() {
		final IProject newProjectHandle= fFirstPage.getProjectHandle();
		final IPath newProjectLocation= fFirstPage.getLocationPath();
		
		fKeepContent= fFirstPage.getDetect();
		
		final boolean initialize= !(newProjectHandle.equals(fCurrProject) && newProjectLocation.equals(fCurrProjectLocation));
		
		final IRunnableWithProgress op= new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				updateProject(initialize, monitor);
			}
		};
	
		try {
			getContainer().run(false, true, new WorkspaceModifyDelegatingOperation(op, newProjectHandle));
		} catch (InvocationTargetException e) {
			final String title= Mevenide.getResourceString("MavenProjectWizardSecondPage.error.title"); //$NON-NLS-1$
			final String message= Mevenide.getResourceString("MavenProjectWizardSecondPage.error.message"); //$NON-NLS-1$
			ExceptionHandler.handle(e, getShell(), title, message);
		} catch  (InterruptedException e) {
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
			fCurrProject= null;
		}
	}
	/**
	 * 
	 * @param initialize
	 * @param monitor
	 * @throws CoreException
	 */
	protected void updateProject(boolean initialize, IProgressMonitor monitor){
		fCurrProject= fFirstPage.getProjectHandle();
		fCurrProjectLocation= fFirstPage.getLocationPath();
		
		final boolean noProgressMonitor= !initialize && !fFirstPage.getDetect();
		
		if (monitor == null || noProgressMonitor ) {
			monitor= new NullProgressMonitor();
		}
		try {
			monitor.beginTask(Mevenide.getResourceString("MavenProjectWizardSecondPage.operation.initialize"), 2); //$NON-NLS-1$

			createProject(fCurrProject, fCurrProjectLocation, new SubProgressMonitor(monitor, 1));
			if (initialize) {
				
				IClasspathEntry[] entries= null;
				IPath outputLocation= null;
				IPath srcPath = new Path("src/java");
				IPath srcTestPath = new Path("src/test");
				IPath binPath = new Path("target/classes");
				
				if (srcPath.segmentCount() > 0) {
					IFolder folder= fCurrProject.getFolder(srcPath);
					CoreUtility.createFolder(folder, true, true, null);
				}
				
				if (binPath.segmentCount() > 0 && !binPath.equals(srcPath)) {
					IFolder folder= fCurrProject.getFolder(binPath);
					CoreUtility.createFolder(folder, true, true, null);
				}
				
				final IPath projectPath= fCurrProject.getFullPath();

				// configure the classpath entries, including the default jre library.
				List cpEntries= new ArrayList();
				//Source Code Source Path
				cpEntries.add(JavaCore.newSourceEntry(projectPath.append(srcPath)));
				//Unit Test Source Path
				cpEntries.add(JavaCore.newSourceEntry(projectPath.append(srcTestPath)));
				
				cpEntries.addAll(Arrays.asList(PreferenceConstants.getDefaultJRELibrary()));
				entries= (IClasspathEntry[]) cpEntries.toArray(new IClasspathEntry[cpEntries.size()]);
				outputLocation= projectPath.append(binPath);

				init(JavaCore.create(fCurrProject), outputLocation, entries, false);
				
				//Setup the MAVEN_REPO variable
				PreferencesManager manager = PreferencesManager.getManager();
				manager.loadPreferences();

				//set maven repo if not set 
				IPath mavenRepoVar = JavaCore.getClasspathVariable("MAVEN_REPO");
				if(mavenRepoVar == null) {
				    //lookup maven repo in all available locations
					IPath mavenRepo = new Path(manager.getValue(MevenidePreferenceKeys.MAVEN_REPO_PREFERENCE_KEY));
				    if ( mavenRepo == null ) {
					    LocationFinderAggregator locationFinder = new LocationFinderAggregator();
					    locationFinder.setEffectiveWorkingDirectory(fCurrProjectLocation.toOSString());
					    System.err.println(fCurrProjectLocation.toOSString());
					    mavenRepo = new Path(locationFinder.getMavenLocalRepository());
					}
				    JavaCore.setClasspathVariable("MAVEN_REPO", mavenRepo, null);
				}
				
			}
			
			//@TODO might make use of templates sometime in the future
			IFile propertiesFile = fCurrProject.getFile("project.properties");
			propertiesFile.create(new ByteArrayInputStream(("#Project property file for " + fCurrProject.getName()).getBytes()), false, null);

			String referencedPomSkeleton =  PomSkeletonBuilder.getSkeletonBuilder().getPomSkeleton(fCurrProject.getName());
			IFile referencedProjectFile = fCurrProject.getFile("project.xml");
			
			referencedProjectFile.create(new ByteArrayInputStream(referencedPomSkeleton.getBytes()), false, null);
			Project pom = ProjectReader.getReader().read(FileUtils.getPom(fCurrProject));
			
			monitor.worked(1);
		}catch(Exception e) {
			//@TODO remove this stacktrace and log insted
			e.printStackTrace();
		} finally {
			monitor.done();
		}		
	}

	/**
	 * This method is called from the wizard on cancel.
	 */
	public void performCancel() {
		if (fCurrProject == null || !fCurrProject.exists()) {
			return;
		}
		
		IRunnableWithProgress op= new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {

				final boolean noProgressMonitor= Platform.getLocation().equals(fCurrProjectLocation);

				if (monitor == null || noProgressMonitor) {
					monitor= new NullProgressMonitor();
				}

				monitor.beginTask(Mevenide.getResourceString("MavenProjectWizardSecondPage.operation.remove"), 3); //$NON-NLS-1$

				try {
					boolean removeContent= !fKeepContent && fCurrProject.isSynchronized(IResource.DEPTH_INFINITE);
					fCurrProject.delete(removeContent, false, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
					fCurrProject= null;
					fKeepContent= false;
				}
			}
		};
	
		try {
			getContainer().run(false, true, new WorkspaceModifyDelegatingOperation(op, fCurrProject));
		} catch (InvocationTargetException e) {
			final String title= Mevenide.getResourceString("MavenProjectWizardSecondPage.error.remove.title"); //$NON-NLS-1$
			final String message= Mevenide.getResourceString("MavenProjectWizardSecondPage.error.remove.message"); //$NON-NLS-1$
			ExceptionHandler.handle(e, getShell(), title, message);		
		} catch  (InterruptedException e) {
			// cancel pressed
		}
	}		
}
