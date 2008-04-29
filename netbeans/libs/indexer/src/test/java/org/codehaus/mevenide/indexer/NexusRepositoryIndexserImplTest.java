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

package org.codehaus.mevenide.indexer;

import java.util.regex.Pattern;
import junit.framework.TestCase;

/**
 *
 * @author mkleint
 */
public class NexusRepositoryIndexserImplTest extends TestCase {
    
    public NexusRepositoryIndexserImplTest(String testName) {
        super(testName);
    }            
    
    public void testPattern() throws Exception {
        int patter = Pattern.MULTILINE + Pattern.DOTALL;
        System.out.println("pattern=" + patter);
        Pattern patt = Pattern.compile(".*/HttpRequest$.*", patter);
        String str = "javax/servlet/HttpRequest";
        assertTrue(patt.matcher(str).matches());
        str = "javax/servlet/HttpRequest\nnextline";
        assertTrue(patt.matcher(str).matches());
        str = "firstline\njavax/servlet/HttpRequest\nnextline";
        assertTrue(patt.matcher(str).matches());
        str = "firstline\njavax/servlet/HttpRequestImpl\nnextline";
        assertFalse(patt.matcher(str).matches());
        str = "firstline\njavax/servlet/MyHttpRequest\nnextline";
        assertFalse(patt.matcher(str).matches());
    }

}
