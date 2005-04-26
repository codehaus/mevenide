package org.codehaus.mevenide.pde.artifact;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.codehaus.mevenide.pde.taglib.PdeTag;

public class PdeEnabledTag extends PdeTag {
	private String var;

	public void doTag(XMLOutput arg0) throws JellyTagException {
		String enabled = m1Project.getProperty("pde.enabled");
		context.setVariable(var, Boolean.valueOf(enabled));
	}
	
	public String getVar() { return var; }
	public void setVar(String var) { this.var = var; }
	
}
