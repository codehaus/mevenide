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
package org.mevenide.environment;

import org.mevenide.environment.sysenv.SysEnvProvider;


/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: SysEnvLocationFinderTest.java,v 1.1 15 nov. 2003 Exp gdodinet 
 * 
 */
public class SysEnvLocationFinderTest extends AbstractLocationFinderTest {
    
    private SysEnvLocationFinder finder;
    private String mavenHome = "testMavenHome";
    private String javaHome = "testJavaHome";
    private static final String MAVEN_HOME = "MAVEN_HOME";
    private static final String JAVA_HOME = "JAVA_HOME";
    
    protected void setUp() throws Exception {
        SysEnvLocationFinder.setDefaultSysEnvProvider(new TestProvider());
        finder = SysEnvLocationFinder.getInstance();
    }
    
    
    
    protected void tearDown() throws Exception {
        finder = null;
        javaHome = null;
        mavenHome = null;
    }
    
    public void testGetJavaHome() {
        assertEquals(javaHome, finder.getJavaHome());
    }

    public void testGetMavenHome() {
		assertEquals(mavenHome, finder.getMavenHome());
    }

    private class TestProvider implements SysEnvProvider {
        public String getProperty(String name) {
            if (MAVEN_HOME.equals(name)) {
                return mavenHome;
            }
            if (JAVA_HOME.equals(name)) {
                return javaHome;
            }
            return null;
        }
    }
}
