package org.codehaus.mevenide.pde.artifact;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.pde.PdePluginException;
import org.codehaus.mevenide.pde.archive.Include;

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

	/** comma separated list of files to exclude from the generated zip */
	protected String excludes;

	/** comma separated list of files to include in the generated zip */
	protected List includes = new ArrayList();
	
	
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
	
	protected void includeResources() {
		//@todo: resources & custom includes
		for ( int u = 0; u < getCommonIncludes().length; u++ ) {
			Include include = new Include();
		    include.setAbsolutePath(new File(basedir, getCommonIncludes()[u]).getAbsolutePath()); 
		    includes.add(include);
		}
	}

	public String getExcludes() { return excludes; }

	public void setExcludes(String excludes) { this.excludes = excludes; }

	public List getIncludes() { return includes; }

	public void setIncludes(List includes) { this.includes = includes; }

	protected abstract String[] getCommonIncludes();
	
}
