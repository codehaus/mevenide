/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
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

import org.apache.maven.project.Project;
import org.mevenide.util.DefaultProjectUnmarshaller;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class PomSkeletonBuilder {
	
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
		InputStream is = null;
		if ( template == null || !new File(template).exists() ) {
			pomBuilder.template = new File(PomSkeletonBuilder.class.getResource(DEFAULT_TEMPLATE).getFile());
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
		InputStream is = new FileInputStream(template);
		Reader reader = new InputStreamReader(is);
		Project project = new DefaultProjectUnmarshaller().parse(reader);
		reader.close();
		is.close();
		
		project.setId(projectName.toLowerCase());
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
	
	