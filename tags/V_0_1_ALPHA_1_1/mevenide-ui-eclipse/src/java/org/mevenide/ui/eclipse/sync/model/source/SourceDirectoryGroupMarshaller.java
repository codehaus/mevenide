/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
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
package org.mevenide.ui.eclipse.sync.model.source;

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
	private static final String INHERIT_ATTR = "isInherited";
    private static final String TYPE_ATTR = "type";
    private static final String PATH_ATTR = "path";
    private static final String SOURCE_DIRECTORY_ELEM = "sourceDirectory";
    private static final String TIMESTAMP_ATTR = "timestamp";
    private static final String PROJECT_NAME_ATTR = "projectName";
    private static final String SOURCE_DIRECTORY_GROUP_ELEM = "sourceDirectoryGroup";
    private static Log log = LogFactory.getLog(SourceDirectoryGroupMarshaller.class);
	
	private SourceDirectoryGroupMarshaller() { 
	}




	public static SourceDirectoryGroup getSourceDirectoryGroup(IProject project, String file, int arg0) throws Exception {
		SourceDirectoryGroup group = new SourceDirectoryGroup(project);
		List sourceDirectoryList = new ArrayList();
		List allSourceDirectoryList = new ArrayList();
	
	
		if ( new File(file).exists() ) {
	
			SAXBuilder builder = new SAXBuilder(false);
		
			Document document = builder.build(file);
		
			Element projects = document.getRootElement();
			List sourceDirectories = projects.getChildren(SOURCE_DIRECTORY_GROUP_ELEM);
			for (int i = 0; i < sourceDirectories.size(); i++) {
				Element sourceDirectoryGroupElement = 
					(Element) sourceDirectories.get(i);
			
				if ( sourceDirectoryGroupElement.getAttributeValue(PROJECT_NAME_ATTR).equals(project.getName()) ) {
					long timestamp = Long.parseLong(sourceDirectoryGroupElement.getAttributeValue(TIMESTAMP_ATTR));
					boolean isGroupInherited = new Boolean(sourceDirectoryGroupElement.getAttributeValue(INHERIT_ATTR)).booleanValue();
				    group.setInherited(isGroupInherited);
				    
					List sources = sourceDirectoryGroupElement.getChildren(SOURCE_DIRECTORY_ELEM);
					for (int j = 0; j < sources.size(); j++) {
						Element sourceDirectoryElement =  (Element) sources.get(j);
					
						SourceDirectory sourceDirectory 
							= new SourceDirectory(sourceDirectoryElement.getAttributeValue(PATH_ATTR), group);
					
						boolean isInherited = Boolean.valueOf(sourceDirectoryElement.getAttributeValue(INHERIT_ATTR)).booleanValue() ;
						sourceDirectory.setInherited(isInherited);
											
						long sdTimestamp = Long.parseLong(sourceDirectoryElement.getAttributeValue(TIMESTAMP_ATTR));
						if ( sdTimestamp == timestamp ){
							sourceDirectory.setDirectoryType(sourceDirectoryElement.getAttributeValue(TYPE_ATTR));
							sourceDirectoryList.add(sourceDirectory);
						}
						allSourceDirectoryList.add(sourceDirectory);
					}
				}
			}
		
			log.debug("Found " + allSourceDirectoryList.size() + " previously saved SourceDirectories - " + sourceDirectoryList.size() + " active ones");
		
			List projectDependencies = group.getSourceDirectories();
			for (int i = 0; i < projectDependencies.size(); i++) {
				boolean alreadyAddedSourceDirectory = false;
				SourceDirectory projectSourceDirectory = (SourceDirectory) projectDependencies.get(i);
				for (int j = 0; j < allSourceDirectoryList.size(); j++) {
					SourceDirectory savedSourceDirectory = (SourceDirectory) allSourceDirectoryList.get(j);
					if ( savedSourceDirectory.getDirectoryPath().equals(projectSourceDirectory.getDirectoryPath()) ) {
						alreadyAddedSourceDirectory = true;
						break;
					}
				}
				if ( !alreadyAddedSourceDirectory ) {
					sourceDirectoryList.add(projectSourceDirectory);
				}
			}
			
			group.setSourceDirectories(sourceDirectoryList);
		
			log.debug("Finished loading SourceDirectoryGroup artifacts. Found " + sourceDirectoryList.size() + " : ");
			for (int i = 0; i < sourceDirectoryList.size(); i++) {
                log.debug("		" + ((SourceDirectory)sourceDirectoryList.get(i)).getDirectoryPath());
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
		
		Element sourceDirGroup = new Element(SOURCE_DIRECTORY_GROUP_ELEM);
		sourceDirGroup.setAttribute(PROJECT_NAME_ATTR, group.getProjectName());
		
		List candidates = document.getRootElement().getChildren(SOURCE_DIRECTORY_GROUP_ELEM);
		for (int i = 0; i < candidates.size(); i++) {
			Element elem = (Element) candidates.get(i);
			String projectName = elem.getAttributeValue(PROJECT_NAME_ATTR);
			if ( projectName != null && projectName.equals(group.getProjectName()) )  {
				sourceDirGroup = elem;
				document.getRootElement().removeContent(elem);
			}
		}
		
		sourceDirGroup.setAttribute(TIMESTAMP_ATTR, Long.toString(timestamp));
		sourceDirGroup.setAttribute(INHERIT_ATTR, Boolean.toString(group.isInherited()));
		if ( group.getSourceDirectories().size() == 0 ) {
			document.getRootElement().addContent(sourceDirGroup);
			return;
		}
		
		saveSourceDirectories(group.getSourceDirectories(), timestamp, sourceDirGroup);
		saveSourceDirectories(group.getExcludedSourceDirectories(), 0, sourceDirGroup);
		
		document.getRootElement().addContent(sourceDirGroup);
		
		File saveFile = new File(file); 
		
		JDomOutputter.output(document, saveFile, false);
		
	}
	private static void saveSourceDirectories(List sourceDirectories, long timestamp, Element sourceDirGroup) {
		log.debug("Saving back " + sourceDirectories.size() + " SourceDirectories - timestamp=" + timestamp);
		List previousSourceDirectories = sourceDirGroup.getChildren(SOURCE_DIRECTORY_ELEM);
		if ( previousSourceDirectories == null ) {
			previousSourceDirectories = new ArrayList();
		}
		
		for (int i = 0; i < sourceDirectories.size(); i++) {
			SourceDirectory dir = (SourceDirectory) sourceDirectories.get(i);
			Element sourceDir = new Element(SOURCE_DIRECTORY_ELEM);
			
			sourceDir.setAttribute(PATH_ATTR, dir.getDirectoryPath()) ;
			sourceDir.setAttribute(TYPE_ATTR, dir.getDirectoryType());
			sourceDir.setAttribute(TIMESTAMP_ATTR, Long.toString(timestamp));
			sourceDir.setAttribute(INHERIT_ATTR, Boolean.toString(dir.isInherited()));
			
			for (int j = 0; j < previousSourceDirectories.size(); j++) {
				Element dirElem = (Element) previousSourceDirectories.get(j);
				if ( dirElem.getAttributeValue(PATH_ATTR).equals(dir.getDirectoryPath()) ) {
					sourceDirGroup.removeContent(dirElem);
				}
			}
			
			sourceDirGroup.addContent( sourceDir );
		}
	}
}
