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
package org.mevenide.project.io;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Build;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.apache.maven.project.UnitTest;
import org.mevenide.project.dependency.DependencyUtil;
import org.mevenide.project.resource.DefaultResourceResolver;
import org.mevenide.project.resource.IResourceResolver;
import org.mevenide.project.resource.ResourceUtil;
import org.mevenide.project.source.SourceDirectoryUtil;

/**
 * 
 * @todo use a single instancce per POM
 * 
 * @doco each addition writes the pom :
 *     <code>write(project, pom);</code>
 * thus if the process fails it doesnt affect 
 * the previously added artifacts.
 * 
 * @todo transaction-awareness 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class ProjectWriter {
	private static Log log = LogFactory.getLog(ProjectWriter.class);
	
	private static ProjectWriter projectWriter = null;
	
	private IResourceResolver resourceResolver = new DefaultResourceResolver();
	private ProjectReader projectReader ;
	private DefaultProjectMarshaller marshaller ; 
	private JarOverrider overrider = new JarOverrider(this);
	
	private ProjectWriter() throws Exception  {
		marshaller = new DefaultProjectMarshaller();
		projectReader = ProjectReader.getReader();
	}
	
	public static synchronized ProjectWriter getWriter() throws Exception {
		if (projectWriter == null) {
			projectWriter = new ProjectWriter();
		}
		return projectWriter;
	}
	
	
	/**
	 * add a resource entry to the ${pom.build} 
	 * the resource is expected to be a directory, however we will 
	 * handle the case where it is a single file
	 * 
	 * @param path
	 * @param pom
	 */
	public void addResource(String path, File pom) throws Exception {
		Project project = projectReader.read(pom);
		Resource resource = ResourceUtil.newResource(path);
		if ( project.getBuild() == null ) {
			project.setBuild(new Build());
		}
		resourceResolver.mergeSimilarResources(project, resource);
		write(project, pom);	
	}
	
	public void addUnitTestResource(String path, File pom) throws Exception {
		Project project = projectReader.read(pom);
		Resource resource = ResourceUtil.newResource(path);
		if ( project.getBuild() == null ) {
			project.setBuild(new Build());
		}
		if ( project.getBuild().getUnitTest() == null ) {
			project.getBuild().setUnitTest(new UnitTest());
		}
		resourceResolver.mergeSimilarUnitTestResources(project, resource);
		write(project, pom);	
	}
	
	/**
	 * add a source directory to the POM *if needed* 
     * 
     * @pre the src directory must not null 
	 * 
	 * @param path
	 * @param pom
	 * @param sourceType
	 * @throws Exception
	 */
	public void addSource(String path, File pom, String sourceType) throws Exception {
		
		Project project = projectReader.read(pom);
		
		if ( !SourceDirectoryUtil.isSourceDirectoryPresent(project, path)) {
			
			SourceDirectoryUtil.addSource(project, path, sourceType);
			write(project, pom);
			
		}
		
	}
	

	
	public void setDependencies(List dependencies, File pom) throws Exception {
		Project project = projectReader.read(pom);
		List nonResolvedDependencies = DependencyUtil.getNonResolvedDependencies(dependencies);
		
		log.debug("writing " + dependencies.size() + " resolved dependencies, " + nonResolvedDependencies.size() + " non resolved ones");
		
		project.setDependencies(dependencies);
		write(project, pom);
		
		File propertiesFile = new File(pom.getParent(), "project.properties");
		if ( !propertiesFile.exists() ) {
			propertiesFile.createNewFile();
		}
		overrider.unsetOverriding(propertiesFile);
		
		//jaroverriding
		for (int i = 0; i < nonResolvedDependencies.size(); i++) {
			Dependency dependency = (Dependency) nonResolvedDependencies.get(i); 
			overrider.jarOverride(dependency.getArtifact(), propertiesFile, pom);
		}
		
	}

	void write(Project project, File pom) throws Exception {
		Writer writer = new FileWriter(pom, false);
		marshaller.marshall(writer, project);
		writer.close();
	}
	
	
	/**
	 * add a project dependency to this POM, reading required information from the referenced POM
	 * 
	 * @param referencedPom
	 * @param pom
	 * @throws Exception
	 */
	public void addProject(File referencedPom, File pom) throws Exception {
		ProjectReader reader = ProjectReader.getReader();
		
		Dependency dependency = reader.extractDependency(referencedPom);
		
		Project project = reader.read(pom);
		project.addDependency(dependency);
		
		write(project, pom);
	}

	public void resetSourceDirectories(File pomFile) throws Exception {
					
		ProjectReader reader = ProjectReader.getReader();
		Project project = reader.read(pomFile);
		if ( project.getBuild() != null ) {
			project.getBuild().setAspectSourceDirectory(null);
			project.getBuild().setIntegrationUnitTestSourceDirectory(null);
			
			project.getBuild().setUnitTestSourceDirectory(null);
			project.getBuild().setUnitTest(null);
			
			project.getBuild().setSourceDirectory(null);
			
			project.getBuild().setResources(new ArrayList());
//			if ( project.getBuild().getUnitTest() != null ) {
//				project.getBuild().getUnitTest().setResources(new ArrayList());
//			}
			
			IProjectMarshaller iProjectMarshaller = new DefaultProjectMarshaller();
			iProjectMarshaller.marshall(new FileWriter(pomFile), project);
	
		}
	}
	
	public void updateExtend(File pomFile, boolean isInherited, String parentPom) throws Exception {
		log.debug("isInherited = " + (isInherited) + " ; parentPom = " + parentPom);
		ProjectReader reader = ProjectReader.getReader();
		Project project = reader.read(pomFile);
		if ( !isInherited ) {
			project.setExtend(null);
		}
		else {
			project.setExtend(parentPom);
		}
		IProjectMarshaller iProjectMarshaller = new DefaultProjectMarshaller();
		iProjectMarshaller.marshall(new FileWriter(pomFile), project);
	}
}
