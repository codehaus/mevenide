package org.codehaus.mevenide.pde.artifact;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

public class PdeBuilderHelper {
	
	private MavenProject project;
	
	public PdeBuilderHelper(MavenProject project) {
		this.project = project;
	}

	//@todo optimize - even better : drop it when correct method found in m2 
    public String getArtifact(Dependency dependency) {
        
        Set artifacts = project.getArtifacts();
        
        String dependencyArtifact = dependency.getArtifact();
        String fullArtifactPath = null;
        
        for (Iterator iter = artifacts.iterator(); iter.hasNext();) {
            Artifact artifact = (Artifact) iter.next();
            File artifactFile = artifact.getFile();
            if ( dependencyArtifact.equals(artifact.getFile().getName()) ) {
                fullArtifactPath = artifactFile.getAbsolutePath();
                break;
            }
        }
        
        return fullArtifactPath;
    }
}
