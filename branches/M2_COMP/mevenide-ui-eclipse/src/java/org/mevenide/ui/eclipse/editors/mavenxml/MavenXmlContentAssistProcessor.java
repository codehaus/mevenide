/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.ui.eclipse.editors.mavenxml;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.mevenide.goals.grabber.DefaultGoalsGrabber;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.goals.manager.GoalsGrabbersManager;
import org.mevenide.ui.eclipse.goals.model.Goal;
import org.mevenide.ui.eclipse.goals.model.Plugin;
import org.mevenide.util.StringUtils;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: MavenXmlContentAssistProcessor.java,v 1.1 18 avr. 2004 Exp gdodinet 
 * 
 */
public class MavenXmlContentAssistProcessor implements IContentAssistProcessor {
	
	private static final Log log = LogFactory.getLog(MavenXmlContentAssistProcessor.class);
	
	private IGoalsGrabber goalsGrabber;
	
	public MavenXmlContentAssistProcessor(TextEditor editor) throws Exception {
		
		IFile editedFile =((FileEditorInput) editor.getEditorInput()).getFile();
		log.debug("Creating GoalsGrabberManager from " + editedFile.getLocation().toOSString());
		try {
			goalsGrabber = GoalsGrabbersManager.getGoalsGrabber(editedFile.getLocation().toOSString());
		} 
		catch (Exception e) {
		    String message = "Cannot create GoalsGrabberManager from " +  editedFile.getLocation().toOSString() + 
                             ". Trying to instantiate the default one..";
			log.error(message, e);
			goalsGrabber = new DefaultGoalsGrabber();
		}
		
	}
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
		IDocument doc = viewer.getDocument();
		
		//Retrieve current selection range
		Point selectedRange = viewer.getSelectedRange();
		
		List proposalList = new ArrayList();
		
		//Create completion proposal array
		ICompletionProposal[] proposals;
		try {
			if (selectedRange.y == 0) {
				//Retrieve tag name
			    String qualifier = getQualifier(doc, documentOffset);

			    //Compute completion proposals
			    proposalList.addAll(computeCompletionTagProposals(qualifier, documentOffset));
				
			    if ( log.isDebugEnabled() ) {
					log.debug("Found the following matching proposals for qualifier " + qualifier);
					for (int i = 0; i < proposalList.size(); i++) {
						log.debug("\t> " + proposalList.get(i));
					}
				}
			}

		} 
		catch (RuntimeException e) {
			if ( log.isDebugEnabled() ) {
			   log.error("Problem occured while computing proposals", e); 
			}
		}
		
		proposals = new ICompletionProposal[proposalList.size()];
		proposalList.toArray(proposals);
		return proposals;
	}
	
	private List computeCompletionTagProposals(String qualifier, int documentOffset) {
		List proposals = new ArrayList();
		Goal goal = createGoalFromQualifier(qualifier);
		if ( goal.getPlugin() == null ) {
			//goal name might actually be a plugin name
			String[] plugins = goalsGrabber.getPlugins();
			proposals.addAll(findMatchingNames(goal, plugins, documentOffset));
			
			//or it may be a custom goal ?
		}
		else {
			String[] goals = goalsGrabber.getGoals(goal.getPlugin().getName());
			proposals.addAll(findMatchingNames(goal, goals, documentOffset));
		}
		return proposals;
	}

	private List findMatchingNames(Goal goal, String[] names, int documentOffset) {
		List matches = new ArrayList();
		if ( names != null ) {
			for (int i = 0; i < names.length; i++) {
				if ( names[i].startsWith(goal.getFullyQualifiedName()) ) {
					String proposalInformation = goalsGrabber.getDescription(names[i]);
				    CompletionProposal proposal = new CompletionProposal(names[i], documentOffset, 0, names[i].length(), 
				                                                         null, names[i], null, proposalInformation);
				    matches.add(proposal);
				}
			}
		}
		return matches;
	}

	private Goal createGoalFromQualifier(String qualifier) {
		String[] parts = StringUtils.splitGoal(qualifier);
		Goal goal = new Goal();
		goal.setName(parts[1]);
		if ( parts[0].length() > 0 ) {
			Plugin plugin = new Plugin();
			plugin.setName(parts[0]);
			goal.setPlugin(plugin);
		}
		return goal;
	}

	private String getQualifier(IDocument doc, int documentOffset) {
		StringBuffer buf = new StringBuffer();
		while (true) {
			try {
				//Read character backwards
				char c = doc.getChar(--documentOffset);
				
				if ( buf.length() == 0 && c == '\"' ) {
					//we were outside of name attribute
					return "";
				}
				
				if ( Character.isWhitespace(c)) {
					return "";
				}
				
				switch ( c ) {
					case '\"': return buf.reverse().toString(); 
					case '>' : return "";
					case '<' : return buf.reverse().toString();
					default  : buf.append(c); 
				}
			} 
			catch (BadLocationException e) {
				//Document start reached, no tag found
				return "";
			}
		}
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public char[] getCompletionProposalAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public char[] getContextInformationAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public IContextInformationValidator getContextInformationValidator() {
		// TODO Auto-generated method stub
		return null;
	}
}
