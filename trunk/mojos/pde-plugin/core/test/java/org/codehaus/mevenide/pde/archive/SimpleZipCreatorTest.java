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
import java.util.ArrayList;
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
    private SimpleZipCreator simpleZipCreator;
    
    protected void setUp() throws Exception {
        File temp = File.createTempFile("mevenide_", "_test.zip");
        output = temp.getAbsolutePath();
        directory = new File(getClass().getResource("/zip.test").getFile());
        
        List includes = new ArrayList();

        File includedPluginDescriptor =  new File(getClass().getResource("/basedir.common/plugin.xml").getFile());
        Include zippedDescriptor = new Include(includedPluginDescriptor.getAbsolutePath(), null);
        includes.add(zippedDescriptor);
        
        File includedLicense =  new File(getClass().getResource("/basedir.common/license/license.txt").getFile());
        Include zippedLicense = new Include(includedLicense.getAbsolutePath(), "/META-INF");
        includes.add(zippedLicense);
        
        simpleZipCreator = new SimpleZipCreator(directory.getAbsolutePath(), output, "**/*.exc");
        simpleZipCreator.setIncludes(includes);
    }
    
    protected void tearDown() throws Exception {
        new File(output).delete();
        simpleZipCreator = null;
    }
    
	public void testZipNoDirectory() throws Exception {
		simpleZipCreator.setDirectory(null);
		simpleZipCreator.zip();
	}
	
    public void testZip() throws Exception {
        simpleZipCreator.zip();
        
        ZipFile zipFile = new ZipFile(output);
        Enumeration zipEntries = zipFile.entries();
        List list = Collections.list(zipEntries);
        assertEquals(4, list.size());
        
        List result = new ArrayList();
        
        for (Iterator it = list.iterator(); it.hasNext();) {
            ZipEntry entry = (ZipEntry) it.next();
            result.add(entry.getName().replaceAll("\\\\","/"));
        }
        
        assertTrue(result.contains("/dir/file.test"));
        assertTrue(result.contains("/file.test"));
        assertTrue(result.contains("/META-INF/license.txt"));
        assertTrue(result.contains("/plugin.xml"));
    }
}

