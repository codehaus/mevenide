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
 */
package org.mevenide.core;

import java.io.File;

import org.apache.commons.discovery.tools.DiscoverClass;


/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public abstract class AbstractRunner {
    /** unmodifiable Maven options */
    private String[] finalOptions = null;
    
    /** finalOptions synchronization object */
    private Object optionsLock = new Object();
   
    /**
     * lazyloading (dcl)
     * @return String[] unmodifiable Maven options
     */  
    public String[] getFinalOptions() {
        if ( finalOptions != null ) {
            return finalOptions;
        }
        synchronized (optionsLock) {
            if ( finalOptions == null ) {
                String basedir = getBasedir();
                finalOptions = new String[3];
                finalOptions[0] = "-b";
                finalOptions[1] = "-f";
                String tmpFile = basedir != null ? basedir + File.separator + "project.xml" : "project.xml" ;
				finalOptions[2] = "file:///" + new File(tmpFile).getAbsolutePath();
            }
            return finalOptions;
        }
    }
    
    /** 
	 * get a concrete implementation of AbstractRunner using commons-discovery
	 * 
	 * @throws Exception
	 */
	public static AbstractRunner getRunner() throws Exception {
		return (AbstractRunner) new DiscoverClass().newInstance(AbstractRunner.class);
	}

	/**
	 * configure the environment and run the specified goals in a new VM 
     * with the given environment
	 *  
	 * @param goals String[] the goals to run
	 */
	public void run(String[] options, String[] goals) {
		String userDir = null;
		
		try {
			//backup user.dir. still needed ?
            userDir = System.getProperty("user.dir");

			setUpEnvironment();
			
			launchVM(options, goals);

		} 
        catch (Exception ex) {
			//ex.printStackTrace();
		} 
        finally {
			//restore user.dir
			if (userDir != null) {
				System.setProperty("user.dir", userDir);
			}
		}
	}
    
    private final void setUpEnvironment() throws Exception {
    	initEnvironment();
    }
    
    /**
     * construct Maven from of unmodifiable otpions, user-defined options and goals
     * 
     * @param options user-defined options
     * @param goals goals to run
     * @return String[] the complete Maven args
     */
    protected String[] getMavenArgs(String[] options, String[] goals) {
        
        String[] mavenArgs = 
                new String[options.length 
                            + goals.length 
                            + getFinalOptions().length];
        
        System.arraycopy(getFinalOptions(), 0, mavenArgs, 0, getFinalOptions().length);
        System.arraycopy(options, 0, mavenArgs, getFinalOptions().length, options.length);
        System.arraycopy(goals, 0, mavenArgs, getFinalOptions().length + options.length , goals.length);
       
        return mavenArgs;
    }

    /**
	 * set the environment variables needed by Maven
	 * 
	 * @see org.mevenide.core.Environment
	 */
	protected abstract void initEnvironment() throws Exception ;

	/**
	 * @return String the working Directory
	 */
	protected abstract String getBasedir();
    
    /**
     * return the specified goals with the specified Maven options in a new VM
     * 
     * @note i tohught a new VM should have solved classloading encountered with eclipse plugin.
     * @note however that wasnt the case. so is a new VM really required ? should we let the user choose ? 
     * 
     * @param options
     * @param goals
     * @throws Exception
     */
    protected abstract void launchVM(String[] options, String[] goals) throws Exception ;



}
