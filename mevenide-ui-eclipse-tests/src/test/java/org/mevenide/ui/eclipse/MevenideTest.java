/*
 * Created on Feb 5, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.mevenide.ui.eclipse;

import junit.framework.TestCase;

/**
 * @author badlap1
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
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
		assertNotNull(Mevenide.getPlugin());
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
		assertNotNull(Mevenide.getPlugin().getResourceBundle());
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
