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
    protected File pluginDescriptor;
    
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
        this.pluginDescriptor = new File(basedir, "plugin.xml");
        if ( !pluginDescriptor.exists() ) {
            throw new ReplaceException(Messages.get("ValuesReplacer.CannotFindDescriptor", basedir));
        }
        this.project = project;
        
    }
	
	protected AbstractPdeDescriptorValuesReplacer() {
	
	}
	
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
        
		replaceCommonElements(pluginElement);
        
		replace(pluginElement);
        
        try {
            XMLOutputter outputter = new XMLOutputter();
			outputter.setFormat(org.jdom.output.Format.getPrettyFormat());
            outputter.output(doc, new FileWriter(pluginDescriptor));
        }
        catch (Exception e) {
            throw new ReplaceException(Messages.get("ValuesReplacer.CannotReplaceValues"), e);
        }
    }
	
	private void replaceCommonElements(Element pluginElement) throws ReplaceException {
		pluginElement.setAttribute("id", artifactName.substring(0, artifactName.lastIndexOf('_')));  
        pluginElement.setAttribute("name", project.getName()); 
        pluginElement.setAttribute("version", new VersionAdapter().adapt(project.getVersion()));  
        pluginElement.setAttribute("provider-name", project.getOrganization().getName());
	}

	protected abstract void replace(Element pluginElement) throws ReplaceException;
	
	public void setArtifactName(String name) { this.artifactName = name; }

}
