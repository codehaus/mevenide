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
    	
    	//remove ignored depencency from dependencuesCopy
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
    
    private boolean lowMatch(Dependency d1, Dependency d2) {
        return d1.getArtifactId().equals(d2.getArtifactId());
    }
    
    
    
}
