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
package org.mevenide.project.io;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import org.mevenide.context.JDomProjectUnmarshaller;
import org.mevenide.util.DefaultProjectUnmarshaller;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class CarefulProjectMarshallerTest extends AbstractMarshallerTestCase {

    protected IProjectMarshaller getMarshaller() throws Exception {
        return new CarefulProjectMarshaller();
    }
    
    public void testMarshall() throws Exception {
        Writer writer = new StringWriter();
        String pomFile = DefaultProjectMarshallerTest.class.getResource("/project.xml").getFile();
        File fil = new File(pomFile);
        JDomProjectUnmarshaller unmars = new JDomProjectUnmarshaller();
        //TODO it seems the BeanContentProvider has problems??
        // this works with the ElementContentProvider
    	((CarefulProjectMarshaller)marshaller).marshall(writer, new ElementContentProvider(unmars.parseRootElement(fil)));
    	Reader reader = new StringReader(writer.toString());
    	assertEquals(testProject, new DefaultProjectUnmarshaller().parse(reader));
    	//System.out.print(writer.toString());
    }	
    
}
 