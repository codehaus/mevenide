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
package org.mevenide.ui.eclipse.sync.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.eclipse.core.resources.IProject;
import org.mevenide.ui.eclipse.sync.event.IActionListener;
import org.mevenide.ui.eclipse.sync.event.IdeArtifactEvent;
import org.mevenide.ui.eclipse.sync.event.PomArtifactEvent;
import org.mevenide.ui.eclipse.sync.model.ArtifactWrapper;
import org.mevenide.ui.eclipse.sync.model.DependencyWrapper;
import org.mevenide.ui.eclipse.sync.model.Directory;
import org.mevenide.ui.eclipse.sync.model.DirectoryWrapper;
import org.mevenide.ui.eclipse.sync.model.ResourceWrapper;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ArtifactAction {
	private static Log log = LogFactory.getLog(ArtifactAction.class);
	
	private List listeners = new ArrayList();
	
	public void addActionListener(IActionListener listener) {
		listeners.add(listener);	
	}
	
	public void removeActionListener(IActionListener listener) {
		listeners.remove(listener);
	}
	
	protected void fireArtifactAddedToClasspath(Object item, IProject project) {
		for (int i = 0; i < listeners.size(); i++) {
			IdeArtifactEvent event = new IdeArtifactEvent(item, project);
			((IActionListener)listeners.get(i)).artifactAddedToClasspath(event);
		}
	}
	
	protected void fireArtifactRemovedFromPom(Object item, Project project) {
		for (int i = 0; i < listeners.size(); i++) {
			PomArtifactEvent event = new PomArtifactEvent(item, project);
			((IActionListener)listeners.get(i)).artifactRemovedFromPom(event);
		}
	}
	
	protected void fireArtifactRemovedFromClasspath(Object item, IProject project) {
		for (int i = 0; i < listeners.size(); i++) {
			IdeArtifactEvent event = new IdeArtifactEvent(item, project);
			((IActionListener)listeners.get(i)).artifactRemovedFromClasspath(event);
		}
	}
	
	protected void fireArtifactAddedToPom(Object item, Project project) {
		log.debug("Artifact (" + item + ") added to POM : " + project.getFile());
		for (int i = 0; i < listeners.size(); i++) {
			PomArtifactEvent event = new PomArtifactEvent(item, project);
			((IActionListener)listeners.get(i)).artifactAddedToPom(event);
		}
	}
	
	protected void fireArtifactIgnored(Object item, IProject project) {
		for (int i = 0; i < listeners.size(); i++) {
			IdeArtifactEvent event = new IdeArtifactEvent(item, project);
			((IActionListener)listeners.get(i)).artifactIgnored(event);
		}
	}
	
	protected void fireArtifactIgnored(Object item, Project project) {
		for (int i = 0; i < listeners.size(); i++) {
			PomArtifactEvent event = new PomArtifactEvent(item, project);
			((IActionListener)listeners.get(i)).artifactIgnored(event);
		}
	}
	
	//crap..
	protected ArtifactWrapper getArtifactWrapper(Object item) {
		if ( item instanceof Dependency ) {
			return new DependencyWrapper((Dependency) item);
		}
		if ( item instanceof Directory ) {
			return new DirectoryWrapper((Directory) item);
		}
		if ( item instanceof Resource ) {
			return new ResourceWrapper((Resource) item);
		}
		return null;
	}
}
