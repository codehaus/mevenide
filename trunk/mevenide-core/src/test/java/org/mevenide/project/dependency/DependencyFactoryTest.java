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

import org.apache.maven.project.Dependency;
import org.mevenide.AbstractMevenideTestCase;
import org.mevenide.Environment;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyFactoryTest extends AbstractMevenideTestCase{
	
	
	private File artefact;
	private DependencyFactory dependencyFactory;

	private File testTypeDirectory;

    protected void setUp() throws Exception {
    	super.setUp();
        String mavenRepo = Environment.getMavenRepository();
		File testArtifactDirectory = new File(mavenRepo, "mevenide"); 
		testTypeDirectory = new File(testArtifactDirectory, "txts");
		testTypeDirectory.mkdirs();
		dependencyFactory = DependencyFactory.getFactory();
    }


    protected void tearDown() throws Exception {
    	super.tearDown();
    	dependencyFactory = null;
    }
	
	public void testGetDependency() throws Exception {
		
		
		artefact = new File(testTypeDirectory, "foo+joe-test2.-bar-1.0.7-dev.txt");
		Dependency dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("mevenide", dep.getGroupId());
		
		Environment.setMavenHome(System.getProperty("user.home"));
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		
		assertEquals("mevenide", dep.getGroupId());
		assertEquals("1.0.7-dev", dep.getVersion());
		assertEquals("foo+joe-test2.-bar", dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "foo+joe-test2.-bar-1.0.7-beta1.txt");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("1.0.7-beta1", dep.getVersion());
		
		artefact = new File(testTypeDirectory, "junit-3.8.1.jar");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("3.8.1", dep.getVersion());
		assertEquals("junit", dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "foo+joe-test2.-bar-1.0.7-beta-1.txt");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		//BUG-DependencySplitter_split-DEP_PATTERN $DEP-1
		assertEquals("1.0.7-beta-1", dep.getVersion());
		assertEquals("foo+joe-test2.-bar", dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "junit-1.0.rc3.pyo");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("1.0.rc3", dep.getVersion());
		assertEquals("junit", dep.getArtifactId());
		
		artefact = new File("c:/jdk1.4.1/jre/lib/rt.jar");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		//BUG-DependencyResolver_getDependency-NOT_RECOGNIZED_PATTERN $DEP-2
		assertNull(dep.getVersion());	
		assertNull(dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "ojb-1.0.rc3.pyo");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("1.0.rc3", dep.getVersion());
		assertEquals("ojb", dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "ojb-1.0.rc3-SNAPSHOT.pyo");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("1.0.rc3-SNAPSHOT", dep.getVersion());
		assertEquals("ojb", dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "ojb-SNAPSHOT.pyo");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("SNAPSHOT", dep.getVersion());
		assertEquals("ojb", dep.getArtifactId());
		
		artefact = new File(testTypeDirectory, "testo-0.0.1.plouf");
		dep = dependencyFactory.getDependency(artefact.getAbsolutePath());
		assertEquals("0.0.1", dep.getVersion());
		assertEquals("testo", dep.getArtifactId());
		
		
		File httpClientRepo = new File(Environment.getMavenRepository(), "commons-httpclient");
		File jarDir = new File(httpClientRepo, "jars");
		jarDir.mkdirs();
		dep = dependencyFactory.getDependency(new File(jarDir, "commons-httpclient-2.0alpha1-20020829.jar").getAbsolutePath());
		assertEquals("2.0alpha1-20020829", dep.getVersion());
		assertEquals("commons-httpclient", dep.getArtifactId());
		assertEquals("commons-httpclient", dep.getGroupId());
		
		dep = dependencyFactory.getDependency("/home/my-fake-0.1.zip");
		assertEquals("0.1", dep.getVersion());
		assertEquals("my-fake", dep.getArtifactId());
		assertEquals("", dep.getGroupId());
	}

	
}
