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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.project.dependency.DependencyUtil;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: Overrider.java,v 1.1 24 sept. 2003 Exp gdodinet 
 * 
 */
class JarOverrider {
	private static Log log = LogFactory.getLog(JarOverrider.class);
	
	private ProjectWriter writer;
	
	JarOverrider(ProjectWriter writer) {
		this.writer = writer;
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
		
		writer.write(project, pom);
	
	}

	private void addPropertiesOverride(String path, File propertiesFile, Dependency dep) throws Exception {
		Properties properties = new Properties();
		properties.load(new FileInputStream(propertiesFile));
	
	
		properties.setProperty("maven.jar.override", "on");
	
		properties.setProperty("maven.jar." + dep.getArtifactId(), path);
	
		properties.store(new FileOutputStream(propertiesFile), null);
	} 

	void unsetOverriding(File propertiesFile) throws Exception {
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

}
