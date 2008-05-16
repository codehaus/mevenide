/* ==========================================================================
 * Copyright 2008 Mevenide Team
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
package org.codehaus.mevenide.netbeans.output;

import junit.framework.*;

/**
 *
 * @author  Anuradha G (anuradha@codehaus.org)
 * 
 * Test case for issue http://jira.codehaus.org/browse/MEVENIDE-637
 */
public class MEVENIDE637Test extends TestCase {

    public MEVENIDE637Test(java.lang.String testName) {
        super(testName);
    }
   
    public static Test suite() {
        TestSuite suite = new TestSuite(MEVENIDE637Test.class);
        return suite;
    }



    public void testSeparatorSplit() {
         String aString="Some text \n a and some more .. \n and more..";//linux and unix
         
         String[] strs = aString.split("\\r |\\n"); //NOI18N
         
         assertEquals(strs.length, 3);
         
         
         aString="Some text \r a and some more .. \r and more..";//Mac
         
         strs = aString.split("\\r |\\n"); //NOI18N
         
         assertEquals(strs.length, 3);
         
         aString="Some text \r\n a and some more .. \r\n and more..";//Windows
         
         strs = aString.split("\\r |\\n"); //NOI18N
         
         assertEquals(strs.length, 3);
         
         //MEVENIDE-637
         aString="\r\n\nMojo: \n\n  org.apache.maven.plugins:maven-compiler-plugin:2.0.2:compile" +
                 "\n\nFAILED for project:" +
                 "\n\n  example:ExampleProject:jar:1.0-SNAPSHOT\n\nReason:\n\nC:" +
                 "\\ExampleProject\\src\\main\\java\\example\\App.java:[11,8] cannot find symbol" +
                 "\n\nsymbol  : class MyObject\n\nlocation: class example.App";
         strs = aString.split("\\r |\\n"); //NOI18N
         System.out.println("AAA:"+strs.length);
         assertEquals(strs.length, 17);
    }
}
