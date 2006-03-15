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