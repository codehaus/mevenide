/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.ui.eclipse.sync.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.util.MevenideUtils;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PomChooser {
    private static final Log log = LogFactory.getLog(PomChooser.class);
    
	private IProject project;
	
	public PomChooser(IProject project) {
		this.project = project;
	}
	
	/**
	 * display a Dialog to allow the user to choose a pom  
	 */
	public List openPomChoiceDialog() throws Exception {
		
		List projects = new ArrayList();
		PomChoiceDialog dialog = new PomChoiceDialog(this);
		
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
	
	
	public List getPoms() {
		File projectRoot = new File(project.getLocation().toString());
		List allPoms = findPoms(projectRoot);
		log.debug("Found " + allPoms.size() + " POM file");
		return allPoms;
	}
	
	private List findPoms(File rootDirectory) {
		List allPoms = new ArrayList(); 
		
		String fileName = "project.xml";
		
		File[] f = rootDirectory.listFiles();
		for (int i = 0; i < f.length; i++) {
			if ( f[i].isDirectory() ) {
				//@todo exclude ${maven.build.dest}, ${maven.test.dest}, etc. => shoudl be customizable thanks a properties file
				allPoms.addAll(findPoms(f[i]));
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
		String resolvedParent = MevenideUtils.resolve(project, parent, true);
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
