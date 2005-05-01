package org.codehaus.mevenide.pde.feature;

import java.util.List;

import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.pde.descriptor.AbstractPdeDescriptorValuesReplacer;
import org.codehaus.mevenide.pde.descriptor.ReplaceException;
import org.jdom.Element;

public class PdeFeatureDescriptorReplacer extends AbstractPdeDescriptorValuesReplacer {

	private List artifactList;
	
	public PdeFeatureDescriptorReplacer(String basedir, MavenProject project) throws ReplaceException {
		super(basedir, project);
	}
	
	protected String getDescriptorName() {
		return "feature.xml";
	}
	
	protected void replace(Element rootElement) throws ReplaceException {
		List plugins = rootElement.getChildren("plugin");
		
		Element requiresElement = rootElement.getChild("requires");
		List requiredPlugins = null;
		
		if ( requiresElement != null ) {
			requiredPlugins = requiresElement.getChildren("import");
		}
		
		detachAll(plugins);
		detachAll(requiredPlugins);
		
		if ( artifactList != null ) {
			for ( int u = 0; u < artifactList.size(); u++ ) {
				PdePluginArtifact artifact = (PdePluginArtifact) artifactList.get(u);
				addPlugin(artifact, rootElement, requiresElement);
			}
		}
	}

	private void addPlugin(PdePluginArtifact artifact, Element rootElement, Element requiresElement) {
		
	}
	
	private void detachAll(List elements) {
		if ( elements != null ) {
			for ( int u = 0; u < elements.size(); u++ ) {
				detach((Element) elements.get(u));
			}	
		}
				
	}
	
	protected void replaceCommonElements(Element rootElement) throws ReplaceException {
		super.replaceCommonElements(rootElement);
		rootElement.setAttribute("label", project.getShortDescription());
	}
	
	public void setArtifactList(List artifactList) { this.artifactList = artifactList; }
	

}
