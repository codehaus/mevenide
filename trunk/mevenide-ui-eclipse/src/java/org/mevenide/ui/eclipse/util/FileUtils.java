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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.apache.maven.util.StringInputStream;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.mevenide.project.io.PomSkeletonBuilder;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.ui.eclipse.DefaultPathResolver;
import org.mevenide.ui.eclipse.IPathResolver;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.util.MevenideUtils;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class FileUtils {
	
	private static Log log = LogFactory.getLog(FileUtils.class);
	
	private FileUtils() {
	}
	
	

	public static boolean inLocalRepository(String entryPath) {
		File localRepo = new File(Mevenide.getInstance().getMavenRepository());
		return MevenideUtils.findFile(localRepo, entryPath);
	}

	public static boolean isClassFolder(String entryPath, IProject project) {
		return new File(project.getLocation().append(new Path(entryPath).removeFirstSegments(1)).toOSString()).isDirectory();
	}

	public static void createPom(IProject project) throws Exception, CoreException {
		 log.debug("Creating pom skeleton using template : " + Mevenide.getInstance().getPomTemplate());
		 PomSkeletonBuilder pomSkeletonBuilder = PomSkeletonBuilder.getSkeletonBuilder( Mevenide.getInstance().getPomTemplate() ); 
		 String referencedPomSkeleton = pomSkeletonBuilder.getPomSkeleton(project.getName());
		 IFile referencedProjectFile = project.getFile("project.xml"); 
		 referencedProjectFile.create(new ByteArrayInputStream(referencedPomSkeleton.getBytes()), false, null);
	}

	public static void copyFile(File in, File out) throws Exception
	{
		FileChannel sourceChannel = new FileInputStream(in).getChannel();
		FileChannel destinationChannel = new FileOutputStream(out).getChannel();
		sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
		sourceChannel.close();
		destinationChannel.close();
	}
	
	public static File getPom(IProject project) {
		//weird trick to fix a NPE. dont know yet why we got that NPE
		if ( project.exists() ) {
			IPathResolver pathResolver = new DefaultPathResolver();
			IPath referencedProjectLocation = project.getLocation();
			return new File(pathResolver.getAbsolutePath(referencedProjectLocation.append("project.xml")) );
		}
		return null;
	}

	public static void refresh(IProject project) throws Exception {
		IFile projectFile = project.getFile("project.xml");
		projectFile.refreshLocal(IResource.DEPTH_ZERO, null);
		IFile propertiesFile = project.getFile("project.properties");
		if ( propertiesFile.exists() ) {
			propertiesFile.refreshLocal(IResource.DEPTH_ZERO, null);
		}
	}

	public static void assertPomNotEmpty(IFile pom) {
		try {
			if ( pom.exists() ) {
				InputStream inputStream = pom.getContents(true);
			
				if ( inputStream.read() == -1 ) {
					InputStream stream = FileUtils.class.getResourceAsStream("/templates/standard/project.xml"); 
					pom.setContents(stream, true, true, null);
					stream.close();
				}
				inputStream.close();
			}
		} catch (Exception e) {
			log.error("Unable to check if POM already exists due to : " + e);
		}
	}
	
	public static IFile assertIgnoreFileExists(IContainer container)  throws Exception {		
		IFile file = container.getFile(new Path(".mvnignore"));
		if ( !file.exists() ) {
			InputStream is = new StringInputStream("");
			file.create(is,true, null);
		}
		return file;	
	}
	
	public static IFile assertIgnoreFileExists(Project project)  throws Exception {		
		File systemFile = new File(project.getFile().getParent(), ".mvnignore");
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		
		IFile file = workspaceRoot.getFileForLocation(new Path(systemFile.getAbsolutePath()).makeAbsolute());
		if ( !file.exists() ) {
			InputStream is = new StringInputStream("");
			file.create(is,true, null);
		}
		return file;	
	}
	
	
	public static List getIgnoredResources(Project project) {
		try {
			IFile file = assertIgnoreFileExists(project);
			return getIgnoredResources(file);
		} 
		catch (Exception e) {
			log.error("Cannot read ignored resources", e);
			return new ArrayList();
		}
	}
	
	public static List getIgnoredResources(IProject project) {
		try {
			IFile file = project.getFile(new Path(".mvnignore"));
			if ( !file.exists() ) {
				return getIgnoredResources(file);
			}
			return new ArrayList();
		} 
		catch (Exception e) {
			log.debug("Cannot read ignored resources", e);
			return new ArrayList();
		} 
	}
	
	private static List getIgnoredResources(IFile ignoredResourceFile) throws Exception {
		RandomAccessFile raf = null;
		List ignoredLines = new ArrayList();
		
		try {
			raf = new RandomAccessFile(ignoredResourceFile.getLocation().toOSString(), "r");
			String line = null;
			while ( (line = raf.readLine()) != null ) {
				if ( line.trim().length() > 0 ) {
				    ignoredLines.add(line.trim());
				}
			}
		}
		finally {
			if ( raf != null ) {
				raf.close();
			}
		}
		
		return ignoredLines;
	}
	
	public static  boolean isArtifactIgnored(String ignoreLine, IProject project) {
		List ignoredResources = getIgnoredResources(project);
		for ( int u = 0; u < ignoredResources.size(); u++ ) {
			if ( ((String) ignoredResources.get(u)).equals(ignoreLine) ) {
				return true;
			}
		}
		return false;
	}
	
	
	public static List getPoms(IProject project) throws Exception {

		Project pom = ProjectReader.getReader().read(FileUtils.getPom(project));
		List visitedPoms = new ArrayList();		

		if ( pom != null ) {
			//dirty trick to avoid infinite loops if user has introduced one by mistake
			visitedPoms.add(pom.getFile());
	
			String extend = pom.getExtend();
			
			//recurse poms
			while ( extend != null && !extend.trim().equals("") ) {
				
				//resolve extend
				extend = extend.replaceAll("\\$\\{basedir\\}", pom.getFile().getParent().replaceAll("\\\\", "/"));
				File extendFile = new File(extend);
				if ( !extendFile.exists() ) {
					
					extendFile = new File(pom.getFile().getParent(), extend);
					if ( !extendFile.exists() ) {
						log.debug(extendFile.getAbsolutePath() + " doesnot exist. break.");
						//@ TODO throw new ExtendDoesnotExistException(..)
						break;
					}
				}
				
				//assert pom has not been visited yet
				if ( visitedPoms.contains(extendFile.getAbsolutePath()) ) {
					//@TODO throw new InfinitePomRecursionException(..)
					break;
				}
				visitedPoms.add(new File(extendFile.getAbsolutePath()));
				pom = ProjectReader.getReader().read(extendFile);
			
				extend = pom.getExtend();
			}
		}
		
		return visitedPoms;
	}
	
	public static IProject getParentProjectForFile(File f) {
	    IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(f.getName());
	    if ( project.exists() ) {
	        return project;
	    }
	    return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(f.getAbsolutePath())).getProject();
	}
	
	public static File getSystemFile(IPath location) {
	    return ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
	}
	
	public static void refreshProperties(IContainer eclipseContainer) {
        IFile file = eclipseContainer.getFile(new Path("project.properties"));
        try {
            if ( file.exists() ) {
                file.refreshLocal(IResource.DEPTH_ZERO, null);
            }
        }
        catch (CoreException e) {
            String message = "Unable to refresh project.properties"; 
            log.error(message, e);
        }
    }
	
	public static File getProjectPropertiesFile(File containerDir) throws IOException {
	    File file = new File(containerDir, "project.properties");
	    String errorMessage = "Unable to create project.properties in directory " + containerDir;
	    if ( !file.exists() ) {
	        try {
                boolean result = file.createNewFile();
                if ( !result ) {
                    log.warn(errorMessage);
                }
            }
            catch (IOException e) {
                log.error(errorMessage, e);
                throw e;
            }
	    }
	    return file;
	}
}
