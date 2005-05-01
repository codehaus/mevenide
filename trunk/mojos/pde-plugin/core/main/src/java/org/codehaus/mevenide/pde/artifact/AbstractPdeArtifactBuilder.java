package org.codehaus.mevenide.pde.artifact;

import org.codehaus.mevenide.pde.PdePluginException;

public abstract class AbstractPdeArtifactBuilder implements IPdeArtifactBuilder {

	public void build() throws PdePluginException {
	    updateDescriptor();
	    collectDependencies();
		createArchive();
    }

}
