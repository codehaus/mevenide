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
			
			log.debug("Found " + allSourceDirectoryList.size() + " previously saved SourceDirectories - " + sourceDirectoryList.size() + " active ones");
			
			for (int i = 0; i < group.getSourceDirectories().size(); i++) {
				SourceDirectory dir = (SourceDirectory) group.getSourceDirectories().get(i);
				boolean shouldAdd = true;
				for (int j = 0; j < allSourceDirectoryList.size(); j++) {
					SourceDirectory savedDir = (SourceDirectory) allSourceDirectoryList.get(j);
					if ( dir.getDirectoryPath().equals(savedDir.getDirectoryPath())) {
						shouldAdd = false;
						break;
					}
				}
				if ( shouldAdd ) {
					sourceDirectoryList.add(dir);
				}
			}
			
			
			group.setSourceDirectories(sourceDirectoryList);
			
			log.debug("Finished loading SourceDirectoryGroup artifacts. Found " + sourceDirectoryList.size());
			
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
		
		saveSourceDirectories(group.getSourceDirectories(), timestamp, sourceDirGroup);
		saveSourceDirectories(group.getExcludedSourceDirectories(), 0, sourceDirGroup);
		
		document.getRootElement().addContent(sourceDirGroup);
		
		File saveFile = new File(file); 
		
		JDomOutputter.output(document, saveFile, false);
		
	}
	private static void saveSourceDirectories(List sourceDirectories, long timestamp, Element sourceDirGroup) {
		log.debug("Saving back " + sourceDirectories.size() + " SourceDirectories - timestamp=" + timestamp);
		List previousSourceDirectories = sourceDirGroup.getChildren("sourceDirectory");
		if ( previousSourceDirectories == null ) {
			previousSourceDirectories = new ArrayList();
		}
		
		for (int i = 0; i < sourceDirectories.size(); i++) {
			SourceDirectory dir = (SourceDirectory) sourceDirectories.get(i);
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
	}
}
