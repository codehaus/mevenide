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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.properties.KeyValuePair;
import org.mevenide.properties.PropertyModel;
import org.mevenide.properties.PropertyModelFactory;
import org.mevenide.util.StringUtils;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: JarOverrideReader.java,v 1.1 13 mars 2004 Exp gdodinet 
 * 
 */
public class JarOverrideReader {
	private static Log log = LogFactory.getLog(JarOverrideWriter.class);
	
	private PropertyModelFactory projectModelFactory;
	private PropertyModel userPropertyModel;
	
	
	JarOverrideReader() throws Exception {
		projectModelFactory = PropertyModelFactory.getFactory();
		userPropertyModel = projectModelFactory.newPropertyModel(new File(System.getProperty("user.home"), "build.properties"));
	}
	
	void processOverride(File pom, Project project) throws Exception {
		boolean isJarOverrideOn = isJarOverrideOn(pom);
		
		log.debug("jar override " + (isJarOverrideOn ? " on " : " off"));
		
		if ( isJarOverrideOn ) {	
			for (int i = 0; i < project.getDependencies().size(); i++) {
				Dependency dependency = (Dependency) project.getDependencies().get(i);
				String dependencyOverrideValue = getOverrideValue(pom, dependency);
				
				log.debug("overriding jar for dep : " + dependency.getId() + " : " + dependencyOverrideValue);
				
				if ( !StringUtils.isNull(dependencyOverrideValue) ) {
					dependency.setJar(dependencyOverrideValue);
				}
			}
		}
	}

	String getOverrideValue(File pom, Dependency dependency) throws Exception {
		PropertyModel projectProperties = projectModelFactory.newPropertyModel(new File(pom.getParent(), "project.properties"), true);
		PropertyModel buildProperties = projectModelFactory.newPropertyModel(new File(pom.getParent(), "project.properties"), true);
		
		String artifactId = dependency.getArtifactId();

		String dependencyOverrideValue = getDependencyOverrideValue(artifactId, projectProperties, buildProperties);
		
		if ( !StringUtils.isNull(dependencyOverrideValue) ) {
			String versionOverrideValue = getVersionOverrideValue(pom, dependency, dependencyOverrideValue);
			if ( versionOverrideValue != null ) dependencyOverrideValue = versionOverrideValue;  
		}
		return dependencyOverrideValue;
	}

	String getVersionOverrideValue(File pom, Dependency dependency, String dependencyOverrideValue) {
		
		if ( Character.isDigit(dependencyOverrideValue.charAt(0)) ) {
			
			LocationFinderAggregator locationFinder = new LocationFinderAggregator();
			locationFinder.setEffectiveWorkingDirectory(pom.getParent());
			
			File artifactGroupPath = new File(locationFinder.getMavenLocalRepository(), dependency.getGroupId());
			
			//only jars are added to .classpath
			File artifactTypePath = new File(artifactGroupPath, "jars");
			File artifactPath = new File(artifactTypePath, dependency.getArtifactId() + "-" + dependencyOverrideValue + ".jar");
			
			return artifactPath.getAbsolutePath();
		}
		
		return null;
	}

	String getDependencyOverrideValue(String artifactId, PropertyModel projectProperties, PropertyModel buildProperties) {
		String key = "maven.jar." + artifactId;
		
		KeyValuePair jarKvp = userPropertyModel.findByKey(key);
		if ( jarKvp != null && !StringUtils.isNull(jarKvp.getValue()) ) {
			return jarKvp.getValue();
		}
		
		jarKvp = buildProperties.findByKey(key);
		if ( jarKvp != null && !StringUtils.isNull(jarKvp.getValue()) ) {
			return jarKvp.getValue();
		}
		
		jarKvp = projectProperties.findByKey(key);
		if ( jarKvp != null && !StringUtils.isNull(jarKvp.getValue()) ) {
			return jarKvp.getValue();
		}
		
		return null;
	}
	
	
	boolean isJarOverrideOn(File pom) throws Exception {
		PropertyModel projectPropertyModel = projectModelFactory.newPropertyModel(new File(pom.getParent(), "project.properties"), true);
		PropertyModel buildPropertyModel = projectModelFactory.newPropertyModel(new File(pom.getParent(), "project.properties"), true);
		
		String overrideProperty = "maven.jar.override";
		KeyValuePair jarOverrideKvp = userPropertyModel.findByKey(overrideProperty);
		
		if( isJarOverrideOn(jarOverrideKvp) ) {
			return true;
		}
		
		jarOverrideKvp = buildPropertyModel.findByKey(overrideProperty);
		if( isJarOverrideOn(jarOverrideKvp) ) {
			return true;
		}
		
		jarOverrideKvp = projectPropertyModel.findByKey(overrideProperty);
		return isJarOverrideOn(jarOverrideKvp);
	}
	
	private boolean isJarOverrideOn(KeyValuePair jarOverrideKvp) {
		if ( jarOverrideKvp != null 
				&& ( "on".equals(jarOverrideKvp.getValue()) 
					 ||	"1".equals(jarOverrideKvp.getValue()) 
					 ||	"true".equals(jarOverrideKvp.getValue())		
					)
			) {
			return true;
		}
		return false;
	}
}
