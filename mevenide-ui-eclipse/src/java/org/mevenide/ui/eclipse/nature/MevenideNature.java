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
package org.mevenide.ui.eclipse.nature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.mevenide.ui.eclipse.Mevenide;

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

	public void configure() throws CoreException {
		try {
			configureProject(project);
		} catch (Exception e) {
			log.debug("Unable to add MevenideNature to project '" + project.getName() + "' due to : " + e);
			throw new CoreException(new Status(IStatus.ERROR, "mevenide", 1, e.getMessage(), e));
		}
	}

	public void deconfigure() throws CoreException {
		deconfigureProject(project);
		
	}
	
	public static void configureProject(IProject project) throws Exception {

		addPomNature(project);
		Mevenide.getPlugin().createPom();
		
			
		synchronizeProject(project);
		
		
	}
	
	private static void addPomNature(IProject project) {
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
			log.debug("Unable to set project description due to : " + e);
		}
	}
	
	public static void deconfigureProject(IProject project) throws CoreException {
		if ( project != null ) {
			IProjectDescription description = project.getDescription();
			String[] prevNatures= description.getNatureIds();
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

	
	private static void synchronizeProject(IProject project) {
		
	}

	
	
	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
