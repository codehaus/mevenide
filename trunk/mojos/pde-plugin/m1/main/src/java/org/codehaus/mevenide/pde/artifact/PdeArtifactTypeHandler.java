package org.codehaus.mevenide.pde.artifact;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.maven.MavenException;
import org.apache.maven.project.Project;
import org.apache.maven.repository.ArtifactTypeHandler;

public class PdeArtifactTypeHandler implements ArtifactTypeHandler {

	private JellyContext context;
	
	public String constructRepositoryFullPath(String type, Project project, String version) throws MavenException {
		PdeArtifactNameTag tag = new PdeArtifactNameTag();
		tag.setProject(project);
		try {
			tag.setContext(context);
		}
		catch (JellyTagException e) {
			throw new MavenException("Unable to set context", e);
		}
		String fileName = tag.getArtifactName();
		
		return (constructRepositoryDirectoryPath(type, project) + fileName + ".jar").toString();
	}

	public String constructRepositoryDirectoryPath(String type, Project project) throws MavenException {
		return (project.getArtifactDirectory() + "/jars/").toString();
	}

	public void setContext(JellyContext context) {
		this.context = context;
	}
	

}
