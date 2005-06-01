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
package org.mevenide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import junit.framework.TestCase;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.environment.LocationFinderAggregator;

/**
 *
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 *
 */
public class AbstractMevenideTestCase extends TestCase {
    
    
    protected File projectFile;
    private File mavenHome;
    private TestQueryContext context;
    
    
    protected void setUp() throws Exception {
        String userHome = System.getProperty("user.home"); //NOI18N
        mavenHome  = new File(userHome, ".mevenide_test");
        if (!mavenHome.exists()) {
            mavenHome.mkdir();
        }
        context = new TestQueryContext();
        context.setUserDirectory(mavenHome);
        context.addUserPropertyValue("maven.home", mavenHome.getAbsolutePath());
        File repoFile = new File(mavenHome, "repository");
        if (!repoFile.exists()) {
            repoFile.mkdir();
        }
        context.addUserPropertyValue("maven.local.repo", repoFile.getAbsolutePath());
        File src = new File(AbstractMevenideTestCase.class.getResource("/fixtures/project-fixture.xml").getFile());
        projectFile = new File(src.getParentFile().getParent(), "project-fixture.xml") ;
        copy(src.getAbsolutePath(), projectFile.getAbsolutePath());
    }
    
    protected void tearDown() throws Exception {
        projectFile.delete();
        delete(mavenHome);
    }
    
    protected void delete(File file) {
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
    
    protected void copy(String sourceFile, String destFile) throws Exception {
        
        FileInputStream from = new FileInputStream(sourceFile);
        FileOutputStream to = new FileOutputStream(destFile);
        try {
            byte[] buffer = new byte[4096];
            int bytes_read;
            while ((bytes_read = from.read(buffer)) != -1) {
                to.write(buffer, 0, bytes_read);
            }
        }
        finally {
            if (from != null) {
                from.close();
            }
            if (to != null) {
                to.close();
            }
        }
        
    }
}
