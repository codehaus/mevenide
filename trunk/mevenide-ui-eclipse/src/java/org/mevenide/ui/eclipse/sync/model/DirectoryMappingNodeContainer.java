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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.mevenide.project.ProjectConstants;
import org.mevenide.project.resource.ResourceUtil;
import org.mevenide.project.source.SourceDirectoryUtil;
import org.mevenide.ui.eclipse.util.FileUtils;
import org.mevenide.util.StringUtils;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DirectoryMappingNodeContainer extends AbstractArtifactMappingNodeContainer {
    private static final Log log = LogFactory.getLog(DirectoryMappingNodeContainer.class);
    
    public String getLabel() {
        return "Source Folders";
    }
    
    public void attachPom(Project project) {
        attachResources(project);
        attachDirectories(project);   
    }
    
    private void attachResources(Project project) {
        List resources = getProjectResources(project);
        if ( resources != null ) {
            
            List orphanResources = removeIgnoredResources(project, resources);
	        
            for (Iterator itr = resources.iterator(); itr.hasNext(); ) {
                Resource resource = (Resource) itr.next();
                
                for (int i = 0; i < nodes.length; i++) {
	                DirectoryMappingNode currentNode = (DirectoryMappingNode) nodes[i];
	                if ( currentNode.getArtifact() == null ) {
		                Directory resolvedDirectory = (Directory) currentNode.getResolvedArtifact();
		                if ( resolvedDirectory != null ) {
		                    if ( lowMatch(resource, resolvedDirectory) ) {
			                    Directory pomDirectory = new Directory();
			                    pomDirectory.setPath(resource.getDirectory());
			                    pomDirectory.setType(ProjectConstants.MAVEN_RESOURCE);
		                        currentNode.setArtifact(pomDirectory);
		                        currentNode.setDeclaringPom(project.getFile());
		                        //ResourceUtil.remove(orphanResources, resource);
		                    }
		                }
	                }
                }
            }
		    
	        attachOrphanArtifacts(orphanResources, project);
        }
    }
    
    private List getProjectResources(Project project) {
    	if ( project.getBuild() != null ) {
			List resources = project.getBuild().getResources() == null ? new ArrayList() : project.getBuild().getResources();
			if ( project.getBuild().getUnitTest() != null && project.getBuild().getUnitTest().getResources() != null ) {
				resources.addAll(project.getBuild().getUnitTest().getResources());
			}
			return resources;
    	}
    	return null;
	}

	private List removeIgnoredResources(Project project, List resources) {
		List orphanResources = new ArrayList(resources);
		
		List ignoredResources = FileUtils.getIgnoredResources(project);
		
		for (Iterator itr = resources.iterator(); itr.hasNext(); ) {
		    Resource pomResource = (Resource) itr.next();
		    if ( ignoredResources.contains(pomResource.getDirectory().replaceAll("\\\\", "/")) ) {
		    	ResourceUtil.remove(orphanResources, pomResource); 
		    }
		
		}
		return orphanResources;
	}

	/**
     * @todo extract-me
     */
    boolean lowMatch(Resource resource, Directory directory) {
        if ( resource.getDirectory() == null ) {
        	return false;
        }
        
        String resourcePath = SourceDirectoryUtil.stripBasedir(resource.getDirectory());
        String directoryPath = SourceDirectoryUtil.stripBasedir(directory.getPath());
        
        resourcePath = StringUtils.removeEndingSlash(resourcePath);
        directoryPath = StringUtils.removeEndingSlash(directoryPath);
        
        //log.debug("resource dir : " + resourcePath + ", directory path : " + directoryPath + " match ? " + (resource.getDirectory() != null && resource.getDirectory().replaceAll("\\\\", "/").equals(directory.getPath().replaceAll("\\\\", "/"))));
        return resourcePath.replaceAll("\\\\", "/").equals(directoryPath.replaceAll("\\\\", "/"));
    }
    
	private void attachOrphanArtifacts(List orphanArtifacts, Project project) {
		debugOrphans(orphanArtifacts);
		
		removeDuplicate(orphanArtifacts);		
		
		debugOrphans(orphanArtifacts);
		
    	IArtifactMappingNode[] newNodes = new IArtifactMappingNode[nodes.length + orphanArtifacts.size()];
        System.arraycopy(nodes, 0, newNodes, 0, nodes.length);

        for (int i = nodes.length; i < newNodes.length; i++) {
            DirectoryMappingNode node = new DirectoryMappingNode();
			node.setArtifact(orphanArtifacts.get(i - nodes.length));
            node.setParent(this);
            node.setDeclaringPom(project.getFile());
            newNodes[i] = node;
        }

        this.nodes = newNodes;
        
    }

	private void debugOrphans(List orphanArtifacts) {
		if ( log.isDebugEnabled() ) {
			String logLine = "";
			for (int i = 0; i < orphanArtifacts.size(); i++) {
				logLine += orphanArtifacts.get(i) + ((i != orphanArtifacts.size() - 1) ? ", " : ""); 
			}
			log.debug("orphans : " + logLine);
		}
	}

	void removeDuplicate(List orphanArtifacts) {
		removeEquivalentItems(orphanArtifacts);
		removeAlreadyPresentNodes(orphanArtifacts);
	}

	
	private void removeAlreadyPresentNodes(List orphanArtifacts) {
		List modifiedOrphanList = new ArrayList(orphanArtifacts);
		for (int j = 0; j < modifiedOrphanList.size(); j++) {
			for (int i = 0; i < nodes.length; i++) {
				DirectoryMappingNode currentNode = (DirectoryMappingNode) nodes[i];
				DirectoryMappingNode orphanNode = new DirectoryMappingNode();
				orphanNode.setArtifact(modifiedOrphanList.get(j));
				if ( haveSamePath(orphanNode, currentNode) ) {
					setConflicting(orphanNode, currentNode);
					orphanArtifacts.remove(modifiedOrphanList.get(j));
				}
			}
		}
	}

	void removeEquivalentItems(List orphanArtifacts) {
		List listCopy = new ArrayList(orphanArtifacts);
		List tempList = new ArrayList();
		int u = 0;
		Iterator itr = listCopy.iterator();
		while ( itr.hasNext() ) {
			DirectoryMappingNode orphanNode = new DirectoryMappingNode();
			orphanNode.setArtifact(itr.next());
			if ( !tempList.contains(orphanNode.getLabel()) ) {
				tempList.add(orphanNode.getLabel());
				u++;
			}
			else {
				orphanArtifacts.remove(u);
			}
		}
	}

	/**
     * @todo extract-me
     */
	boolean haveSamePath(DirectoryMappingNode node1, DirectoryMappingNode node2) {
		return node1.getLabel().equals(node2.getLabel());
	}

	private void setConflicting(DirectoryMappingNode orphanNode, DirectoryMappingNode currentNode) {
		//TODO which one is conflicting : orphan ? current ? 
 		//also since both have the same label howto differentiate them ?
		//do we even want to mark them as conflictual, does it make sense ?
	}
    
    private void attachDirectories(Project project) {
        if ( project.getBuild() != null ) {
            Map directories = getPomSourceDirectories(project);
            Map orphanDirectories = new HashMap(directories);
            
            List ignoredResources = FileUtils.getIgnoredResources(project);
            
		    for (Iterator itr = directories.keySet().iterator(); itr.hasNext(); ) {
                String directoryType = (String) itr.next();
                String directory = (String) directories.get(directoryType);
                
                if ( ignoredResources.contains(directory.replaceAll("\\\\", "/")) ) {
                	orphanDirectories.remove(directoryType);
                }
                else {
		            for (int i = 0; i < nodes.length; i++) {
		                DirectoryMappingNode currentNode = (DirectoryMappingNode) nodes[i];
		                if ( currentNode.getArtifact() == null ) {
			                Directory resolvedDirectory = (Directory) currentNode.getResolvedArtifact();
			                if ( resolvedDirectory != null ) {
			                    if ( directory.replaceAll("\\\\", "/").equals(resolvedDirectory.getPath().replaceAll("\\\\", "/")) ) {
				                    Directory pomDirectory = new Directory();
				                    pomDirectory.setPath(directory);
				                    pomDirectory.setType(directoryType);
			                        currentNode.setArtifact(pomDirectory);
			                        currentNode.setDeclaringPom(project.getFile());
			                        orphanDirectories.remove(directoryType);
			                    }
			                }
		                }
	                }
                }
            }
            attachOrphanDirectories(orphanDirectories, project);
        }
    }

    private void attachOrphanDirectories(Map directoriesCopy, Project project) {
		
		List orphanDirectories = new ArrayList();
		
		for (Iterator it = directoriesCopy.keySet().iterator(); it.hasNext(); ) {
			String directoryType = (String) it.next();
			String directoryPath = (String) directoriesCopy.get(directoryType);
			
			Directory directory = new Directory();
			directory.setPath(directoryPath);
			directory.setType(directoryType);
			
			orphanDirectories.add(directory);
		}
		attachOrphanArtifacts(orphanDirectories, project);
	}

    /**
     * @todo extract-me
     */
	private Map getPomSourceDirectories(Project project) {
        //use HashTable to disallow null values..
        Map directories = new Hashtable();
        if ( project.getBuild().getSourceDirectory() != null ) {
            directories.put(ProjectConstants.MAVEN_SRC_DIRECTORY, project.getBuild().getSourceDirectory());
        }
        if ( project.getBuild().getUnitTestSourceDirectory() != null ) {
            directories.put(ProjectConstants.MAVEN_TEST_DIRECTORY, project.getBuild().getUnitTestSourceDirectory());
        }
        if ( project.getBuild().getAspectSourceDirectory() != null ) {
            directories.put(ProjectConstants.MAVEN_ASPECT_DIRECTORY, project.getBuild().getAspectSourceDirectory());
        }
        return directories;
    }
}

