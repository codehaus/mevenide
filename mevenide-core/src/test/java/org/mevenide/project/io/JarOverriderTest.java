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
package org.mevenide.project.io;

import java.io.File;

import org.apache.maven.project.Project;
import org.mevenide.AbstractMevenideTestCase;


/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: JarOverriderTest.java,v 1.1 24 sept. 2003 Exp gdodinet 
 * 
 */
public class JarOverriderTest  extends AbstractMevenideTestCase {
	private JarOverrider overrider ;
	private ProjectWriter pomWriter;
	
    protected void setUp() throws Exception {
        super.setUp();
		pomWriter = ProjectWriter.getWriter();
        overrider = new JarOverrider(pomWriter);
    }
	
	protected void tearDown() throws Exception {
		super.tearDown();
		pomWriter = null;
		overrider = null;
    }
	
	public void testJarOverride() throws Exception {
		File propFile = new File(projectFile.getParent(), "project.properties");
	
		Project project = ProjectReader.getReader().read(projectFile);
		int prev = project.getDependencies().size();
	
		String path = "C:\\temp\\bleah\\fake.jar";
		String path2 = "C:\\temp\\bleah\\fake2.jar";
		String path3 = "C:\\temp space temp\\bleah\\fake fake2.jar";
	
		overrider.jarOverride(path, propFile, projectFile);
		overrider.jarOverride(path2, propFile, projectFile);
		overrider.jarOverride(path3, propFile, projectFile);
		overrider.jarOverride(path2, propFile, projectFile);
	
		project = ProjectReader.getReader().read(projectFile);
	
		assertEquals(prev + 3, project.getDependencies().size());
	
	}
}
