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
package org.mevenide.project.source;

import org.apache.maven.model.Build;
import org.apache.maven.model.UnitTest;
import org.apache.maven.project.MavenProject;
import org.mevenide.project.ProjectConstants;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public final class SourceDirectoryUtil {
	
	private SourceDirectoryUtil() {
	}
	
	public static void addSource(MavenProject project, String path, String sourceType) {
		if ( project.getModel().getBuild() == null ) {
			project.getModel().setBuild(new Build());
		}
		
		if ( ProjectConstants.MAVEN_ASPECT_DIRECTORY.equals(sourceType) ) {
			project.getModel().getBuild().setAspectSourceDirectory(path);
		}
		if ( ProjectConstants.MAVEN_SRC_DIRECTORY.equals(sourceType) ) {
			project.getModel().getBuild().setSourceDirectory(path);
		}
		if ( ProjectConstants.MAVEN_TEST_DIRECTORY.equals(sourceType) ) {
			project.getModel().getBuild().setUnitTestSourceDirectory(path);
			UnitTest unitTest = project.getModel().getBuild().getUnitTest();
			if ( unitTest == null || unitTest.getIncludes() == null || unitTest.getIncludes().size() == 0) {
				unitTest = new UnitTest();
				unitTest.addInclude("**/*Test.java");
				project.getModel().getBuild().setUnitTest(unitTest);
			}
		}
	}
	
	public static boolean isSourceDirectoryPresent(MavenProject project, String path) {
		if ( project.getModel().getBuild() == null || path == null ) {
			return false;
		}
			
		String srcDirectory = project.getModel().getBuild().getSourceDirectory();
		String aspectSrcDirectory = project.getModel().getBuild().getAspectSourceDirectory();
		String unitTestSourceDirectory = project.getModel().getBuild().getUnitTestSourceDirectory();
			
		return path.equals(srcDirectory)
			   || path.equals(aspectSrcDirectory)
		       || path.equals(unitTestSourceDirectory);
		
	}
	
	public static String stripBasedir(String strg) {
		if ( "${basedir}".equals(strg) || "${basedir}/".equals(strg) || "${basedir}\\".equals(strg) ) {
			return strg;
		}
		String result = stripHeadingString(strg, "${basedir}/");
		result = stripHeadingString(result, "${basedir}\\");
		result = stripHeadingString(result, "${basedir}");
		return result;
	}

	private static String stripHeadingString(String strg, String headingString) {
		if ( strg.startsWith(headingString) ) {
			strg = strg.substring(headingString.length());
	    }
		return strg;
	}
	
}
