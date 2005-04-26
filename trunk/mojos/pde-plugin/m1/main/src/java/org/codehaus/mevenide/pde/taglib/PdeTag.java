package org.codehaus.mevenide.pde.taglib;

import org.apache.commons.jelly.tags.core.JellyTag;
import org.apache.maven.project.Project;

public abstract class PdeTag extends JellyTag {
	protected Project m1Project;

	public Project getProject() { return m1Project; }
	public void setProject(Project project) { m1Project = project; }
	
}
