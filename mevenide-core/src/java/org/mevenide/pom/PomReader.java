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
import java.util.Hashtable;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class PomReader {
	
	public static Hashtable getAllSourceDirectories(File pom) throws JDOMException {
		Hashtable srcDirs = new Hashtable();
		Document pomDoc = new SAXBuilder().build(pom);
		getSource(BuildConstants.MAVEN_SRC, pomDoc, srcDirs);
		getSource(BuildConstants.MAVEN_ASPECT, pomDoc, srcDirs);
		getSource(BuildConstants.MAVEN_TEST, pomDoc, srcDirs);
		return srcDirs;
	}

	private static void getSource(String sourceType, Document pomDocument, Hashtable srcDirs) {
		try {
			String resolvedSourceType = BuildConstants.getResolvedSourceType(sourceType);
			Element src = pomDocument.getRootElement().getChild("build").getChild(resolvedSourceType);
			if ( src != null ) {
				srcDirs.put(src.getText(), sourceType);
			}
		}
		catch (InvalidSourceTypeException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isWellFormed(File pom) {
		try {
			new SAXBuilder().build(pom);
			return true;
		}
		catch (JDOMException e) {
			return false;
		}
	}
	
}
