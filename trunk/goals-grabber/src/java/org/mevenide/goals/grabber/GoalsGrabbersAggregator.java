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
package org.mevenide.goals.grabber;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: GoalsGrabberAggregator.java 6 sept. 2003 Exp gdodinet 
 * 
 */
public class GoalsGrabbersAggregator implements IGoalsGrabber {
	private static Log log = LogFactory.getLog(GoalsGrabbersAggregator.class);
	
	private List goalsGrabbers = new ArrayList();
	
	public void refresh() throws Exception {
        
        Iterator iterator = goalsGrabbers.iterator();
        while ( iterator.hasNext() ) {
        	IGoalsGrabber goalsGrabber = (IGoalsGrabber) iterator.next();
        	goalsGrabber.refresh();
        }
    }
    
    public void addGoalsGrabber(IGoalsGrabber goalsGrabber) {
    	goalsGrabbers.add(goalsGrabber);
    } 

	public void removeGoalsGrabber(IGoalsGrabber goalsGrabber) {
		goalsGrabbers.remove(goalsGrabber);
	}
    
    public String getDescription(String fullyQualifiedGoalName) {
        String description = null;
        for (int i = 0; i < goalsGrabbers.size(); i++) {
            description = ((IGoalsGrabber)goalsGrabbers.get(i)).getDescription(fullyQualifiedGoalName);
			if ( description != null ) {
				return description;
			}
        }
        return description;
    }

    public String[] getGoals(String plugin) {
		String[] goals = null;
		for (int i = 0; i < goalsGrabbers.size(); i++) {
			goals = ((IGoalsGrabber)goalsGrabbers.get(i)).getGoals(plugin);
			if ( goals != null ) {
				return goals;
			}
		}
		return goals;
    }

    public String[] getPlugins() {
        String[] plugins = new String[0];
		for (int i = 0; i < goalsGrabbers.size(); i++) {
			String[] currentPlugins = ((IGoalsGrabber)goalsGrabbers.get(i)).getPlugins();

			String[] tmpArray = new String[plugins.length];
			System.arraycopy(plugins, 0, tmpArray, 0, plugins.length);
			
			plugins = new String[plugins.length + currentPlugins.length];
			System.arraycopy(currentPlugins, 0, plugins, 0, currentPlugins.length);
			System.arraycopy(tmpArray, 0, plugins, currentPlugins.length, tmpArray.length);
		}
		return plugins;
    }

    public String[] getPrereqs(String fullyQualifiedGoalName) {
        String[] prereqs = null;
        for (int i = 0; i < goalsGrabbers.size(); i++) {
			prereqs = ((IGoalsGrabber)goalsGrabbers.get(i)).getPrereqs(fullyQualifiedGoalName);
            if ( prereqs != null ) {
				return prereqs;
			} 
        }
        return prereqs;
    }

    public List getGoalsGrabbers() {
        return goalsGrabbers;
    }

    public void setGoalsGrabbers(List goalsGrabbers) {
        this.goalsGrabbers = goalsGrabbers;
    }

}
