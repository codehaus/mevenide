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
package org.mevenide.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class JDomOutputter {
	
	private JDomOutputter() {
	}

	public static String convertToString(Document doc) {
		XMLOutputter outputter = newXmlOutputter(false);
		return outputter.outputString(doc);
	}

	public static void output(Document doc, File pom) throws IOException {
		output(doc, pom, true);
	}

	public static void output(Document doc, File pom, boolean expandEmptyElements) throws IOException {
		XMLOutputter outputter = newXmlOutputter(expandEmptyElements);
		outputter.output(doc, new FileOutputStream(pom));
	}

	public static XMLOutputter newXmlOutputter(boolean expandEmptyElements) {
		XMLOutputter outputter = new XMLOutputter();
		outputter.setIndentSize(4);
		outputter.setIndent(true);
		outputter.setExpandEmptyElements(expandEmptyElements);
		outputter.setNewlines(true);
		return outputter;
	}
}
