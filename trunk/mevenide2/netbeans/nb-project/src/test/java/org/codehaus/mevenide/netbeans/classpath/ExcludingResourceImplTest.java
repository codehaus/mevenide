/*
 *  Copyright 2008 mkleint.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.codehaus.mevenide.netbeans.classpath;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import org.apache.maven.model.Resource;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author mkleint
 */
public class ExcludingResourceImplTest extends TestCase {
    
    public ExcludingResourceImplTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetRoots() throws Exception {
        System.out.println("getRoots");
        File file = FileUtil.normalizeFile(new File("/home/mkleint2/tmp"));
            
        ExcludingResourceImpl instance = new ExcludingResourceImpl2(false, 
                Collections.singletonList(createRes(file.getAbsolutePath(),
                new String[] {
                    "NOTE.txt",
                    "LICENSE.txt"
                },
                null)));
        URL expResult = new URL("file:/"+file.getAbsolutePath()+"/");
        URL[] result = instance.getRoots();
        assertEquals(1, result.length);
        
        assertEquals(expResult, result[0]);
    }

    public void testIncludes() {
        System.out.println("includes");
        File file = FileUtil.normalizeFile(new File("/home/mkleint2/tmp/"));
        ExcludingResourceImpl instance = new ExcludingResourceImpl2(false, 
                Collections.singletonList(createRes(file.getAbsolutePath(),
                new String[] {
                    "NOTE.txt",
                    "LICENSE.txt"
                },
                null)));
        URL url = instance.getRoots()[0];
        assertFalse(instance.includes(url, "src/test/java"));
        assertFalse(instance.includes(url, "Note.txt"));
        assertFalse(instance.includes(url, "src/LICENSE.txt"));
        assertTrue(instance.includes(url, "LICENSE.txt"));
        
        instance = new ExcludingResourceImpl2(false, 
                Collections.singletonList(createRes(file.getAbsolutePath(),
                new String[] {
                    "**/Bundle.properties",
                    "*/Bundle_ja.properties",
                    "org/milos/**/*.gif"
                },
                new String[] {
                    "org/milos/obsolete/**/Bundle.properties",
                    "**/xman.gif"
                }
        )));
        url = instance.getRoots()[0];
        assertTrue(instance.includes(url, "Bundle.properties"));
        assertTrue(instance.includes(url, "org/milos/Bundle.properties"));
        assertFalse(instance.includes(url, "org/milos/obsolete/Bundle.properties"));
        assertFalse(instance.includes(url, "org/milos/obsolete/xman/Bundle.properties"));
        assertFalse(instance.includes(url, "org/milos/Bundle_ja.properties"));
        assertTrue(instance.includes(url, "org/Bundle_ja.properties"));
        
        assertTrue(instance.includes(url, "org/milos/xman/xman2.gif"));
        assertFalse(instance.includes(url, "org/milos/xman2/xman.gif"));
        
        instance = new ExcludingResourceImpl2(false, 
                Collections.singletonList(createRes(file.getAbsolutePath(),
                null, null
        )));
        url = instance.getRoots()[0];
        
        assertTrue(instance.includes(url, "Bundle.properties"));
        assertTrue(instance.includes(url, "org/milos/Bundle.properties"));
        assertTrue(instance.includes(url, "org/milos/obsolete/Bundle.properties"));
        assertTrue(instance.includes(url, "org/milos/obsolete/xman/Bundle.properties"));
        assertTrue(instance.includes(url, "org/milos/Bundle_ja.properties"));
        assertTrue(instance.includes(url, "org/Bundle_ja.properties"));
        
    }

    private Resource createRes(String basedir, String[] includes, String[] excludes) {
        Resource res = new Resource();
        res.setDirectory(basedir);
        if (includes != null) {
            res.setIncludes(Arrays.asList(includes));
        }
        if (excludes != null) {
            res.setExcludes(Arrays.asList(excludes));
        }
        return res;
    }
    
    private class ExcludingResourceImpl2 extends ExcludingResourceImpl {
        private List<Resource> resources;

        ExcludingResourceImpl2(boolean test, List<Resource> res) {
            super(test);
            resources = res;
        }
        
        @Override
        protected File getBase() {
            return new File(System.getProperty("user.home"));
        }

        @Override
        protected List<Resource> getResources(boolean istest) {
            return resources;
        }
        
    }

}
