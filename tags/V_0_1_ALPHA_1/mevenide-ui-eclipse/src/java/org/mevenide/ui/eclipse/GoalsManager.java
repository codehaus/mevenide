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
package org.mevenide.ui.eclipse;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.preference.PreferenceStore;
import org.mevenide.core.AbstractGoalsManager;


/**
 * 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 *
 */
public class GoalsManager extends AbstractGoalsManager{
	private static Log log = LogFactory.getLog(AbstractGoalsManager.class);
	
	/** the eclipse plugin */
    private Mevenide plugin = Mevenide.getPlugin();
    
    /** the preference store used to save/load the runnableGoals */
    private PreferenceStore preferenceStore;
    
    /** list of available goals */
    private File xmlGoals;
   
    /**
	 * @see org.mevenide.core.AbstractGoalsManager#getXmlGoals()
	 */
	protected File getXmlGoals() {
		return xmlGoals;
	}

	/** 
	 * @see org.mevenide.core.AbstractGoalsManager#load()
	 */
	protected void load() throws IOException {
		String storedPlugins = this.preferenceStore.getString("mevenide.goals.plugins");
		StringTokenizer pluginsTokenizer = new StringTokenizer(storedPlugins, ";");
		while ( pluginsTokenizer.hasMoreTokens() ) {
			String plugin = pluginsTokenizer.nextToken();
			load(plugin);
		}
	}
	private void load(String plugin) {
		String storedGoals = this.preferenceStore.getString("mevenide.goals.plugin." + plugin);
		StringTokenizer goalsTokenizer = new StringTokenizer(storedGoals, ";");
		while ( goalsTokenizer.hasMoreTokens() ) {
			String goal = goalsTokenizer.nextToken();
			this.addGoal(plugin, goal);
		}
	}

	/** 
	 * @see org.mevenide.core.AbstractGoalsManager#save()
	 */
	public void save() throws IOException {
		storePlugins();
		preferenceStore.save();
	}
	private void storePlugins() {
		String plugins = "";
		String[] selectedPlugins = getPlugins();
		for (int i = 0; i < selectedPlugins.length; i++) {
			String plugin = selectedPlugins[i]; 
			plugins += plugin + ";";
			storeGoals(plugin);
		}
		preferenceStore.setValue("mevenide.goals.plugins", plugins);
	}
	private void storeGoals(String plugin) {
		String goals = "";
		Collection selectedGoals = goalsGrabber.getGoals(plugin);
		for (Iterator iter = selectedGoals.iterator(); iter.hasNext();) {
			goals += (String) iter.next() + ";";
			
		}
		preferenceStore.setValue("mevenide.goals.plugin." + plugin, goals);
	}
	
	/**
	 * @see org.mevenide.core.AbstractGoalsManager#initialize()
	 */
	protected void initialize() throws Exception {
        if ( plugin == null ) {
            plugin = Mevenide.getPlugin();
        }
        String prefs = plugin.getGoalsPreferencesFilename();
        if ( !new File(prefs).exists() ) {
        	new File(prefs).createNewFile();
        }
        preferenceStore = new PreferenceStore(prefs);
        try {
			preferenceStore.load();
		} 
		catch (IOException e) {
			log.debug("Unable to load goals preference PreferenceStore due to : " + e);
		}
        xmlGoals = new File(plugin.getXmlGoalsFile());
	}

	
    
}
