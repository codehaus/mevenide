package org.codehaus.mevenide.pde.artifact;

import org.codehaus.mevenide.pde.CollectException;
import org.codehaus.mevenide.pde.PdePluginException;
import org.codehaus.mevenide.pde.archive.PdeArchiveException;
import org.codehaus.mevenide.pde.descriptor.ReplaceException;

public interface IPdeArtifactBuilder {
	
	void build() throws PdePluginException;
	void updateDescriptor() throws ReplaceException;
	void collectDependencies() throws CollectException;
	void createArchive() throws PdeArchiveException;
}
