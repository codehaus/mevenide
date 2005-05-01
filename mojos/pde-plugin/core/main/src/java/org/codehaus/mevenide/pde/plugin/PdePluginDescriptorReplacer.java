package org.codehaus.mevenide.pde.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.pde.descriptor.AbstractPdeDescriptorValuesReplacer;
import org.codehaus.mevenide.pde.descriptor.ReplaceException;
import org.codehaus.plexus.util.StringUtils;
import org.jdom.Element;

public class PdePluginDescriptorReplacer extends AbstractPdeDescriptorValuesReplacer {
	
	/** indicates if the primary artifact should marked as exported in the plugin descriptor */
	protected boolean shouldExportArtifact; 
	
    /** default library destination folder - f.i. lib */
	protected String libraryFolder;
    
	/** list of already added dependencies - used to avoid duplicate libs in plugin descriptor */
	protected List addedLibraries = new ArrayList();
	
	/** true if project has sources */
	protected boolean sourcesPresent;
	
	/** indicates if single jar'd should be generated */
	protected boolean singleJar;
	
	/** name of jar containing artifact classes */
	protected String classesJarName;
	
	
	public PdePluginDescriptorReplacer(String basedir, MavenProject project, String libraryFolder) throws ReplaceException {
		super(basedir, project);
		if ( libraryFolder == null ) {
            libraryFolder = "lib"; 
        }
        this.libraryFolder = StringUtils.stripEnd(libraryFolder.replaceAll("\\\\", "/"), "/");
	}

	protected String getDescriptorName() {
		return "plugin.xml";
	}
	
    protected void replace(Element rootElement) throws ReplaceException {
        Element runtime = rootElement.getChild("runtime");
        
        if ( runtime != null ) {
            runtime.detach();
			//detach(runtime);
        }
        runtime = new Element("runtime");

        Element requires = rootElement.getChild("requires");
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
        
		if ( sourcesPresent ) {
			runtimeUpdated |= addThisDependency(runtime);
		}
			
        if ( runtimeUpdated ) {
            rootElement.addContent(runtime);
        }
        
        rootElement.addContent(requires);
    	
    }
	
	private boolean addThisDependency(Element runtime) {
		String thisDependencyName;
		if ( !singleJar ) {
			thisDependencyName = classesJarName;
		}
		else {
			thisDependencyName = ".";
		}
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
			String targetPath = properties.getProperty("maven.pde.targetPath");
			String folder = org.codehaus.plexus.util.StringUtils.isEmpty(targetPath) ? libraryFolder : targetPath;
	        library.setAttribute("name", folder + "/" + libraryName);
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
        boolean alreadyPresent = false;
		Properties props = dependency.getProperties();
		
		String pluginName = null;
		
		if ( !StringUtils.isEmpty(dependency.getArtifact()) ) {
			pluginName = StringUtils.split(dependency.getArtifact(), "_")[0];
		}
		else {
			pluginName = dependency.getArtifactId();
		}
		if ( props != null && !StringUtils.isEmpty((String) props.getProperty("maven.pde.name")) ) {
			pluginName = (String) props.getProperty("maven.pde.name");
		}
		
        for (Iterator iter = children.iterator(); iter.hasNext();) {
            Element element = (Element) iter.next();
            if ( pluginName.equals(element.getAttributeValue("plugin")) ) {
				alreadyPresent = true;
				break;
            }
        }
        if ( !alreadyPresent ) {
            Element importElement = new Element("import");
            importElement.setAttribute("plugin", pluginName);
            requires.addContent(importElement);
            updated = true;
        }
        return updated;
    }
	
	public void shouldExportArtifact(boolean export) { shouldExportArtifact = export; }

	public void setSingleJar(boolean singleJar) { this.singleJar = singleJar; }
	public void setClassesJarName(String classesJarName) { this.classesJarName = classesJarName; }

	public void setSourcesPresent(boolean sourcesPresent) { this.sourcesPresent = sourcesPresent; }
}
