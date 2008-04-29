/* ==========================================================================
 * Copyright 2007 Mevenide Team
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

package org.codehaus.mevenide.netbeans;

import java.net.MalformedURLException;
import java.net.URL;
import junit.framework.TestCase;

/**
 *
 * @author mkleint
 */
public class CPExtenderTest extends TestCase {
    
    public CPExtenderTest(String testName) {
        super(testName);
    }

    public void testCheckLibrary() throws MalformedURLException {
        System.out.println("checkLibrary");
        URL[] repos = new URL[] {
            new URL("http://repo1.maven.org/maven2/"),
            new URL("http://download.java.net/maven/1/")
        };
        URL pom = new URL("http://repo1.maven.org/maven2/junit/junit/3.8.2/junit-3.8.2.pom");
        String[] result = CPExtender.checkLibrary(pom, repos);
        assertNotNull(result);
        assertEquals("default", result[0]);
        assertEquals("http://repo1.maven.org/maven2/", result[1]);
        assertEquals("junit", result[2]);
        assertEquals("junit", result[3]);
        assertEquals("3.8.2", result[4]);
        pom = new URL("http://download.java.net/maven/1/toplink.essentials/poms/toplink-essentials-agent-2.0-36.pom");
        result = CPExtender.checkLibrary(pom, repos);
        assertNotNull(result);
        assertEquals("legacy", result[0]);
        assertEquals("http://download.java.net/maven/1/", result[1]);
        assertEquals("toplink.essentials", result[2]);
        assertEquals("toplink-essentials-agent", result[3]);
        assertEquals("2.0-36", result[4]);
        
        pom = new URL("http://repo1.maven.org/maven2/org/codehaus/mevenide/netbeans-deploy-plugin/1.2.3/netbeans-deploy-plugin-1.2.3.pom");
        result = CPExtender.checkLibrary(pom, repos);
        assertNotNull(result);
        assertEquals("default", result[0]);
        assertEquals("http://repo1.maven.org/maven2/", result[1]);
        assertEquals("org.codehaus.mevenide", result[2]);
        assertEquals("netbeans-deploy-plugin", result[3]);
        assertEquals("1.2.3", result[4]);
    }
    
}
