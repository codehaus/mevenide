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
package org.mevenide.util;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: DefaultProjectUnmarshallerTest.java 8 mai 2003 15:32:4913:34:35 Exp gdodinet 
 * 
 */
public class DefaultProjectUnmarshallerTest extends TestCase {

	private DefaultProjectUnmarshaller unmarshaller;
	private FileReader reader;
	
	protected void setUp() throws Exception {
		File pom = new File(DefaultProjectUnmarshallerTest.class.getResource("/project.xml").getFile());
		reader = new FileReader(pom);
		unmarshaller = new DefaultProjectUnmarshaller(); 
	}

	
	protected void tearDown() throws Exception {
		unmarshaller = null;
	}
	
	public void testUnmarshallProperties() throws Exception {
		Project project = unmarshaller.parse(reader);
		List deps = project.getDependencies();
		for (int i = 0; i < deps.size(); i++) {
			Dependency d = (Dependency) deps.get(i);
			if ( "maven".equals(d.getGroupId()) && "maven".equals(d.getArtifactId() ) ) {
				assertEquals(2, d.getProperties().size());
				assertEquals("true", d.getProperty("test.prop"));
				assertEquals("it worked", d.getProperty("anotherProp"));
			}
		}	
	}
	
}
