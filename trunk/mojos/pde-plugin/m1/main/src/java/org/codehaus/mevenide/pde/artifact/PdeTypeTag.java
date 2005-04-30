package org.codehaus.mevenide.pde.artifact;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.codehaus.mevenide.pde.taglib.PdeTag;
import org.codehaus.plexus.util.StringUtils;

public class PdeTypeTag extends PdeTag {
	private String var;

	public void doTag(XMLOutput arg0) throws JellyTagException {
		String type = project.getProperty("maven.pde.type");
		if ( StringUtils.isEmpty(type) ) {
			type = "plugin";
		}
		if ( !"plugin".equals(type) && 
			  "feature".equals(type) &&
			  "site".equals(type) ) {
			throw new JellyTagException("Unknown pde type: " + type);
		}
		context.setVariable(var, type);
	}
	
	public String getVar() { return var; }
	public void setVar(String var) { this.var = var; }
	
}
