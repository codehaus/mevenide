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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mevenide.*;


/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public abstract class AbstractGoalsManager implements IGoalsManager {
	
	/** 
     * list of goals to be run. 
     * 
     * I used to use a Map instead of a Set, but it appears that using 
     * a Set makes finally things simpler. 
     * 
     * So a goal is now indentified thanks its fully qualified form plugin:goal 
     *
     */
    protected Set runnableGoals = new TreeSet();

    /** bean that holds the list of all available goals */
	private IGoalsGrabber goalsGrabber;
	
    /**
     * initalize goalsGrabber and load the previously saved runnableGoals
     * implement the Template pattern :
     *      1. [abstract] initialize() 
     *      2. initialize goalsGrabber
     *      3. [abstract] load() 
     */
	public AbstractGoalsManager() {
        try {
            //give subclasses a chance to do proper initialization 
            initialize();
            goalsGrabber = AbstractGoalsGrabber.getGrabber(getXmlGoals().getAbsolutePath());
            load();
        }
        catch ( Exception e ) {
            e.printStackTrace();
            throw new RuntimeException("A problem occured while trying to load the checked categories/goals. Reason : " + e);
        }
	}

	/**
     * add the plugin:goal to the list of runnable goals. 
     * If goal is null just add plugin
     * 
	 * @param plugin
	 * @param goal 
	 */
	public void addGoal(String plugin, String goal) throws GoalNotFoundException {
		assertExist(plugin, goal);
        String runnableGoal = goal == null ? plugin : plugin + ":" + goal;
		runnableGoals.add(runnableGoal);
	}

    /**
	 * remove plugin:goal from the list of runnable goals
     * if goal is null, remove all goals declared by the plugin 
     * 
     * 
	 * @param plugin
	 * @param goal  
	 */
	public void removeGoal(String plugin, String goal) {
        if ( goal != null ) {
            String runnableGoal = plugin + ":" + goal;
            runnableGoals.remove(runnableGoal);
        }
        else {
            Set goalsCopy = new TreeSet();
            goalsCopy.addAll(runnableGoals);
            Iterator iterator = goalsCopy.iterator();
            while (iterator.hasNext()) {
				String nextGoal = (String) iterator.next();
				if ( nextGoal.startsWith(plugin) ) {
                    runnableGoals.remove(nextGoal);
				}
			}
        }
         
		
	}
	
    /**
     * check that plugin:goal exists 
     * if goal is null just check the existence of plugin
     *  
     * @param plugin
     * @param goal
     * 
     * @see org.mevenide.core.AbstractGoalsGrabber
     */
    private void assertExist(String plugin, String goal)  {
        if ( goalsGrabber.getGoals(plugin) == null || 
            ( goal != null && !goalsGrabber.getGoals(plugin).contains(goal) )) {
            String runnableGoal = goal == null ? plugin : plugin + ":" + goal;
            throw new GoalNotFoundException(runnableGoal + " not found");
        }
    }
	
    /**
     * @return String[] the list of goals to be run
     */
	public String[] getGoalsToRun() {
        String[] goalsToRun = new String[runnableGoals.size()];
    	Iterator iterator = runnableGoals.iterator();
        int i = 0; 
        while ( iterator.hasNext() ) {
            goalsToRun[i++] = (String) iterator.next();
        }
		return goalsToRun;
	}
	
    /**
     * 
     * @param plugin
     * @return String[] the selected goals under <code>plugin</code> control 
     */
    public String[] getGoals(String plugin) {
        List selectedGoals = new ArrayList(); 
        Iterator iterator = runnableGoals.iterator();
        while ( iterator.hasNext() ) {
            String goal = (String) iterator.next();
            if ( goal.equals(plugin) ) {
                selectedGoals.addAll(goalsGrabber.getGoals(plugin));
                break;
            }
            if ( goal.startsWith(plugin) ) {
                selectedGoals.add(goal);    
            }
        }
        String[] goals = new String[selectedGoals.size()];
        for (int i = 0; i < selectedGoals.size(); i++) {
			goals[i] = (String) selectedGoals.get(i);
		}
        return goals;
    }
    
    /**
     * run the runnableGoals
     * @todo FUNCTIONAL add options
     */
    public void runGoals() {
        try {
            AbstractRunner.getRunner().run(new String[0], getGoalsToRun());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @see org.mevenide.core.IGoalsManager#reset()
     */
    public void reset() {
       runnableGoals.clear();
    }
    
	/**
     * initialize runnableGoals from a previous saved state
     * it is the subclass responsability to determine the format of the saved state
     * @throws IOException
     */
	protected abstract void load() throws IOException;
   
    /**
     * @throws IOException
     */
    public abstract void save() throws IOException;
    
    /**
     * @return File the marshalled list of available goals
     * @see com.gdfact.maven.plugin.getgoals.GoalsBean
     */
	protected abstract File getXmlGoals();
    
    /** 
     * pre-initialization 
     */
    protected abstract void initialize();

	/**
	 * for testing purpose
	 */
	Set getRunnableGoals() {
		return runnableGoals;
	}

}
