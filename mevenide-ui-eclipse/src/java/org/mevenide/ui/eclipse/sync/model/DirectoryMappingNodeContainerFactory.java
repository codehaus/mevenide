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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.mevenide.ui.eclipse.DefaultPathResolver;
import org.mevenide.ui.eclipse.util.SourceDirectoryTypeUtil;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DirectoryMappingNodeContainerFactory {
    private static Log log = LogFactory.getLog(DirectoryMappingNodeContainerFactory.class);
    
    private static final DirectoryMappingNodeContainerFactory factory = new DirectoryMappingNodeContainerFactory();

    public static DirectoryMappingNodeContainerFactory getFactory() {
        return factory;
    }
    
    public IArtifactMappingNodeContainer getContainer(IJavaProject javaProject)  {
		List nodes = new ArrayList();
        DirectoryMappingNodeContainer con = new DirectoryMappingNodeContainer();
        try {
	        IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath(true);
	        for (int i = 0; i < classpathEntries.length; i++) {
	            if ( classpathEntries[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
	                
	                IClasspathEntry classpathEntry = classpathEntries[i];
	                DirectoryMappingNode node = createDirectoryMappingNode(javaProject, classpathEntry);
	                node.setParent(con);
	                nodes.add(node);
	            }
	        }
			IArtifactMappingNode[] artifactNodes = new IArtifactMappingNode[nodes.size()]; 
			for (int i = 0; i < nodes.size(); i++) {
			    artifactNodes[i] = (IArtifactMappingNode) nodes.get(i);
            }
			con.setNodes(artifactNodes);
			con.attachJavaProject(javaProject);
        }
        catch (  Exception e ) {
            e.printStackTrace();
            log.error(e);
        }
        return con;
    }

   
    private DirectoryMappingNode createDirectoryMappingNode(IJavaProject javaProject, IClasspathEntry classpathEntry) {
        String path = new DefaultPathResolver().getRelativeSourceDirectoryPath(classpathEntry, javaProject.getProject());
        Directory directory = new Directory();
        String sourceType = SourceDirectoryTypeUtil.guessSourceType(path);
        
        log.debug("creating directory node (" + path + ", " + sourceType + ")");
        directory.setPath(path);
        directory.setType(sourceType);
           
        DirectoryMappingNode node = new DirectoryMappingNode();
        node.setResolvedDirectory(directory);
        node.setIdeEntry(classpathEntry);
        return node;
    }
}
