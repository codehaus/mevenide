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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.lang.BooleanUtils;
import org.apache.maven.jelly.MavenJellyContext;
import org.codehaus.mevenide.pde.PdePluginException;
import org.codehaus.mevenide.pde.converter.ConverterException;
import org.codehaus.mevenide.pde.converter.MavenProjectConverter;
import org.codehaus.mevenide.pde.taglib.PdeTag;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PdePluginTag extends PdeTag {
    
    public void doTag(XMLOutput arg0) throws JellyTagException {
        try {
			boolean pdeEnabled = BooleanUtils.toBoolean(project.getProperty("maven.pde.enabled"));
			if ( pdeEnabled ) {
				createPlugin();
			}
        }
        catch (PdePluginException e) {
            throw new JellyTagException("Unable to build plugin", e);
        }
    }
	
	private void createPlugin() throws PdePluginException, ConverterException {
		String artifactName = getArtifactName();
		
		String destinationFolder = (String) context.getVariable("maven.build.dir");
		
		PdePluginBuilder builder = new PdePluginBuilder();
		configureBuilder(builder);
		
		builder.setLibFolder((String) context.getVariable("maven.pde.libTargetPath"));
		builder.setClassesLocation((String) context.getVariable("maven.build.dest"));
		builder.setCleanLib(BooleanUtils.toBoolean((String) context.getVariable("maven.pde.cleanLib")));
		builder.setProject(new MavenProjectConverter(project, (MavenJellyContext) context).convert());
		
		boolean export = BooleanUtils.toBoolean((String) context.getVariable("maven.pde.export"));
		builder.setExportArtifact(export);
		
		boolean singleJar = BooleanUtils.toBoolean((String) context.getVariable("maven.pde.single"));
		String classesJarName = (String) context.getVariable("maven.final.name") + ".jar";
		
		String classeJarLocation = new File(destinationFolder, classesJarName).getAbsolutePath();
		
		builder.setSingleJar(singleJar);
		if ( !singleJar ) {
			builder.setClassesJarLocation(classeJarLocation);	
		}
		
		builder.setSourcesPresent(BooleanUtils.toBoolean((String) context.getVariable("sourcesPresent")));
		
		builder.build();
	}

	

    
 
}
