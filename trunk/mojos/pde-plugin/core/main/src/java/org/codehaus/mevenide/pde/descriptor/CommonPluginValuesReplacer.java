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
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.pde.resources.Messages;
import org.codehaus.mevenide.pde.version.VersionAdapter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class CommonPluginValuesReplacer {
    
    /** plugin.xml File */
    private File pluginDescriptor;
    
    /** pom from which the replacement values will be extracted */
    private MavenProject project;
    
    /** libraryFolder library destination folder - f.i. lib */
    private String libraryFolder;
    
	/** list of already added dependencies - used to avoid duplicate libs in plugin descriptor */
    private List addedLibraries = new ArrayList();
    
	/** indicates if the primary artifact should marked as exported in the plugin descriptor */
	private boolean shouldExportArtifact; 
	
	/** artifactName referencing the primary artifact */
	private String artifactName;
	
    /**
     * @param basedir plugin.xml parent directory
     * @param project maven project on which to operate
     * @param libraryFolder library destination folder - f.i. lib
     */
    public CommonPluginValuesReplacer(String basedir, MavenProject project, String libraryFolder) throws ReplaceException {
        this.pluginDescriptor = new File(basedir, "plugin.xml");
        if ( !pluginDescriptor.exists() ) {
            throw new ReplaceException(Messages.get("ValuesReplacer.CannotFindDescriptor", basedir));
        }
        this.project = project;
        if ( libraryFolder == null ) {
            libraryFolder = "lib"; 
        }
        this.libraryFolder = StringUtils.stripEnd(libraryFolder.replaceAll("\\\\", "/"), "/");
    }
    
    /**
     * try to replace common values 
     */
    public void replace() throws ReplaceException {
        Element pluginElement = null;
        Document doc = null;
        
        try {
            doc = new SAXBuilder().build(pluginDescriptor);
            pluginElement = doc.getRootElement();
        }
        catch (Exception e) {
            throw new ReplaceException(Messages.get("ValuesReplacer.CannotReplaceValues"), e);
        }
        
        replacePluginAttributes(pluginElement);
        updateDependencies(pluginElement);
        
        try {
            XMLOutputter outputter = new XMLOutputter();
			outputter.setFormat(org.jdom.output.Format.getPrettyFormat());
            outputter.output(doc, new FileWriter(pluginDescriptor));
        }
        catch (Exception e) {
            throw new ReplaceException(Messages.get("ValuesReplacer.CannotReplaceValues"), e);
        }
    }

	private void detach(Element kid) {
		if (kid.getParent() != null) {
			kid.getParent().removeContent(kid);
		}
	}
	
    private void updateDependencies(Element pluginElement) throws ReplaceException {
        Element runtime = pluginElement.getChild("runtime");
        
        if ( runtime != null ) {
            runtime.detach();
			//detach(runtime);
        }
        runtime = new Element("runtime");

        Element requires = pluginElement.getChild("requires");
        if ( requires == null ) {
            requires = new Element("requires");
        }
        else {
            requires.detach();
			//detach(requires);
        }
        
        List dependencies = project.getDependencies();
        
        boolean requiresUpdated = false;
        boolean runtimeUpdated = false;
        
        for (Iterator it = dependencies.iterator(); it.hasNext();) {
            Dependency dependency = (Dependency) it.next();
            Properties properties = dependency.getProperties();
            if ( !"true".equals(properties.getProperty("maven.pde.exclude")) && 
                 !"true".equals(properties.getProperty("maven.pde.requires")) ) {
		        addRuntimeLibrary(runtime, dependency);
		        runtimeUpdated = true;
            }
            if ( "true".equals(properties.getProperty("maven.pde.requires")) ) {
	            requiresUpdated = requiresUpdated | updateRequires(requires, dependency);
        	}
        }
        
		runtimeUpdated |= addThisDependency(runtime); 
			
        if ( runtimeUpdated ) {
            pluginElement.addContent(runtime);
        }
        
        pluginElement.addContent(requires);
    	
    }
	
	private boolean addThisDependency(Element runtime) {
		String thisDependencyName = getArtifactName();
		boolean shouldUpdate = true;
		List libraries = runtime.getChildren("library");
		for ( int u = 0; u < libraries.size(); u++ ) {
			Element library = (Element) libraries.get(u);
			if ( library.getText().equals(thisDependencyName) ) {
				shouldUpdate = false;
				break;
			}
		}
		if ( shouldUpdate ) {
			Element library = new Element("library");
			library.setAttribute("name", thisDependencyName);
			if ( shouldExportArtifact ) {
				exportLibrary(library);
			}
			runtime.addContent(library);
		}
		return shouldUpdate;
	}

	private void exportLibrary(Element library) {
		Element export = new Element("export");
		export.setAttribute("name", "*");
		library.addContent(export);
	}
	
    private void addRuntimeLibrary(Element runtime, Dependency dependency) throws ReplaceException {
        Properties properties = dependency.getProperties();
        Element library = new Element("library");
        
        String libraryName = dependency.getArtifact();
        
        if ( !addedLibraries.contains(libraryName) ) {
	        library.setAttribute("name", libraryFolder + "/" + libraryName);
	        if ( !"false".equals(properties.getProperty("maven.pde.export")) ) {
				exportLibrary(library);
	        }
	        if ( properties.getProperty("maven.pde.package") != null ) {
	            Element packageElement = new Element("package");
	            packageElement.setAttribute("prefixes", properties.getProperty("maven.pde.package"));
	            library.addContent(packageElement);
	        }
	        runtime.addContent(library);
	        addedLibraries.add(libraryName);
        }
    }

    private boolean updateRequires(Element requires, final Dependency dependency) {
        boolean updated = false;
        List children = requires.getChildren();
        List imports = new ArrayList();
        for (Iterator iter = children.iterator(); iter.hasNext();) {
            Element element = (Element) iter.next();
            String dependencyId = dependency.getId().replaceAll(":", ".");
            if ( dependencyId.equals(element.getAttributeValue("plugin")) ) {
                imports.add(element);
            }
        }
        if ( imports.size() == 0 ) {
            Element importElement = new Element("import");
            importElement.setAttribute("plugin", dependency.getId().replaceAll(":", "."));
            requires.addContent(importElement);
            updated = true;
        }
        return updated;
    }
    

    private void replacePluginAttributes(Element pluginElement) {
        //@todo : how to most accurately compute id ?
        pluginElement.setAttribute("id", (project.getPackage() != null ? project.getPackage() : project.getGroupId() + "." + project.getArtifactId()).replaceAll("-", ".")); //id="org.mevenide.ui" 
        pluginElement.setAttribute("name", project.getName()); //name="Mevenide UI"
        pluginElement.setAttribute("version", new VersionAdapter().adapt(project.getVersion())); //version="0.3.0" 
        pluginElement.setAttribute("provider-name", project.getOrganization().getName()); //provider-name="The Codehaus"
        //no replacement for class attribute
    }
	
	public void shouldExportArtifact(boolean export) { shouldExportArtifact = export; }
	public void setArtifactName(String name) { this.artifactName = name; }
    
	private String getArtifactName() {
		if ( artifactName == null || artifactName.trim().length() == 0 ){
			String artifactId = project.getArtifactId();
			String version = project.getVersion();
			String thisDependencyName = artifactId + "-" + version + ".jar";
			return thisDependencyName;
		}
		else {
			return artifactName + ".jar";
		}
	}
}
 