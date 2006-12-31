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

package org.mevenide.context;

import junit.framework.*;
import java.io.File;
import org.mevenide.properties.IPropertyResolver;

/**
 * @author  <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 *
 */
public class DefaultProjectContextTest extends TestCase {
    
    public DefaultProjectContextTest(String testName) {
        super(testName);
    }

    protected void setUp() throws java.lang.Exception {
    }

    protected void tearDown() throws java.lang.Exception {
    }

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite(DefaultProjectContextTest.class);
        
        return suite;
    }

    /**
     * Test of doReplaceExtend method, of class org.mevenide.context.DefaultProjectContext.
     */
    public void testDoReplaceExtend() {
        File basedir = new File("sweet");
        IPropertyResolver resolver = new Resolver();
        String result = DefaultProjectContext.doReplaceExtend(basedir, resolver, "hello");
        assertEquals(result, "hello");
        result = DefaultProjectContext.doReplaceExtend(basedir, resolver, "${basedir} yes");
        assertEquals(result, basedir.getAbsolutePath() + " yes");
        result = DefaultProjectContext.doReplaceExtend(basedir, resolver, "${prop.test1} xxx");
        assertEquals(result, "has " + basedir.getAbsolutePath() + " value1 xxx");
        result = DefaultProjectContext.doReplaceExtend(basedir, resolver, "${prop.test2} xx");
        assertEquals(result, "has " + basedir.getAbsolutePath() + " value12 xx");
        result = DefaultProjectContext.doReplaceExtend(basedir, resolver, "yy ${prop.test2} xx ${prop.test3}");
        assertEquals(result, "yy has " +basedir.getAbsolutePath() + " value12 xx has " + basedir.getAbsolutePath() + " value123");
        //fix for MEVENIDE-267
        result = DefaultProjectContext.doReplaceExtend(basedir, resolver, "yy ${prop.test2} xx ${prop.test3 cc");
        assertEquals(result, "yy has " +basedir.getAbsolutePath() + " value12 xx ${prop.test3 cc");
    }

    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    
    private class Resolver implements IPropertyResolver {
        
        public String getResolvedValue(String key) {
            throw new IllegalStateException();
        }

        public String getValue(String key) {
            if ("prop.test1".equals(key)) {
                return "has ${basedir} value1";
            }
            if ("prop.test2".equals(key)) {
                return "${prop.test1}2";
            }
            if ("prop.test3".equals(key)) {
                return "${prop.test2}3";
            }
            return null;
        }

        public void reload() {
            throw new IllegalStateException();
        }

        public String resolveString(String original) {
            throw new IllegalStateException();
        }
        
    }
    
}
