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
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.ui.eclipse.DefaultPathResolver;
import org.mevenide.ui.eclipse.IPathResolver;
import org.mevenide.ui.eclipse.util.EclipseProjectUtils;
import org.mevenide.ui.eclipse.util.FileUtils;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyMappingNodeContainerFactory {
	private static Log log = LogFactory.getLog(DependencyMappingNodeContainerFactory.class);

    private static DependencyMappingNodeContainerFactory factory = new DependencyMappingNodeContainerFactory();
    
    public static DependencyMappingNodeContainerFactory getFactory() {
        return factory;
    }

	public List getContainer(IJavaProject javaProject, List pomFiles) {
	    List cons = new ArrayList();
	    
		try {
		    for (int u = 0; u < pomFiles.size(); u++) {
				DependencyMappingNodeContainer con = new DependencyMappingNodeContainer();
                
				Project pom = (Project) pomFiles.get(u);
		        
	            List nodes = new ArrayList();
				IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath(true);
				IPathResolver pathResolver = new DefaultPathResolver();
				
				for (int i = 0; i < classpathEntries.length; i++) {
				   if ( ((classpathEntries[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY
							&& !FileUtils.isClassFolder(classpathEntries[i].getPath().toOSString(), javaProject.getProject()) )
							|| classpathEntries[i].getEntryKind() == IClasspathEntry.CPE_PROJECT) 
							&& !EclipseProjectUtils.getJreEntryList(javaProject.getProject()).contains(pathResolver.getAbsolutePath(classpathEntries[i].getPath())) ) {
	
						IClasspathEntry classpathEntry = classpathEntries[i];
						DependencyMappingNode node = createDependencyMappingNode(javaProject, classpathEntry);
						String ignoreLine = ((Dependency) node.getResolvedArtifact()).getGroupId() + ":" + ((Dependency) node.getResolvedArtifact()).getArtifactId();
						if ( !FileUtils.isArtifactIgnored(ignoreLine, javaProject.getProject()) ) {
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

    private DependencyMappingNode createDependencyMappingNode(IJavaProject javaProject, IClasspathEntry classpathEntry) throws Exception, CoreException {
        

        DependencyMappingNode node = new DependencyMappingNode();
        Dependency resolvedDependency = null;

        if ( classpathEntry.getEntryKind() == IClasspathEntry.CPE_LIBRARY ) { 	
	        String path = classpathEntry.getPath().toOSString();
	        if ( !new File(path).exists() ) {
	        	//not the best way to get the absoluteFile ... 
	        	path = javaProject.getProject().getLocation().append(classpathEntry.getPath().removeFirstSegments(1)).toOSString();
	        }
            resolvedDependency = DependencyFactory.getFactory().getDependency(path);
        }
        else {
	        String path = classpathEntry.getPath().toString();
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			//crap..
        	IProject referencedProject = root.getProject(path.substring(1, path.length()));
			if ( !referencedProject.getName().equals(javaProject.getProject().getName()) ) {
     		    resolvedDependency = createDependencyFromProject(referencedProject);
			}
        }

        node.setIdeEntry(classpathEntry);
        node.setResolvedDependency(resolvedDependency);
		//node.setDependency(findBestMatch(resolvedDependency));
        return node;
    }
    
	private Dependency createDependencyFromProject(IProject referencedProject) throws Exception {
		if ( referencedProject.exists() )  {
                
            File referencedPom = FileUtils.getPom(referencedProject);
            //check if referencedPom exists, tho it should since we just have created it

            if ( !referencedPom.exists() ) {
                FileUtils.createPom(referencedProject);
            }
            ProjectReader reader = ProjectReader.getReader();
            return reader.extractDependency(referencedPom);
        }
		return null;
	}

}
