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
package org.mevenide.ui.eclipse.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.ui.eclipse.DefaultPathResolver;
import org.mevenide.ui.eclipse.IPathResolver;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class EclipseProjectUtils {
	private static Log log = LogFactory.getLog(EclipseProjectUtils.class);
	
	private EclipseProjectUtils() {
	}

	public static List getJreEntryList(IProject project) throws Exception {
		IPathResolver pathResolver = new DefaultPathResolver();
		
		IClasspathEntry jreEntry = JavaRuntime.getJREVariableEntry();
		IClasspathEntry resolvedJreEntry = JavaCore.getResolvedClasspathEntry(jreEntry);
		String jrePath = pathResolver.getAbsolutePath(resolvedJreEntry.getPath());
		
		IClasspathContainer container = JavaCore.getClasspathContainer(new Path(Mevenide.getResourceString("ProjectUtil.eclipse.jre.container")), JavaCore.create(project));
		IClasspathEntry[] jreEntries = container.getClasspathEntries();
		
		List jreEntryList = new ArrayList();
		
		for (int i = 0; i < jreEntries.length; i++) {
			jreEntryList.add(pathResolver.getAbsolutePath(jreEntries[i].getPath()));
		}    
		jreEntryList.add(jrePath);
		return jreEntryList;
	}

	public static List getCrossProjectDependencies(IProject project) throws Exception {
	    List deps = new ArrayList();
	    IProject[] referencedProjects = project.getReferencedProjects();		
	    for (int i = 0; i < referencedProjects.length; i++) {
	        IProject referencedProject = referencedProjects[i];
	        
	        if ( referencedProject.exists() && !referencedProject.getName().equals(project.getName()) )  {
	            
	            File referencedPom = FileUtils.getPom(referencedProject);
	            //check if referencedPom exists, tho it should since we just have created it

	            if ( !referencedPom.exists() ) {
	                FileUtils.createPom(referencedProject);
	            }
	            ProjectReader reader = ProjectReader.getReader();
	            Dependency projectDependency = reader.extractDependency(referencedPom);
	            log.debug("dependency artifact : " + projectDependency.getArtifact());
	            deps.add(projectDependency);
	        }
	    }
	    return deps;
	}
	
	
	/**
     * @param iproject the IProject for which we want to retrieve the output folders.  
	 * @return empty List if <code>iproject</code> doesnot have the Java nature, the output folders list otherwise
     * the ouput folders list contains the default output folder as well as specific output folders defined on a perartifact basis
     * if an error occurs retunrs empty List. 
     * 
     */
	public static List getOutputFolders(IProject iproject) {
	    List outputFolders = new ArrayList();
	    
		IJavaProject project = JavaCore.create(iproject);
		
		if ( project.exists() ) { 
			log.debug("retrieving output folders for project " + iproject.getName());
            try {
				//add default ouput location
				IPath defaultOuputFolder = project.getOutputLocation();
				IResource resource;

				//handle case where Project has not been configured yet and output folders is set to /
				if ( defaultOuputFolder.segmentCount() == 1 ) {
					//System.err.println(defaultOuputFolder.removeTrailingSeparator().toString());
					resource = ResourcesPlugin.getWorkspace().getRoot().getProject(defaultOuputFolder.removeTrailingSeparator().toString());
				}
				else {
					resource = ResourcesPlugin.getWorkspace().getRoot().getFolder(defaultOuputFolder);
				}

				outputFolders.add(resource.getLocation().toFile());
				log.debug("Added " + resource.getLocation() + " to output folder list");

				//iterate each classpath entry and add output location
			    IClasspathEntry[] classpathEntries = project.getRawClasspath();
	            for (int i = 0; i < classpathEntries.length; i++) {
		            IClasspathEntry cpEntry = classpathEntries[i];
		            if ( cpEntry.getOutputLocation() != null ) {
						IResource outputFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(cpEntry.getOutputLocation());
		                outputFolders.add(outputFolder.getLocation().toFile());
						log.debug("Added " + outputFolder + " to output folder list");
		            }
		        }
            } 
			catch (JavaModelException e) {
                 log.error("Unable to obtain output Folders for project " + iproject.getName() );
            }
		}
	    
	    return outputFolders;
	}
	
	public static void attachJavaNature(IProject project) throws CoreException {
		if ( !project.hasNature(JavaCore.NATURE_ID) ) {
			IProjectDescription description = project.getDescription();
			String[] natureIds = description.getNatureIds();
			String[] newNatures = new String[natureIds.length + 1];
			System.arraycopy(natureIds, 0, newNatures, 0, natureIds.length);
			newNatures[newNatures.length - 1] = JavaCore.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
		}
	}
}
