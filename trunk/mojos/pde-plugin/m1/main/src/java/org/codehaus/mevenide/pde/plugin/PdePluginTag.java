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
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.JellyTag;
import org.apache.maven.project.Project;
import org.codehaus.mevenide.pde.PdePluginException;
import org.codehaus.mevenide.pde.archive.Include;
import org.codehaus.mevenide.pde.converter.MavenProjectConverter;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PdePluginTag extends JellyTag {
    
    public void doTag(XMLOutput arg0) throws JellyTagException {
        try {
            PdePluginBuilder builder = new PdePluginBuilder();
            
            builder.setArtifactName((String) getContext().getVariable("maven.final.name"));
            
            File basedir = new File((String) getContext().getVariable("basedir"));
            builder.setBasedir(basedir);
           
            builder.setClassesLocation((String) getContext().getVariable("maven.build.dir"));
            builder.setExcludes((String) getContext().getVariable("maven.pde.excludes"));
            builder.setLibFolder((String) getContext().getVariable("maven.pde.libTargetPath"));
            builder.setProject(new MavenProjectConverter((Project) getContext().getVariable("pom")).convert());
            
            List includes = getCommonIncludes(basedir);
                
            //@todo custom includes
            builder.setIncludes(includes);
            
            builder.build();
        }
        catch (PdePluginException e) {
            throw new JellyTagException("Unable to build plugin", e);
        }
    }

    private List getCommonIncludes(File basedir) {
        Include descriptor = new Include();
        descriptor.setAbsolutePath(new File(basedir, "plugin.xml").getAbsolutePath());
        Include properties = new Include();
        properties.setAbsolutePath(new File(basedir, "plugin.properties").getAbsolutePath());
        Include license = new Include();
        license.setAbsolutePath(new File(basedir, "license.txt").getAbsolutePath());
        
        List includes = new ArrayList();
        includes.add(descriptor);
        includes.add(properties);
        includes.add(license);
        return includes;
    }
 
}
