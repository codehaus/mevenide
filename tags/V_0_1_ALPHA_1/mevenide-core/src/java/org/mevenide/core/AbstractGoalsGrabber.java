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
import java.util.Collection;

import org.apache.commons.discovery.tools.DiscoverClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.*;
import org.mevenide.util.PostGoal;

import com.gdfact.maven.plugin.getgoals.GoalsBean;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public abstract class AbstractGoalsGrabber implements IGoalsGrabber{
    private static Log log = LogFactory.getLog(AbstractGoalsGrabber.class);
    
    /** singleton related */
	private static AbstractGoalsGrabber grabber = null;
    private static Object lock = new Object();

    /** goals marshaller/unmarshaller */
	protected GoalsBean goalsBean; 
	
	/**
	 * maven runner needed since we use the eclipse:get-goals goal 
	 */
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
			log.debug("Unable to load goals from file '" + xmlGoals + "' due to : " + ex);  
		}
	}

    /**
     * return the available plugins 
     * @return Collection
     */
	public Collection getPlugins()  {
		if ( goalsBean != null ) { 
			return goalsBean.getPlugins();
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
   public static AbstractGoalsGrabber getGrabber(String xmlGoals) throws Exception {
        if (grabber != null) {
            return grabber;
        }
        synchronized (lock) {
            if (grabber == null) {
                grabber = (AbstractGoalsGrabber) new  DiscoverClass().newInstance(AbstractGoalsGrabber.class);
                if ( !new File(xmlGoals).exists() ) {
					PostGoal.create(new File(xmlGoals));
                }
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
        System.out.println(output);
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

