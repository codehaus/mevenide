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
package org.mevenide.goals;

import java.io.File;

import org.mevenide.goals.test.util.TestUtils;

import junit.framework.TestCase;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.environment.LocationFinderAggregator;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: AbstractTestCase.java,v 1.1 21 sept. 2003 Exp gdodinet 
 * 
 */
public class AbstractTestCase extends TestCase {
    protected File pluginsLocal;
    protected File userHomeDir;
    
    protected TestQueryContext context;
    
    protected void setUp() throws Exception {
        String userHome = System.getProperty("user.home"); //NOI18N
        userHomeDir  = new File(userHome, ".mevenide_test");
        if (!userHomeDir.exists()) {
            userHomeDir.mkdir();
        }
        context = new TestQueryContext();
        context.setUserDirectory(userHomeDir);
        context.addUserPropertyValue("maven.repo.remote", "http://mevenide.codehaus.org");
        context.addUserPropertyValue("maven.plugin.unpacked.dir", "${user.home}/plugins");
        pluginsLocal = new File(userHomeDir, "plugins");
        if (!pluginsLocal.exists()) {
            pluginsLocal.mkdir();
        }
    }
    
    protected void tearDown() throws Exception {
        TestUtils.delete(userHomeDir);
    }
    
}
