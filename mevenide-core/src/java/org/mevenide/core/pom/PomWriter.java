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
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.mevenide.core.util.JDomOutputter;
/**
 * 
 * @todo JDOM do NOT manipulate POM directly ?
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class PomWriter {
	private static PomWriter pomGeneratorInstance = null;
	private static Object lock = new Object();
	private PomWriter() {
		super();
	}
	public static PomWriter getWriter() {
		if (pomGeneratorInstance != null) {
			return pomGeneratorInstance;
		}
		else {
			synchronized (lock) {
				if (pomGeneratorInstance == null) {
					pomGeneratorInstance = new PomWriter();
				}
			}
			return pomGeneratorInstance;
		}
	}
	
	public void addSource(String path, File pom, String sourceType)
		throws JDOMException, IOException {
		Document doc = new SAXBuilder().build(pom);
		Element project = doc.getRootElement();
		Element build = project.getChild("build");
		Element sourceDirectoryType = build.getChild(sourceType);
		if (sourceDirectoryType != null) {
			build.removeChild(sourceType);
		}
		Element srcDir = new Element(sourceType);
		srcDir.setText(path);
		build.addContent(srcDir);
		JDomOutputter.output(doc, pom);
	}
	
	public void addDependency(String dependencyName, File pom)
		throws JDOMException, IOException {
		Document doc = new SAXBuilder().build(pom);
		dependencyName = new File(dependencyName).getName();
		Element project = doc.getRootElement();
		Element dependenciesElement = project.getChild("dependencies");
		List dependencies = dependenciesElement.getChildren("dependency");
		boolean foundLib =
			DependencyUtil.isDependencyPresent(dependencyName, dependencies);
		if (!foundLib) {
			dependenciesElement.addContent(
				DependencyUtil.getDependency(dependencyName));
		}
		JDomOutputter.output(doc, pom);
	}
	
	public String getSkeleton(String projectName) throws Exception {
		return PomSkeleton.getSkeleton(projectName);
	}
}
