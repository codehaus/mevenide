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
package org.codehaus.mevenide.pde.archive;

import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import junit.framework.TestCase;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SimpleZipCreatorTest extends TestCase {
    
    private File directory;
    private String output;
    
    protected void setUp() throws Exception {
        File temp = File.createTempFile("mevenide", "test");
        output = temp.getAbsolutePath();
        directory = new File(getClass().getResource("/zip.test").getFile());
    }
    
    protected void tearDown() throws Exception {
        new File(output).delete();
    }
    
    public void testZip() throws Exception {
        new SimpleZipCreator(directory.getAbsolutePath(), output).zip();
        
        ZipFile zipFile = new ZipFile(output);
        Enumeration zipEntries = zipFile.entries();
        List list = Collections.list(zipEntries);
        assertEquals(2, list.size());
        for (Iterator it = list.iterator(); it.hasNext();) {
            ZipEntry entry = (ZipEntry) it.next();
            assertTrue("/dir/file.test".equals(entry.getName().replaceAll("\\\\","/")) || 
                       "/file.test".equals(entry.getName().replaceAll("\\\\","/")));
        }
    }
}
