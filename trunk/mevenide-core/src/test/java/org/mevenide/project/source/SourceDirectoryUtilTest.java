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
package org.mevenide.project.source;

import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.mevenide.AbstractMevenideTestCase;
import org.mevenide.ProjectConstants;
//causes a cycle
import org.mevenide.project.io.ProjectReader;


/**
 * 
 * This test introduces a cycle in package dependencies -__-; see imports
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SourceDirectoryUtilTest extends AbstractMevenideTestCase {

	
	private ProjectReader reader; 
	protected Project project; 
	
	protected void setUp() throws Exception {
		super.setUp();
		reader  = ProjectReader.getReader();
		project = reader.read(projectFile);	
	}

	public void testAddSource() {
		SourceDirectoryUtil.addSource(project, "src/pyo/javaa", ProjectConstants.MAVEN_SRC_DIRECTORY);
		SourceDirectoryUtil.addSource(project, "src/pyo/teest/javaa", ProjectConstants.MAVEN_TEST_DIRECTORY);
		SourceDirectoryUtil.addSource(project, "src/pyo/aspeect", ProjectConstants.MAVEN_ASPECT_DIRECTORY);
		SourceDirectoryUtil.addSource(project, "src/pyo/iut/javaa", ProjectConstants.MAVEN_INTEGRATION_TEST_DIRECTORY);
		
		assertEquals("src/pyo/javaa", project.getBuild().getSourceDirectory());
		assertEquals("src/pyo/teest/javaa", project.getBuild().getUnitTestSourceDirectory());
		assertEquals("src/pyo/aspeect", project.getBuild().getAspectSourceDirectory());
		assertEquals("src/pyo/iut/javaa", project.getBuild().getIntegrationUnitTestSourceDirectory());
	}
	
	public void testIsDirectoryPresent() {
		project.setBuild(null);
		assertFalse(SourceDirectoryUtil.isSourceDirectoryPresent(project, "bleah"));
		project.setBuild(new Build());
		assertFalse(SourceDirectoryUtil.isSourceDirectoryPresent(project, "bleah"));
		
		SourceDirectoryUtil.addSource(project, "src/pyo/javaa", ProjectConstants.MAVEN_SRC_DIRECTORY);
		assertTrue(SourceDirectoryUtil.isSourceDirectoryPresent(project, "src/pyo/javaa"));
		
		SourceDirectoryUtil.addSource(project, "src/pyo/teest/javaa", ProjectConstants.MAVEN_TEST_DIRECTORY);
		assertTrue(SourceDirectoryUtil.isSourceDirectoryPresent(project, "src/pyo/teest/javaa"));
		
		SourceDirectoryUtil.addSource(project, "src/pyo/aspeect", ProjectConstants.MAVEN_ASPECT_DIRECTORY);
		assertTrue(SourceDirectoryUtil.isSourceDirectoryPresent(project, "src/pyo/aspeect"));
		
		SourceDirectoryUtil.addSource(project, "src/pyo/iut", ProjectConstants.MAVEN_INTEGRATION_TEST_DIRECTORY);
		assertTrue(SourceDirectoryUtil.isSourceDirectoryPresent(project, "src/pyo/iut"));
	}

}
