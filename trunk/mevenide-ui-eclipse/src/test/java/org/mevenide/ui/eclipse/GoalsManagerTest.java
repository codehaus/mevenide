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
package org.mevenide.ui.eclipse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;

/**
 * 
 * requires org.eclipse.pde.junit plugin
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class GoalsManagerTest extends TestCase {

	private File goalsPrefs ;
	private GoalsManager manager;
	
	protected void setUp() throws Exception {
		initGoalsPrefs();
		manager = new GoalsManager();
	}

	private void initGoalsPrefs() throws IOException {
		goalsPrefs = createTestFile("goals_prefs.ini");
		FileWriter writer = new FileWriter(goalsPrefs, false);
		byte[] bytes = getPreferenceGoals().getBytes();
		char[] chars = new char[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			chars[i] = Byte.toString(bytes[i]).charAt(0);
		}
		writer.write(chars); 
	}

	protected void tearDown() throws Exception {
		goalsPrefs = null;
		manager = null;
	}

	public void testLoad() throws Exception {
		manager.load();
		assertEquals(2, manager.getGoals("eclipse").length);
	}

	public void testSave() {
	}

	public void testInitialize() {
	}

	public void testGetXmlGoals() {
	}
	
	private File createTestFile(String fname) {
		try {
			URL installBase = MavenPlugin.getPlugin().getDescriptor().getInstallURL();
			return new File(new File(Platform.resolve(installBase).getFile()).getAbsolutePath(), fname);
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String getPreferenceGoals() {
		return "#Sat Apr 05 15:22:38 CEST 2003\n" +
			   "mevenide.goals.plugin.eclipse=generate-project;generate-classpath;\n"+
			   "mevenide.goals.plugins=eclipse;";
	}
	

}
