/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.maven.project.Project;
import org.mevenide.util.MevenideUtils;
import org.mevenide.util.StringUtils;
import org.mevenide.context.JDomProjectUnmarshaller;
//import org.mevenide.util.DefaultProjectUnmarshaller;

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
	 * equivalent to getPomSkeleton(projectName, projectName, projectName)
	 *  
	 * @param projectName
	 * @return a new project skeleton 
	 * @throws Exception
	 */
	public String getPomSkeleton(String projectName) throws Exception {
        return getPomSkeleton(projectName, projectName, projectName, null, null);
	}
	
	/**
	 *
	 * @param projectName
	 * @param groupId
	 * @param artifactId 
	 * @return a new project skeleton 
	 * @throws Exception 
	 */ 
	public String getPomSkeleton(String projectName, String groupId, String artifactId, String version, String shortDescription) throws Exception {
	    if ( StringUtils.isNull(projectName) ) {
	        throw new Exception("Project name should be defined");
	    }
        InputStream is = null;
	    try {
            if ( template != null ) {
                is = new FileInputStream(template);
            }
            else {
                is = PomSkeletonBuilder.class.getResourceAsStream(DEFAULT_TEMPLATE);
            }
            File file = MevenideUtils.createFile(is);
            Project project = new JDomProjectUnmarshaller().parse(file);
            
            if ( StringUtils.isNull(artifactId) ) {
                artifactId = projectName;
            }
            if ( StringUtils.isNull(groupId) ) {
                groupId = projectName;
            }
            project.setId(artifactId.toLowerCase());
            project.setName(projectName);
            project.setGroupId(groupId.toLowerCase());
            project.setArtifactId(artifactId.toLowerCase());
            project.setInceptionYear(getCurrentYear());
            
            if ( !StringUtils.isNull(version) ) {
                project.setCurrentVersion(version);
            }
            if ( !StringUtils.isNull(shortDescription) ) {
                project.setShortDescription(shortDescription);
            }
            Writer writer = new StringWriter();
            new CarefulProjectMarshaller().marshall(writer, project);
            writer.close();
            return writer.toString();
        }
        finally {
            if ( is != null ) {
                is.close();
            }
        }
	}
	
	
	
	private static String getCurrentYear() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		return Integer.toString(calendar.get(GregorianCalendar.YEAR));
	}
}
	
	