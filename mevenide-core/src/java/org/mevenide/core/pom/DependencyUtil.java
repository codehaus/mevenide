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
package org.mevenide.core.pom;

import java.util.List;
import java.util.StringTokenizer;

import org.jdom.Element;

/**
 * @todo JDOM use Maven classes instead of JDom
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
class DependencyUtil {
	static boolean isDependencyPresent(
		String dependencyName,
		List dependencies) {
		boolean foundLib = false;
		for (int i = 0; i < dependencies.size(); i++) {
			Element dependency = (Element) dependencies.get(i);
			String dependencyId = dependency.getChildText("id");
			String dependencyVersion = dependency.getChildText("version");
			if (dependencyName.startsWith(dependencyId)
				&& (dependencyVersion != null
					&& dependencyName.endsWith(dependencyVersion))) {
				//&& libraryName.endsWith(depVersion) ) {
				foundLib = true;
				break;
			}
		}
		return foundLib;
	}
	
	static Element getDependency(String dependencyName) {
		String dependencyId = getDependencyId(dependencyName);
		Element dependencyElement = new Element("dependency");
		Element dependencyIdElement = new Element("id");
		dependencyIdElement.addContent(dependencyId);
		Element dependencyVersion = new Element("version");
		try {
			dependencyVersion.addContent(
				getDependencyVersion(dependencyName, dependencyId));
		}
		catch (IndexOutOfBoundsException ex) {
		}
		Element url = new Element("url");
		dependencyElement.addContent(dependencyIdElement);
		dependencyElement.addContent(dependencyVersion);
		dependencyElement.addContent(url);
		return dependencyElement;
	}
	
	private static String getDependencyId(String libraryFullName) {
		StringTokenizer tokenizer = new StringTokenizer(libraryFullName, "-");
		StringBuffer name = new StringBuffer("");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (!token.equals("SNAPSHOT")
				&& new Character(token.charAt(0)).compareTo(new Character('9'))
					> 0) {
				if (!name.toString().equals("")) {
					name.append('-');
				}
				name.append(token);
			}
		}
		return name.toString();
	}
	
	private static String getDependencyVersion(String libraryFullName, String libId) {
		return libraryFullName.substring(
			libId.length() + 1,
			libraryFullName.lastIndexOf('.')
		);
	}
}
