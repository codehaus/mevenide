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
package org.mevenide.goals;

import java.io.File;

import org.mevenide.Environment;
import org.mevenide.goals.test.util.TestUtils;

import junit.framework.TestCase;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: AbstractTestCase.java,v 1.1 21 sept. 2003 Exp gdodinet 
 * 
 */
public class AbstractTestCase extends TestCase {
	protected File mavenHomeLocal;
	protected File pluginsLocal;
	
	protected void setUp() throws Exception {
		mavenHomeLocal = new File(System.getProperty("user.home"), ".mevenide");
		if (!mavenHomeLocal.exists()) {
			mavenHomeLocal.mkdirs();
		}
		Environment.setMavenHome(mavenHomeLocal.getAbsolutePath());
		
		pluginsLocal = new File(mavenHomeLocal, "plugins");
		Environment.setMavenPluginsInstallDir(pluginsLocal.getAbsolutePath());
		
		if (!pluginsLocal.exists()) {
			pluginsLocal.mkdir();
		}
    }
    
    protected void tearDown() throws Exception {
		TestUtils.delete(mavenHomeLocal);
    }
}
