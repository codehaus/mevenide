package org.codehaus.mevenide.pde.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.pde.PdeConstants;
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
        
        Set artifacts  = project.getArtifacts();
        
        boolean requiresUpdated = false;
        boolean runtimeUpdated = false;
        
        for (Iterator it = artifacts.iterator(); it.hasNext();) {
            Artifact artifact = (Artifact) it.next();
            if ( !Artifact.SCOPE_TEST.equals(artifact.getScope()) ) { 
                if ( !PdeConstants.PDE_TYPE.equals(artifact.getType()) ) { 
                    runtimeUpdated |= addRuntimeLibrary(runtime, artifact);
                }
                else {
                    requiresUpdated |= updateRequires(requires, artifact);
                }
            }
            
        }
        
		if ( sourcesPresent ) {
			runtimeUpdated |= addThisDependency(runtime);
		}
			
        if ( runtimeUpdated ) {
            rootElement.addContent(runtime);
        }
        
        if ( requiresUpdated ) {
            rootElement.addContent(requires);
        }
    	
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
	
    private boolean addRuntimeLibrary(Element runtime, Artifact artifact) throws ReplaceException {
        boolean shouldUpdate = false;
        
        Element library = new Element("library");
        
        String libraryName = artifact.getFile().getName();
        
        if ( !addedLibraries.contains(libraryName) ) {
			String targetPath = libraryFolder;
			String folder = org.codehaus.plexus.util.StringUtils.isEmpty(targetPath) ? libraryFolder : targetPath;
	        library.setAttribute("name", folder + "/" + libraryName);
	        exportLibrary(library);
	        
	        /* Element packageElement = new Element("package");
	        packageElement.setAttribute("prefixes", properties.getProperty("maven.pde.package"));
	        library.addContent(packageElement); */
	        
	        runtime.addContent(library);
	        addedLibraries.add(libraryName);
            
            shouldUpdate = true;
        }
        
        return shouldUpdate;
    }

    private boolean updateRequires(Element requires, final Artifact artifact) {
        boolean updated = false;
        List children = requires.getChildren();
        boolean alreadyPresent = false;
		
		String pluginName = artifact.getArtifactId();
        
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
