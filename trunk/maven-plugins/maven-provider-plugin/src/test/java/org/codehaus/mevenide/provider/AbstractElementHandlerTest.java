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
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.ElementNameAndAttributeQualifier;
import org.mevenide.properties.Element;
import org.mevenide.properties.PropertyModel;
import org.mevenide.properties.PropertyModelFactory;
import junit.framework.TestCase;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class AbstractElementHandlerTest extends TestCase {
  
    private IElementHandler elementHandler;
    
    protected void setUp() throws Exception {
        InputStream stream = getClass().getResourceAsStream("/test_plugin.properties");
        elementHandler = getElementHandler();
        elementHandler.setPluginName("maven-aspectj-plugin");
        elementHandler.setPluginVersion("3.1.1");
        PropertyModel model = PropertyModelFactory.getFactory().newPropertyModel(stream);
        List modelElements = model.getList();
        for (int i = 0; i < modelElements.size(); i++) {
            Element element = (Element) modelElements.get(i);
            elementHandler.handle(element);
        }
    }
    
    protected abstract IElementHandler getElementHandler();
    
    public void testGetXmlDescription() throws Exception {
        File expectedResult = new File(getClass().getResource("/expected.xml").getFile());
        Diff diff = new Diff(new FileReader(expectedResult), new StringReader(elementHandler.getXmlDescription()));
        diff.overrideElementQualifier(new ElementNameAndAttributeQualifier());
        assertTrue(diff.similar());
    }
}
