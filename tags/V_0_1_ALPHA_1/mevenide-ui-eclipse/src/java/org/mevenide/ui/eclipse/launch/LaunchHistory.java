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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;

/**
 * i guess it will be dropped when i ll take a look at the Eclipse LaunchConfiguration
 * However for now its quicker to implement
 * 
 * @author <a href="mailto:rhill@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class LaunchHistory {
	private static Log log = LogFactory.getLog(LaunchHistory.class);
	
	private LaunchedAction lastlaunched;
	
	private List launchedActions = new ArrayList();
	
	private static LaunchHistory history ;
	
	
	private LaunchHistory() {
	}
	
	
	public synchronized static LaunchHistory getHistory() {
		if ( history == null ) {
			history = new LaunchHistory();
			history.load();
		}
		return history;	
	}
	
	public LaunchedAction[] getLaunchedActions() {
		LaunchedAction[] hist = new LaunchedAction[launchedActions.size()];
		for (int i = 0; i < hist.length; i++) {
			hist[i] = (LaunchedAction) launchedActions.get(i);
		}
		return hist;
	}
	
	/**
	 * save config to disk and add it to memory list (top)
	 * 
	 * @param project
	 * @param options
	 * @param goals
	 */
	public void save(IProject project, String[] options, String[] goals) {
		LaunchedAction action = new LaunchedAction(project, options, goals);
		try {
			
			if ( launchedActions.contains(action) ) {
				
				LaunchMarshaller.removeConfig(action);
				launchedActions.remove(action);
				
			}
	
			LaunchMarshaller.saveConfig(action);
		} 
		catch (Exception e) {
			//e.printStackTrace();
			log.error("Unable to save action " + action + " due to : " + e);
		}
	
		launchedActions.add(0, action);
		lastlaunched = action;
	}
	
	/**
	 * delete memory history and file on disk
	 *
	 */
	public void clear() {
		try {
			LaunchMarshaller.clearConfigs();
			launchedActions = new ArrayList();
			lastlaunched = null;
		} 
		catch (Exception e) {
			//e.printStackTrace();
			log.error("Unable to drop configurations due to : " + e);
		}
	}
	
	/**
	 * 
	 * load previously saved config 
	 *
	 */
	private void load() {
		try {
			launchedActions = LaunchMarshaller.getSavedConfigs();
		} 
		catch (Exception e) {
			e.printStackTrace();
			log.error("Unable to load previously saved configs due to : " + e);
		}
	}
	
	public LaunchedAction getLastlaunched() {
		return lastlaunched;
	}

	public void setLastlaunched(LaunchedAction action) {
		lastlaunched = action;
	}

}
