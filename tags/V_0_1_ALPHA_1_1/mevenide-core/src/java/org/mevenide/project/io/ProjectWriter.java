/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Build;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.apache.maven.project.UnitTest;
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.project.dependency.DependencyUtil;
import org.mevenide.project.resource.DefaultResourceResolver;
import org.mevenide.project.resource.IResourceResolver;
import org.mevenide.project.resource.ResourceUtil;
import org.mevenide.project.source.SourceDirectoryUtil;

/**
 * 
 * @todo use a single instancce per POM
 * 
 * each addition writes the pom :
 *     <code>write(project, pom);</code>
 * thus if the process fails it doesnt affect 
 * the previously added artifacts. that is obviously not optimized, tho performance 
 * isnot a issue here. 
 * 
 * @todo should provide a convenience method to write all artifacts in a single transaction
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class ProjectWriter {
	private static Log log = LogFactory.getLog(ProjectWriter.class);
	
	private static IResourceResolver resourceResolver = new DefaultResourceResolver();
	
	private static ProjectWriter projectWriter = null;
	private static Object lock = new Object();
	
	private ProjectReader projectReader ;
	
	private DefaultProjectMarshaller marshaller ; 
		
	public static ProjectWriter getWriter() throws Exception {
		if (projectWriter != null) {
			return projectWriter;
		}
		else {
			synchronized (lock) {
				if (projectWriter == null) {
					projectWriter = new ProjectWriter();
				}
			}
			return projectWriter;
		}
	}
	
	private ProjectWriter() throws Exception  {
		marshaller = new DefaultProjectMarshaller();
		projectReader = ProjectReader.getReader();
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
		unsetOverriding(propertiesFile);
		
		//jaroverriding
		for (int i = 0; i < nonResolvedDependencies.size(); i++) {
			Dependency dependency = (Dependency) nonResolvedDependencies.get(i); 
			jarOverride(dependency.getArtifact(), propertiesFile, pom);
		} 
	}

	private void write(Project project, File pom) throws IOException, Exception {
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
		
		Dependency dependency = reader.getDependency(referencedPom);
		
		Project project = reader.read(pom);
		project.addDependency(dependency);
		
		write(project, pom);
	}

	
    
    void jarOverride(String path, File propertiesFile, File pom) throws Exception {
		Project project = ProjectReader.getReader().read(pom);
		if ( project.getDependencies() == null ) {
			project.setDependencies(new ArrayList());
		}
				
		Dependency dep = DependencyFactory.getFactory().getDependency(path);
		
		String groupId = dep.getGroupId();
		String artifactId = dep.getArtifactId();
		
		if ( groupId == null || groupId.length() == 0 ) {
			dep.setGroupId("nonResolvedGroupId");
		}
		if ( artifactId == null || artifactId.length() == 0 ) {
			String fname = new File(path).getName().substring(0, new File(path).getName().lastIndexOf('.'));
			dep.setArtifactId(fname);
		}
		
		addPropertiesOverride(path, propertiesFile, dep);
		
		//project.getDependencies().remove(DependencyFactory.getFactory().getDependency(path));
		log.debug("adding unresolved dependency (" + path + ")" + DependencyUtil.toString(dep) + "to file " + pom.getAbsolutePath());
		project.addDependency(dep);
		
		write(project, pom);

		
	}

	private void addPropertiesOverride(String path, File propertiesFile, Dependency dep) throws Exception {
		Properties properties = new Properties();
		properties.load(new FileInputStream(propertiesFile));
		
		
		properties.setProperty("maven.jar.override", "on");
		
		properties.setProperty("maven.jar." + dep.getArtifactId(), path);
		
		properties.store(new FileOutputStream(propertiesFile), null);
	} 
	
	private void unsetOverriding(File propertiesFile) throws Exception {
		Properties properties = new Properties();
		properties.load(new FileInputStream(propertiesFile));
					
		List keys = Collections.list(properties.keys());
	    for (int i = 0; i < keys.size(); i++) {
		    Object key = keys.get(i);
		    if ( key instanceof String && ((String) key).startsWith("maven.jar.") ) {
		  	    properties.remove(key);
		    }
	    }
	    properties.store(new FileOutputStream(propertiesFile), null);
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
			
			IProjectMarshaller marshaller = new DefaultProjectMarshaller();
			marshaller.marshall(new FileWriter(pomFile), project);
	
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
		IProjectMarshaller marshaller = new DefaultProjectMarshaller();
		marshaller.marshall(new FileWriter(pomFile), project);
	}
}
