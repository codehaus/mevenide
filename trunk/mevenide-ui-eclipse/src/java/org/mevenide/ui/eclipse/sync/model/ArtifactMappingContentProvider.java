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
package org.mevenide.ui.eclipse.sync.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.util.EclipseProjectUtils;

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

        if ( parentElement instanceof EclipseContainerContainer ) {
            //@TODO move to EclipseContainerContainer
            IProject project = ((EclipseContainerContainer) parentElement).getProject().getProject();
            List dependencyContainers = null;
            List directoryContainers = null;
            try {
            	if ( !project.hasNature(JavaCore.NATURE_ID) ) {
                    //ask user if we should attach java nature and process or stop here
            		MessageDialog dialog = new MessageDialog(
            									PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
												"Attach Java Nature...",
												null,
												"Current project doesnot have JavaNature. Should we attach it ? ",
												MessageDialog.QUESTION,
												new String[] {"Yes", "No"}, 0);
            		int userChoice = dialog.open();
            		if ( userChoice == Window.OK ) {
            			EclipseProjectUtils.attachJavaNature(project);
            		}
            		else { 
            			return null;
            		}
                }
                if ( project.hasNature(JavaCore.NATURE_ID) ) {
                    dependencyContainers = DependencyMappingNodeContainerFactory.getFactory().getContainer(JavaCore.create(project), poms);
                    for (int i = 0; i < dependencyContainers.size(); i++) {
                        AbstractArtifactMappingNodeContainer dependencyContainer = (AbstractArtifactMappingNodeContainer) dependencyContainers.get(i);
                        dependencyContainer.setParent((EclipseContainerContainer) parentElement);
	                    dependencyContainer.filter(direction);
                    }
                    directoryContainers = DirectoryMappingNodeContainerFactory.getFactory().getContainer(JavaCore.create(project), poms);
                    for (int i = 0; i < directoryContainers.size(); i++) {
                        AbstractArtifactMappingNodeContainer directoryContainer = (AbstractArtifactMappingNodeContainer) directoryContainers.get(i);
                        directoryContainer.setParent((EclipseContainerContainer) parentElement);
                        directoryContainer.filter(direction);
                    }
                }
            }
            catch (CoreException e) {
                log.error(e);
            }
            
            List allContainers = new ArrayList(directoryContainers);
            allContainers.addAll(dependencyContainers);

            Object[] containers = null; 
			try {
				containers = getPomContainers(allContainers, (EclipseContainerContainer) parentElement); 
			}
          	catch ( Exception e ) {
				log.error("Unable to retrieve pom containers", e);
			}
			((EclipseContainerContainer) parentElement).setPomContainers(containers);
            return containers;
        }
        if ( parentElement instanceof IArtifactMappingNodeContainer ) {
        	return ((IArtifactMappingNodeContainer) parentElement).getNodes();
        }
        if ( parentElement instanceof DependencyMappingNode ) {
        	Dependency dependency = (Dependency) ((DependencyMappingNode) parentElement).getWrappedObject();
        	Object[] properties = new Object[dependency.getProperties().size()];
        	for (int i = 0; i < properties.length; i++) {
        		properties[i] = new DependencyPropertyWrapper(dependency, (String) dependency.getProperties().get(i));
			}
        	return properties;
        }
        return null;
    }
    
	private Object[] getPomContainers(List containers, EclipseContainerContainer projectContainer) throws Exception {
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
        if ( element instanceof EclipseContainerContainer )  {
			return ((EclipseContainerContainer) element).getPomContainers() != null && ((EclipseContainerContainer) element).getPomContainers().length > 0;
		}
		if ( element instanceof PomContainer )  {
			return ((PomContainer) element).getNodes() != null && ((PomContainer) element).getNodes().length > 0;
		}
        if ( element instanceof AbstractArtifactMappingNodeContainer ) {
            return ((AbstractArtifactMappingNodeContainer) element).getNodes() != null && ((AbstractArtifactMappingNodeContainer) element).getNodes().length > 0;
        }
        if ( element instanceof DirectoryMappingNode ) {
            return false;
        }
        if ( element instanceof DependencyPropertyWrapper ) {
        	return false;
        }
        return getChildren(element) != null && getChildren(element).length > 0;
    }
    
    public void dispose() {
    }
    
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
    
    public Object[] getElements(Object inputElement) {
        return new Object[] { new EclipseContainerContainer((IProject) inputElement) };
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

