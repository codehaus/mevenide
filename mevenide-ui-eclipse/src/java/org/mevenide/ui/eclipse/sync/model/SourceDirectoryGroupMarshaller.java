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
package org.mevenide.ui.eclipse.sync.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
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
public class SourceDirectoryGroupMarshaller {
	private static Log log = LogFactory.getLog(SourceDirectoryGroupMarshaller.class);
	
	private SourceDirectoryGroupMarshaller() { 
	}
	/**
	 * 
	 * @param project
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static SourceDirectoryGroup getSourceDirectoryGroup(IProject project, String file) throws Exception {
		SourceDirectoryGroup group = new SourceDirectoryGroup(project);
		List sourceDirectoryList = new ArrayList();
		List allSourceDirectoryList = new ArrayList();
		
		
		if ( new File(file).exists() ) {
		
			SAXBuilder builder = new SAXBuilder(false);
			
			Document document = builder.build(file);
			
			Element projects = document.getRootElement();
			List sourceDirectories = projects.getChildren("sourceDirectoryGroup");
			for (int i = 0; i < sourceDirectories.size(); i++) {
				Element sourceDirectoryGroupElement = 
					(Element) sourceDirectories.get(i);
				
				if ( sourceDirectoryGroupElement.getAttributeValue("projectName").equals(project.getName()) ) {
					long timestamp = Long.parseLong(sourceDirectoryGroupElement.getAttributeValue("timestamp"));
					
					List sources = sourceDirectoryGroupElement.getChildren("sourceDirectory");
					for (int j = 0; j < sources.size(); j++) {
						Element sourceDirectoryElement =  (Element) sources.get(j);
						SourceDirectory sourceDirectory 
							= new SourceDirectory(sourceDirectoryElement.getAttributeValue("path"));
						long sdTimestamp = Long.parseLong(sourceDirectoryElement.getAttributeValue("timestamp"));
						//if ( group.getSourceDirectories().contains(sourceDirectory) ) {
						if ( sdTimestamp == timestamp ){
							sourceDirectory.setDirectoryType(sourceDirectoryElement.getAttributeValue("type"));
							sourceDirectoryList.add(sourceDirectory);
						}
						allSourceDirectoryList.add(sourceDirectory);
					}
				}
			}
			
			
			for (int i = 0; i < group.getSourceDirectories().size(); i++) {
				SourceDirectory dir = (SourceDirectory) group.getSourceDirectories().get(i);
				if ( !allSourceDirectoryList.contains(dir) ) {
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
		long timestamp = new Date().getTime();
		
		if ( group == null ) {
			return;
		}
		
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
		
		Element sourceDirGroup = new Element("sourceDirectoryGroup");
		sourceDirGroup.setAttribute("projectName", group.getProjectName());
		
		List candidates = document.getRootElement().getChildren("sourceDirectoryGroup");
		for (int i = 0; i < candidates.size(); i++) {
			Element elem = (Element) candidates.get(i);
			String projectName = elem.getAttributeValue("projectName");
			if ( projectName != null && projectName.equals(group.getProjectName()) )  {
				sourceDirGroup = elem;
				document.getRootElement().removeContent(elem);
			}
		}
		
		sourceDirGroup.setAttribute("timestamp", Long.toString(timestamp));
		
		if ( group.getSourceDirectories().size() == 0 ) {
			document.getRootElement().addContent(sourceDirGroup);
			return;
		}
		
		List previousSourceDirectories = sourceDirGroup.getChildren("sourceDirectory");
		if ( previousSourceDirectories == null ) {
			previousSourceDirectories = new ArrayList();
		}
		
		for (int i = 0; i < group.getSourceDirectories().size(); i++) {
			SourceDirectory dir = (SourceDirectory) group.getSourceDirectories().get(i);
			Element sourceDir = new Element("sourceDirectory");
			sourceDir.setAttribute("path", dir.getDirectoryPath()) ;
			sourceDir.setAttribute("type", dir.getDirectoryType());
			sourceDir.setAttribute("timestamp", Long.toString(timestamp));
			
			for (int j = 0; j < previousSourceDirectories.size(); j++) {
				Element dirElem = (Element) previousSourceDirectories.get(j);
				if ( dirElem.getAttributeValue("path").equals(dir.getDirectoryPath()) ) {
					sourceDirGroup.removeContent(dirElem);
				}
			}
			
			sourceDirGroup.addContent( sourceDir );
		}
		
		document.getRootElement().addContent(sourceDirGroup);
		
		File saveFile = new File(file); 
		
		JDomOutputter.output(document, saveFile, false);
		
	}
}
