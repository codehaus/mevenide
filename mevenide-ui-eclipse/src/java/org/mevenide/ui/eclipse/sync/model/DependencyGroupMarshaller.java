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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.eclipse.core.resources.IProject;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.mevenide.project.dependency.DependencyUtil;
import org.mevenide.util.JDomOutputter;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class DependencyGroupMarshaller {
	private static final String PROPERTY_VALUE = "value";
	private static final String PROPERTY_KEY = "key";
	private static final String PROPERTY_ELEM = "property";
	private static final String PROPERTIES_ELEM = "properties";
	private static Log log = LogFactory.getLog(DependencyGroupMarshaller.class);
	
	
	private static final String ROOT_ELEM = "dependencyGroups";
	
	private static final String DEPENDENCY_GROUP_ELEM = "dependencyGroup";
	private static final String PROJECT_NAME_ATTR = "projectName";
	
	private static final String DEPENDENCY_ELEM = "dependency";
	
	private static final String TYPE_ATTR = "type";
	private static final String VERSION_ATTR = "version";
	private static final String GROUP_ID_ATTR = "groupId";
	private static final String ARTIFACT_ID_ATTR = "artifactId";
	private static final String ARTIFACT_ATTR = "artifact";
	private static final String TIMESTAMP_ATTR = "timestamp";
	
	
	
	private DependencyGroupMarshaller() {
	}
	
	public static DependencyGroup getDependencyGroup(IProject project, String file) throws Exception {
		DependencyGroup group = new DependencyGroup(project);
		List dependenciesList = new ArrayList();
	
		List allDependenciesList = new ArrayList();
		
		if ( new File(file).exists() ) {

			SAXBuilder builder = new SAXBuilder(false);
	
			Document document = builder.build(file);
	
			Element projects = document.getRootElement();
			List dependencies = projects.getChildren(DEPENDENCY_GROUP_ELEM);
			for (int i = 0; i < dependencies.size(); i++) {
				Element dependencyGroupElement = 
					(Element) dependencies.get(i);
		
				if ( dependencyGroupElement.getAttributeValue(PROJECT_NAME_ATTR).equals(project.getName()) ) {
					long timeStamp = Long.parseLong(dependencyGroupElement.getAttributeValue(TIMESTAMP_ATTR));
					List sources = dependencyGroupElement.getChildren(DEPENDENCY_ELEM);
					for (int j = 0; j < sources.size(); j++) {
						Element dependencyElement =  (Element) sources.get(j);
						
						Dependency dependency = new Dependency();
						
						String savedArtifact = dependencyElement.getAttributeValue(ARTIFACT_ATTR);
						dependency.setArtifact(savedArtifact == null ? "" : savedArtifact);
						
						String savedArtifactId = dependencyElement.getAttributeValue(ARTIFACT_ID_ATTR);
						dependency.setArtifactId(savedArtifactId == null ? "" : savedArtifactId);
						
						String savedGroupId = dependencyElement.getAttributeValue(GROUP_ID_ATTR);
						dependency.setGroupId(savedGroupId == null ? "" : savedGroupId);
						
						String savedVersion = dependencyElement.getAttributeValue(VERSION_ATTR);
						dependency.setVersion(savedVersion == null ? "" : savedVersion);
						
						String savedType = dependencyElement.getAttributeValue(TYPE_ATTR);
						dependency.setType(savedType == null ? "" : savedType);
						
						dependency.setProperties(getProperties(dependencyElement, timeStamp));
						
						DependencyUtil.refreshGroupId(dependency);
						
						String l = dependencyElement.getAttributeValue(TIMESTAMP_ATTR);
						if ( timeStamp == Long.parseLong(l) ) {
							dependenciesList.add(dependency);
						}
						allDependenciesList.add(dependency);
						
					}
				}
			}
	
			log.debug("Found " + dependenciesList.size() + " previously stored dependencies");
			
//          dont check project again. all required pom dependencies should have been saved to file
 
			List projectDependencies = group.getDependencies();
			for (int i = 0; i < projectDependencies.size(); i++) {
				boolean alreadyAddedDependency = false;
				Dependency projectDependency = (Dependency) projectDependencies.get(i);
				for (int j = 0; j < allDependenciesList.size(); j++) {
					Dependency savedDependency = (Dependency) allDependenciesList.get(j);
					if ( savedDependency.getArtifact().equals(projectDependency.getArtifact()) ) {
						alreadyAddedDependency = true;
						break;
					}
				}
				if ( !alreadyAddedDependency ) {
					dependenciesList.add(projectDependency);
				}
			}
		
			group.setDependencies(dependenciesList);
			
	
		}

		return group;
	}
	
	private static Map getProperties(Element dependencyElement, long timestamp) {
		Map properties = new HashMap();
		
		Element propertiesElement = dependencyElement.getChild(PROPERTIES_ELEM);
		
		List propertyElements = propertiesElement.getChildren(PROPERTY_ELEM);
		
		for (int i = 0; i < propertyElements.size(); i++) {
			Element propertyElement = (Element) propertyElements.get(i);
			long propertyTimeStamp = Long.parseLong(propertyElement.getAttribute(TIMESTAMP_ATTR).getValue());
			if ( timestamp == propertyTimeStamp ) {
				properties.put(propertyElement.getAttribute(PROPERTY_KEY).getValue(), propertyElement.getAttribute(PROPERTY_VALUE).getValue());
			}
		}
		
		return properties;
	}
	
	public static void saveDependencyGroup(DependencyGroup group, String file) throws Exception {
		if ( group == null ) {
			log.debug("DependencyGroup is null // shouldnot happen");
			return;
		}
		
		long timeStamp = new Date().getTime();

		Document document = null;
		
		
		if ( new File(file).exists() ) {
	
			SAXBuilder builder = new SAXBuilder(false);
			document = builder.build(file);
		}
		else {
			document = new Document();
			Element root = new Element(ROOT_ELEM);
			document.setRootElement(root);
		}
	
		Element dependencyGroup = new Element(DEPENDENCY_GROUP_ELEM);
		dependencyGroup.setAttribute(PROJECT_NAME_ATTR, group.getProjectName());
		
		List candidates = document.getRootElement().getChildren(DEPENDENCY_GROUP_ELEM);
		
		for (int i = 0; i < candidates.size(); i++) {
			Element elem = (Element) candidates.get(i);
			if ( elem.getAttributeValue(PROJECT_NAME_ATTR).equals(group.getProjectName()) )  {
				dependencyGroup = elem;
				document.getRootElement().removeContent(elem);
			}
		}
		
		dependencyGroup.setAttribute(TIMESTAMP_ATTR, Long.toString(timeStamp));
		
//		if ( group.getDependencies().size() == 0 ) {
//			document.getRootElement().addContent(dependencyGroup);
//			return;
//		}
		
		saveDependencies(group.getDependencies(), timeStamp, dependencyGroup);
		saveDependencies(group.getExcludedDependencies(), 0, dependencyGroup);
		
		log.debug("Saving " + dependencyGroup.getChildren(DEPENDENCY_ELEM).size() + " dependencies");
		
		document.getRootElement().addContent(dependencyGroup);
	
		File saveFile = new File(file); 
	
		JDomOutputter.output(document, saveFile, false);
	
	}

	private static void saveDependencies(List dependencies, long timeStamp, Element dependencyGroup) {
		log.debug("saving " + dependencies.size() + " dependencies - timestamp=" + timeStamp);
		List previousDependencies = null;
		if ( dependencyGroup != null ) {
			previousDependencies = dependencyGroup.getChildren(DEPENDENCY_ELEM);
		}
		if ( previousDependencies == null ) {
			previousDependencies = new ArrayList();
		}
		
		
		for (int i = 0; i < dependencies.size(); i++) {
			Dependency dependency = (Dependency) dependencies.get(i);
			Element dependencyElement = new Element(DEPENDENCY_ELEM);
			dependencyElement.setAttribute(GROUP_ID_ATTR, dependency.getGroupId() == null ? "" : dependency.getGroupId()) ;
			dependencyElement.setAttribute(ARTIFACT_ID_ATTR, dependency.getArtifactId() == null ? "" : dependency.getArtifactId()) ;
			dependencyElement.setAttribute(ARTIFACT_ATTR, dependency.getArtifact() == null ? "" : dependency.getArtifact()) ;
			dependencyElement.setAttribute(VERSION_ATTR, dependency.getVersion() == null ? "" : dependency.getVersion()) ;
			dependencyElement.setAttribute(TYPE_ATTR, dependency.getType() == null ? "" : dependency.getType()) ;
			dependencyElement.setAttribute(TIMESTAMP_ATTR, Long.toString(timeStamp));
			
			saveDependencyProperties(dependency, dependencyElement, timeStamp);
			
			
			for (int j = 0; j < previousDependencies.size(); j++) {
				Element depElem = (Element) previousDependencies.get(j);
				if ( depElem.getAttributeValue(ARTIFACT_ATTR).equals(dependency.getArtifact()) ) {
					dependencyGroup.removeContent(depElem);
				}
			}
			
			dependencyGroup.addContent( dependencyElement );
						
		}
	}
	
	private static void saveDependencyProperties(Dependency dependency, Element dependencyElement, long timestamp) {
		Element elem = dependencyElement.getChild(PROPERTIES_ELEM);
		if ( elem != null ) {
			dependencyElement.removeChild(PROPERTIES_ELEM);
		}
		Element propertiesElement = new Element(PROPERTIES_ELEM);
		
		Map depProperties = dependency.getProperties();
		Iterator it = depProperties.keySet().iterator();
		 
		while ( it.hasNext() ) {
			
			String nextKey =  (String) it.next();
			String nextValue = (String) depProperties.get(nextKey);
			
			Element propertyElement = new Element(PROPERTY_ELEM);
			
			propertyElement.setAttribute(PROPERTY_KEY, nextKey);
			propertyElement.setAttribute(PROPERTY_VALUE, nextValue);
			propertyElement.setAttribute(TIMESTAMP_ATTR, Long.toString(timestamp));
			
			propertiesElement.addContent(propertyElement);
			
		}
		
		dependencyElement.addContent(propertiesElement);
		
	}
}
