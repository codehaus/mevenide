/*
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.mevenide.pom;

import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.mevenide.util.JDomOutputter;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
class PomSkeleton {
	private static String template = "/templates/standard/project.xml";
	
	/**
	 * 
	 * Still not the best way to manage project skeleton, 
	 * but MavenUtils doesnt seem to do the trick since it serializes 
	 * the global MavenSession, including lots of crap we really dont need
	 * 
	 * @todo JDOM get rid of jdom use 
	 * 
	 * @param projectName
	 * @return
	 * @throws Exception
	 */
	static String getSkeleton(String projectName) throws Exception {
		File projectDescriptor = new File(PomSkeleton.class.getResource(template).getFile());
		
		//Project project = 
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(projectDescriptor);
		Element project = document.getRootElement();
		
		project.addContent(new Element("id").setText(projectName.toLowerCase()));
		project.addContent(new Element("name").setText(projectName));
		project.getChild("inceptionYear").setText(getCurrentYear());

		String doc2String = JDomOutputter.convertToString(document);
		//doc2String.replaceAll("${pom.name}", projectName);
		return doc2String;
	}

	private static String getCurrentYear() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		return Integer.toString(calendar.get(GregorianCalendar.YEAR));
	}
}
	
	