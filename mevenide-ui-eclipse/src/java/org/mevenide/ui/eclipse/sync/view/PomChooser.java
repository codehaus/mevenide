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
package org.mevenide.ui.eclipse.sync.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.properties.resolver.ProjectWalker;
import org.mevenide.ui.eclipse.util.FileUtils;
import org.mevenide.ui.eclipse.util.JavaProjectUtils;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PomChooser {
    private static final Log log = LogFactory.getLog(PomChooser.class);
    
	private IContainer container;
	
	private List poms;

	/**
	 * explicitely set poms [size = 1], so we wont recurse into container. 
	 * 
	 * @warn if using this constructor, container isnot initialized
	 * @open do we want to recurse into pom hierarchy ??  
	 */
	public PomChooser(Project project) {
		poms = new ArrayList();
		poms.add(project.getFile());
	}
	
	public PomChooser(IContainer container) {
		this.container = container;
	}
	
	/**
	 * display a Dialog to allow the user to choose a pom. 
	 * if theres only zero or one available pom, it directly returned  
	 */
	public List openPomChoiceDialog(boolean singleSelection) throws Exception {
		
		List projects = new ArrayList();
		
		//handle case when theres no available pom
		if ( getPoms().size() == 0 ) {
			return projects;
		}

		//special handling when theres only one pom into the current container
		if ( getPoms().size() == 1 ) {
		    File pom = (File) getPoms().get(0);
			Project project = ProjectReader.getReader().read(pom);
			project.setFile(pom);
			projects.add(project);
			return projects;
		}
		
		PomChoiceDialog dialog = new PomChoiceDialog(this, singleSelection);
		
		int result = dialog.open();
		
		if ( result == Dialog.CANCEL ) {
			return projects;
		}
		
		List chosenPoms = dialog.getPoms();
		for (int i = 0; i < chosenPoms.size(); i++) {
		    File pom = (File) chosenPoms.get(i);
			Project project = ProjectReader.getReader().read(pom);
			project.setFile(pom);
			projects.add(project);
        }
		return projects;
	}
	
	
	public synchronized List getPoms() {
		//donot search multiple times
		if ( poms == null ) {
			File projectRoot = new File(container.getLocation().toOSString());
			List allPoms = findPoms(projectRoot);
			log.debug("Found " + allPoms.size() + " POM file");
			poms = allPoms;
		}
		return poms;
	}
	
	/** 
	 * @pre rootDirectory is a valid Eclipse IResource
	 */
	private List findPoms(File rootDirectory) {
	    
	    IProject project = FileUtils.getParentProjectForFile(rootDirectory);
	    
	    List outputFolders = JavaProjectUtils.getOutputFolders(project);
		if ( log.isDebugEnabled() ) {
		    log.debug("Found " + outputFolders.size() + " output folders");
			for (int j = 0; j < outputFolders.size(); j++) {
				log.debug("Found OutputFolder : " + outputFolders.get(j));
			}
		}
	    
		List allPoms = new ArrayList(); 
		
		String fileName = "project.xml";
		
		File[] f = rootDirectory.listFiles();
		for (int i = 0; i < f.length; i++) {
			log.debug(f[i].getAbsolutePath());
			if ( f[i].isDirectory() ) {
				//@todo exclude ${maven.build.dest}, ${maven.test.dest}, etc. => shoudl be customizable thanks a properties file
				
			    if ( !outputFolders.contains(f[i]) ) {
			        allPoms.addAll(findPoms(f[i]));
			    }
			}
			else {
				if ( f[i].getName().equals(fileName) ) {
					allPoms.add(f[i]);
					allPoms.addAll(findAncestors(f[i]));
				}
			}
		}
		return allPoms;
	}
	
	private List findAncestors(File pom) {
		List ancestors = new ArrayList();
		
		try {
		
			Project project = ProjectReader.getReader().read(pom);
			String parent = project.getExtend();
			log.debug(parent);
			if ( parent != null ) {
				File parentPomFile = resolveFile(pom, project, parent);
				if ( parentPomFile.exists() ) {
					ancestors.add(parentPomFile);
					ancestors.addAll(findAncestors(parentPomFile));
				}
			}
		}
		catch ( Exception e ) {
			log.error("Unable to retrieve ancestors for " + pom.getAbsolutePath(), e);
		}
		
		return ancestors;
	}

	private File resolveFile(File pom, Project project, String parent) throws Exception{
		String resolvedParent = new ProjectWalker(project).resolve(parent, true);
		resolvedParent = resolvedParent.replaceAll("\\$\\{basedir\\}", pom.getParent().replaceAll("\\\\", "/"));
		log.debug(resolvedParent);
		File parentPomFile = new File(resolvedParent).getCanonicalFile();
		if ( !parentPomFile.exists() ) {
			//most probably extend isnot prefixed by^${basedir}
			//what are the other other use cases ?
			parentPomFile = new File(pom.getParent(), resolvedParent).getCanonicalFile(); 
		}
		return parentPomFile;
	}
}
