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
package org.mevenide.reports;

import java.io.File;
import java.util.Arrays;

import org.mevenide.goals.AbstractTestCase;
import org.mevenide.goals.test.util.TestUtils;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: DefaultReportsFinderTest.java,v 1.1 21 sept. 2003 Exp gdodinet 
 * 
 */
public class DefaultReportsFinderTest extends AbstractTestCase {
	private File mavenLocalHome;
	
	private String[] testPluginDirs = 
		{
			"maven-faq-plugin-1.1-SNAPSHOT", //nota - faq-plugin has been modified in src/test/conf so that doc:registerReport gets called twice
			"maven-findbugs-plugin-1.0-SNAPSHOT",
			"maven-tasklist-plugin-2.2-SNAPSHOT",
			"maven-test-plugin-1.4-SNAPSHOT",
		};
		
    protected void setUp() throws Exception {
    	super.setUp();
    	for (int i = 0; i < testPluginDirs.length; i++) {
    		File src = new File(DefaultReportsFinderTest.class.getResource("/plugins/" + testPluginDirs[i] + "/plugin.jelly").getFile());
			File pluginDir = new File(pluginsLocal, testPluginDirs[i]);
			if ( !pluginDir.exists() ) {
				pluginDir.mkdirs();
			}
			File destJellyFile = new File(pluginDir, "plugin.jelly");
			TestUtils.copy(src.getAbsolutePath(), destJellyFile.getAbsolutePath());            
        }
    	
    }

    protected void tearDown() throws Exception {
    	super.tearDown();
    }

    public void testFindReports() throws Exception {
    	String[] reports = new DefaultReportsFinder().findReports();
    	assertEquals(3, reports.length);
    	assertTrue(Arrays.asList(reports).contains("maven-faq-plugin"));
		assertTrue(Arrays.asList(reports).contains("maven-findbugs-plugin"));
		assertTrue(Arrays.asList(reports).contains("maven-tasklist-plugin"));
    }

}
