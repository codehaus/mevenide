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
package org.mevenide.runner;

import junit.framework.TestCase;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: OptionsRegistryTest.java 8 mai 2003 15:58:1313:34:35 Exp gdodinet 
 * 
 */
public class OptionsRegistryTest extends TestCase {

	private OptionsRegistry optionsRegistry ;
	
	protected void setUp() throws Exception {
		optionsRegistry = OptionsRegistry.getRegistry();
    }

	public void testGetOptionDescription() throws Exception {
	   assertEquals("Define a system property", optionsRegistry.getDescription('D'));
       assertEquals("Produce logging information without adornments", optionsRegistry.getDescription('E'));
       assertEquals("Produce execution debug output", optionsRegistry.getDescription('X'));
       assertEquals("Produce exception stack traces", optionsRegistry.getDescription('e'));
       assertEquals("Build is happening offline", optionsRegistry.getDescription('o'));
       try {
	       optionsRegistry.getDescription('Z');
           fail("Excepted InvalidOptionException");
       } 
       catch (InvalidOptionException e) { }
    }
}
