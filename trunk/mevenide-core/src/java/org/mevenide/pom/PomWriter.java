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
import java.io.IOException;
import java.util.List;

import org.apache.maven.project.Project;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.mevenide.util.BetwixtHelper;
import org.mevenide.util.JDomOutputter;
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
	
	public void addSource(String path, File pom, String sourceType) throws Exception {
		
		Project project = BetwixtHelper.readProject(pom);
		
		if ( BuildConstants.MAVEN_ASPECT.equals(sourceType) ) {
			project.getBuild().setAspectSourceDirectory(path);
		}
		if ( BuildConstants.MAVEN_SRC.equals(sourceType) ) {
			project.getBuild().setSourceDirectory(path);
		}
		if ( BuildConstants.MAVEN_TEST.equals(sourceType) ) {
			project.getBuild().setUnitTestSourceDirectory(path);
		}
		
		BetwixtHelper.writeProject(pom, project);

	}
	
	public void addDependency(String depName, File pom)
		throws JDOMException, IOException {
		Document doc = new SAXBuilder().build(pom);
		String dependencyName = new File(depName).getName();
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
