/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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
package org.mevenide.properties;

import java.io.InputStream;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PropertyModelFactoryTest extends TestCase {
    private PropertyModelFactory factory;
    private InputStream propertyStream;

//	Test File :   
//
//#comment1
//my.prop.1 = myProp1
//my.prop.2    =myProp2
//   #comment2
//   
//  !  comment3
//  
//  my.prop.3=  myProp3
//
//###comment4
// my.prop.4: myOveriddenProp
// my.prop.4   : myProp4
//
//!comment5
//my.prop.5 = \
// myProp5 \
// is defined on multiple lines
//    
// my.prop.6 = trailing whitespaces and a tab   	
    
    protected void setUp() throws Exception {
        factory = PropertyModelFactory.getFactory();
        propertyStream = PropertyModelFactoryTest.class.getResourceAsStream("/org/mevenide/properties/test_prop.properties");
    }

    protected void tearDown() throws Exception {
        factory = null;
    }

    public void testNewPropertyModel_InputStream() throws Exception {
        PropertyModel model = factory.newPropertyModel(propertyStream);
		
		
        assertEquals("my.prop.1", model.findByKey("my.prop.1").getKey());
		assertEquals("myProp1", model.findByKey("my.prop.1").getValue());

		assertEquals("my.prop.2", model.findByKey("my.prop.2").getKey());
		assertEquals("myProp2", model.findByKey("my.prop.2").getValue());

		assertEquals("my.prop.3", model.findByKey("my.prop.3").getKey());
		assertEquals("myProp3", model.findByKey("my.prop.3").getValue());

		assertEquals("my.prop.4", model.findByKey("my.prop.4").getKey());
		assertEquals("myProp4", model.findByKey("my.prop.4").getValue());

		assertEquals("my.prop.5", model.findByKey("my.prop.5").getKey());
		assertEquals("myProp5 is defined on multiple lines", model.findByKey("my.prop.5").getValue());
		
		assertEquals("my.prop.6", model.findByKey("my.prop.6").getKey());
		assertEquals("trailing whitespaces and a tab", model.findByKey("my.prop.6").getValue());
    }

}
