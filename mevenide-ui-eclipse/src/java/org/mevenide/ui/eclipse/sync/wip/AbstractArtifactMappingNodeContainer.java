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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public abstract class AbstractArtifactMappingNodeContainer implements IArtifactMappingNodeContainer {
    private static final Log log = LogFactory.getLog(AbstractArtifactMappingNodeContainer.class);
    
    protected IArtifactMappingNode[] nodes;
    
    protected int direction;
    
    private ProjectContainer parent;
    
    private Project primaryPom;
    
    public void attachJavaProject(IJavaProject javaProject) throws Exception {
        Project pom = ProjectReader.getReader().read(FileUtils.getPom(javaProject.getProject()));
        	
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

    public ProjectContainer getParent() {
        return parent;
    }
    
    public void setParent(ProjectContainer parent) {
        this.parent = parent;
    }

    public Project getPrimaryPom() {
        return primaryPom;
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
}
