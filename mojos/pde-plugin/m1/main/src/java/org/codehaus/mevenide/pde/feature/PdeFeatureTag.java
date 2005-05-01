package org.codehaus.mevenide.pde.feature;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.lang.BooleanUtils;
import org.apache.maven.jelly.MavenJellyContext;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.pde.PdePluginException;
import org.codehaus.mevenide.pde.converter.MavenProjectConverter;
import org.codehaus.mevenide.pde.taglib.PdeTag;
import org.codehaus.plexus.util.StringUtils;

public class PdeFeatureTag extends PdeTag {
	
	public void doTag(XMLOutput arg0) throws JellyTagException {
		try {
			boolean pdeEnabled = BooleanUtils.toBoolean(project.getProperty("maven.pde.enabled"));
			if ( pdeEnabled ) {
				createFeature();
			}
        }
        catch (PdePluginException e) {
            throw new JellyTagException("Unable to build plugin", e);
        }
	}

	private void createFeature() throws PdePluginException {
		boolean prefix = BooleanUtils.toBoolean((String) context.getVariable("maven.pde.prefix"));
		boolean adaptVersion = BooleanUtils.toBoolean((String) context.getVariable("maven.pde.adaptVersion"));
		boolean normalize = BooleanUtils.toBoolean((String) context.getVariable("maven.pde.normalize"));
		
		PdeFeatureBuilder builder = new PdeFeatureBuilder();
		configureBuilder(builder);
		
		String excludes = StringUtils.stripEnd((String) context.getVariable("maven.pde.excludes"), ",");
		builder.setExcludes(excludes);
		
		MavenProject mavenProject = new MavenProjectConverter(project, (MavenJellyContext) context).convert();
		builder.setProject(mavenProject);
		
		builder.build();
	}
}
