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
package org.mevenide.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.GoalNotFoundException;
import org.mevenide.IGoalsGrabber;
import org.mevenide.IGoalsManager;




/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public abstract class AbstractGoalsManager implements IGoalsManager {
	private static Log log = LogFactory.getLog(AbstractGoalsManager.class);
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

	/**
	 * bean that holds the list of all available goals 
	 */
	protected IGoalsGrabber goalsGrabber;

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
            log.debug("Unable to init AbstractGoalsManager due to : " + e);
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
	
	public String[] getPlugins() {
		ArrayList plugins = new ArrayList();
	
		for (int i = 0; i < getGoalsToRun().length; i++) {
			String qualifiedGoal = getGoalsToRun()[i];
			int separator = qualifiedGoal.indexOf(':');
			if ( separator != -1 ) {
				String plugin = qualifiedGoal.substring(0, separator);
				if ( !plugins.contains(plugin) ) {
					plugins.add(plugin);
				}		
			}
			else {
				plugins.add(qualifiedGoal);
			}
		}
	
		return toArray(plugins);
	}
	
	private String[] toArray(List plugins) {
		String[] retVal = new String[plugins.size()];
		for (int i = 0; i < retVal.length; i++) {
			retVal[i] = (String) plugins.get(i);
		}
		return retVal;
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
    protected abstract void initialize() throws Exception ;
 
    /**
	 * for testing purpose
	 */
	Set getRunnableGoals() {
		return runnableGoals;
	}
    

}