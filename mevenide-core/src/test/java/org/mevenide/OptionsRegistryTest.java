/* 
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 */
package org.mevenide;

import junit.framework.TestCase;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: OptionsRegistryTest.java 8 mai 2003 15:58:1313:34:35 Exp gdodinet 
 * 
 */
public class OptionsRegistryTest extends TestCase {

	public void testGetOptionDescription() throws Exception {
	   assertEquals("Define a system property", OptionsRegistry.getDescription('D'));
       assertEquals("Produce logging information without adornments", OptionsRegistry.getDescription('E'));
       assertEquals("Produce execution debug output", OptionsRegistry.getDescription('X'));
       assertEquals("Produce exception stack traces", OptionsRegistry.getDescription('e'));
       assertEquals("Build is happening offline", OptionsRegistry.getDescription('o'));
       try {
	       OptionsRegistry.getDescription('Z');
           fail("Excepted InvalidOptionException");
       } 
       catch (InvalidOptionException e) { }
    }
}
