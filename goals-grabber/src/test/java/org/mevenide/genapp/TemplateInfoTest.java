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

import java.net.URL;
import junit.framework.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;
import org.mevenide.context.IQueryContext;
import org.mevenide.goals.TestQueryContext;

/**
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class TemplateInfoTest extends TestCase {
    
    public TemplateInfoTest(String testName) {
        super(testName);
    }

    private TemplateInfo info;
    private File rootDir;
    
    protected void setUp() throws java.lang.Exception {
        rootDir = new File(this.getClass().getResource("/templates/default").getFile());
        TestQueryContext context = new TestQueryContext();
        // populate context with genapp plugin defaults..
        context.addUserPropertyValue("maven.genapp.prompt.template", "Enter a project template to use:");
        context.addUserPropertyValue("maven.genapp.default.template", "default");
        context.addUserPropertyValue("maven.genapp.param", "id,name,package,user");
        context.addUserPropertyValue("maven.genapp.default.id", "app");
        context.addUserPropertyValue("maven.genapp.prompt.id", "Please specify an id for your application:");
        context.addUserPropertyValue("maven.genapp.default.name", "Example Application");
        context.addUserPropertyValue("maven.genapp.prompt.name", "Please specify a name for your application:");
        context.addUserPropertyValue("maven.genapp.default.package", "example.app");
        context.addUserPropertyValue("maven.genapp.prompt.package", "Please specify the package for your application:");
        context.addUserPropertyValue("maven.genapp.default.user", "${user.name}");

        info = new TemplateInfo(rootDir, context);
        
    }

    protected void tearDown() throws java.lang.Exception {
    }

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite(TemplateInfoTest.class);
        
        return suite;
    }

    /**
     * Test of getName method, of class org.mevenide.genapp.TemplateInfo.
     */
    public void testGetName() {
        assertEquals("default", info.getName());
    }

    /**
     * Test of getDisplayName method, of class org.mevenide.genapp.TemplateInfo.
     */
    public void testGetDisplayName() {
        assertEquals("default", info.getDisplayName());
    }

    /**
     * Test of getRepackageRooots method, of class org.mevenide.genapp.TemplateInfo.
     */
    public void testGetRepackageRooots() {
        File[] roots = info.getRepackageRooots();
        // should be java,test
        assertNotNull(roots);
        assertEquals(2, roots.length);
        assertEquals(new File(rootDir, "java"), roots[0]);
        assertEquals(new File(rootDir, "test"), roots[1]);
    }

    /**
     * Test of getParameters method, of class org.mevenide.genapp.TemplateInfo.
     */
    public void testGetParameters() {
        String[] pars = info.getParameters();
        assertNotNull(pars);
        assertEquals(4, pars.length);
        assertEquals("id", pars[0]);
        assertEquals("name", pars[1]);
        assertEquals("package", pars[2]);
        assertEquals("user", pars[3]);
    }

    /**
     * Test of getDefaultValue method, of class org.mevenide.genapp.TemplateInfo.
     */
    public void testGetDefaultValue() {
        String def = info.getDefaultValue("package");
        assertEquals("example.app.test", def);
    }

    /**
     * Test of getPromptText method, of class org.mevenide.genapp.TemplateInfo.
     */
    public void testGetPromptText() {
        String def = info.getPromptText("package");
        assertEquals("Test prompt", def);
    }

    /**
     * Test of hasCustomScript method, of class org.mevenide.genapp.TemplateInfo.
     */
    public void testHasCustomScript() {
        assertEquals(false, info.hasCustomScript());
    }
    
    
}
