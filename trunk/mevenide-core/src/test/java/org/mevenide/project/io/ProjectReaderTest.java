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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mevenide.project.ProjectConstants;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ProjectReaderTest extends TestCase {
	
	private File pom ;
	private ProjectReader projectReader;
	
	protected void setUp() throws Exception {
		pom = new File(ProjectReaderTest.class.getResource("/project.xml").getFile());
		projectReader = ProjectReader.getReader();
	}

	protected void tearDown() throws Exception {
		pom = null;
		projectReader = null;
	}

	public void testGetSourceDirectories() throws Exception {
		Map sourceDirectories = projectReader.readSourceDirectories(pom);
		
//		Iterator it = sourceDirectories.keySet().iterator();
//		while (it.hasNext()) {
//			String sourceType = (String) it.next();
//			System.err.println(sourceType + " : " + sourceDirectories.get(sourceType));
//		}
		
		assertEquals(3, sourceDirectories.size());
		
		List expectedSources = new ArrayList();
		expectedSources.add("src/aspect");
		expectedSources.add("src/java");
		expectedSources.add("src/test/java");
		
		List expectedTypes = new ArrayList();
		expectedTypes.add(ProjectConstants.MAVEN_ASPECT_DIRECTORY);
		expectedTypes.add(ProjectConstants.MAVEN_SRC_DIRECTORY);
		expectedTypes.add(ProjectConstants.MAVEN_TEST_DIRECTORY);
		
		List resultSources = new ArrayList();
		List resultTypes = new ArrayList();
		
		Iterator iterator = sourceDirectories.keySet().iterator();
		while (iterator.hasNext()) {
			String sourceType = (String) iterator.next();
			resultTypes.add(sourceType);
			resultSources.add(sourceDirectories.get(sourceType));	
		}
		
//		assertEquals(expectedSources, resultSources);
//		assertEquals(expectedTypes, resultTypes);
		
		assertEquals("src/aspect", sourceDirectories.get(ProjectConstants.MAVEN_ASPECT_DIRECTORY));
		assertEquals("src/java", sourceDirectories.get(ProjectConstants.MAVEN_SRC_DIRECTORY));
		assertEquals("src/test/java", sourceDirectories.get(ProjectConstants.MAVEN_TEST_DIRECTORY));
		
	}	

}
