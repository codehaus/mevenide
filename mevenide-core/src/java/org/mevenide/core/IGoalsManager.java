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

import java.io.IOException;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: IGoalsManager.java 6 mai 2003 20:03:5813:34:35 Exp gdodinet 
 * 
 */
public interface IGoalsManager {
	/**
	 * add the plugin:goal to the list of runnable goals
	 * if goal is null just add plugin
	 * 
	 * @param category
	 * @param goal 
	 */
	public abstract void addGoal(String plugin, String goal)
		throws GoalNotFoundException;
	/**
	 * remove plugin:goal from the list of runnable goals
	 * if goal is null remove plugin
	 * @param category
	 * @param goals   
	 */
	public abstract void removeGoal(String plugin, String goal);
	/**
	 * @return String[] the list of goals to be run
	 */
	public abstract String[] getGoalsToRun();
	/**
	 * @todo IMPLEMENTME
	 * 
	 * @param plugin
	 * @return String[] the selected goals under <code>plugin</code> control 
	 */
	public abstract String[] getGoals(String plugin);
	/**
	 * run the runnableGoals
	 * @todo FUNCTIONAL add options
	 */
	public abstract void runGoals();
	/**
	 * @throws IOException
	 */
	public abstract void save() throws IOException;
}