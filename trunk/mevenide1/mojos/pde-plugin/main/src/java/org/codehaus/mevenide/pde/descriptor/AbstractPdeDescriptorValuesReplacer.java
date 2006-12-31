package org.codehaus.mevenide.pde.descriptor;

import java.io.File;
import java.io.FileWriter;

import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.pde.resources.Messages;
import org.codehaus.mevenide.pde.version.VersionAdapter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public abstract class AbstractPdeDescriptorValuesReplacer implements IPdeDescriptorValuesReplacer {
	 
	/** plugin.xml File */
    protected File descriptor;
    
    /** pom from which the replacement values will be extracted */
	protected MavenProject project;

	/** artifactName referencing the primary artifact */
	protected String artifactName;
	
	/**
     * @param basedir plugin.xml parent directory
     * @param project maven project on which to operate
     * @param libraryFolder library destination folder - f.i. lib
     */
    public AbstractPdeDescriptorValuesReplacer(String basedir, MavenProject project) throws ReplaceException {
        this.descriptor = new File(basedir, getDescriptorName());
        if ( !descriptor.exists() ) {
            throw new ReplaceException(Messages.get("ValuesReplacer.CannotFindDescriptor", basedir));
        }
        this.project = project;
        
    }
	
	protected abstract String getDescriptorName() ;

	protected AbstractPdeDescriptorValuesReplacer() {
	
	}
	
	protected void detach(Element kid) {
		if (kid.getParent() != null) {
			kid.getParent().removeContent(kid);
		}
	}
	
	public void replace() throws ReplaceException {
        Element rootElement = null;
        Document doc = null;
        
        try {
            doc = new SAXBuilder().build(descriptor);
            rootElement = doc.getRootElement();
        }
        catch (Exception e) {
            throw new ReplaceException(Messages.get("ValuesReplacer.CannotReplaceValues"), e);
        }
        
		replaceCommonElements(rootElement);
        
		replace(rootElement);
        
        try {
            XMLOutputter outputter = new XMLOutputter();
			outputter.setFormat(org.jdom.output.Format.getPrettyFormat());
            outputter.output(doc, new FileWriter(descriptor));
        }
        catch (Exception e) {
            throw new ReplaceException(Messages.get("ValuesReplacer.CannotReplaceValues"), e);
        }
    }
	
	protected void replaceCommonElements(Element rootElement) throws ReplaceException {
		rootElement.setAttribute("id", artifactName.substring(0, artifactName.lastIndexOf('_')));  
        rootElement.setAttribute("name", project.getName()); 
        rootElement.setAttribute("version", new VersionAdapter().adapt(project.getVersion()));  
        rootElement.setAttribute("provider-name", project.getOrganization().getName());
	}

	protected abstract void replace(Element rootElement) throws ReplaceException;
	
	public void setArtifactName(String name) { this.artifactName = name; }

}
