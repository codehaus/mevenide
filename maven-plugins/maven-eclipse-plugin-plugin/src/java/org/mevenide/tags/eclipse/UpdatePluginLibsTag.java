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

import org.apache.maven.project.Project;
import org.apache.maven.repository.Artifact;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.mevenide.tags.AbstractMevenideTag;
import org.mevenide.tags.InvalidDirectoryException;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class UpdatePluginLibsTag extends AbstractMevenideTag {
    private static final Log log = LogFactory.getLog(UpdatePluginLibsTag.class);

    private static final String RUNTIME_ELEM = "runtime";
    private static final String LIBRARY_ELEM = "library";
    private static final String EXPORT_ELEM = "export";
    private static final String PACKAGES_ELEM = "packages";
    private static final String NAME_ATTR = "name";
    private static final String PREFIXES_ATTR = "prefixes";

    private static final String DESCRIPTOR_DIR_PROPERTY = "maven.eclipse.plugin.src.dir";
    private static final String BUNDLE_DEPENDENCY_PROPERTY = "eclipse.plugin.bundle";
    private static final String PLUGIN_FILENAME = "plugin.xml";
    
    private static final String BUNDLE_LIB_DIR = "maven.eclipse.plugin.bundle.lib.dir";

    private static final String TRUE = "true";
    private static final String FS_SEPARATOR = "/";
    private static final String STAR_PATTERN = "*";


    private Project pom;
    private Document descriptor;

    private String bundledLibraryDir; 

    
    public void doTag(XMLOutput arg0) throws Exception {
        
		setUpDescriptor();
        
		bundledLibraryDir = (String) context.getVariable(BUNDLE_LIB_DIR);

        List artifacts = pom.getArtifacts();

        for (int i = 0; i < artifacts.size(); i++) {
            Artifact artifact = (Artifact) artifacts.get(i);
            updateDescriptor(artifact);
        }
        
    }
    
    private void updateDescriptor(Artifact artifact) throws InvalidDirectoryException {
        //@TODO
        boolean shouldBundleDependency = TRUE.equals(artifact.getDependency().getProperty(BUNDLE_DEPENDENCY_PROPERTY));
        log.debug(artifact.getDependency() + " > ${eclipse.plugin.bundle} = " + shouldBundleDependency);
        
		if ( shouldBundleDependency ) {
	        if ( getBundleMode().equals("dist") ) {
				updateRequires(artifact);
	        }
	        else {
				updateRuntime(artifact);            
	        }
		}

    }

	 /**
	 * update $plugin/requires with artifact if necessary
     */    
	private void updateRequires(Artifact artifact) {
		//@TODO
	}

    /**
	 * update $plugin/runtime with artifact if necessary
     */    
	private void updateRuntime(Artifact artifact) throws InvalidDirectoryException {
		Element pluginElem = descriptor.getRootElement();

        if ( new File(bundledLibraryDir).exists() ) {
        	throw new InvalidDirectoryException(bundledLibraryDir, true, BUNDLE_LIB_DIR); 
        }

		assertRuntimePresent();
		
        if ( !isLibraryDeclared(artifact) ) {
        	addRuntimeLibrary(artifact);
        }

    }

	/**
	 * affectively add library derived from artifact to runtime element
     */
	private void addRuntimeLibrary(Artifact artifact) {
        Element artifactLibraryElem = new Element(LIBRARY_ELEM);
        artifactLibraryElem.setAttribute(NAME_ATTR, bundledLibraryDir + FS_SEPARATOR + new File(artifact.getPath()).getName());
        if ( shouldExport(artifact) ) {
        	Element exportElem = new Element(EXPORT_ELEM);  
        	artifactLibraryElem.setAttribute(NAME_ATTR, STAR_PATTERN);
        }
        if ( getPackagesPrefixes(artifact) != null && !getPackagesPrefixes(artifact).trim().equals("") ) {
        	Element packagesElem = new Element(PACKAGES_ELEM);
        	packagesElem.setAttribute(PREFIXES_ATTR, getPackagesPrefixes(artifact).trim());
        }
    }

    /**
	 * assert runtime element is present. add it if necessary
 	 */
	private void assertRuntimePresent() {
		Element pluginElem = descriptor.getRootElement();
        
        Element runtimeElem = pluginElem.getChild(RUNTIME_ELEM);
        if ( runtimeElem == null ) {
            runtimeElem = new Element(RUNTIME_ELEM);
            pluginElem.addContent(runtimeElem);
        }
    }

	/**
	 * check wether artifact.dependency is already declared in plugin descriptor
	 */
    private boolean isLibraryDeclared(Artifact artifact) {
		Element runtimeElem = descriptor.getRootElement().getChild(RUNTIME_ELEM);
        List libraryElems = runtimeElem.getChildren(LIBRARY_ELEM);
        boolean isDependencyPresent = false;
        for (int i = 0; i < libraryElems.size(); i++) {
            Element library = (Element) libraryElems.get(i);
            String libraryName = library.getAttributeValue(NAME_ATTR);
            if ( libraryName != null && libraryName.indexOf(artifact.getPath()) >= 0 ) {
                isDependencyPresent = true;
                break;
            }
        }
        return isDependencyPresent;
    }

    private String getBundleMode() {
		String bundleMode = (String) context.getVariable("maven.eclipse.plugin.build.mode");
		return bundleMode == null ? "" : bundleMode.trim();
	}

    /**
	 * @todo not sure about the format : dependency property ? csv project property ?
	 */
	private boolean shouldExport(Artifact artifact) {
		return true;
	}

	/**
	 * @todo not sure about the format : dependency property ? project property ?
	 */
	private String getPackagesPrefixes(Artifact artifact) {
		return null;
	}

    private void outputDescriptor() {
        //@TODO
    }
    
    private void setUpDescriptor() throws JDOMException, IOException {
        String descriptorDirectory = (String) context.getVariable(DESCRIPTOR_DIR_PROPERTY);
        File descriptorPath = new File(descriptorDirectory, PLUGIN_FILENAME).getCanonicalFile(); 
        
        log.debug(descriptorPath);
        
        descriptor = new SAXBuilder().build(descriptorPath);

		validateDescriptor();
    }

	private void validateDescriptor() {
		//@TODO
	}

    public Project getPom() {
        return pom;
    }
    
    public void setPom(Project pom) throws MissingAttributeException {
        checkAttribute(pom, "pom");
        this.pom = pom;
    }
}
