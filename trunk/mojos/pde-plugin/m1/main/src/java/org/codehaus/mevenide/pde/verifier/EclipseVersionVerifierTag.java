package org.codehaus.mevenide.pde.verifier;

import java.io.File;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.codehaus.mevenide.pde.ConfigurationException;
import org.codehaus.mevenide.pde.ParameterException;
import org.codehaus.mevenide.pde.taglib.PdeTag;
import org.codehaus.plexus.util.StringUtils;

public class EclipseVersionVerifierTag extends PdeTag {
	
	private String result; 
	
	
	public void doTag(XMLOutput arg0) throws JellyTagException {
		String eclipseHome = (String) context.getVariable("eclipse.home");
	    
		if ( StringUtils.isEmpty(eclipseHome) ) {
			throw new JellyTagException("eclipse.home must be defined");
		}
		
		String configurationFolder = (String) context.getVariable("eclipse.configuration.folder");
		
	    String maxBuildIdParameter = (String) context.getVariable("maven.pde.maxBuildId");
	    String minBuildIdParameter = (String) context.getVariable("maven.pde.minBuildId");
		
		try {
			File conf = !StringUtils.isEmpty(configurationFolder) ? new File(configurationFolder) : null; 
			new CompatibilityChecker(new File(eclipseHome), conf).checkBuildId(minBuildIdParameter, maxBuildIdParameter);
			context.setVariable(result, Boolean.TRUE);
		}
		catch (ParameterException e) {
			context.setVariable(result, Boolean.FALSE);
		}
		catch (ConfigurationException e) {
			throw new JellyTagException("Problem while checking platform compatibility", e); 
		}
	    
	}


	public String getResult() { return result; }
	public void setResult(String result) { this.result = result; }
	
	
}
