package org.codehaus.mevenide.pde.artifact;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.lang.BooleanUtils;
import org.apache.maven.repository.ArtifactTypeHandler;
import org.apache.maven.repository.DefaultArtifactTypeHandler;
import org.codehaus.mevenide.pde.taglib.PdeTag;

public class PdeInstallParametersTag extends PdeTag {
	
	private String artifact, type, typeHandler;
	
	public void doTag(XMLOutput arg0) throws JellyTagException {
		ArtifactTypeHandler handlerVar = null;
		String typeVar = null;
		String artifactVar = null;
		
		if ( BooleanUtils.toBoolean(m1Project.getProperty("maven.pde.enabled")) ) {
			handlerVar = new PdeArtifactTypeHandler();
			((PdeArtifactTypeHandler) handlerVar).setContext(context);
			typeVar = "pde";
			
			PdeArtifactNameTag tag = new PdeArtifactNameTag();
			tag.setContext(context);
			tag.setProject(m1Project);
			artifactVar = tag.getArtifactName() + ".jar";
		}
		else {
			handlerVar = new DefaultArtifactTypeHandler();
			typeVar = "jar";
			artifactVar = context.getVariable("maven.build.dir") + "/" + context.getVariable("maven.final.name") + ".jar" ;
		}
		
		context.setVariable(artifact, artifactVar);
		context.setVariable(type, typeVar);
		context.setVariable(typeHandler, handlerVar);
	}

	public String getArtifact() { return artifact; }
	public void setArtifact(String artifact) { this.artifact = artifact; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public String getTypeHandler() { return typeHandler; }
	public void setTypeHandler(String typeHandler) { this.typeHandler = typeHandler; }
	
	
	
}
