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
