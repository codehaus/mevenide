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

package org.mevenide.reports;
import junit.framework.*;
import java.io.File;
import org.mevenide.goals.TestQueryContext;

/**
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public abstract class AbstractResultTest extends TestCase {
    
    protected TestQueryContext context;
    public AbstractResultTest(String testName) {
        super(testName);
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}

    protected void setUp() throws Exception {
        context = new TestQueryContext();
        File rootDir = new File(this.getClass().getResource("/reports").getFile());
        context.addProjectPropertyValue("maven.build.dir", rootDir.getAbsolutePath());
    }

    
}
