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
package org.codehaus.mevenide.pde.descriptor;

import java.io.File;
import junit.framework.TestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.embed.Embedder;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class CommonPluginValuesReplacerTest extends TestCase {

    private CommonPluginValuesReplacer replacer;
    private File basedirFile;
    private MavenProject project;
    
    protected void setUp() throws Exception {
        basedirFile = new File(getClass().getResource("/basedir.replacer").getFile());
        String basedir = basedirFile.getAbsolutePath();
        Embedder embedder = new Embedder();
        ClassWorld classWorld = new ClassWorld("core", this.getClass().getClassLoader());
        embedder.start(classWorld);
        MavenProjectBuilder builder = (MavenProjectBuilder) embedder.lookup(MavenProjectBuilder.ROLE);
        project = builder.build(new File(basedirFile, "project.xml"));
        replacer = new CommonPluginValuesReplacer(basedir, project, "lib");
    }

    protected void tearDown() throws Exception {
        replacer = null;
    }
    
    public void testReplace() throws Exception {
        replacer.replace();
        Document doc = new SAXBuilder().build(new File(basedirFile, "plugin.xml"));
        Element plugin = doc.getRootElement();
        //@todo : assertions...
    }
}
