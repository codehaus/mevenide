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
package org.mevenide.genapp;

import junit.framework.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.goals.TestQueryContext;
import org.mevenide.properties.IPropertyFinder;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Hashtable;
import org.mevenide.properties.IPropertyResolver;

/**
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class GenAppTemplateFinderTest extends TestCase {
    
    public GenAppTemplateFinderTest(String testName) {
        super(testName);
    }

    private TestQueryContext context;
    private File rootDir;
    
    protected void setUp() throws java.lang.Exception {
        rootDir = new File(this.getClass().getResource("/templates").getFile());
        context = new TestQueryContext();
        // populate context with genapp plugin defaults..
        context.addUserPropertyValue("maven.genapp.template.repository", rootDir.getAbsolutePath());

    }


    protected void tearDown() throws java.lang.Exception {
    }

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite(GenAppTemplateFinderTest.class);
        
        return suite;
    }

    /**
     * Test of getTemplates method, of class org.mevenide.genapp.GenAppTemplateFinder.
     */
    public void testGetTemplates() {
        GenAppTemplateFinder finder = new GenAppTemplateFinder(context);
        TemplateInfo[] infos = finder.getTemplates(GenAppTemplateFinder.LOCATION_USER);
        assertNotNull(infos);
        assertEquals(2, infos.length);
        assertEquals(infos[1].getName(), "default");
        assertEquals(infos[0].getName(), "ejb");
    }
    
    
}
