package org.codehaus.mevenide.pde.taglib;

import org.apache.commons.jelly.tags.core.JellyTag;
import org.apache.maven.project.Project;

public abstract class PdeTag extends JellyTag {
	protected Project project;

	public Project getProject() { return project; }
	public void setProject(Project project) { this.project = project; }
	
}
