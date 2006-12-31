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
package org.codehaus.mevenide.provider;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.ElementNameAndAttributeQualifier;
import org.custommonkey.xmlunit.XMLTestCase;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PropertyGrabberTest extends XMLTestCase {

    private PropertyGrabber grabber;
    private File pluginProperties;
    
    protected void setUp() throws Exception {
        grabber = new PropertyGrabber();
        pluginProperties = new File(getClass().getResource("/test_plugin.properties").getFile());
        grabber.setPropertyFile(pluginProperties);
        grabber.setPluginName("maven-aspectj-plugin");
        grabber.setPluginVersion("3.1.1");
    }
    
    protected void tearDown() throws Exception {
        grabber = null;
        pluginProperties = null;
    }
    
    public void testGrab() throws Exception {
        grabber.grab();
        File expectedResult = new File(getClass().getResource("/expected.xml").getFile());
        Diff diff = new Diff(new FileReader(expectedResult), new StringReader(grabber.getPropertyDescription()));
        diff.overrideElementQualifier(new ElementNameAndAttributeQualifier());
        assertTrue(diff.similar());
    }
}
