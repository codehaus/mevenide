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
package org.mevenide.environment;

import org.mevenide.AbstractMevenideTestCase;


/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: PropertiesLocationFinderTest.java,v 1.1 16 nov. 2003 Exp gdodinet 
 * 
 */
public abstract class AbstractPropertiesLocationFinderTest extends AbstractMevenideTestCase {

    private PropertiesLocationFinder finder;
    
    protected abstract PropertiesLocationFinder getPropertiesLocationFinder() throws Exception ; 
    
    protected String getSerializedProperties() {
        return  "java.home=java_home\n" +
        		"maven.home=maven_home\n" +
        		"maven.home.local=maven_local_home\n" +
        		"maven.repo.local=maven_repo_local\n" +
        		"maven.plugins.dir=maven_plugins_dir" ;
        
    }
    
    protected void setUp() throws Exception {
        finder = getPropertiesLocationFinder();
    }

    protected void tearDown() throws Exception {
        finder = null;
    }

    public void testGetJavaHome() {
        assertEquals("java_home", finder.getJavaHome());
    }

    public void testGetMavenHome() {
		assertEquals("maven_home", finder.getMavenHome());
    }

    public void testGetMavenLocalHome() {
		assertEquals("maven_local_home", finder.getMavenLocalHome());
    }

    public void testGetMavenLocalRepository() {
		assertEquals("maven_repo_local", finder.getMavenLocalRepository());
    }

    public void testGetMavenPluginsDir() {
		assertEquals("maven_plugins_dir", finder.getMavenPluginsDir());
    }

}
