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
package org.mevenide.properties.resolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import junit.framework.TestCase;

import org.mevenide.environment.LocationFinderAggregator;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class AbstractResolverTestCase extends TestCase {
    
    protected File userHomeDir;
    protected File projectDir;
    protected LocationFinderAggregator finder;
    /** Creates a new instance of DefaultsResolverTest */
    public AbstractResolverTestCase() {
    }
    
    protected void setUp() throws Exception {
        String userHome = System.getProperty("user.home"); //NOI18N
        userHomeDir  = new File(userHome, ".mevenide_test");
        if (!userHomeDir.exists()) {
            userHomeDir.mkdir();
        }
        File userprop = new File(AbstractResolverTestCase.class.getResource("/org/mevenide/properties/user.properties").getFile());
        File copyTo = new File(userHomeDir, "build.properties");
        copy(userprop.getAbsolutePath(), copyTo.getAbsolutePath());
        projectDir = new File (userHomeDir, "test_project");
        if (!projectDir.exists()) {
            projectDir.mkdir();
        }
        File buildprop = new File(AbstractResolverTestCase.class.getResource("/org/mevenide/properties/build.properties").getFile());
        copyTo = new File(projectDir, "build.properties");
        copy(buildprop.getAbsolutePath(), copyTo.getAbsolutePath());

        File projectprop = new File(AbstractResolverTestCase.class.getResource("/org/mevenide/properties/project.properties").getFile());
        copyTo = new File(projectDir, "project.properties");
        copy(projectprop.getAbsolutePath(), copyTo.getAbsolutePath());
        
        finder = new LocationFinderAggregator();
        finder.setEffectiveWorkingDirectory(projectDir.getAbsolutePath());
        
    }

    protected void tearDown() throws Exception {
        delete(userHomeDir);
        finder = null;
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
