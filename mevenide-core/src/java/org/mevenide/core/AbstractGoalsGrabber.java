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
import java.util.Collection;

import org.apache.commons.discovery.tools.DiscoverClass;
import org.mevenide.core.util.PostGoal;

import com.gdfact.maven.plugin.getgoals.GoalsBean;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public abstract class AbstractGoalsGrabber implements IGoalsGrabber{
    /** singleton related */
	private static AbstractGoalsGrabber grabber = null;
    private static Object lock = new Object();

    /** goals marshaller/unmarshaller */
	protected GoalsBean goalsBean; 
	
    /** maven runner needed since we use the eclipse:get-goals goal */
    protected AbstractRunner mavenRunner;
    
    /** xml file that holds the available goals */
	protected String xmlGoals;
	
    /**
     * initialize goalsBean from the file whose path is passed as parameter 
     * @param xmlGoals the location of the file holding the available goals
     */ 
	public void load(String xmlGoals) {
		try {
			mavenRunner = AbstractRunner.getRunner();
			goalsBean = new GoalsBean();
			this.xmlGoals = xmlGoals;
			if ( !new File(xmlGoals).exists() ) {
				load();
			}
			goalsBean.unMarshall(xmlGoals);
		}
		catch ( Exception ex ) {
			ex.printStackTrace();  
		}
	}

    /**
     * return the available plugins 
     * @return Collection
     */
	public Collection getPlugins()  {
		if ( goalsBean != null ) { 
			return goalsBean.getGoalCategories();
		}
		return null;
	}

    /**
     * return the goals declared by the plugin whose name is passed as parameter
     * @param plugin   
     * @return Collection
     */
	public Collection getGoals(String plugin) {
		if ( goalsBean != null ) {
			return goalsBean.getGoals(plugin);
		}
		return null;
	}
    
    /**
     * return the description of plugin:goal 
     * @param plugin
     * @param goal
     * @return
     */
	public String getDescription(String plugin, String goal) {
		if ( goalsBean != null ) {
			return goalsBean.getDescription(plugin, goal);
		}
		return null;
	}
	
    /**
     * load the goalsBean, required preinitialization is 
     * under the subclass responsability
     * @throws Exception
     * @see org.mevenide.ui.eclipse.GoalsGrabber
     */
	public abstract void load() throws Exception ;
    
    /** 
     * protected singleton constructor 
     */
    protected AbstractGoalsGrabber()  {
    }
    
    /**
     * singleton factory 
     * @param xmlGoals
     * @return AbstractGoalsGrabbers
     * @throws Exception
     */
    public static IGoalsGrabber getGrabber(String xmlGoals) throws Exception {
        if (grabber != null) {
            return grabber;
        }
        synchronized (lock) {
            if (grabber == null) {
                grabber = (AbstractGoalsGrabber) new  DiscoverClass().newInstance(AbstractGoalsGrabber.class);
                grabber.load(xmlGoals);
            }
            return grabber;
        }
    }
    
    /**
     * create/update a maven.xml file which specifies postgoal for eclipse:get-goals  
     * @note public visibility for testing purpose.. crap !
     * @param effectiveDirectory
     * @param output
     */
    public void createMavenXmlFile(String effectiveDirectory, String output) {
        File mavenXml = new File(effectiveDirectory, "maven.xml");
        if ( !PostGoal.validate(mavenXml, output) ) {
            PostGoal.create(mavenXml);
        }
    }
	


	/**
	 * @return GoalsBean the bean that holds the registered plugins
	 */
	public GoalsBean getGoalsBean() {
		return goalsBean;
	}

}

