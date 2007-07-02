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

package org.mevenide.netbeans.project.nodes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import junit.framework.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class DirScannerSubClassTest extends TestCase {
    private File rootTempDir;
    private FileObject rootTempFO;
    public DirScannerSubClassTest(java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(DirScannerSubClassTest.class);
        return suite;
    }

    protected void setUp() throws java.lang.Exception {
        File tempFile = File.createTempFile("DirScannerSubClassTest", "test");
        File tempDir = new File(tempFile.getParentFile(), "DirScannerSubClassTest");
        rootTempDir = tempDir;
        tempDir.mkdirs();
        tempDir = new File(tempDir, "CVS");
        tempDir.mkdirs();
        File file = new File(tempDir, "hello.txt");
        file.createNewFile();
        tempDir = new File(tempDir, "templates");
        tempDir.mkdirs();
        tempDir = new File(tempDir, "CVS");
        tempDir.mkdirs();
        file = new File(tempDir, "hello2.txt");
        file.createNewFile();
        tempDir = new File(tempDir, "something");
        tempDir.mkdirs();
        tempDir = new File(tempDir, "CVS");
        tempDir.mkdirs();
        file = new File(tempDir, "hello2.txt");
        file.createNewFile();
        tempDir = new File(tempDir, "something2");
        boolean isCreated = tempDir.mkdirs();
        file = new File(tempDir, "hello2.txt");
        file.createNewFile();
        rootTempFO = FileUtil.toFileObject(FileUtil.normalizeFile(rootTempDir));
//        if (rootTempFO == null) {
//            throw new Exception("is null");
//        }
    }

    protected void tearDown() throws java.lang.Exception {
        delete(rootTempDir);
    }

    /**
     * Test of checkIncludedImpl method, of class org.mevenide.netbeans.project.nodes.DirScannerSubClass.
     */
    public void testCheckIncludedImpl() throws Exception {
//        dotestWithPattern("**", null, new boolean[] {true, true, true, true, true, true, true});
//        dotestWithPattern("*/*.txt", null, new boolean[] {false, true, true, true, true, true, true});
//        dotestWithPattern("*/*.*", "*/hello2.txt", new boolean[] {false, false, false, false, true, true, true});
//        dotestWithPattern("templates/*.txt", null, new boolean[] {false, true, false, false, true, false, false});
//        dotestWithPattern("templates/something/*", null, new boolean[] {false, false, true, true, true, true, true});
    }
    
    public void testCheckVisible() {
        File cvs = new File(rootTempDir, "CVS");
        assertFalse(DirScannerSubClass.checkVisible(cvs, rootTempDir));
        File noncvs = new File(rootTempDir, "templates");
        assertTrue(DirScannerSubClass.checkVisible(noncvs, rootTempDir));
        cvs = new File(new File(rootTempDir, "templates"), "CVS");
        assertFalse(DirScannerSubClass.checkVisible(cvs, rootTempDir));
    }
    
    private void dotestWithPattern(String include, String exclude, boolean[] success) throws Exception {
        List includes = new ArrayList();
        if (include != null) {
            includes.add(include);
        }
        List excludes = new ArrayList();
        if (exclude != null) {
            excludes.add(exclude);
        }
        FileObject file = FileUtil.toFileObject(new File(rootTempDir, "hello.txt"));
        
        assertEquals(success[0], DirScannerSubClass.checkIncludedImpl(file, rootTempFO, includes, excludes)); 

        file = FileUtil.toFileObject(new File(rootTempDir, "templates/hello2.txt"));
        assertEquals(success[1], DirScannerSubClass.checkIncludedImpl(file, rootTempFO, includes, excludes)); 
        
        file = FileUtil.toFileObject(new File(rootTempDir, "templates/something/hello2.txt"));
        assertEquals(success[2], DirScannerSubClass.checkIncludedImpl(file, rootTempFO, includes, excludes)); 
        
        file = FileUtil.toFileObject(new File(rootTempDir, "templates/something/something2/hello2.txt"));
        assertEquals(success[3], DirScannerSubClass.checkIncludedImpl(file, rootTempFO, includes, excludes)); 
        
        file = FileUtil.toFileObject(new File(rootTempDir, "templates"));
        assertEquals(success[4], DirScannerSubClass.checkIncludedImpl(file, rootTempFO, includes, excludes)); 
        
        file = FileUtil.toFileObject(new File(rootTempDir, "templates/something"));
        assertEquals(success[5], DirScannerSubClass.checkIncludedImpl(file, rootTempFO, includes, excludes)); 
        
        file = FileUtil.toFileObject(new File(rootTempDir, "templates/something/something2"));
        assertEquals(success[6], DirScannerSubClass.checkIncludedImpl(file, rootTempFO, includes, excludes)); 
    }
    
	public static void delete(File file) {
		if ( file.isFile() ) {
			file.delete();
		}
		else {
			File[] files = file.listFiles();
			if ( files != null ) {
				for (int i = 0; i < files.length; i++) {
					delete(files[i]);
				}
			}
			file.delete();
		}
	}   
}
