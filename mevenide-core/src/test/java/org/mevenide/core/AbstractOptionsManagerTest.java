/*
 * Created on 11 mai 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.mevenide.core;



import junit.framework.TestCase;

/**
 * @author gdodinet
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AbstractOptionsManagerTest extends TestCase {
	AbstractOptionsManager manager ;

	protected void setUp() throws Exception {
		manager = new AbstractOptionsManager() {} ;
	}

	protected void tearDown() throws Exception {
		manager = null;
	}

	public void testGetOptions() {
		assertEquals(0, manager.getOptions().length);
	}

}
