/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.project.dependency;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.properties.KeyValuePair;
import org.mevenide.properties.PropertyModel;
import org.mevenide.properties.PropertyModelFactory;

/**  
 * resolve <code>dependency</code> path. if declaringPom isNotNull, 
 * then we try to resolve jar override. there might be some error cases 
 * not managed yet..   
 * 
 * this is not the best way to resolve dependency but since artifacts list is not built 
 * i cannot think of any other way..
 * 
 * @todo provide factory and some kind of cache ?
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: DefaultDependencyFinder.java,v 1.1 28 mars 2004 Exp gdodinet 
 * 
 */
public class DefaultDependencyPathFinder implements IDependencyPathFinder {
	private static final Log log = LogFactory.getLog(DefaultDependencyPathFinder.class);
	
	private Dependency dependency;
	private File declaringPom;

	public DefaultDependencyPathFinder(Dependency d) {
		this(d, null);
	}
	
	public DefaultDependencyPathFinder(Dependency d, File pom) {
		this.dependency = d;
		this.declaringPom = pom;
	}
	
	public String resolve() {
		String path = null;
		
		if ( getDeclaringPom() != null && isMavenOverrideOn() ) {
			path = getPathOverride();
		}
		
		if ( path == null ) {
			path = buildArtifactPath(dependency.getVersion());
		}
		
		return path.replaceAll("\\\\", "/");
	}
	
	private String buildArtifactPath(String version) {
		String type = dependency.getType() == null ? "jar" : dependency.getType();
		File group = new File(ConfigUtils.getDefaultLocationFinder().getMavenLocalRepository(), dependency.getGroupId());
		return new File(group, type + "s/" + dependency.getArtifactId() + "-" + version + "." + type).getAbsolutePath();
	}
	
	private boolean isMavenOverrideOn() {
		PropertyModel model = getPropertyModel();
		if ( model == null ) {
			return false;
		}
		KeyValuePair pair = model.findByKey("maven.jar.override");
		return pair != null 
				&& pair.getValue() != null 
				&& (pair.getValue().equals("on") || pair.getValue().equals("1") || pair.getValue().equals("true")); 
	}
	
	private PropertyModel getPropertyModel() {
		PropertyModel model = null;
		
		File projectProperties = new File(getDeclaringPom().getParentFile(), "project.properties");
		
		if ( projectProperties.exists() ) {
		
			PropertyModelFactory factory = PropertyModelFactory.getFactory();
			
			try {
				model = factory.newPropertyModel(projectProperties);
			} 
			catch (IOException e) {
				log.error("Unable to mount project PropertyModel", e);
			}
		}
		
		return model;
	}

	private String getPathOverride() {
		String result = null;
		
		PropertyModel model = getPropertyModel();
		KeyValuePair kvp = model.findByKey("maven.jar." + dependency.getArtifactId());
		
		if ( kvp != null ) {
			String value = kvp.getValue();
			if ( new File(value).exists() ) {
			    result = value;
			}
			else {
			    //assume value is a version
			    String path = buildArtifactPath(value);
			    //System.err.println(path);
			    if ( new File(path).exists() ) {
			    	result = path;
			    }
			    else {
			    	//if no valid artifact file can be constructed then it is problematic.. 
			        //@TODO warn user about that problem..
			        result = value;
			    }
			}
		}
		return resolvePathOverride(result);
		
	}
	
	private String resolvePathOverride(String result) {
		if ( result != null && !new File(result).isAbsolute() ) {
			if ( result.startsWith("{basedir}") ) {
			    result = result.replaceAll("{basedir}", this.getDeclaringPom().getParent());
			}
			else {
			    result = new File(this.getDeclaringPom().getParent(), result).getAbsolutePath();
			}
		}
		return result;
	}
	
	private File getDeclaringPom() {
		return declaringPom;
	}
}
