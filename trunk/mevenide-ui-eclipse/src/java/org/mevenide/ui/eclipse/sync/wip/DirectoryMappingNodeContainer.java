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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
class DirectoryMappingNodeContainer extends AbstractArtifactMappingNodeContainer {
    private static final Log log = LogFactory.getLog(DirectoryMappingNodeContainer.class);
    
    public String getLabel() {
        return "Source Folders";
    }
    
    public IArtifactMappingNodeContainer filter(int direction) {
        return this;
    }
    
    public void attachPom(Project project) {
        //attachDirectories(project);
        attachResources(project);
    }
    
    private void attachResources(Project project) {
        if ( project.getBuild() != null ) {
            List resources = project.getBuild().getResources() == null ? new ArrayList() : project.getBuild().getResources();
            List resourcesCopy = new ArrayList(resources);
            
		    for (int i = 0; i < nodes.length; i++) {
		        DirectoryMappingNode currentNode = (DirectoryMappingNode) nodes[i];
		        Directory resolvedDirectory = (Directory) currentNode.getResolvedArtifact();
		        for (Iterator itr = resources.iterator(); itr.hasNext(); ) {
		            Resource pomResource = (Resource) itr.next();
		            if ( resolvedDirectory == null || lowMatch(pomResource, resolvedDirectory) ) {
		                System.err.println();
		                currentNode.setArtifact(pomResource);
		                currentNode.setDeclaringPom(project.getFile());
		                resourcesCopy.remove(pomResource);
		            }
		        }
		    }
	        //attachOrphanResources(resourcesCopy);
        }
    }
    
    private boolean lowMatch(Resource resource, Directory directory) {
        log.debug("resource dir : " + resource.getDirectory());
        log.debug("directory path : " + directory.getPath());
        if ( resource.getDirectory() == null ) return false;
        return resource.getDirectory().replaceAll("\\\\", "/").equals(directory.getPath().replaceAll("\\\\", "/"));
    }
}
