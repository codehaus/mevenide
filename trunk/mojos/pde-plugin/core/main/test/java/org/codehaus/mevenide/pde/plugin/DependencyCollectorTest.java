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
package org.codehaus.mevenide.pde.plugin;

import java.io.File;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.codehaus.plexus.ArtifactEnabledPlexusTestCase;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyCollectorTest extends ArtifactEnabledPlexusTestCase {

    private PluginDependencyCollector collector;
    private MavenProject project;
    private MavenProjectBuilder builder;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        File basedirFile = new File(getClass().getResource("/it.v3").getFile());
        
        builder = (MavenProjectBuilder) lookup( MavenProjectBuilder.ROLE );
        project = builder.build(new File(basedirFile, "project.xml"));
        collector = new PluginDependencyCollector(basedirFile.getAbsolutePath(), "lib", project);
        
    }

    protected void tearDown() throws Exception {
        collector = null;
        builder = null;
    }
    
    public void testCollect() throws Exception {
        collector.collect();
    }
}
