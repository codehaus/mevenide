package org.codehaus.mevenide.pde.artifact;

import org.codehaus.mevenide.pde.version.VersionAdapter;
import org.codehaus.plexus.util.StringUtils;

public class PdeArtifactNameBuilder {
	
	/** artifact name as specified in project properties */
	private String pdeArtifactName;
	
	/** artifact version as specified in project properties */
	private String pdeArtifactVersion;
	
	/** artifact prefix as specified in project properties */
	private String pdeArtifactPrefix;
	
	/** true if artifactName should normalized */
	private boolean normalizeName;
	
	/** true if artifact version should be adapted */
	private boolean adaptVersion;
	
	/** current project artifactId */ 
	private String artifactId;
	
	/** current project version */
	private String version;
	
	/**
	 * build the final artifact name, 
	 * normalizing it and adapting version if necessary 
	 * 
	 * @return artifact name
	 */
	public String getArtifactName() {
		boolean skipPrefix = !StringUtils.isEmpty(pdeArtifactName);
		if ( StringUtils.isEmpty(pdeArtifactName) ) {
			pdeArtifactName = artifactId; 
		}
		if ( normalizeName ) {
			pdeArtifactName = pdeArtifactName.replaceAll("-", ".");
		}
		if ( StringUtils.isEmpty(pdeArtifactVersion) ) {
			pdeArtifactVersion = version;
		}
		if ( adaptVersion ) {
			pdeArtifactVersion = new VersionAdapter().adapt(pdeArtifactVersion);
		}
		
		pdeArtifactPrefix = skipPrefix || StringUtils.isEmpty(pdeArtifactPrefix) ? "" : pdeArtifactPrefix + ".";
		String artifactName = pdeArtifactPrefix + pdeArtifactName + "_" + pdeArtifactVersion;
		
		return artifactName;
	}

	public void setAdaptVersion(boolean adaptVersion) { this.adaptVersion = adaptVersion; }
	public void setArtifactId(String artifactId) { this.artifactId = artifactId; }
	public void setNormalizeName(boolean normalize) { this.normalizeName = normalize; }
	public void setPdeArtifactName(String pdeArtifactName) { this.pdeArtifactName = pdeArtifactName; }
	public void setPdeArtifactPrefix(String pdeArtifactPrefix) { this.pdeArtifactPrefix = pdeArtifactPrefix; }
	public void setPdeArtifactVersion(String pdeArtifactVersion) { this.pdeArtifactVersion = pdeArtifactVersion; }
	public void setVersion(String version) { this.version = version; }
	
}
