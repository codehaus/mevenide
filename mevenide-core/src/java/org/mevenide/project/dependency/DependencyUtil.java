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
package org.mevenide.project.dependency;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.Environment;
import org.mevenide.util.MevenideUtil;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DependencyUtil {
	private static Log log = LogFactory.getLog(DependencyUtil.class);
	
	private DependencyUtil() {}
	
	public static boolean areEquals(Dependency d1, Dependency d2) {
		return  (d1 == null && d2 == null) ||
				(   
				    d1 != null && d2 != null
				    && areEquals(d1.getArtifactId(), d2.getArtifactId())
					//&& areEquals(d1.getGroupId(), d2.getGroupId())
					&& areEquals(d1.getVersion(), d2.getVersion())
				);
	}
	
	private static boolean areEquals(String s1, String s2) {
		return  (s1 == null && s2 == null) ||
				(
				    s1 != null && s1.equals(s2)
				);
	}
	
	public static boolean isValidGroupId(final String groupId) {
		File repo = new File(Environment.getMavenRepository());
		File[] children = repo.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory() && pathname.getName().equals(groupId);
			}
		});
		return children != null && children.length != 0;
	}
	
	public static boolean isValid(Dependency d) {
		if ( d == null ) { 
			log.debug("null dependency found");
			return false;
		} 
		boolean valid = !MevenideUtil.isNull(d.getGroupId()) && !MevenideUtil.isNull(d.getArtifactId()) && !MevenideUtil.isNull(d.getVersion());
		log.debug("Dependency " + d.getArtifact() + " valid=" + valid + toString(d));
		return  valid;
	}

	public static String toString(Dependency d) {
		return " <groupId=" + d.getGroupId() + ">, <artifactId=" + d.getArtifactId() + ">, <version=" + d.getVersion() + ">" ;
	}

	/**
	 * modifies the list parameter passed, removing all non resolved dependencies
	 * 
	 * @param dependencies
	 * @return list of non resolved dependencies  
	 */
	public static List getNonResolvedDependencies(List dependencies) {
		List temp = new ArrayList(dependencies);
		List nonResolvedDependencies = new ArrayList();
		for (int i = 0; i < temp.size(); i++) {
			Dependency dependency = (Dependency)temp.get(i); 
			if ( !DependencyUtil.isValid(dependency) ) {
				dependencies.remove(dependency);
				nonResolvedDependencies.add(dependency);
			}
			
		} 
		return nonResolvedDependencies;
	}

	/**
	 * checks if a Dependency identified by its artifact path is present in the POM.
	 * 
	 * testing artifact doesnt seem to be a good solution since it is often omitted
	 * we rather have to test artifactId and version. 
	 * 
	 * i dont rely either on groupId cuz i dont think it is not relevant. 
	 * 
	 * @param project
	 * @param absoluteFileName
	 * @return
	 */
	public static boolean isDependencyPresent(Project project, Dependency dependency) {
		log.debug("searched dependency : " + DependencyUtil.toString(dependency));
		List dependencies = project.getDependencies();
		if ( dependencies == null ) {
			return false;
		}
		for (int i = 0; i < dependencies.size(); i++) {
			Dependency declaredDependency = (Dependency) dependencies.get(i);
	
			String version = declaredDependency.getVersion(); 
			String artifactId = declaredDependency.getArtifactId();
			
			log.debug("found dependency : " + DependencyUtil.toString(declaredDependency));
			
			if (  artifactId != null && artifactId.equals(dependency.getArtifactId()) 
				  && version != null && version.equals(dependency.getVersion())) {
				return true;
			}
		}
		return false;
	}
	
	public static void refreshGroupId(Dependency dependency) {
		try {
			if ( dependency.getGroupId().equals("") ) {
				dependency.setGroupId(
						AbstractDependencyResolver.newInstance(dependency.getArtifact()).guessGroupId());
			}
		}
		catch ( Exception ex ) {
			log.debug("Still unable to resolve groupId due to : " + ex);
		}	
	}
}
