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
package org.mevenide.ui.eclipse;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

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
	/** the eclipse plugin */
    private MavenPlugin plugin = MavenPlugin.getPlugin();
    
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
		//@todo TESTME
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
		//@todo TESTME
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
	protected void initialize() {
        if ( plugin == null ) {
            plugin = MavenPlugin.getPlugin();
        }
        String prefs = plugin.getGoalsPreferencesFilename();
        preferenceStore = new PreferenceStore(prefs);
        try {
			preferenceStore.load();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
        xmlGoals = new File(plugin.getXmlGoalsFile());
	}

	
    
}
