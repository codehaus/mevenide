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
		
		List previouslyLaunched = document.getRootElement().getChildren(ACTION_ELEM);
		
		for (int i = 0; i < previouslyLaunched.size(); i++) {
			Element elem = (Element) previouslyLaunched.get(i);
			
			String projectName = elem.getAttributeValue(PROJECT_ATTR);
			
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			String[] actionOptions = extractOptions(elem);
			String[] actionGoals = extractGoals(elem);
			
			LaunchedAction action = new LaunchedAction(project, actionOptions, actionGoals);
			previous.add(action);
			
			if ( Boolean.valueOf(elem.getAttributeValue(LAST_LAUNCHED_ATTR)).booleanValue() ) {
				LaunchHistory.getHistory().setLastlaunched(action);
			}
			
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
