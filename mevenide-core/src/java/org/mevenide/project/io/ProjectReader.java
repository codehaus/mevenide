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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Build;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.mevenide.project.ProjectConstants;
import org.mevenide.util.DefaultProjectUnmarshaller;
import org.mevenide.util.StringUtils;


/**
 * 
 *
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class ProjectReader {
	private static final Log log = LogFactory.getLog(ProjectReader.class);
	
	private DefaultProjectUnmarshaller unmarshaller ; 
	
	private static ProjectReader projectReader = null;

	
	private JarOverrideReader jarOverrideReader;
	
	
	public static synchronized ProjectReader getReader() throws Exception {
		if (projectReader == null) {
			projectReader = new ProjectReader();
		}
		return projectReader;
	}
	
	private ProjectReader() throws Exception {
		unmarshaller = new DefaultProjectUnmarshaller();
		jarOverrideReader = new JarOverrideReader();
	}
	
	/**
	 * return the instance of org.apache.maven.project.Project derivated from pom
	 * 
	 */
	public Project read(File pom) throws Exception {
		
		Reader reader = null;
		try {
			reader = new FileReader(pom);
			Project project = unmarshaller.parse(reader);
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
		
		String integrationUnitTestSourceDirectory = build.getIntegrationUnitTestSourceDirectory();
		if ( !StringUtils.isNull(integrationUnitTestSourceDirectory) ) {
			sourceDirectories.put(
				ProjectConstants.MAVEN_INTEGRATION_TEST_DIRECTORY,
				integrationUnitTestSourceDirectory
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
		Project referencedProject = read(referencedPom);
		Dependency dependency = new Dependency();
		dependency.setGroupId(referencedProject.getGroupId());
		dependency.setArtifactId(referencedProject.getArtifactId());
		dependency.setVersion(referencedProject.getCurrentVersion());
		//dependency.setArtifact(referencedPom.getParent());
		//dependency.setJar(referencedPom.getParent());
		return dependency;
	}

	/**
	 * return all resources directories declared in pom. keys are resource directories while map values can be one of :
	 * 
	 *   o ProjectConstants.MAVEN_RESOURCE
	 *   o ProjectConstants.MAVEN_TEST_RESOURCE
	 * 
	 * @param referencedPom
	 * @return build.resources and unitTest.build.resources 
	 * @throws Exception
	 */
	public Map readAllResources(File pom) throws Exception {
		Map allResources = new Hashtable();
		Project project = read(pom);
		if ( project.getBuild() != null ) {
		    List resources = project.getBuild().getResources();
			allResources.putAll(readResources(project, resources, ProjectConstants.MAVEN_RESOURCE));
			if ( project.getBuild().getUnitTest() != null ) {
			    List unitTestResources = project.getBuild().getUnitTest().getResources();
				allResources.putAll(readResources(project, unitTestResources, ProjectConstants.MAVEN_TEST_RESOURCE));
			}
		}
		return allResources;
	}
	
	Map readResources(Project project, List resources, String resourceType) {
	    Map resourceMap = new TreeMap();
        for (int i = 0; i < resources.size(); i++) {
            String directory = ((Resource) resources.get(i)).getDirectory();
            if ( !resourceMap.containsValue(directory) ) {
            	resourceMap.put(directory, resourceType);
            }
        }
        return resourceMap;
    }

    private Build getBuild(File pom) throws Exception {
		Project project; 
		if ( pom != null ) {
			project = new DefaultProjectUnmarshaller().parse(new FileReader(pom));
		}		
		else {
			project = new Project();
		}
		
		Build build = project.getBuild();
		
		if ( build == null ) {
		    build = new Build();
		}
		
		return build;
	}
}
