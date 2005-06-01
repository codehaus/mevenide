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
import org.apache.maven.repository.Artifact;
import org.apache.maven.repository.DefaultArtifactFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
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
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.ui.eclipse.DefaultPathResolver;
import org.mevenide.ui.eclipse.IPathResolver;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class JavaProjectUtils {
	
    private static Log log = LogFactory.getLog(JavaProjectUtils.class);

    private static final String JRE_CONTAINER_ID = "org.eclipse.jdt.launching.JRE_CONTAINER"; //$NON-NLS-1$
	
	private JavaProjectUtils() {
	}

	public static List getJreEntryList(IProject project) throws Exception {
		List jreEntryList = new ArrayList();
		
		IPathResolver pathResolver = new DefaultPathResolver();
		IClasspathEntry jreEntry = JavaRuntime.getJREVariableEntry();
		
		IClasspathEntry resolvedJreEntry = JavaCore.getResolvedClasspathEntry(jreEntry);
		if ( resolvedJreEntry != null ) {
			String jrePath = pathResolver.getAbsolutePath(resolvedJreEntry.getPath());
			
			IClasspathContainer container = JavaCore.getClasspathContainer(new Path(JRE_CONTAINER_ID), JavaCore.create(project));
			IClasspathEntry[] jreEntries = container.getClasspathEntries();
			
			
			for (int i = 0; i < jreEntries.length; i++) {
				jreEntryList.add(pathResolver.getAbsolutePath(jreEntries[i].getPath()));
			}    
			jreEntryList.add(jrePath);
		}
		return jreEntryList;
	}

	public static List getCrossProjectDependencies(IProject project) throws Exception {
	    List deps = new ArrayList();
	    IProject[] referencedProjects = project.getReferencedProjects();		
	    for (int i = 0; i < referencedProjects.length; i++) {
	        IProject referencedProject = referencedProjects[i];
	        
	        if ( referencedProject.exists() && !referencedProject.getName().equals(project.getName()) )  {
	            
	            File referencedPom = FileUtils.getPom(referencedProject);

	            //check if referencedPom exists, altho it should since we just have created it
	            if ( !referencedPom.exists() ) {
	                FileUtils.createPom(referencedProject);
	            }
	            ProjectReader reader = ProjectReader.getReader();
	            Dependency projectDependency = reader.extractDependency(referencedPom);
	            log.debug("dependency artifact : " + projectDependency.getArtifact()); //$NON-NLS-1$
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
			log.debug("retrieving output folders for project " + iproject.getName()); //$NON-NLS-1$
            try {
				//add default ouput location
				IPath defaultOuputFolder = project.getOutputLocation();
				IResource resource;

				//handle case where Project has not been configured yet and output folders is set to /
				if ( defaultOuputFolder.segmentCount() == 1 ) {
					log.debug(defaultOuputFolder.removeTrailingSeparator().toString());
					resource = ResourcesPlugin.getWorkspace().getRoot().getProject(defaultOuputFolder.removeTrailingSeparator().toString());
				}
				else {
					resource = ResourcesPlugin.getWorkspace().getRoot().getFolder(defaultOuputFolder);
				}

				outputFolders.add(resource.getLocation().toFile());
				log.debug("Added " + resource.getLocation() + " to output folder list"); //$NON-NLS-1$ //$NON-NLS-2$

				//iterate each classpath entry and add output location
			    IClasspathEntry[] classpathEntries = project.getRawClasspath();
	            for (int i = 0; i < classpathEntries.length; i++) {
		            IClasspathEntry cpEntry = classpathEntries[i];
		            if ( cpEntry.getOutputLocation() != null ) {
						IResource outputFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(cpEntry.getOutputLocation());
		                outputFolders.add(outputFolder.getLocation().toFile());
						log.debug("Added " + outputFolder + " to output folder list");  //$NON-NLS-1$//$NON-NLS-2$
		            }
		        }
            } 
			catch (JavaModelException e) {
                 log.error("Unable to obtain output Folders for project " + iproject.getName() ); //$NON-NLS-1$
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
	
	public static void addClasspathEntry(IJavaProject project, IClasspathEntry entry) throws Exception {
		IClasspathEntry[] oldEntries = project.getRawClasspath();
		if ( entry.getEntryKind() == IClasspathEntry.CPE_SOURCE ) {
			boolean exclusionAdded = excludePath(entry, oldEntries);
			//should warn user ? would it be better to let him choose wether he wants to : 
			//1. cancel operation, 2. add the exclusion pattern 3. remove conflicting entry ?
		}
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = entry;
		//checkOutputConflict(project, entry);
		project.setRawClasspath(newEntries, null);
	}
	
	private static void checkOutputConflict(IJavaProject project, IClasspathEntry entry) throws Exception {
		IPath projectOutputLocation = project.getOutputLocation();
		if ( entry.getPath().toString().startsWith(projectOutputLocation.toString()) ) {
			//project.setOutputLocation(new Path("/"  + project.getProject().getName() + outputFolder), null);
		}
	}
	
	/**
	 * iterate through <code>oldEntries</code> and foreach entry exclude <code>entry.path</code> if necessary
	 * 
	 * @return true if <code>entry.path</code> has been excluded from one entry, false otherwise
	 */
    private static boolean excludePath(IClasspathEntry entry, IClasspathEntry[] oldEntries) {
		boolean exclusionAdded = false;
    	for (int i = 0; i < oldEntries.length; i++) {
			IClasspathEntry oldEntry = oldEntries[i];
			IPath newEntryPath = entry.getPath();
			if ( newEntryPath.toString().startsWith(oldEntry.getPath().toString()) ) {
				IPath[] exclusionPaths = oldEntry.getExclusionPatterns();
				IPath[] newExclusionPaths = new IPath[exclusionPaths.length + 1];
				System.arraycopy(exclusionPaths, 0, newExclusionPaths, 0, exclusionPaths.length);
				newExclusionPaths[exclusionPaths.length] = newEntryPath.removeFirstSegments(oldEntry.getPath().segmentCount()).addTrailingSeparator();
				oldEntries[i] = JavaCore.newSourceEntry(oldEntry.getPath(), newExclusionPaths);
				exclusionAdded = true;
			}
		}
    	return exclusionAdded;
	}

	public static String[] findExclusionPatterns(String eclipseSourceFolder, IProject eclipseProject) {
    	try {
    		String[] exclusionPatterns = null;
    		IJavaProject javaProject = JavaCore.create(eclipseProject);
    		IClasspathEntry[] entries = javaProject.getRawClasspath();
    		for (int i = 0; i < entries.length; i++) {
    			IClasspathEntry entry = entries[i];
    			if ( entry.getEntryKind() == IClasspathEntry.CPE_SOURCE && entry.getPath().removeFirstSegments(1).makeRelative().toString().equals(eclipseSourceFolder.replaceAll("\\\\", "/")) ) { //$NON-NLS-1$ //$NON-NLS-2$
    				IPath[] eclipseExclusions = entry.getExclusionPatterns();
    				exclusionPatterns = new String[eclipseExclusions.length];
    				for (int j = 0; j < eclipseExclusions.length; j++) {
    					exclusionPatterns[j] = eclipseExclusions[j].makeRelative().toString();	
    				}
    			}
    		}
    		return exclusionPatterns;
    	} 
    	catch (JavaModelException e) {
    		String message = "Unable to get exclusion patterns for " + eclipseSourceFolder;  //$NON-NLS-1$
    		log.error(message, e);
    		return null;
    	}
    }

    public static String getRelativeDefaultOuputFolder(IProject project) throws JavaModelException {
    	return getDefaultOuputFolder(project).getLocation().toOSString();
    }

    private static IResource getDefaultOuputFolder(IProject iproject) throws JavaModelException {
    	IJavaProject project = JavaCore.create(iproject);
    	
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
    	return resource;
    }

    public static Artifact createArtifactFromProjectEntry(IProject project, IClasspathEntry entry) throws Exception {
    	Artifact artifact = null;
    	String path = entry.getPath().toOSString();
    
    	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    	String projectName = getProjectName(path);
    	IProject referencedProject = root.getProject(projectName);
    	if ( !referencedProject.getName().equals(project.getProject().getName()) ) {
    	    artifact = DefaultArtifactFactory.createArtifact(createDependencyFromProject(referencedProject));
    	}
    
    	return artifact;
    }

    private static String getProjectName(String projectBasedir) {
    	//crap..
    	String projectName ;
    	if ( projectBasedir.substring(1).indexOf('/') < 0 ) {
    		projectName = projectBasedir.substring(1);
    	}
    	else {
    		projectName = projectBasedir.substring(1, projectBasedir.substring(1).indexOf('/') + 1) ; 
    	}
    	return projectName;
    }

    private static Dependency createDependencyFromProject(IProject referencedProject) throws Exception {
    	if ( referencedProject.exists() )  {
                
            File referencedPom = FileUtils.getPom(referencedProject);
            //check if referencedPom exists, tho it should since we just have created it
    
            if ( !referencedPom.exists() ) {
                FileUtils.createPom(referencedProject);
            }
            ProjectReader reader = ProjectReader.getReader();
            Dependency dependency = reader.extractDependency(referencedPom);
            dependency.addProperty("eclipse.dependency:true"); //$NON-NLS-1$
            dependency.resolvedProperties().put("eclipse.dependency", "true");  //$NON-NLS-1$//$NON-NLS-2$
            return dependency;
        }
    	return null;
    }

    public static Artifact createArtifactFromLibraryEntry(IProject project, IClasspathEntry entry) throws Exception {
    	String path = entry.getPath().toOSString();
    	if ( !new File(path).exists() ) {
    		//not the best way to get the absoluteFile ... 
    		path = project.getProject().getLocation().append(entry.getPath().removeFirstSegments(1)).toOSString();
    	}
    	Artifact artifact = DefaultArtifactFactory.createArtifact(DependencyFactory.getFactory().getDependency(path));
    	artifact.setPath(path);
    	
    	return artifact;
    }
    
    public static void setDefaultOuputLocation(IProject project, String outputLocation) throws Exception {
        IJavaProject javaProject = JavaCore.create(project);
        javaProject.setOutputLocation(project.getFullPath().append(outputLocation), null);
        
    }
    
}

