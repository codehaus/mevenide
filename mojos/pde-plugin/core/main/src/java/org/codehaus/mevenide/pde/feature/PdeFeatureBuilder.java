package org.codehaus.mevenide.pde.feature;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Dependency;
import org.codehaus.mevenide.pde.CollectException;
import org.codehaus.mevenide.pde.archive.PdeArchiveException;
import org.codehaus.mevenide.pde.artifact.AbstractPdeArtifactBuilder;
import org.codehaus.mevenide.pde.descriptor.ReplaceException;

public class PdeFeatureBuilder extends AbstractPdeArtifactBuilder {

	public void collectDependencies() throws CollectException {
		List dependencies = project.getDependencies();
		
		for (Iterator it = dependencies.iterator(); it.hasNext();) {
            Dependency dependency = (Dependency) it.next();
            
            Properties props = dependency.getProperties();
			
        }
	}

	public void createArchive() throws PdeArchiveException {
	}

	public void updateDescriptor() throws ReplaceException {
	}
	
}
