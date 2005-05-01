package org.codehaus.mevenide.pde.artifact;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.pde.PdePluginException;

public abstract class AbstractPdeArtifactBuilder implements IPdeArtifactBuilder {
	
	/** base directory */    
    protected File basedir;
    
    /** project under construction */
	protected MavenProject project;
	
	/** artifactName referencing the primary artifact */
	protected String artifactName;
	
	/** name of generated zip file */
	protected String artifact;
	
	/** helper */
	protected PdeBuilderHelper helper;
	
	public void build() throws PdePluginException {
	    collectDependencies();
	    updateDescriptor();
		createArchive();
    }
	
	public File getBasedir() { return basedir; }
    public void setBasedir(File basedir) { this.basedir = basedir; }
	
	public MavenProject getProject() { return project; }
    public void setProject(MavenProject project) { 
		this.project = project; 
		helper = new PdeBuilderHelper(project);
	}
	
	public void setArtifactName(String name) { this.artifactName = name; }

	public String getArtifact() { return artifact; }
    public void setArtifact(String artifact) { this.artifact = artifact; }
}
