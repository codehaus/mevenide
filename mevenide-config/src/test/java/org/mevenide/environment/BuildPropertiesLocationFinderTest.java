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
 * @version $Id: BuildPropertiesLocationFinderTest.java,v 1.1 16 nov. 2003 Exp gdodinet
 *
 */
public class BuildPropertiesLocationFinderTest extends AbstractPropertiesLocationFinderTest {
    private File buildProperties;

    protected void setUp() throws Exception {
        createBuildProperties();
        super.setUp();
    }

	private void createBuildProperties() throws Exception {
        FileOutputStream fos = null;

        try {
            buildProperties = File.createTempFile("___", "tmp");
            System.setProperty("user.home", buildProperties.getParent());
            fos = new FileOutputStream(buildProperties);
            byte[] bytes = super.getSerializedProperties().getBytes();
            fos.write( bytes );
			copy(buildProperties.getAbsolutePath(), new File(buildProperties.getParent(), "build.properties").getAbsolutePath());
        }
         finally {
            if ( fos != null ) {
                fos.close();
            }
        }
    }

    protected PropertiesLocationFinder getPropertiesLocationFinder() throws Exception {
        return BuildPropertiesLocationFinder.getInstance();
    }

    protected void tearDown(  ) throws Exception {
		delete(new File(buildProperties.getParent(), "build.properties"));
		delete(buildProperties);
        super.tearDown( );
    }
}
