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
package org.mevenide.project.dependency;

import java.io.File;

import org.mevenide.AbstractMevenideTestCase;
import org.mevenide.environment.ConfigUtils;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DependencyResolverTest extends AbstractMevenideTestCase {
	
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGuessExtension() throws Exception {
		IDependencyResolver resolver = DependencyResolverFactory.getFactory().newInstance("/home/bleah/bouh/foo+joe-test2.-bar-1.0.7-beta-1.txt");
		String ext = resolver.guessExtension();
		assertEquals("txt", ext);
		
		resolver = DependencyResolverFactory.getFactory().newInstance("/home/bleah/bouh/rt.jar");
		ext = resolver.guessExtension();
		assertEquals("jar", ext);
		
		//BUG-DefaultDependencyResolver_DEP_guessVersion $DEP-3 depends on $DEP-1
		//assertEquals("tar.gz", ext);
		
	}

	public void testGuess() throws Exception {
		File jarDir = new File(ConfigUtils.getDefaultLocationFinder().getMavenLocalRepository(), "commons-httpclient/jars");
		jarDir.mkdirs();
		File jar = new File(jarDir, "commons-httpclient-2.0alpha1-20020829.jar");
		jar.createNewFile();
		IDependencyResolver resolver = DependencyResolverFactory.getFactory().newInstance(jar.getAbsolutePath());
		assertEquals("2.0alpha1-20020829", resolver.guessVersion());
		assertEquals("commons-httpclient", resolver.guessArtifactId());
		assertEquals("commons-httpclient", resolver.guessGroupId());
		
	}
	
	
	
}
