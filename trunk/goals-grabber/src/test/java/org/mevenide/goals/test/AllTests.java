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
package org.mevenide.goals.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: AllTests.java,v 1.1 7 sept. 2003 Exp gdodinet 
 * 
 */
public class AllTests {
	private AllTests() {
	}

	public static Test suite() {
		TestSuite suite = new TestSuite();

		suite.addTest(org.mevenide.goals.grabber.AllTests.suite());
		suite.addTest(org.mevenide.goals.manager.AllTests.suite());
		suite.addTest(org.mevenide.reports.AllTests.suite());
		
		return suite;
	}
}
