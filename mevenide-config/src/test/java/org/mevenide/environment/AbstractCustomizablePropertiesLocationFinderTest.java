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
package org.mevenide.environment;

import java.io.File;
import java.io.FileOutputStream;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: AbstractCustomizablePropertiesLocationFinderTest.java,v 1.1 16 nov. 2003 Exp gdodinet 
 * 
 */
public abstract class AbstractCustomizablePropertiesLocationFinderTest extends AbstractPropertiesLocationFinderTest {
	private File propertyFile ;
	
    protected void setUp() throws Exception {
    	createPropertiesFile();
    	super.setUp();
    }

    private void createPropertiesFile() throws Exception {
    	FileOutputStream fos = null;
    
    	try {
    		propertyFile = File.createTempFile("___", ".tmp");
    		fos = new FileOutputStream(propertyFile);
    		byte[] bytes = super.getSerializedProperties().getBytes();
    		fos.write( bytes );
    		copy(propertyFile.getAbsolutePath(), new File(propertyFile.getParent(), getFileName()).getAbsolutePath());
    	}
    	 finally {
    		if ( fos != null ) {
    			fos.close();
    		}
    	}
    }

    protected void tearDown() throws Exception {
    	delete(new File(propertyFile.getParent(), getFileName()));
    	delete(propertyFile);
    	super.tearDown( );
    }

    
    private String getFileName() {
        return "build.properties";
    }

    protected File getPropertyFile() {
        return propertyFile;
    }
}
