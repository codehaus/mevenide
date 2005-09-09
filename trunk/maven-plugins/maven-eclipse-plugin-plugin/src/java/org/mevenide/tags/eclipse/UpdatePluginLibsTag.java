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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.commons.jelly.JellyTagException;

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
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mevenide.tags.AbstractMevenideTag;
import org.mevenide.tags.InvalidDirectoryException;

/**
 * update the Eclipse plugin descriptor with project dependency informations  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class UpdatePluginLibsTag extends AbstractMevenideTag {
	/**
	 * @todo this tag should surely take an Artifact as attribute. 
	 * Artifacts iteration would then be performed within plugin.jelly
	 */
	
    private static final Log log = LogFactory.getLog(UpdatePluginLibsTag.class);

    //xml elements
    private static final String RUNTIME_ELEM = "runtime";
    private static final String REQUIRES_ELEM = "requires";
    private static final String LIBRARY_ELEM = "library";
    private static final String EXPORT_ELEM = "export";
    private static final String IMPORT_ELEM = "import";
    private static final String PACKAGES_ELEM = "packages";
    private static final String NAME_ATTR = "name";
    private static final String PREFIXES_ATTR = "prefixes";
    private static final String PLUGIN_ATTR = "plugin";
    
    //context variable
	private static final String TEMP_DIR_PROPERTY = "maven.eclipse.plugin.temp.dir";
    private static final String BUNDLE_LIB_DIR = "maven.eclipse.plugin.bundle.lib.dir";
    private static final String DESCRIPTOR_DIR_PROPERTY = "maven.eclipse.plugin.src.dir";
    private static final String BUNDLE_DEPENDENCY_PROPERTY = "eclipse.plugin.bundle";
	private static final String BUILD_MODE_PROPERTY = "maven.eclipse.plugin.build.mode";
    private static final String DEFAULT_EXPORT_PROPERTY = "maven.eclipse.plugin.export.default";
	private static final String DEPENDENCY_PREFIXES_PROPERTY = "eclipse.plugin.packages";
	private static final String DEPENDENCY_EXPORT_PROPERTY = "eclipse.plugin.export";

	private static final String PLUGIN_FILENAME = "plugin.xml";
    private static final String TRUE = "true";
    private static final String FS_SEPARATOR = "/";
    private static final String STAR_PATTERN = "*";
	private static final String SHOULD_BUNDLE = "bundle";
	
	
    /** the project descriptor of the Eclipse plugin under construction **/
    private Project pom;
    
    private Document descriptor;

    private String bundledLibraryDir; 

    /*
     * (non-Javadoc)
     * @see org.apache.commons.jelly.Tag#doTag(org.apache.commons.jelly.XMLOutput)
     */
    public void doTag(XMLOutput arg0) throws MissingAttributeException, JellyTagException {
        try {
    		setUpDescriptor();
        
    		List artifacts = pom.getArtifacts();
    		
    		if ( artifacts != null ) {
	            for (int i = 0; i < artifacts.size(); i++) {
	                Artifact artifact = (Artifact) artifacts.get(i);
	                updateDescriptor(artifact);
	            }
    		}
        
            outputDescriptor();
        } catch (Exception exc) {
            throw new JellyTagException(exc);
        }
        
    }
    
    /**
     * update descriptor with artifact if necessary. dependeing on the mode the plugin is run 
     * the artifact can be pushed aither as runtime library or required plugin. 
     */ 
    void updateDescriptor(Artifact artifact) throws InvalidDirectoryException {
        boolean shouldBundleDependency = TRUE.equals(artifact.getDependency().getProperty(BUNDLE_DEPENDENCY_PROPERTY));
        log.debug(artifact.getDependency() + " > ${eclipse.plugin.bundle} = " + shouldBundleDependency);
       
		if ( shouldBundleDependency ) {
	        if ( !getBundleMode().equals(SHOULD_BUNDLE) ) {
				updateRequires(artifact);
	        }
	        else {
				updateRuntime(artifact);            
	        }
		}

    }

	/**
	 * update $plugin/requires with artifact if needed
     */    
	void updateRequires(Artifact artifact) {
	    assertRequiresPresent();
	    
	    if ( !isRequiredPluginDeclared(artifact) ) {
	        addRequiresPlugin(artifact);
	    }
	}

    /**
	 * update $plugin/runtime with artifact if needed
     */    
	void updateRuntime(Artifact artifact) throws InvalidDirectoryException {
		if ( new File(bundledLibraryDir).isAbsolute() ) {
        	throw new InvalidDirectoryException(bundledLibraryDir, true, BUNDLE_LIB_DIR); 
        }

		assertRuntimePresent();
		
        if ( !isLibraryDeclared(artifact) ) {
        	addRuntimeLibrary(artifact);
        }

    }
	
	/**
	 * effectively add plugin derived from artifact to required plugins
	 *
   	 * @pre requires element is present in plugin descriptor
     */  
	void addRequiresPlugin(Artifact artifact) {
	    Element importPluginElem = new Element(IMPORT_ELEM);
	    importPluginElem.setAttribute(PLUGIN_ATTR, getPluginName(artifact));
	    Element pluginElem = descriptor.getRootElement();
	    pluginElem.getChild(REQUIRES_ELEM).addContent(importPluginElem);
	}
	
	/**
	 * generated plugin resides in ECLIPSE_HOME/plugins/${dependency.groupId}
	 */
	 String getPluginName(Artifact artifact) {
	    return artifact.getDependency().getGroupId();
	}
	
	/**
	 * effectively add library derived from artifact to runtime element
	 *
   	 * @pre runtime element is present in plugin descriptor
     */
	 void addRuntimeLibrary(Artifact artifact) {
        Element artifactLibraryElem = new Element(LIBRARY_ELEM);
        artifactLibraryElem.setAttribute(NAME_ATTR, bundledLibraryDir + FS_SEPARATOR + new File(artifact.getPath()).getName());
        if ( getExport(artifact) != null && !getExport(artifact).trim().equals("")) {
        	Element exportElem = new Element(EXPORT_ELEM);  
        	exportElem.setAttribute(NAME_ATTR, STAR_PATTERN);
        	artifactLibraryElem.addContent(exportElem);
        }
        if ( getPackagesPrefixes(artifact) != null && !getPackagesPrefixes(artifact).trim().equals("") ) {
        	Element packagesElem = new Element(PACKAGES_ELEM);
        	packagesElem.setAttribute(PREFIXES_ATTR, getPackagesPrefixes(artifact).trim());
        	artifactLibraryElem.addContent(packagesElem);
        }
        Element pluginElem = descriptor.getRootElement();
	    pluginElem.getChild(RUNTIME_ELEM).addContent(artifactLibraryElem);
    }

    /**
	 * assert runtime element is present. add it if necessary
 	 */
	 void assertRuntimePresent() {
	    Element pluginElem = descriptor.getRootElement();
        
        Element runtimeElem = pluginElem.getChild(RUNTIME_ELEM);
        if ( runtimeElem == null ) {
            runtimeElem = new Element(RUNTIME_ELEM);
            pluginElem.addContent(runtimeElem);
        }
    }
	
	/**
	 * assert requires element is present. add it if necessary
	 */
	 void assertRequiresPresent() {
	    Element pluginElem = descriptor.getRootElement();
	    
	    Element requiresElem = pluginElem.getChild(REQUIRES_ELEM);
	    if ( requiresElem == null ) {
	        requiresElem = new Element(REQUIRES_ELEM);
	        pluginElem.addContent(requiresElem);
	    }
	}
	
	/**
	 * check wether artifact.dependency is already declared in plugin descriptor as a plugin required dependency
	 */
	 boolean isRequiredPluginDeclared(Artifact artifact) {
	    Element requiresElem = descriptor.getRootElement().getChild(REQUIRES_ELEM);
	    List importElems = requiresElem.getChildren(IMPORT_ELEM);
	    boolean isRequiredPluginPresent = false;
	    for (int i = 0; i < importElems.size(); i++) {
	        Element theImport = (Element) importElems.get(i);
	        String pluginName = theImport.getAttributeValue(PLUGIN_ATTR);
	        if ( pluginName != null && pluginName.equals(artifact.getDependency().getGroupId()) ) {
	            isRequiredPluginPresent = true;
	            break;
	        }
	    }
	    return isRequiredPluginPresent;
	}
	
	/**
	 * check wether artifact.dependency is already declared in plugin descriptor as plugin runtime library
	 */
     boolean isLibraryDeclared(Artifact artifact) {
		Element runtimeElem = descriptor.getRootElement().getChild(RUNTIME_ELEM);
        List libraryElems = runtimeElem.getChildren(LIBRARY_ELEM);
        boolean isDependencyPresent = false;
        for (int i = 0; i < libraryElems.size(); i++) {
            Element library = (Element) libraryElems.get(i);
            String libraryName = library.getAttributeValue(NAME_ATTR);
            if ( libraryName != null && libraryName.indexOf(new File(artifact.getPath()).getName()) >= 0 ) {
                isDependencyPresent = true;
                break;
            }
        }
        return isDependencyPresent;
    }

     String getBundleMode() {
		String bundleMode = (String) context.getVariable(BUILD_MODE_PROPERTY);
		return bundleMode == null ? "" : bundleMode.trim();
	}

    /**
	 * @return the user defined runtime/library/export child value
	 */
	 String getExport(Artifact artifact) {
	    String exportPattern = artifact.getDependency().getProperty(DEPENDENCY_EXPORT_PROPERTY);
	    if ( exportPattern == null ) {
	    	exportPattern = (String) context.getVariable(DEFAULT_EXPORT_PROPERTY);
	    }
		return exportPattern;
	}

	/**
	 * @return the user defined runtime/library/export value
	 */
	 String getPackagesPrefixes(Artifact artifact) {
	    String prefixes = artifact.getDependency().getProperty(DEPENDENCY_PREFIXES_PROPERTY);
		return prefixes;
	}

	/**
     * output plugin descriptor to temp directory.
 	 *  
     * @post outputted descriptor has not been filtered yet
     * @see {@link setUpDescriptor()}
     */
     void outputDescriptor() throws Exception {
     	XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		
		String tempDir = (String) context.getVariable(TEMP_DIR_PROPERTY);
		
		FileOutputStream fos = new FileOutputStream(new File(tempDir, PLUGIN_FILENAME));
		outputter.output(descriptor, fos);
		
		try {
			fos.close();
		} 
		catch (IOException e) {
			log.error("Unable to close handle : " + PLUGIN_FILENAME);
		}
    }
    
	/**
     * setup descriptor from the plugin descriptor template, so dependencies will be refreshed at each build 
     */
     void setUpDescriptor() throws JDOMException, IOException {
        String descriptorDirectory = (String) context.getVariable(DESCRIPTOR_DIR_PROPERTY);
        File descriptorPath = new File(descriptorDirectory, PLUGIN_FILENAME).getCanonicalFile(); 
        
        log.debug(descriptorPath);
        
        descriptor = new SAXBuilder().build(descriptorPath);

		validateDescriptor();
		
		bundledLibraryDir = (String) context.getVariable(BUNDLE_LIB_DIR);
    }

	 void validateDescriptor() {
		//@TODO
	}

    public Project getPom() {
        return pom;
    }
    
    /** the project descriptor of the Eclipse plugin under construction **/
    public void setPom(Project pom) throws MissingAttributeException {
        checkAttribute(pom, "pom");
        this.pom = pom;
    }
    
    Document getDescriptor() {
        return descriptor;
    }
}
