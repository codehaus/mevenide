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
package org.mevenide.ui.eclipse;

import junit.framework.TestCase;

/**
 * 
 * @author Jeffrey Bonevich <jeff@bonevich.com>
 * @version $Id$
 */
public class MevenideTest extends TestCase {

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Constructor for MevenideTest.
	 * @param arg0
	 */
	public MevenideTest(String arg0) {
		super(arg0);
	}

	public void testGetPlugin() {
		assertNotNull(Mevenide.getInstance());
	}

	public void testGetWorkspace() {
		assertNotNull(Mevenide.getWorkspace());
	}

	/*
	 * Class to test for String getResourceString(String)
	 */
	public void testGetResourceStringString() {
		//FIXME: refactor plugin to allow unit testing of bundle
	}

	/*
	 * Class to test for String getResourceString(String, String)
	 */
	public void testGetResourceStringStringString() {
		//FIXME: refactor plugin to allow unit testing of bundle
	}

	/*
	 * Class to test for String getResourceString(String, String[])
	 */
	public void testGetResourceStringStringStringArray() {
		//FIXME: refactor plugin to allow unit testing of bundle
	}

	public void testGetResourceBundle() {
		assertNotNull(Mevenide.getInstance().getResourceBundle());
	}

	public void testGetImageDescriptor() {
	}

	public void testGetPreferencesFilename() {
	}

	public void testGetFile() {
	}

	public void testCreatePom() {
	}

	public void testCreateProjectProperties() {
	}

	public void testPopUp() {
	}

	public void testGetEffectiveDirectory() {
	}

	public void testGetForeheadConf() {
	}

	public void testInitEnvironment() {
	}

	public void testSetBuildPath() {
	}

	public void testGetJavaHome() {
	}

	public void testSetJavaHome() {
	}

	public void testGetMavenHome() {
	}

	public void testSetMavenHome() {
	}

	public void testGetMavenRepository() {
	}

	public void testSetMavenRepository() {
	}

	public void testGetCurrentDir() {
	}

	public void testSetCurrentDir() {
	}

	public void testSetProject() {
	}

	public void testGetCheckTimestamp() {
	}

	public void testSetCheckTimestamp() {
	}

	public void testGetPomTemplate() {
	}

	public void testSetPomTemplate() {
	}

	public void testGetDefaultGoals() {
	}

	public void testSetDefaultGoals() {
	}

	public void testGetPluginsInstallDir() {
	}

	public void testGetMavenLocalHome() {
	}

	public void testSetMavenLocalHome() {
	}

	public void testGetHeapSize() {
	}

	public void testSetHeapSize() {
	}

}
