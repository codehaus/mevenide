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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.jdt.core.IJavaProject;
import org.mevenide.project.io.ProjectReader;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class AbstractArtifactMappingNodeContainer implements IArtifactMappingNodeContainer {
    private static final Log log = LogFactory.getLog(AbstractArtifactMappingNodeContainer.class);
    
    protected IArtifactMappingNode[] nodes;
    
    protected int direction;
    
    private EclipseContainerContainer parent;
    
    private Project primaryPom;
    
    public void attachJavaProject(IJavaProject javaProject, Project pom) throws Exception {
        //Project pom = ProjectReader.getReader().read(FileUtils.getPom(javaProject.getProject()));
        	
        primaryPom = pom;
        
        attachPom(pom);
        
        //dirty trick to avoid infinite loops if user has introduced one by mistake
        List visitedPoms = new ArrayList();
        
        String extend = pom.getExtend();
        
        //recurse poms
        while ( extend != null && !extend.trim().equals("") ) {
            
            //resolve extend
            extend = extend.replaceAll("\\$\\{basedir\\}", pom.getFile().getParent().replaceAll("\\\\", "/"));
            File extendFile = new File(extend);
            if ( !extendFile.exists() ) {
              
                extendFile = new File(pom.getFile().getParent(), extend);
                if ( !extendFile.exists() ) {
                    log.debug(extendFile.getAbsolutePath() + " doesnot exist. break.");
                    //@ TODO throw new ExtendDoesnotExistException(..)
                    break;
                }
            }
            
            //assert pom has not been visited yet
            if ( visitedPoms.contains(extendFile.getAbsolutePath()) ) {
                //@TODO throw new InfinitePomRecursionException(..)
                break;
            }
            visitedPoms.add(extendFile.getAbsolutePath());
            
            //attach pom
            pom = ProjectReader.getReader().read(extendFile);
            attachPom(pom);
            extend = pom.getExtend();
        }
    }
    
    public int getDirection() {
        return direction;
    }
    public void setDirection(int direction) {
        this.direction = direction;
    }
    public IArtifactMappingNode[] getNodes() {
        return nodes;
    }
    public void setNodes(IArtifactMappingNode[] nodes) {
        this.nodes = nodes;
    }

    public EclipseContainerContainer getParent() {
        return parent;
    }
    
    public void setParent(EclipseContainerContainer parent) {
        this.parent = parent;
    }

    public IArtifactMappingNodeContainer filter(int direction) {
        IArtifactMappingNodeContainer newContainer = this;
    	newContainer.setDirection(direction);
    
    	List newNodeList = new ArrayList(); 
    	if ( nodes != null ) {
    		for (int i = 0; i < nodes.length; i++) {
    			if ( (nodes[i].getChangeDirection() & direction) != 0) {
    				newNodeList.add(nodes[i]);
    			}
    		}
        }
    
    	IArtifactMappingNode[] newNodeArray = new IArtifactMappingNode[newNodeList.size()]; 
    	for (int i = 0; i < newNodeArray.length; i++) {
    		IArtifactMappingNode newNode = (IArtifactMappingNode) newNodeList.get(i);
    		newNode.setParent(newContainer);
            newNodeArray[i] = newNode;
        }
    	newContainer.setNodes(newNodeArray);
    
        return newContainer;
    }
    
    public void removeNode(Object node) {
    	int idx = -1;
    	
    	for (int i = 0; i < nodes.length; i++) {
			if ( nodes[i].equals(node) ) {
				idx = i;
				break;
			}
		}
    	
    	if ( idx > -1 ) {
    		IArtifactMappingNode[] newNodes = new IArtifactMappingNode[nodes.length - 1];
    		for (int i = 0; i < nodes.length; i++) {
				if ( i < idx ) {
					newNodes[i] = nodes[i];
				}
				if ( i > idx ){
					newNodes[i - 1] = nodes[i];
				}
			}
    		nodes = newNodes;
    	}
    }

    public Project getPrimaryPom() {
        return primaryPom;
    }

}
