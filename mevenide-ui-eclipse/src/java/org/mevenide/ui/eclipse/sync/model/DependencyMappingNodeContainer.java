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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.project.dependency.DependencyUtil;
import org.mevenide.ui.eclipse.util.FileUtils;


/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyMappingNodeContainer extends AbstractArtifactMappingNodeContainer {
    private static final Log log = LogFactory.getLog(DependencyMappingNodeContainer.class);
    
    public String getLabel() {
        return "Libraries";
    }
    
    public void attachPom(Project pom) {
        List dependencies = pom.getDependencies();
        List dependenciesCopy = new ArrayList(dependencies);
        
        for (int i = 0; i < nodes.length; i++) {
            DependencyMappingNode currentNode = (DependencyMappingNode) nodes[i];
            Dependency resolvedDependency = (Dependency) currentNode.getResolvedArtifact();
            if ( currentNode.getArtifact() == null ) {
	            for (Iterator itr = dependencies.iterator(); itr.hasNext(); ) {
	                Dependency pomDependency = (Dependency) itr.next();
	                if ( resolvedDependency != null && lowMatch(pomDependency, resolvedDependency) ) {
	                    currentNode.setDependency(pomDependency);
						currentNode.setDeclaringPom(pom.getFile());
	                    dependenciesCopy.remove(pomDependency);
	                }
	            }
            }
        }
        
        attachOrphanDependencies(dependenciesCopy, pom);
    }

    private void attachOrphanDependencies(List dependenciesCopy, Project project) {
    	removeIgnoredDependencies(dependenciesCopy, project);
    	
        attachDependencies(dependenciesCopy, project);
        
        removeDuplicateNodes();
    }
    
    /**
     * add all items of dependenciesCopy to the nodes List
     */
    private void attachDependencies(List dependenciesCopy, Project project) {
		IArtifactMappingNode[] newNodes = new IArtifactMappingNode[nodes.length + dependenciesCopy.size()];
        System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
        for (int i = nodes.length; i < newNodes.length; i++) {
            DependencyMappingNode node = new DependencyMappingNode();
            node.setDependency((Dependency) dependenciesCopy.get(i - nodes.length));
			node.setParent(this);
			node.setDeclaringPom(project.getFile());
			newNodes[i] = node;
        }
        this.nodes = newNodes;
	}

	/**
	 * remove ignored depencency from dependenciesCopy
	 * dependenciesCopy is altered
	 * 
	 */
	private void removeIgnoredDependencies(List dependenciesCopy, Project project) {
		
    	List ignoredResources = FileUtils.getIgnoredResources(project);
    	List deps = new ArrayList(dependenciesCopy);
    	for (int i = 0; i < deps.size(); i++) {
    		for (int j = 0; j < ignoredResources.size(); j++) {
    			String ignoredResource = (String) ignoredResources.get(j);
				Dependency dependency = (Dependency) deps.get(i);
				if ( ignoredResource.equals(dependency.getGroupId() + ":" + dependency.getArtifactId()) ) {
					dependenciesCopy.remove(dependency);
				}
    		}
		}
	}

	class InternalDependency {
		private DependencyMappingNode node;
		InternalDependency(DependencyMappingNode node) {
			this.node = node;
		}
		public boolean equals(Object obj) {
			if ( !(obj instanceof InternalDependency) ) { 
				return false;
			}
			InternalDependency internalDependency = (InternalDependency) obj;
			return DependencyUtil.areEquals((Dependency) internalDependency.node.getWrappedObject(), (Dependency) node.getWrappedObject());
		}
	}
	
	private void removeDuplicateNodes() {
		List internalDependencyNodes = new ArrayList();
		for (int i = 0; i < nodes.length; i++) {
			InternalDependency dep = new InternalDependency(((DependencyMappingNode) nodes[i]));
			InternalDependency currentDep;
			boolean isPresent = isInternalDependencyPresent(internalDependencyNodes, dep);
			if ( !isPresent ) {
				internalDependencyNodes.add(dep);
			}	
		}
		setNodes(internalDependencyNodes);
    }
    
    private boolean isInternalDependencyPresent(List internalDependencyNodes, InternalDependency dep) {
		boolean isPresent = false;
		for (int j = 0; j < internalDependencyNodes.size(); j++) {
			InternalDependency currentDep = (InternalDependency) internalDependencyNodes.get(j);  
			if ( currentDep.equals(dep) ) {
				isPresent = true;
				break;
			}
		}
		return isPresent;
	}

	private void setNodes(List internalDependencyNodes) {
		IArtifactMappingNode[] newNodes = new IArtifactMappingNode[internalDependencyNodes.size()];
		Iterator itr = internalDependencyNodes.iterator();
		int o = 0;
		while (itr.hasNext()) {
			InternalDependency element = (InternalDependency) itr.next();
			newNodes[o] = element.node;
			o++;	
		}	
		this.nodes = newNodes;
	}

	private boolean lowMatch(Dependency d1, Dependency d2) {
        return d1.getArtifactId().equals(d2.getArtifactId());
    }
    
    
    
}
