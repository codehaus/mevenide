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
package org.mevenide.reports;

import java.io.File;
import java.util.Arrays;
import org.mevenide.goals.AbstractTestCase;
import org.mevenide.goals.test.util.TestUtils;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class AbstractReportsFinderTest extends AbstractTestCase {
	
    private IReportsFinder finder;
    
	private String[] testPluginDirs = 
		{
			"maven-faq-plugin-1.1-SNAPSHOT", //nota - faq-plugin has been modified in src/test/conf so that doc:registerReport gets called twice
			"maven-findbugs-plugin-1.0-SNAPSHOT",
			"maven-tasklist-plugin-2.2-SNAPSHOT",
			"maven-test-plugin-1.4-SNAPSHOT",
			"maven-toto-plugin-1.1"
		};
		
    protected void setUp() throws Exception {
    	super.setUp();
    	finder = getReportsFinder();
    	for (int i = 0; i < testPluginDirs.length; i++) {
    		File src = new File(AbstractReportsFinderTest.class.getResource("/plugins/" + testPluginDirs[i] + "/plugin.jelly").getFile());
			File pluginDir = new File(pluginsLocal, testPluginDirs[i]);
			if ( !pluginDir.exists() ) {
				pluginDir.mkdirs();
			}
			File destJellyFile = new File(pluginDir, "plugin.jelly");
			TestUtils.copy(src.getAbsolutePath(), destJellyFile.getAbsolutePath());            
        }
    	
    }

    protected abstract IReportsFinder getReportsFinder();

    protected void tearDown() throws Exception {
    	super.tearDown();
    }

    public void testFindReports() throws Exception {
    	String[] reports = finder.findReports();
    	assertEquals(4, reports.length);
    	assertTrue(Arrays.asList(reports).contains("maven-faq-plugin"));
    	assertTrue(Arrays.asList(reports).contains("maven-tutu-plugin"));
		assertTrue(Arrays.asList(reports).contains("maven-findbugs-plugin"));
		assertTrue(Arrays.asList(reports).contains("maven-tasklist-plugin"));
    }
}