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
import org.mevenide.ui.eclipse.util.FileUtils;

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
        if ( project.getBuild() != null ) {
            List resources = project.getBuild().getResources() == null ? new ArrayList() : project.getBuild().getResources();
            List orphanResources = new ArrayList(resources);
            
            List ignoredResource = FileUtils.getIgnoredResources(project);
            
	        for (Iterator itr = resources.iterator(); itr.hasNext(); ) {
	            Resource pomResource = (Resource) itr.next();
	            if ( ignoredResource.contains(pomResource.getDirectory().replaceAll("\\\\", "/")) ) {
	            	orphanResources.remove(pomResource);
	            }
	            else {
		            for (int i = 0; i < nodes.length; i++) {
				        DirectoryMappingNode currentNode = (DirectoryMappingNode) nodes[i];
				        Directory resolvedDirectory = (Directory) currentNode.getResolvedArtifact();
				        if ( currentNode.getArtifact() == null ) {
				            if ( resolvedDirectory == null || lowMatch(pomResource, resolvedDirectory) ) {
				                currentNode.setArtifact(pomResource);
				                currentNode.setDeclaringPom(project.getFile());
				                orphanResources.remove(pomResource);
				            }
				        }
			        }
	            }
	    	}
	        attachOrphanArtifacts(orphanResources, project);
        }
    }
    
    private boolean lowMatch(Resource resource, Directory directory) {
        log.debug("resource dir : " + resource.getDirectory() + ", directory path : " + directory.getPath() + " match ? " + (resource.getDirectory() != null && resource.getDirectory().replaceAll("\\\\", "/").equals(directory.getPath().replaceAll("\\\\", "/"))));
        if ( resource.getDirectory() == null ) return false;
        return resource.getDirectory().replaceAll("\\\\", "/").equals(directory.getPath().replaceAll("\\\\", "/"));
    }
    
    private void attachOrphanArtifacts(List orphanArtifacts, Project project) {
		
		removeDuplicate(orphanArtifacts);		
		
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

	private void removeDuplicate(List orphanArtifacts) {
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

	private boolean haveSamePath(DirectoryMappingNode node1, DirectoryMappingNode node2) {
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

