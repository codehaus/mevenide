/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Mevenide @ Sourceforge.net.  All rights
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
package org.mevenide.ui.eclipse.sync.wip;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.eclipse.jdt.core.IJavaProject;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.ui.eclipse.util.FileUtils;


/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
class DependencyMappingNodeContainer implements IArtifactMappingNodeContainer {
    private static final Log log = LogFactory.getLog(DependencyMappingNodeContainer.class);
    
    private IArtifactMappingNode[] nodes;
    
    private int direction;
    
    
    public String getLabel() {
        return "Libraries";
    }
    
    //@ TODO generalize
    private void attachPom(Project pom) {
        List dependencies = pom.getDependencies();
        List dependenciesCopy = new ArrayList(dependencies);
        
        for (int i = 0; i < nodes.length; i++) {
            DependencyMappingNode currentNode = (DependencyMappingNode) nodes[i];
            Dependency resolvedDependency = (Dependency) currentNode.getResolvedArtifact();
            for (Iterator itr = dependencies.iterator(); itr.hasNext(); ) {
                Dependency pomDependency = (Dependency) itr.next();
                if ( resolvedDependency != null && lowMatch(pomDependency, resolvedDependency) ) {
                    currentNode.setDependency(pomDependency);
					currentNode.setDeclaringPom(pom.getFile());
                    dependenciesCopy.remove(pomDependency);
                }
            }
        }
        
        attachOrphanDependencies(dependenciesCopy);
    }

    private void attachOrphanDependencies(List dependenciesCopy) {
        IArtifactMappingNode[] newNodes = new IArtifactMappingNode[nodes.length + dependenciesCopy.size()];
        System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
        for (int i = nodes.length; i < newNodes.length; i++) {
            DependencyMappingNode node = new DependencyMappingNode();
            node.setDependency((Dependency) dependenciesCopy.get(i - nodes.length));
			node.setParent(this);
			newNodes[i] = node;
        }
        this.nodes = newNodes;
    }
    
    private boolean lowMatch(Dependency d1, Dependency d2) {
        return d1.getArtifactId().equals(d2.getArtifactId());
    }
    
    public IArtifactMappingNodeContainer filter(int direction) {
        DependencyMappingNodeContainer newContainer = new DependencyMappingNodeContainer();
		newContainer.setDirection(direction);

		List newNodeList = new ArrayList(); 
		for (int i = 0; i < nodes.length; i++) {
		    if ( (nodes[i].getChangeDirection() & direction) != 0) {
				newNodeList.add(nodes[i]);
			}
        }

		DependencyMappingNode[] newNodeArray = new DependencyMappingNode[newNodeList.size()]; 
		for (int i = 0; i < newNodeArray.length; i++) {
			DependencyMappingNode newNode = (DependencyMappingNode) newNodeList.get(i);
			newNode.setParent(newContainer);
            newNodeArray[i] = newNode;
        }
		newContainer.setNodes(newNodeArray);

        return newContainer;
    }
    
    
    
    // @TODO pull-up those ones
    
    public void attachJavaProject(IJavaProject javaProject) throws Exception {
        Project pom = ProjectReader.getReader().read(FileUtils.getPom(javaProject.getProject()));
        
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
			    log.debug("extend doesnot exist");
			    extendFile = new File(pom.getFile().getParent(), extend);
			    if ( !extendFile.exists() ) {
			        log.debug(extendFile.getAbsolutePath() + " doesnot exist either. break.");
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
}
