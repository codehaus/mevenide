/**
 * 
 */
package org.codehaus.mevenide.pde.feature;

class PdePluginArtifact {
	String version;
	String name;
	String file;
	
	PdePluginArtifact(String version, String name, String file) {
		this.version = version;
		this.name = name;
		this.file = file;
	}
}