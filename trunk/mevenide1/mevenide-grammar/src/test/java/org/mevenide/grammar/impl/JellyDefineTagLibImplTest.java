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
package org.mevenide.grammar.impl;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import junit.framework.TestCase;
import org.jdom.Element;

/**
 * 
 * @author <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 * 
 */
public class JellyDefineTagLibImplTest extends TestCase {

    private static ClassLoader mavenClassLoader;
    
    protected void setUp() throws Exception {
        String maven_home = System.getProperty("user.home") + "/" + "maven";//NOI18N
        File pluginDir = new File(maven_home, "plugin");
        File jar = new File(pluginDir, "maven.jar");
        try {
            URL url = jar.toURL();
            mavenClassLoader = new URLClassLoader(new URL[] {url});
        } catch (MalformedURLException exc)
        {
            System.out.println("error" + exc);
        }
        
    }

    protected void tearDown() throws Exception {
    }

    public void testFindDynamicTagLibraryElement() throws Exception {
        InputStream str = getClass().getResourceAsStream("/org/mevenide/grammar/dynamic/plugin1.jelly");
        Element el = JellyDefineTagLibImpl.findDynamicTagLibraryElement(str);
        assertNotNull("element is null", el);
        assertEquals(el.getName(), "taglib");

        str = getClass().getResourceAsStream("/org/mevenide/grammar/dynamic/plugin2.jelly");
        el = JellyDefineTagLibImpl.findDynamicTagLibraryElement(str);
        assertNotNull("element is null", el);
        assertEquals(el.getName(), "taglib");
    }

    public void testSimpleTagLibDef1() throws Exception {
        InputStream str = getClass().getResourceAsStream("/org/mevenide/grammar/dynamic/plugin1.jelly");
        JellyDefineTagLibImpl tagLib = new JellyDefineTagLibImpl(str);
        assertEquals(tagLib.getName(), "artifact");
        Collection roottags = tagLib.getRootTags();
        assertEquals(roottags.size(), 5); // manifest file
        assertTrue(roottags.contains("manifest-file"));
        assertTrue(roottags.contains("deploy-snapshot"));
        assertTrue(roottags.contains("install"));
    }

    public void testSimpleTagLibDef2() throws Exception {
        InputStream str = getClass().getResourceAsStream("/org/mevenide/grammar/dynamic/plugin2.jelly");
        JellyDefineTagLibImpl tagLib = new JellyDefineTagLibImpl(str);
        assertEquals(tagLib.getName(), "license");
        Collection roottags = tagLib.getRootTags();
        assertEquals(roottags.size(), 2); // manifest file
        assertTrue(roottags.contains("fileName"));
        assertTrue(roottags.contains("relativeFileName"));
    }
    
}
