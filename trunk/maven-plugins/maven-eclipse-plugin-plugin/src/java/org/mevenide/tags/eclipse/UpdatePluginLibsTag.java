/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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
package org.mevenide.tags.eclipse;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.repository.Artifact;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.mevenide.tags.AbstractMevenideTag;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class UpdatePluginLibsTag extends AbstractMevenideTag {
    private static final Log log = LogFactory.getLog(UpdatePluginLibsTag.class);
    
    private static final String DESCRIPTOR_DIR_PROPERTY = "maven.eclipse.plugin.src.dir";
    private static final String BUNDLE_PROPERTY_NAME = "eclipse.plugin.bundle";
    private static final String PLUGIN_FILENAME = "plugin.xml";
    private static final String TRUE = "true";
    
    private Project pom;
    private Document descriptor; 
    
    public void doTag(XMLOutput arg0) throws Exception {
        
        setUpDescriptor();
        
        List artifacts = pom.getArtifacts();
        for (int i = 0; i < artifacts.size(); i++) {
            Artifact artifact = (Artifact) artifacts.get(i);
            Dependency dependency = artifact.getDependency();
            if ( TRUE.equals(dependency.getProperty(BUNDLE_PROPERTY_NAME)) ) {
                log.debug("eclipse.plugin.bundle = true");
                //System.err.println("eclipse.plugin.bundle = true");
                updateDescriptor(dependency);
            }
        }
        
    }
    
    private void updateDescriptor(Dependency dependency) {
        //@TODO
    }
    
    private void outputDescriptor() {
        //@TODO
    }
    
    private void setUpDescriptor() throws JDOMException, IOException {
        String descriptorDirectory = (String) context.getVariable(DESCRIPTOR_DIR_PROPERTY);
        File descriptorPath = new File(descriptorDirectory, PLUGIN_FILENAME).getCanonicalFile(); 
        
        log.debug(descriptorPath);
        //System.err.println(descriptorPath);
        
        descriptor = new SAXBuilder().build(descriptorPath);
    }

    public Project getPom() {
        return pom;
    }
    
    public void setPom(Project pom) throws MissingAttributeException {
        checkAttribute(pom, "pom");
        this.pom = pom;
    }
}
