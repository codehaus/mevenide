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
package org.codehaus.mevenide.pde.classpath;

import java.io.File;
import java.util.Collection;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.JellyTag;
import org.codehaus.mevenide.pde.PdePluginException;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PdeClasspathTag extends JellyTag {

    private String var;
    
    public void doTag(XMLOutput arg0) throws JellyTagException {
        File basedir = new File((String) getContext().getVariable("basedir"));
        String eclipseHome = (String) getContext().getVariable("maven.pde.eclipseHome");
        
        PluginClasspathResolver resolver = new PluginClasspathResolver(basedir, eclipseHome);
        try {
            Collection artifacts = resolver.extractEclipseDependencies();
            context.setVariable(var, artifacts);
        }
        catch (PdePluginException e) {
            throw new JellyTagException("Unable to extract eclipse dependencies from descriptor", e);
        }    
    }
    
    public String getVar() { return var; }
    public void setVar(String var) { this.var = var; }
}
