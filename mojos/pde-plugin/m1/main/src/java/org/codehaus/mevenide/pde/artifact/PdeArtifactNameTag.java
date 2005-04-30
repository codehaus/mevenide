package org.codehaus.mevenide.pde.artifact;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.lang.BooleanUtils;
import org.codehaus.mevenide.pde.taglib.PdeTag;

public class PdeArtifactNameTag extends PdeTag {
	
	private String var;
	
	public void doTag(XMLOutput arg0) throws JellyTagException {
		context.setVariable(var, getArtifactName());
	}
	
	public String getArtifactName() {
		
		String pdeArtifactName = (String) context.getVariable("maven.pde.name");
		String pdeArtifactVersion = (String) context.getVariable("maven.pde.version");
		String pdeArtifactPrefix = (String) context.getVariable("maven.pde.prefix");
		
		boolean normalize = BooleanUtils.toBoolean((String) context.getVariable("maven.pde.normalizeName"));
		boolean adaptVersion = BooleanUtils.toBoolean((String) context.getVariable("maven.pde.adaptVersion"));
		
		String artifactId = project.getArtifactId();
		String version = project.getCurrentVersion();
		
		PdeArtifactNameBuilder artifactNameBuilder = new PdeArtifactNameBuilder();
		artifactNameBuilder.setAdaptVersion(adaptVersion);
		artifactNameBuilder.setArtifactId(artifactId);
		artifactNameBuilder.setNormalizeName(normalize);
		artifactNameBuilder.setPdeArtifactName(pdeArtifactName);
		artifactNameBuilder.setPdeArtifactPrefix(pdeArtifactPrefix);
		artifactNameBuilder.setPdeArtifactVersion(pdeArtifactVersion);
		
		return artifactNameBuilder.getArtifactName();
	}

	public String getVar() { return var; }
	public void setVar(String var) { this.var = var; }
	
}
