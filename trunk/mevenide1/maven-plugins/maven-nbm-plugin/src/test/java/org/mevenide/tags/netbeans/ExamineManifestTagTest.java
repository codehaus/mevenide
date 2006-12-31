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
package org.mevenide.tags.netbeans;

import java.io.InputStream;
import java.util.jar.Manifest;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ExamineManifestTagTest extends TestCase {

    private ExamineManifestTag tag;
    
    protected void setUp() throws Exception {
        tag = new ExamineManifestTag();
    }

    protected void tearDown() throws Exception {
        tag = null;
    }

    public void testNetbeansModule1() throws Exception {
        InputStream str = getClass().getResourceAsStream("/org/mevenide/tags/netbeans/nb1.mf");
        Manifest man = new Manifest(str);
//        Attributes attrs = man.getMainAttributes();
//        Iterator it = attrs.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry entry = (Map.Entry)it.next();
//            System.out.println("key=" + entry.getKey() + "-");
//            System.out.println("value=" + entry.getValue());
//        }
//        System.out.println("" + attrs);
        tag.resetExamination();
        tag.processManifest(man);
        assertEquals(true, tag.isNetbeansModule);
        assertEquals(true, tag.isLocalized);
        assertEquals("1.0.1", tag.specVersion);
        assertEquals("200307302351", tag.implVersion);
        assertEquals("org.openide.io > 1.0", tag.moduleDeps);
        assertEquals("org.openide.execution", tag.module);        
    }

    public void testNetbeansModule2() throws Exception {
        InputStream str = getClass().getResourceAsStream("/org/mevenide/tags/netbeans/nb2.mf");
        Manifest man = new Manifest(str);
//        Attributes attrs = man.getMainAttributes();
//        Iterator it = attrs.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry entry = (Map.Entry)it.next();
//            System.out.println("key=" + entry.getKey() + "-");
//            System.out.println("value=" + entry.getValue());
//        }
//        System.out.println("" + attrs);
        tag.resetExamination();
        tag.processManifest(man);
        assertEquals(true, tag.isNetbeansModule);
        assertEquals(true, tag.isLocalized);
        assertEquals("org/netbeans/modules/xml/core/resources/Bundle.properties", tag.locBundle);
        assertEquals("0.8.1", tag.specVersion);
        assertEquals("XMLr35", tag.implVersion);
        assertEquals("org.netbeans.api.xml/1 > 1.1.1,org.openide.execution > 1.0,org.openide.io > 1.0", tag.moduleDeps);
        assertEquals("org.netbeans.modules.xml.core/2", tag.module);
    }

    public void testJar1() throws Exception {
        InputStream str = getClass().getResourceAsStream("/org/mevenide/tags/netbeans/no_nb1.mf");
        Manifest man = new Manifest(str);
        tag.resetExamination();
        tag.processManifest(man);
        assertEquals(false, tag.isNetbeansModule);
        assertEquals(false, tag.isLocalized);
        assertEquals(null, tag.locBundle);
        assertEquals("1.3", tag.specVersion);
        assertEquals("1.3", tag.implVersion);
        assertEquals(null, tag.moduleDeps);
        assertEquals("org.dom4j", tag.module);
    }
    
    public void testJar2() throws Exception {
        InputStream str = getClass().getResourceAsStream("/org/mevenide/tags/netbeans/no_nb2.mf");
        Manifest man = new Manifest(str);
        tag.resetExamination();
        tag.processManifest(man);
        assertEquals(false, tag.isNetbeansModule);
        assertEquals(false, tag.isLocalized);
        assertEquals(null, tag.locBundle);
        assertEquals(null, tag.specVersion);
        assertEquals(null, tag.implVersion);
        assertEquals(null, tag.moduleDeps);
        assertEquals("org.apache.commons.jelly", tag.module);
    }    
}
