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

package org.mevenide.netbeans.project.writer;

import java.io.StringBufferInputStream;
import junit.framework.*;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class CountNewLinesReaderTest extends TestCase {
    
    public CountNewLinesReaderTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(CountNewLinesReaderTest.class);
        
        return suite;
    }


    /**
     * Test of getNewLineString method, of class org.mevenide.netbeans.project.writer.CountNewLinesReader.
     */
    public void testGetNewLineString() throws Exception {
        StringBufferInputStream str = new StringBufferInputStream("lineone\nline2\nline3\nline4");
        CountNewLinesReader reader = new CountNewLinesReader(str);
        reader.read(new char[2000],0,2000);
        assertEquals("\n", reader.getNewLineString());
        
        str = new StringBufferInputStream("lineone\r\nline2\r\nline3\r\nline4");
        reader = new CountNewLinesReader(str);
        reader.read(new char[2000],0,2000);
        assertEquals("\r\n", reader.getNewLineString());
        
        str = new StringBufferInputStream("lineone\rline2\rline3\rline4");
        reader = new CountNewLinesReader(str);
        reader.read(new char[2000],0,2000);
        assertEquals("\r", reader.getNewLineString());
        
        str = new StringBufferInputStream("");
        reader = new CountNewLinesReader(str);
        reader.read(new char[2000],0,2000);
        assertEquals(System.getProperty("line.separator"), reader.getNewLineString());
    }

    
}
