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
package org.mevenide.core;

import java.io.File;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class AbstractRunnerTest extends TestCase {

	private AbstractRunner runnerNullBaseDir;
	private AbstractRunner runnerNotNullBaseDir;
	
	protected void setUp() throws Exception {
		runnerNullBaseDir = new AbstractRunnerStub();
		runnerNotNullBaseDir = 
			new AbstractRunnerStub() { 
				protected String getBasedir() {return "someBasedir";} 
			};
	}

	protected void tearDown() throws Exception {
		runnerNullBaseDir = null;
		runnerNotNullBaseDir = null;
	}

	public void testGetMavenArgs() {
		String[] options = runnerNullBaseDir.getMavenArgs(new String[]{"-X", "-e", "-DsomeProperty=someValue"}, new String[]{"somePlugin:someGoal", "somePlugin:anotherGoal"});
		String[] expected = new String[]{"-b", "-f", "project.xml", "-X", "-e", "-DsomeProperty=someValue", "somePlugin:someGoal", "somePlugin:anotherGoal"};
		
		assertEquality(expected, options);
		
		options = runnerNotNullBaseDir.getMavenArgs(new String[]{"-X", "-e", "-DsomeProperty=someValue"}, new String[]{"somePlugin:someGoal", "somePlugin:anotherGoal"});
		expected = new String[]{"-b", "-f", "someBasedir" + File.separator + "project.xml", "-X", "-e", "-DsomeProperty=someValue", "somePlugin:someGoal", "somePlugin:anotherGoal"};
		assertEquality(expected, options);
	}

	private void assertEquality(String[] expected, String[] options) {
		assertEquals(expected.length, options.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], options[i]);
		}
	}

}
