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
package org.mevenide.ui.eclipse.sync.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.mevenide.ui.eclipse.sync.source.*;
import org.mevenide.util.JDomOutputter;

/**
 * 
 * Crappy ! this is just a proto that should be rewritten.
 * 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SourceDirectoryMarshaller {
	
	/**
	 * @refactor cyclomatic complexity &gt;&gt; 4
	 * 
	 * @param project
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static SourceDirectoryGroup getSourceDirectoryGroup(IProject project, String file) throws Exception {
		SourceDirectoryGroup group = new SourceDirectoryGroup(project);
		List sourceDirectoryList = new ArrayList();
		
		if ( new File(file).exists() ) {
		
			SAXBuilder builder = new SAXBuilder(false);
			
			Document document = builder.build(file);
			
			Element projects = document.getRootElement();
			List sourceDirectories = projects.getChildren("sourceDirectoryGroup");
			for (int i = 0; i < sourceDirectories.size(); i++) {
				Element sourceDirectoryGroupElement = 
					(Element) sourceDirectories.get(i);
				
				if ( sourceDirectoryGroupElement.getAttributeValue("projectName").equals(project.getName()) ) {
					List sources = sourceDirectoryGroupElement.getChildren("sourceDirectory");
					for (int j = 0; j < sources.size(); j++) {
						Element sourceDirectoryElement =  (Element) sources.get(j);
						SourceDirectory sourceDirectory 
							= new SourceDirectory(sourceDirectoryElement.getAttributeValue("path"));
						if ( group.getSourceDirectories().contains(sourceDirectory) ) {
							sourceDirectory.setDirectoryType(sourceDirectoryElement.getAttributeValue("type"));
							sourceDirectoryList.add(sourceDirectory);
						}
					}
				}
			}
			
			for (int i = 0; i < group.getSourceDirectories().size(); i++) {
				if ( !sourceDirectoryList.contains(group.getSourceDirectories().get(i)) ) {
					sourceDirectoryList.add(group.getSourceDirectories().get(i));
				}
			}
			
			if ( sourceDirectoryList.size() > 0 ) {
				group.setSourceDirectories(sourceDirectoryList);
			}
			
		}
		
		return group;
	}

	public static void saveSourceDirectoryGroup(SourceDirectoryGroup group, String file) throws Exception {
	
		Document document = null;
		
		
		if ( new File(file).exists() ) {
		
			SAXBuilder builder = new SAXBuilder(false);
			document = builder.build(file);
		}
		else {
			document = new Document();
			Element root = new Element("sourceDirectoryGroups");
			document.setRootElement(root);
		}
		
		List candidates = document.getRootElement().getChildren("sourceDirectoryGroup");
		for (int i = 0; i < candidates.size(); i++) {
			Element elem = (Element) candidates.get(i);
			if ( elem.getAttributeValue("projectName").equals(group.getProject().getProject().getName()) )  {
				document.getRootElement().removeContent(elem);
			}
		}
		
		Element sourceDirGroup = new Element("sourceDirectoryGroup");
		sourceDirGroup.setAttribute("projectName", group.getProject().getProject().getName());
		for (int i = 0; i < group.getSourceDirectories().size(); i++) {
			SourceDirectory dir = (SourceDirectory) group.getSourceDirectories().get(i);
			Element sourceDir = new Element("sourceDirectory");
			sourceDir.setAttribute("path", dir.getDirectoryPath()) ;
			sourceDir.setAttribute("type", dir.getDirectoryType());
			sourceDirGroup.addContent( sourceDir );
		}
		
		document.getRootElement().addContent(sourceDirGroup);
		
		File saveFile = new File(file); 
		
		JDomOutputter.output(document, saveFile, false);
		
	}
}
