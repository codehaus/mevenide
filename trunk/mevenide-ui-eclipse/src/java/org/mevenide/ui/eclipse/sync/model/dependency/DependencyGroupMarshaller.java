/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.ui.eclipse.sync.model.dependency;

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
import org.mevenide.util.JDomUtils;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class DependencyGroupMarshaller {
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
	private static final String INHERIT_ATTR = "isInherited";

    private static final String PROPERTY_VALUE = "value";
	private static final String PROPERTY_KEY = "key";
	private static final String PROPERTY_ELEM = "property";
	private static final String PROPERTIES_ELEM = "properties";
	
	
	
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
					boolean isGroupInherited = Boolean.valueOf(dependencyGroupElement.getAttributeValue(INHERIT_ATTR)).booleanValue();
					group.setInherited(isGroupInherited);
					log.debug("Group.isInherited = " + (isGroupInherited));
					
					List sources = dependencyGroupElement.getChildren(DEPENDENCY_ELEM);
					
					for (int j = 0; j < sources.size(); j++) {
						Element dependencyElement =  (Element) sources.get(j);
						
						Dependency dependency = new Dependency();
						
						String savedArtifact = dependencyElement.getAttributeValue(ARTIFACT_ATTR);
						//dependency.setArtifact(savedArtifact == null ? "" : savedArtifact);
						dependency.setJar(savedArtifact == null ? "" : savedArtifact);
						
						String savedArtifactId = dependencyElement.getAttributeValue(ARTIFACT_ID_ATTR);
						dependency.setArtifactId(savedArtifactId == null ? "" : savedArtifactId);
						
						String savedGroupId = dependencyElement.getAttributeValue(GROUP_ID_ATTR);
						dependency.setGroupId(savedGroupId == null ? "" : savedGroupId);
						
						String savedVersion = dependencyElement.getAttributeValue(VERSION_ATTR);
						dependency.setVersion(savedVersion == null ? "" : savedVersion);
						
						String savedType = dependencyElement.getAttributeValue(TYPE_ATTR);
						dependency.setType(savedType == null ? "" : savedType);
						
						
						//dependency.setProperties(getProperties(dependencyElement, timeStamp));
						Map ps = getProperties(dependencyElement, timeStamp);
						Iterator it = ps.keySet().iterator();
						while ( it.hasNext() ) {
							String pName = (String) it.next();
							String pValue = (String) ps.get(pName);
							dependency.addProperty(pName + ":" + pValue);
							dependency.resolvedProperties().put(pName, pValue); 
						}
						
						
						DependencyUtil.refreshGroupId(dependency);
						
						boolean isInherited = Boolean.valueOf(dependencyElement.getAttributeValue(INHERIT_ATTR)).booleanValue();

						String l = dependencyElement.getAttributeValue(TIMESTAMP_ATTR);
						if ( timeStamp == Long.parseLong(l) ) {
							dependenciesList.add(new DependencyWrapper(dependency, isInherited, group));
						}
						allDependenciesList.add(new DependencyWrapper(dependency, isInherited, group));
						
					}
				}
			}
	
			log.debug("Found " + dependenciesList.size() + " previously stored dependencies");
			
//          dont check project again. all required pom dependencies should have been saved to file
 
			List projectDependencies = group.getDependencyWrappers();
			for (int i = 0; i < projectDependencies.size(); i++) {
				boolean alreadyAddedDependency = false;
				DependencyWrapper projectDependency = (DependencyWrapper) projectDependencies.get(i);
				for (int j = 0; j < allDependenciesList.size(); j++) {
					DependencyWrapper savedDependency = (DependencyWrapper) allDependenciesList.get(j);
					if ( savedDependency.getDependency().getArtifact().equals(projectDependency.getDependency().getArtifact()) ) {
						alreadyAddedDependency = true;
						break;
					}
				}
				if ( !alreadyAddedDependency ) {
					dependenciesList.add(projectDependency);
				}
			}
		
			group.setDependencies(dependenciesList);
//			group.setDependencies(new ArrayList());
//			for (int i = 0; i < dependenciesList.size(); i++) {
//                DependencyWrapper wrapper = (DependencyWrapper) dependenciesList.get(i);
//                group.addDependency(wrapper); 
//            }
	
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
		
		log.debug("Saving DependencyGroup - isInherited = " + (group.isInherited()));
		//dependencyGroup.setAttribute(INHERIT_ATTR, Boolean.toString(group.isInherited()));
		
		List candidates = document.getRootElement().getChildren(DEPENDENCY_GROUP_ELEM);
		
		for (int i = 0; i < candidates.size(); i++) {
			Element elem = (Element) candidates.get(i);
			if ( elem.getAttributeValue(PROJECT_NAME_ATTR).equals(group.getProjectName()) )  {
				dependencyGroup = elem;
				document.getRootElement().removeContent(elem);
			}
		}
		
		dependencyGroup.setAttribute(TIMESTAMP_ATTR, Long.toString(timeStamp));
		dependencyGroup.setAttribute(INHERIT_ATTR, Boolean.toString(group.isInherited()));
		log.debug("Saving DependencyGroup - jdomElem.isInherited = " + (dependencyGroup.getAttributeValue(INHERIT_ATTR)));
		
		
//		if ( group.getDependencies().size() == 0 ) {
//			document.getRootElement().addContent(dependencyGroup);
//			return;
//		}
		
		saveDependencies(group.getDependencyWrappers(), timeStamp, dependencyGroup);
		saveDependencies(group.getExcludedDependencyWrappers(), 0, dependencyGroup);
		
		log.debug("Saving " + dependencyGroup.getChildren(DEPENDENCY_ELEM).size() + " dependencies");
		
		document.getRootElement().addContent(dependencyGroup);
	
		File saveFile = new File(file); 
	
		JDomUtils.output(document, saveFile, false);
	
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
			Dependency dependency = ((DependencyWrapper) dependencies.get(i)).getDependency();
			Element dependencyElement = new Element(DEPENDENCY_ELEM);
			dependencyElement.setAttribute(GROUP_ID_ATTR, dependency.getGroupId() == null ? "" : dependency.getGroupId()) ;
			dependencyElement.setAttribute(ARTIFACT_ID_ATTR, dependency.getArtifactId() == null ? "" : dependency.getArtifactId()) ;
			dependencyElement.setAttribute(ARTIFACT_ATTR, dependency.getArtifact() == null ? "" : dependency.getArtifact()) ;
			dependencyElement.setAttribute(VERSION_ATTR, dependency.getVersion() == null ? "" : dependency.getVersion()) ;
			dependencyElement.setAttribute(TYPE_ATTR, dependency.getType() == null ? "" : dependency.getType()) ;
			dependencyElement.setAttribute(TIMESTAMP_ATTR, Long.toString(timeStamp));
			dependencyElement.setAttribute(INHERIT_ATTR, Boolean.toString(((DependencyWrapper) dependencies.get(i)).isInherited()));
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
		
		//Map depProperties = dependency.getProperties();
		Map depProperties = dependency.resolvedProperties();
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
