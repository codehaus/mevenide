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
package org.mevenide.ui.eclipse.launch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.util.JDomOutputter;


/**
  @author <a href="mailto:rhill@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class LaunchMarshaller {
	
	private static String ROOT_ELEM = "launchedActions";
	private static String ACTION_ELEM = "action";
	private static String PROJECT_ATTR = "project";
	
	private static String GOALS_ELEM = "goals";
	private static String GOAL_NAME_ATTR = "name";
	private static String GOAL_ELEM = "goal";
	
	private static String OPTIONS_ELEM = "options";
	private static String OPTION_ELEM = "option";
	private static String OPTION_NAME_ATTR = "value";
	
	private LaunchMarshaller() {
	}
	
	public static void saveConfig(LaunchedAction action) throws Exception {
		String file = Mevenide.getPlugin().getFile("launchedActions.xml");
		
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
		
		Element actionElem = new Element(ACTION_ELEM);
		actionElem.setAttribute(PROJECT_ATTR, action.getProject().getName());
		
		Element goalsElem = new Element(GOALS_ELEM);
		for (int i = 0; i < action.getGoals().length; i++) {
			Element goalElem = new Element(GOAL_ELEM);
			goalElem.setAttribute(GOAL_NAME_ATTR, action.getGoals()[i]);
			goalsElem.addContent(goalElem);
		}
		actionElem.addContent(goalsElem);
		
		Element optionsElem = new Element(OPTIONS_ELEM);
		for (int i = 0; i < action.getOptions().length; i++) {
			Element optionElem = new Element(OPTION_ELEM);
			optionElem.setAttribute(OPTION_NAME_ATTR, action.getOptions()[i]);
			optionsElem.addContent(optionElem);
		}
		actionElem.addContent(optionsElem);
		
		document.getRootElement().addContent(actionElem);
		
		File saveFile = new File(file); 
	
		JDomOutputter.output(document, saveFile, false);
	}
	
	public static void clearConfigs() {
		
	}
	
	public static List getSavedConfigs() {
		
		return new ArrayList();
	}
}
