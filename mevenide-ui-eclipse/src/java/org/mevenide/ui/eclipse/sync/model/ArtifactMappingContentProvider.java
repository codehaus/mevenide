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
package org.mevenide.ui.eclipse.sync.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ArtifactMappingContentProvider implements ITreeContentProvider {
    private static Log log = LogFactory.getLog(ArtifactMappingContentProvider.class); 
    
    private int direction;
    
    private List poms;
    
    public Object[] getChildren(Object parentElement) {
		if ( parentElement instanceof PomContainer ) {
			return ((PomContainer) parentElement).getNodes();
		}

        if ( parentElement instanceof ProjectContainer ) {
            //@TODO move to ProjectContainer
            IProject project = ((ProjectContainer) parentElement).getProject();
            List dependencyContainers = null;
            List directoryContainers = null;
            try {
                if ( project.hasNature(JavaCore.NATURE_ID) ) {
                    dependencyContainers = DependencyMappingNodeContainerFactory.getFactory().getContainer(JavaCore.create(project), poms);
                    for (int i = 0; i < dependencyContainers.size(); i++) {
                        AbstractArtifactMappingNodeContainer dependencyContainer = (AbstractArtifactMappingNodeContainer) dependencyContainers.get(i);
                        dependencyContainer.setParent((ProjectContainer) parentElement);
	                    dependencyContainer.filter(direction);
                    }
                    directoryContainers = DirectoryMappingNodeContainerFactory.getFactory().getContainer(JavaCore.create(project), poms);
                    for (int i = 0; i < directoryContainers.size(); i++) {
                        AbstractArtifactMappingNodeContainer directoryContainer = (AbstractArtifactMappingNodeContainer) directoryContainers.get(i);
                        directoryContainer.setParent((ProjectContainer) parentElement);
                        directoryContainer.filter(direction);
                    }
                }
                else {
                    //@TODO user should know that the project has not been processed b/c it is not a java project 
                }
            }
            catch (CoreException e) {
                log.error(e);
            }
            
            
            List allContainers = new ArrayList(directoryContainers);
            allContainers.addAll(dependencyContainers);
            int idx = 0;
            while ( idx < allContainers.size() ) {
				IArtifactMappingNodeContainer container = (IArtifactMappingNodeContainer) allContainers.get(idx);
                if ( container.getNodes().length == 0 ) {
                    allContainers.remove(container);
                }
				idx++;
            }

            Object[] containers = null; 
			try {
				containers = getPomContainers(allContainers, (ProjectContainer) parentElement); 
			}
          	catch ( Exception e ) {
				log.error("Unable to retrieve pom containers", e);
			}
			((ProjectContainer) parentElement).setPomContainers(containers);
            return containers;
        }
        if ( parentElement instanceof IArtifactMappingNodeContainer ) {
            return ((IArtifactMappingNodeContainer) parentElement).getNodes();
        }
        return null;
    }
    
	private Object[] getPomContainers(List containers, ProjectContainer projectContainer) throws Exception {
		Map hashMap = new HashMap();
		for (int i = 0; i < containers.size(); i++) {
            IArtifactMappingNodeContainer container = (IArtifactMappingNodeContainer) containers.get(i);
			Project pom = container.getPrimaryPom();
			if ( hashMap.containsKey(pom.getFile()) ) {
				((List) hashMap.get(pom.getFile())).add(container);
			}
			else {
        	    List list = new ArrayList();
				list.add(container);
				hashMap.put(pom.getFile(), list);
			}
        }

		List pomContainers = new ArrayList();
		Iterator itr = hashMap.keySet().iterator();
		while ( itr.hasNext() ) {
			File pomFile = (File) itr.next();
            PomContainer pomContainer = new PomContainer(pomFile);
			pomContainer.setNodes(((List) hashMap.get(pomFile)).toArray());
			pomContainer.setParent(projectContainer);
			pomContainers.add(pomContainer);
		}

		return pomContainers.toArray();
	}
 
    public Object getParent(Object element) {
		if ( element instanceof PomContainer )  {
			return ((PomContainer) element).getParent();
		}
        if ( element instanceof IArtifactMappingNode ) {
            return ((IArtifactMappingNode) element).getParent();
        }
        if ( element instanceof AbstractArtifactMappingNodeContainer ) {
            return ((AbstractArtifactMappingNodeContainer) element).getParent();
        }
        return null;
    }
    
    public boolean hasChildren(Object element) {
        if ( element instanceof ProjectContainer )  {
			return ((ProjectContainer) element).getPomContainers() != null && ((ProjectContainer) element).getPomContainers().length > 0;
		}
		if ( element instanceof PomContainer )  {
			return ((PomContainer) element).getNodes() != null && ((PomContainer) element).getNodes().length > 0;
		}
        if ( element instanceof AbstractArtifactMappingNodeContainer ) {
            return ((AbstractArtifactMappingNodeContainer) element).getNodes() != null && ((AbstractArtifactMappingNodeContainer) element).getNodes().length > 0;
        }
        if ( element instanceof IArtifactMappingNode ) {
            return false;
        }
        return getChildren(element) != null && getChildren(element).length > 0;
    }
    
    public void dispose() {
    }
    
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
    
    public Object[] getElements(Object inputElement) {
        return new Object[] { new ProjectContainer((IProject) inputElement) };
    }
    
    public int getDirection() {
        return direction;
    }
    
    public void setDirection(int direction) {
        this.direction = direction;
    }
    
    public void setPoms(List poms) {
        this.poms = poms;
    }
}

