/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.runner;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public abstract class AbstractRunner {
	private static final Log log =  LogFactory.getLog(AbstractRunner.class);
	
	
    /** unmodifiable Maven options */
    private String[] finalOptions = null;
    
   
    /**
     * @return String[] unmodifiable Maven options
     */  
    public synchronized String[] getFinalOptions() {
        if ( finalOptions == null ) {
            String basedir = getBasedir();
            finalOptions = new String[3];
            finalOptions[0] = "-b";
            finalOptions[1] = "-f";
            String tmpFile = basedir != null ? basedir + File.separator + "project.xml" : "project.xml" ;
			finalOptions[2] = new File(tmpFile).getAbsolutePath();
        }
        return finalOptions;
    }
    
	/**
	 * configure the environment and run the specified goals in a new VM 
     * with the given environment
	 *  
	 * @param goals String[] the goals to run
	 */
	public void run(String[] options, String[] goals) {
		try {
			initEnvironment();
			launchVM(options, goals);
		} 
        catch (Exception e) {
			log.debug("Cannot run Maven due to : " + e, e);
		}
	}
    
   
    /**
     * construct Maven from list of unmodifiable options, user-defined options and goals
     * 
     * @param options user-defined options
     * @param goals goals to run
     * @return String[] the complete Maven args
     */
    protected String[] getMavenArgs(String[] options, String[] goals) {
        
        String[] mavenArgs = new String[options.length + goals.length + getFinalOptions().length];
        
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
     * run the specified goals with the specified Maven options in a new VM
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
