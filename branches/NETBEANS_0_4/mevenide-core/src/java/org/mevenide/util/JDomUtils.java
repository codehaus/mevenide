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
package org.mevenide.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public final class JDomUtils {
	
	private JDomUtils() {
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
                Format format = Format.getPrettyFormat();
                format.setIndent("    ");
                format.setLineSeparator(System.getProperty("line.separator"));
                format.setExpandEmptyElements(expandEmptyElements);
                outputter.setFormat(format);
		return outputter;
	}
}
