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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.mevenide.ui.eclipse.DefaultPathResolver;
import org.mevenide.ui.eclipse.util.FileUtils;
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
    
    public List getContainer(IJavaProject javaProject, List pomFiles)  {
        List cons = new ArrayList();
        try {
	        for (int u = 0; u < pomFiles.size(); u++) {
	            
	            Project pom = (Project) pomFiles.get(u);
	            
	            List nodes = new ArrayList();
	            DirectoryMappingNodeContainer con = new DirectoryMappingNodeContainer();
		        
	            IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath(true);
		        for (int i = 0; i < classpathEntries.length; i++) {
		            if ( classpathEntries[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
		                
		                IClasspathEntry classpathEntry = classpathEntries[i];
		                DirectoryMappingNode node = createDirectoryMappingNode(javaProject, classpathEntry);
		                String ignoreLine = ((Directory) node.getResolvedArtifact()).getPath();
		                if ( !FileUtils.isArtifactIgnored(ignoreLine, javaProject.getProject())) { 
		                	node.setParent(con);
		                	nodes.add(node);
		                }
		            }
		        }
				IArtifactMappingNode[] artifactNodes = new IArtifactMappingNode[nodes.size()]; 
				for (int i = 0; i < nodes.size(); i++) {
				    artifactNodes[i] = (IArtifactMappingNode) nodes.get(i);
	            }
				con.setNodes(artifactNodes);
				con.attachJavaProject(javaProject, pom);
				
				cons.add(con);
	        }
        }
        catch (  Exception e ) {
            e.printStackTrace();
            log.error(e);
        }
        return cons;
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
