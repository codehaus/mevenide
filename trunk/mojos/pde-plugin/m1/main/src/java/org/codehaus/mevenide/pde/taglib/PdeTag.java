package org.codehaus.mevenide.pde.taglib;

import java.io.File;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.tags.core.JellyTag;
import org.apache.maven.project.Project;
import org.codehaus.mevenide.pde.PdePluginException;
import org.codehaus.mevenide.pde.artifact.AbstractPdeArtifactBuilder;
import org.codehaus.mevenide.pde.artifact.PdeArtifactNameTag;
import org.codehaus.plexus.util.StringUtils;

public abstract class PdeTag extends JellyTag {
	protected Project project;

	protected String getArtifactName() throws PdePluginException {
		try {
			PdeArtifactNameTag tag = new PdeArtifactNameTag();
			tag.setProject(project);
			tag.setContext(context);
			return tag.getArtifactName();
		} 
		catch (JellyTagException e) {
			//shouldnot happen. 
			throw new PdePluginException("Unable to construct artifact name", e);
		}
	}
	
	protected void configureBuilder(AbstractPdeArtifactBuilder builder) throws PdePluginException {
		String artifactName = getArtifactName();
		builder.setArtifactName(artifactName);
		
		String destinationFolder = (String) context.getVariable("maven.build.dir");
		builder.setArtifact(destinationFolder + "/" + artifactName + ".jar");
		
		File basedir = new File((String) context.getVariable("basedir"));
		builder.setBasedir(basedir);
		
		String excludes = StringUtils.stripEnd((String) context.getVariable("maven.pde.excludes"), ",");
		builder.setExcludes(excludes);
	}
	
	public Project getProject() { return project; }
	public void setProject(Project project) { this.project = project; }
	
	
}
