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
import java.util.List;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.ArtifactEnabledPlexusTestCase;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;


/**  
 * @todo use eclipse property indirection (%value) 
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class CommonPluginValuesReplacerTest extends ArtifactEnabledPlexusTestCase {

    private CommonPluginValuesReplacer replacer;
    private File basedirFile;
    private MavenProject project;
    private MavenProjectBuilder builder;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        builder = (MavenProjectBuilder) lookup(MavenProjectBuilder.ROLE);
        
        replacer = getReplacer("/basedir.replacer");
    }

    private CommonPluginValuesReplacer getReplacer(String basedirName) throws ProjectBuildingException, ReplaceException {
        basedirFile = new File(getClass().getResource(basedirName).getFile());
        String basedir = basedirFile.getAbsolutePath();
        project = builder.build(new File(basedirFile, "project.xml"));
        return new CommonPluginValuesReplacer(basedir, project, "lib");
    }


    protected void tearDown() throws Exception {
        replacer = null;
        basedirFile = null;
        project = null;
        builder = null;
    }
    
    public void testReplace() throws Exception {
        replacer.replace();
        Document doc = new SAXBuilder().build(new File(basedirFile, "plugin.xml"));
        Element plugin = doc.getRootElement();
        
        //this one fails because package is not correctly retrieved
        //assertEquals("org.codehaus.mevenide.pde", plugin.getAttributeValue("id"));
        assertEquals("mevenide.maven.pde.plugin", plugin.getAttributeValue("id"));
        assertEquals("maven pde plugin", plugin.getAttributeValue("name"));
        assertEquals("the codehaus", plugin.getAttributeValue("provider-name"));
        assertEquals("0.1.0", plugin.getAttributeValue("version"));
        
        List requires = plugin.getChild("requires").getChildren("import"); 
        assertEquals(3, requires.size());
        assertEquals("mevenide.requires-test", ((Element) requires.get(0)).getAttributeValue("plugin"));
        
        List libraries = plugin.getChild("runtime").getChildren("library");
        assertEquals(2, libraries.size());
        
        Element lib_0 = (Element) libraries.get(0);
        assertEquals("lib/jdom-1.0.jar", lib_0.getAttributeValue("name"));
        assertNotNull(lib_0.getChild("export"));
        assertEquals("*", lib_0.getChild("export").getAttributeValue("name"));
        assertNotNull(lib_0.getChild("package"));
        assertEquals("org.jdom", lib_0.getChild("package").getAttributeValue("prefixes"));
 
        Element lib_1 = (Element) libraries.get(1);
        assertEquals("lib/commons-lang-2.0.jar", lib_1.getAttributeValue("name"));
        assertNull(lib_1.getChild("export"));
        assertNull(lib_1.getChild("package"));
        
        replacer = getReplacer("/basedir.minreplacer"); 
        replacer.replace();
        doc = new SAXBuilder().build(new File(basedirFile, "plugin.xml"));
        plugin = doc.getRootElement();
        assertNull(plugin.getChild("runtime"));
        assertNull(plugin.getChild("requires"));
    }
}
