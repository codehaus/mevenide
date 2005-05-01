package org.codehaus.mevenide.pde.feature;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.codehaus.mevenide.pde.CollectException;
import org.codehaus.mevenide.pde.archive.PdeArchiveException;
import org.codehaus.mevenide.pde.archive.SimpleZipCreator;
import org.codehaus.mevenide.pde.artifact.AbstractPdeArtifactBuilder;
import org.codehaus.mevenide.pde.descriptor.ReplaceException;
import org.codehaus.plexus.util.StringUtils;

public class PdeFeatureBuilder extends AbstractPdeArtifactBuilder {

	private String prefix;
	
	private boolean adaptVersion, normalize;
	
	private List artifactList = new ArrayList();
	
	public void collectDependencies() throws CollectException {
		List dependencies = project.getDependencies();
		
		for (Iterator it = dependencies.iterator(); it.hasNext();) {
            Dependency dependency = (Dependency) it.next();
            
			PdePluginArtifact pluginArtifact = getPluginArtifact(dependency);
			
			artifactList.add(pluginArtifact);
        }
	}
	
	private PdePluginArtifact getPluginArtifact(Dependency dependency) {
		String version = null;
		String name = null;
		String file = null;
		
		if( dependency.getArtifact() != null ) {
			String[] parts = StringUtils.split(dependency.getArtifact(), "_");
			name = parts[0];
			version = parts[1];
		}
		else {
			name = dependency.getArtifactId();
			version = dependency.getVersion();
		}
		
		return new PdePluginArtifact(version, name, file); 
	}

	public void createArchive() throws PdeArchiveException {
		SimpleZipCreator zipCreator = new SimpleZipCreator(null, new File(artifact).getAbsolutePath());
		includeResources();
		zipCreator.setIncludes(includes);
		zipCreator.zip();
	}

	protected String[] getCommonIncludes() {
		return new String[] { "feature.xml", "feature.properties", "license.txt" };
	}
	
	public void updateDescriptor() throws ReplaceException {
		PdeFeatureDescriptorReplacer replacer = new PdeFeatureDescriptorReplacer(basedir.getAbsolutePath(), project);
		replacer.setArtifactList(artifactList);
		replacer.setArtifactName(artifactName);
		replacer.replace();
	}

	public void setAdaptVersion(boolean adaptVersion) { this.adaptVersion = adaptVersion; }
	public void setNormalize(boolean normalize) { this.normalize = normalize; }
	public void setPrefix(String prefix) { this.prefix = prefix; }
	
	
	
}
