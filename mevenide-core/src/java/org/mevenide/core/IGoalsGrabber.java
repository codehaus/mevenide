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
package org.mevenide.core;

import java.util.Collection;

/**
 * really just for mocking purpose since MockCreator 
 * doesnt support abstract classes.
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: IGoalsGrabber.java 3 mai 2003 22:20:0113:34:35 Exp gdodinet 
 * 
 */
public interface IGoalsGrabber {
	/**
	 * initialize goalsBean from the file whose path is passed as parameter 
	 * @param xmlGoals the location of the file holding the available goals
	 */
	public abstract void load(String xmlGoals);
	/**
	 * return the available plugins 
	 * @return Collection
	 */
	public abstract Collection getPlugins();
	/**
	 * return the goals declared by the plugin whose name is passed as parameter
	 * @param plugin   
	 * @return Collection
	 */
	public abstract Collection getGoals(String plugin);
	/**
	 * return the description of plugin:goal 
	 * @param plugin
	 * @param goal
	 * @return
	 */
	public abstract String getDescription(String plugin, String goal);
	/**
	 * load the goalsBean, required preinitialization is 
	 * under the subclass responsability
	 * @throws Exception
	 * @see org.mevenide.ui.eclipse.GoalsGrabber
	 */
	public abstract void load() throws Exception;
	/**
	 * create/update a maven.xml file which specifies postgoal for eclipse:get-goals  
	 * @note public visibility for testing purpose.. crap !
	 * @param effectiveDirectory
	 * @param output
	 */
	public abstract void createMavenXmlFile(
		String effectiveDirectory,
		String output);
	
}