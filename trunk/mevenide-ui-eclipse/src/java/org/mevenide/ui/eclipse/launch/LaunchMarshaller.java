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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.util.JDomOutputter;


/**
  @author <a href="mailto:rhill@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class LaunchMarshaller {
	
	private static final String ACTIONS_FILE = Mevenide.getPlugin().getFile("launchedActions.xml");
	
	private static String ROOT_ELEM = "launchedActions";
	
	private static String ACTION_ELEM = "action";
	private static String PROJECT_ATTR = "project";
	private static String LAST_LAUNCHED_ATTR = "isLastLaunchedAction";
	
	private static String GOALS_ELEM = "goals";
	private static String GOAL_NAME_ATTR = "name";
	private static String GOAL_ELEM = "goal";
	
	private static String OPTIONS_ELEM = "options";
	private static String OPTION_ELEM = "option";
	private static String OPTION_NAME_ATTR = "value";
	
	private LaunchMarshaller() {
	}
	
	public static void saveConfig(LaunchedAction action) throws Exception {
		
		Document document = getDocument();
		
		List previouslyLaunched = document.getRootElement().getChildren();
		for (int i = 0; i < previouslyLaunched.size(); i++) {
			Element elem = (Element) previouslyLaunched.get(i);
			elem.setAttribute(LAST_LAUNCHED_ATTR, "false");
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
		
		actionElem.setAttribute(LAST_LAUNCHED_ATTR, "true");
		
		document.getRootElement().addContent(actionElem);
		
		JDomOutputter.output(document, new File(ACTIONS_FILE), false);
	}

	private static Document getDocument() throws JDOMException {
	
		Document document = null;
		
		
		if ( new File(ACTIONS_FILE).exists() ) {
		
			SAXBuilder builder = new SAXBuilder(false);
			document = builder.build(ACTIONS_FILE);
			
		}
		else {
			document = new Document();
			Element root = new Element(ROOT_ELEM);
			document.setRootElement(root);
		}
		return document;
	}
	
	public static void removeConfig(LaunchedAction action) throws Exception {

		Document document = getDocument();
		
		List previouslyLaunched = document.getRootElement().getChildren();
		for (int i = 0; i < previouslyLaunched.size(); i++) {
			Element elem = (Element) previouslyLaunched.get(i);
			if ( elem.getAttributeValue(PROJECT_ATTR).equals(action.getProject().getName()) ) {
				document.getRootElement().removeContent(elem);	
			} 
		}
		
		JDomOutputter.output(document, new File(ACTIONS_FILE), false);
	}
	
	public static void clearConfigs() throws Exception {
		Document document = getDocument();
		
		document.getRootElement().removeChildren();
		
		JDomOutputter.output(document, new File(ACTIONS_FILE), false);
	}
	
	public static List getSavedConfigs() throws Exception {
		List previous = new ArrayList();
		
		Document document = getDocument();
		
		List previouslyLaunched = document.getRootElement().getChildren();
		
		for (int i = 0; i < previouslyLaunched.size(); i++) {
			Element elem = (Element) previouslyLaunched.get(i);
			
			String projectName = elem.getAttributeValue(PROJECT_ATTR);
			
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			String[] actionOptions = extractOptions(elem);
			String[] actionGoals = extractGoals(elem);
			
			LaunchedAction action = new LaunchedAction(project, actionOptions, actionGoals);
			previous.add(action);
			
			if ( Boolean.getBoolean(elem.getAttributeValue(LAST_LAUNCHED_ATTR)) ) {
				LaunchHistory.getHistory().setLastlaunched(action);
			}
			
			elem.getAttributeValue(LAST_LAUNCHED_ATTR);
		}
		
		return previous;
	}

	private static String[] extractGoals(Element elem) {
		List declaredGoals = new ArrayList(); 
		
		Element goals = elem.getChild(GOALS_ELEM);
		List allGoals = goals.getChildren(GOAL_ELEM);
		for (int k = 0; k < allGoals.size(); k++) {
			Element goal = (Element) allGoals.get(k);
			declaredGoals.add(goal.getAttributeValue(GOAL_NAME_ATTR));
		}
		
		String[] actionGoals = new String[declaredGoals.size()];
		for (int j = 0; j < actionGoals.length; j++) {
			actionGoals[j] = (String) declaredGoals.get(j);
		}
		
		return actionGoals;
	}
	
	private static String[] extractOptions(Element elem) {
		List declaredGoals = new ArrayList(); 
	
		Element goals = elem.getChild(OPTIONS_ELEM);
		List allGoals = goals.getChildren(OPTION_ELEM);
		for (int k = 0; k < allGoals.size(); k++) {
			Element goal = (Element) allGoals.get(k);
			declaredGoals.add(goal.getAttributeValue(OPTION_NAME_ATTR));
		}
	
		String[] actionOptions = new String[declaredGoals.size()];
		for (int j = 0; j < actionOptions.length; j++) {
			actionOptions[j] = (String) declaredGoals.get(j);
		}
	
		return actionOptions;
	}
}
