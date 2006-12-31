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
package org.codehaus.mevenide.pde.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mevenide.pde.PdePluginException;
import org.codehaus.mevenide.pde.plugin.PdePluginBuilder;

/**
 * 
 * @goal pde
 * @phase package
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * 
 */
public class PdePluginBuilderMojo extends AbstractMojo {
    
    
    /**
     * in the future will allow to switch builder implementation
     * which will permit to implement different strategioes (2.1, 3.1, MANIFEST-based, etc.)
     */ 
    private String builderClass = "org.codehaus.mevenide.pde.plugin.PdePluginBuilder";
    
    
    
    public void execute() throws MojoExecutionException {
        try {
            PdePluginBuilder builder = getBuilder();
            builder.build();
        } 
        catch (PdePluginException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
       
    }



    private PdePluginBuilder getBuilder() {
        PdePluginBuilder builder = new PdePluginBuilder();
        
        return builder;
    }

}
