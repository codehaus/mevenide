/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.project.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public final class PomSkeletonBuilder {
	
	/**
	 * default template used if user doesnot provide one
	 */
	private static final String DEFAULT_TEMPLATE = "/templates/standard/project.xml";
	
	private File template;
	
	private PomSkeletonBuilder() {
	}

	/** 
	 * constructs a new Skeleton instance. it can create pom skeleton based on user-supplied file template
	 * 
	 * @param projectName
	 * @param template
	 * @return
	 * @throws Exception
	 */
	public static PomSkeletonBuilder getSkeletonBuilder(String template) throws Exception {
		PomSkeletonBuilder pomBuilder = new PomSkeletonBuilder();
		if ( template == null || !new File(template).exists() ) {
			//pomBuilder.template = new File(PomSkeletonBuilder.class.getResource(DEFAULT_TEMPLATE).getFile());
		}
		else {
			pomBuilder.template = new File(template);
		}
		return pomBuilder;
	}
	
	public static PomSkeletonBuilder getSkeletonBuilder() throws Exception {
		return getSkeletonBuilder((String) null);
	}
	
	/**
	 * return the pom skeleton as a string
	 * 
	 * @param projectName
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public String getPomSkeleton(String projectName) throws Exception {
	    InputStream is = null;
	    if ( template != null ) {
	        is = new FileInputStream(template);
	    }
	    else {
	        is = PomSkeletonBuilder.class.getResourceAsStream(DEFAULT_TEMPLATE);
	    }
		Reader reader = new InputStreamReader(is);
		MavenProject project = new MavenProject();
		Model model = new MavenXpp3Reader().read(reader);
		project.setModel(model);
		reader.close();
		is.close();
		
		project.setName(projectName);
		project.setGroupId(projectName.toLowerCase());
		project.setArtifactId(projectName.toLowerCase());
		project.setInceptionYear(getCurrentYear());
		
		Writer writer = new StringWriter();
		new DefaultProjectMarshaller().marshall(writer, project);
		writer.close();
		return writer.toString();
	}
	
	
	
	private static String getCurrentYear() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		return Integer.toString(calendar.get(GregorianCalendar.YEAR));
	}
}
	
	