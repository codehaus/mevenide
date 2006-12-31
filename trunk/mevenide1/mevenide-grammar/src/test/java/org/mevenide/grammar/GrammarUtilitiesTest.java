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
package org.mevenide.grammar;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 * 
 */
public class GrammarUtilitiesTest extends TestCase {

    private static final String[] LINES_TO_EXTRACT = new String[] {
        "",
        "helloworld",
        "${nice",
        "${nice-to-have",
        "goal1, goal2",
        "goal1,goal2",
        "${property.name",
        "${nice "
    };
    private static final String[] RESULTS = new String[] {
        "",
        "helloworld",
        "nice",
        "nice-to-have",
        "goal2",
        "goal2",
        "property.name",
        ""
    };
    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void testExtractLastWord() throws Exception {
        for (int i = 0; i < LINES_TO_EXTRACT.length; i++) {
            String result = GrammarUtilities.extractLastWord(LINES_TO_EXTRACT[i]);
            assertEquals("expected='" + RESULTS[i] + "' returned='" + result + "'", result, RESULTS[i]);
        }
    }
    
    
}
