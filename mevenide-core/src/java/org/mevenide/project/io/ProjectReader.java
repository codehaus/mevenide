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
package org.mevenide.project.io;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Resource;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.mevenide.project.ProjectConstants;
import org.mevenide.util.StringUtils;


/**
 * 
 *
 * @author Gilles Dodinet (rhill2@free.fr)
 * @version $Id$
 * 
 */
public class ProjectReader {
	private static final Log log = LogFactory.getLog(ProjectReader.class);
	
	private MavenXpp3Reader unmarshaller ; 
	
	private static ProjectReader projectReader = null;

	
	private JarOverrideReader jarOverrideReader;
	
	
	public static synchronized ProjectReader getReader() throws Exception {
		if (projectReader == null) {
			projectReader = new ProjectReader();
		}
		return projectReader;
	}
	
	private ProjectReader() throws Exception {
		unmarshaller = new MavenXpp3Reader();
		jarOverrideReader = new JarOverrideReader();
	}
	
	/**
	 * return the instance of org.apache.maven.project.MavenProject derivated from pom
	 * 
	 */
	public MavenProject read(File pom) throws Exception {
		
		Reader reader = null;
		try {
			reader = new FileReader(pom);
			MavenProject project = new MavenProject();
			Model model = unmarshaller.read(reader);
			project.setModel(model);
			project.setFile(pom);
			
			jarOverrideReader.processOverride(pom, project);
			
			return project;
		}
		finally {
			if ( reader != null ) {
				reader.close();
			}
		}
	}
	
	/**
	 * returns all source directories specified in the POM.
	 * returns a Map whose keys are the src directories type (src, test, aspects, integration) 
	 * and whose entries are the absolute path of the src dirs.
	 * @param pom
	 * @return
	 * @throws Exception
	 */
	public Map readSourceDirectories(File pom) throws Exception {
		Map sourceDirectories = new HashMap();
		
		Build build = getBuild(pom);
		
		String aspectSourceDirectory = build.getAspectSourceDirectory();
		if ( !StringUtils.isNull(aspectSourceDirectory)) {
			sourceDirectories.put(
				ProjectConstants.MAVEN_ASPECT_DIRECTORY,
				aspectSourceDirectory
			);
		}
		
		String sourceDirectory = build.getSourceDirectory();
		if ( !StringUtils.isNull(sourceDirectory) ) {
			sourceDirectories.put(
				ProjectConstants.MAVEN_SRC_DIRECTORY,
				sourceDirectory
			);	
		}
		
		String unitTestSourceDirectory = build.getUnitTestSourceDirectory();
		if ( !StringUtils.isNull(unitTestSourceDirectory) ) {
			sourceDirectories.put(
				ProjectConstants.MAVEN_TEST_DIRECTORY,
				unitTestSourceDirectory
			);
		}
		
		return sourceDirectories;
	}

	/**
	 * return the dependency of the artifact describeed by referencedPom. No assumptions is done about its type. 
	 * 
	 * @param referencedPom
	 * @return
	 * @throws FileNotFoundException
	 * @throws Exception
	 * @throws IOException
	 */
	public Dependency extractDependency(File referencedPom) throws Exception {
		MavenProject referencedProject = read(referencedPom);
		Dependency dependency = new Dependency();
		dependency.setGroupId(referencedProject.getModel().getGroupId());
		dependency.setArtifactId(referencedProject.getModel().getArtifactId());
		dependency.setVersion(referencedProject.getModel().getCurrentVersion());
		//dependency.setArtifact(referencedPom.getParent());
		//dependency.setJar(referencedPom.getParent());
		return dependency;
	}

	/**
	 * return all resources directories declared in pom. map keys can be one of :
	 * 
	 *   o ProjectConstants.MAVEN_RESOURCE
	 *   o ProjectConstants.MAVEN_TEST_RESOURCE
	 * 
	 * @param referencedPom
	 * @return
	 * @throws Exception
	 */
	public Map readAllResources(File pom) throws Exception {
		Map allResources = new HashMap();
		MavenProject project = read(pom);
		if ( project.getModel().getBuild() != null ) {
			List res = project.getModel().getBuild().getResources();
			for (int i = 0; i < res.size(); i++) {
                String directory = ((Resource) res.get(i)).getDirectory();
                if ( !allResources.containsValue(directory) ) {
                	allResources.put(ProjectConstants.MAVEN_RESOURCE, directory);
                }
            }
			if ( project.getModel().getBuild().getUnitTest() != null ) {
				List utRes = project.getModel().getBuild().getUnitTest().getResources();
				for (int i = 0; i < utRes.size(); i++) {
					String directory = ((Resource) utRes.get(i)).getDirectory();
					if ( !allResources.containsValue(directory) ) {
						allResources.put(ProjectConstants.MAVEN_TEST_RESOURCE, directory);
					}
				}
			}
		}
		return allResources;
	}
	
	private Build getBuild(File pom) throws Exception {
		Model model; 
		if ( pom != null ) {
			model = unmarshaller.read(new FileReader(pom));
		}		
		else {
			model = new Model();
		}
		
		Build build = model.getBuild();
		return build;
	}
}
