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

package org.codehaus.mevenide.netbeans.embedder;

import java.util.List;
import junit.framework.TestCase;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.extension.ExtensionManager;
import org.apache.maven.extension.ExtensionManagerException;
import org.apache.maven.model.Extension;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;

/**
 *
 * @author mkleint
 */
public class NbExtensionManagerTest extends TestCase {
    
    public NbExtensionManagerTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     */
    public void testExtensionManagerMethod() throws Exception {
        new Ext();
    }
    
    private class Ext implements ExtensionManager {

        public void addExtension(Extension arg0, MavenProject arg1, MavenExecutionRequest arg2) throws ExtensionManagerException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void registerWagons() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void addExtension(Extension arg0, Model arg1, List arg2, MavenExecutionRequest arg3) throws ExtensionManagerException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void addPluginAsExtension(Plugin arg0, Model arg1, List arg2, MavenExecutionRequest arg3) throws ExtensionManagerException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }

}
