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


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
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

	public DependencyMappingNodeContainer getContainer(IJavaProject javaProject) {
		DependencyMappingNodeContainer con = new DependencyMappingNodeContainer();

		try {
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
					//String ignoreLine = ((Dependency) node.getResolvedArtifact()).getGroupId() + ":" + ((Dependency) node.getResolvedArtifact()).getArtifactId(); 
					//if ( !FileUtils.isArtifactIgnored(ignoreLine, javaProject.getProject()) ) {
					node.setParent(con);
					nodes.add(node);
					//}
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
