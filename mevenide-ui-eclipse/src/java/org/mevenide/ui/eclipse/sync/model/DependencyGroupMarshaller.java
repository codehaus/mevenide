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
 * 
 */
package org.mevenide.ui.eclipse.sync.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.eclipse.core.resources.IProject;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.mevenide.util.JDomOutputter;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyGroupMarshaller {
	
	private static Log log = LogFactory.getLog(DependencyGroupMarshaller.class);
	
	private DependencyGroupMarshaller() {
	}
	
	public static DependencyGroup getDependencyGroup(IProject project, String file) throws Exception {
		DependencyGroup group = new DependencyGroup(project);
		List dependenciesList = new ArrayList();
		
		if ( new File(file).exists() ) {

			SAXBuilder builder = new SAXBuilder(false);
	
			Document document = builder.build(file);
	
			Element projects = document.getRootElement();
			List dependencies = projects.getChildren("dependencyGroup");
			for (int i = 0; i < dependencies.size(); i++) {
				Element dependencyGroupElement = 
					(Element) dependencies.get(i);
		
				if ( dependencyGroupElement.getAttributeValue("projectName").equals(project.getName()) ) {
					long timeStamp = Long.parseLong(dependencyGroupElement.getAttributeValue("timestamp"));
					List sources = dependencyGroupElement.getChildren("dependency");
					for (int j = 0; j < sources.size(); j++) {
						Element dependencyElement =  (Element) sources.get(j);
						
						Dependency dependency = new Dependency();
						
						dependency.setArtifact(dependencyElement.getAttributeValue("artifact"));
						dependency.setArtifactId(dependencyElement.getAttributeValue("artifactId"));
						dependency.setGroupId(dependencyElement.getAttributeValue("groupId"));
						dependency.setVersion(dependencyElement.getAttributeValue("version"));
						dependency.setType(dependencyElement.getAttributeValue("type"));
						
						//for (int k = 0; k < group.getDependencies().size(); k++) {
							String l = dependencyElement.getAttributeValue("timestamp");
							//if ( ((Dependency) group.getDependencies().get(k)).getArtifact().equals(dependency.getArtifact()) ) {
							if ( timeStamp == Long.parseLong(l) ) {
								dependenciesList.add(dependency);
							}
						//}
					}
				}
			}
	
			log.debug("Found " + dependenciesList.size() + " previously stored dependencies");
			
//          dont check project again. all required pom dependencies should have been saved to file
// 
//			List projectDependencies = group.getDependencies();
//			for (int i = 0; i < projectDependencies.size(); i++) {
//				boolean alreadyAddedDependency = false;
//				Dependency projectDependency = (Dependency) projectDependencies.get(i);
//				for (int j = 0; j < dependenciesList.size(); j++) {
//					Dependency savedDependency = (Dependency) dependenciesList.get(j);
//					if ( savedDependency.getArtifact().equals(projectDependency.getArtifact()) ) {
//						alreadyAddedDependency = true;
//						break;
//					}
//				}
//				if ( !alreadyAddedDependency ) {
//					dependenciesList.add(projectDependency);
//				}
//			}
		
			if ( dependenciesList.size() > 0 ) {
				group.setDependencies(dependenciesList);
			}
	
		}

		return group;
	}
	
	
	public static void saveDependencyGroup(DependencyGroup group, String file) throws Exception {
		long timeStamp = new Date().getTime();

		Document document = null;
	
	
		if ( new File(file).exists() ) {
	
			SAXBuilder builder = new SAXBuilder(false);
			document = builder.build(file);
		}
		else {
			document = new Document();
			Element root = new Element("dependencyGroups");
			document.setRootElement(root);
		}
	
		Element dependencyGroup = new Element("dependencyGroup");
		
		List candidates = document.getRootElement().getChildren("dependencyGroup");
		for (int i = 0; i < candidates.size(); i++) {
			Element elem = (Element) candidates.get(i);
			if ( elem.getAttributeValue("projectName").equals(group.getProject().getProject().getName()) )  {
				dependencyGroup = elem;
				document.getRootElement().removeContent(elem);
			}
		}
		
		List previousDependencies = dependencyGroup.getChildren("dependency");
		if ( previousDependencies == null ) {
			previousDependencies = new ArrayList();
		}
		dependencyGroup.setAttribute("timestamp", Long.toString(timeStamp));
		for (int i = 0; i < group.getDependencies().size(); i++) {
			Dependency dependency = (Dependency) group.getDependencies().get(i);
			Element dependencyElement = new Element("dependency");
			dependencyElement.setAttribute("groupId", dependency.getGroupId()) ;
			dependencyElement.setAttribute("artifactId", dependency.getArtifactId()) ;
			dependencyElement.setAttribute("artifact", dependency.getArtifact()) ;
			dependencyElement.setAttribute("version", dependency.getVersion()) ;
			dependencyElement.setAttribute("type", dependency.getType()) ;
			dependencyElement.setAttribute("timestamp", Long.toString(timeStamp));
			
			for (int j = 0; j < previousDependencies.size(); j++) {
				Element depElem = (Element) previousDependencies.get(j);
				if ( depElem.getAttributeValue("artifact").equals(dependency.getArtifact()) ) {
					dependencyGroup.removeContent(depElem);
				}
			}
			
			dependencyGroup.addContent( dependencyElement );
						
		}
	
		log.debug("Saving " + dependencyGroup.getChildren("dependency").size() + " dependencies");
		
		document.getRootElement().addContent(dependencyGroup);
	
		File saveFile = new File(file); 
	
		JDomOutputter.output(document, saveFile, false);
	
	}
}
