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
package org.codehaus.mevenide.pde.classpath;

import java.io.File;
import java.util.Collection;
import junit.framework.TestCase;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PluginClasspathResolverTest extends TestCase {
    
    private PluginClasspathResolver resolver;
    
    private File eclipseHome;
    
    private File basedir;
    
    protected void setUp() throws Exception {
        super.setUp();
        basedir = new File(getClass().getResource("/basedir.usecontainer").getFile());
        eclipseHome = new File(getClass().getResource("/eclipse.home").getFile());
        resolver = new PluginClasspathResolver(basedir, eclipseHome.getAbsolutePath());
    }

    
    protected void tearDown() throws Exception {
        super.tearDown();
        resolver = null;
    }
    
    public void testCheckEclipseDependenciesContainer() throws Exception {
        assertTrue(resolver.checkEclipseDependenciesContainer());
        
        basedir = new File(getClass().getResource("/basedir.nocontainer").getFile());
        resolver = new PluginClasspathResolver(basedir, eclipseHome.getAbsolutePath());
        assertFalse(resolver.checkEclipseDependenciesContainer());
        assertEquals(1, resolver.getInfos().size());
        
        basedir = new File(System.getProperty("user.dir"));
        resolver = new PluginClasspathResolver(basedir, eclipseHome.getAbsolutePath());
        assertFalse(resolver.checkEclipseDependenciesContainer());
        assertEquals(1, resolver.getInfos().size());
    }
    
    public void testExtractDependenciesFromDescriptor() throws Exception {
        
        Collection dependencies = resolver.extractDependenciesFromDescriptor(basedir.getAbsolutePath());
        
        assertEquals(3, dependencies.size());
        
        File dependency1 = new File(eclipseHome, "plugins" 
									                + File.separatorChar 
									                + "org.eclipse.text_3.1.0" 
									                + File.separatorChar
									                + "lib_2.jar");
        File dependency2 = new File(eclipseHome, "plugins" 
	                                                + File.separatorChar 
	                                                + "org.eclipse.text_3.1.0" 
	                                                + File.separatorChar
	                                                + "runtime"
	                                                + File.separatorChar
	                                                + "test_.jar");
        File dependency3 = new File(eclipseHome, "plugins" 
									                + File.separatorChar 
									                + "org.eclipse.core.runtime_3.1.0" 
									                + File.separatorChar
									                + "lib_1.jar");
        
        assertTrue(dependencies.contains(dependency1.getAbsolutePath()));
        assertTrue(dependencies.contains(dependency2.getAbsolutePath()));
        assertTrue(dependencies.contains(dependency3.getAbsolutePath()));
    }
}
