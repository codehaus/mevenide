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
package org.mevenide.util;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class StringUtilsTest extends TestCase {
	public void testIsNull() {
		assertTrue(StringUtils.isNull(null));
		assertTrue(StringUtils.isNull(""));
		assertTrue(StringUtils.isNull("   "));
		assertFalse(StringUtils.isNull("  e "));
	}
	
	public void testRelaxEqual() {
		assertTrue(StringUtils.relaxEqual(null, null));
		assertFalse(StringUtils.relaxEqual(null, "1"));
		assertFalse(StringUtils.relaxEqual("1", null));
		assertFalse(StringUtils.relaxEqual("2", "1"));
		assertTrue(StringUtils.relaxEqual(" 1  ", "1 "));
	}
}

