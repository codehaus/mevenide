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
package org.codehaus.mevenide.pde.core.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.codehaus.mevenide.pde.archive.SimpleZipCreatorTest;
import org.codehaus.mevenide.pde.classpath.PluginClasspathResolverTest;
import org.codehaus.mevenide.pde.license.LicenseReaderTest;
import org.codehaus.mevenide.pde.verifier.CompatibilityCheckerTest;
import org.codehaus.mevenide.pde.version.VersionAdapterTest;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class AllTests {
    public static Test suite() {
        TestSuite suite = new TestSuite();
        
        suite.addTestSuite(VersionAdapterTest.class);
        suite.addTestSuite(LicenseReaderTest.class);
        suite.addTestSuite(PluginClasspathResolverTest.class);
        suite.addTestSuite(CompatibilityCheckerTest.class);
        suite.addTestSuite(PluginClasspathResolverTest.class);
        suite.addTestSuite(SimpleZipCreatorTest.class);
        //commented until i find a way to correctly build pom
        //suite.addTestSuite(CommonPluginValuesReplacerTest.class);
        
        return suite;
    }
}
