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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: AbsractGoalsGrabber.java 5 sept. 2003 Exp gdodinet 
 * 
 */
public abstract class AbstractGoalsGrabber implements IGoalsGrabber {
	protected Map plugins, prereqs, descriptions = new HashMap();
	
	protected AbstractGoalsGrabber() {
		initMaps();
	}
	
	public void refresh() throws Exception {
		initMaps();
    }

    private void initMaps() {
        plugins = new HashMap();
		prereqs  = new HashMap();
		descriptions  = new HashMap();
    }

    protected void registerGoal(String fullyQualifiedGoalName, String properties) {
    	registerGoalName(fullyQualifiedGoalName);
    	registerGoalProperties(fullyQualifiedGoalName, properties);
    }

    protected void registerGoalName(String fullyQualifiedGoalName) {
        String[] splittedGoal = StringUtils.split(fullyQualifiedGoalName, ":");
    	String plugin = splittedGoal[0];
    
    	String goalName = "(default)";
    	if ( splittedGoal.length > 1 ) {
    		goalName = splittedGoal[1];
    	}
    
    	List goals = (List) plugins.get(plugin);
    	if ( goals == null ) { 
    		goals = new ArrayList();
    	}
    	if ( !goals.contains(goalName) ) {
    		goals.add(goalName);
    	}
    	plugins.remove(plugin);
    	plugins.put(plugin, goals);
    }

    protected void registerGoalProperties(String fullyQualifiedGoalName, String properties) {
        String[] splittedProperties = StringUtils.split(properties, ">");
    	if ( splittedProperties.length > 0 ) {
    		String description = splittedProperties[0];
    		descriptions.put(fullyQualifiedGoalName, description);
    	}
    	if ( splittedProperties.length > 1 ) {
    		String[] commaSeparatedPrereqs = StringUtils.split(splittedProperties[1], ",");
    		prereqs.put(fullyQualifiedGoalName, commaSeparatedPrereqs);
    	}
    }

    public String[] getPlugins() {
        return toStringArray(plugins.keySet());
    }

    public String[] getGoals(String plugin) {
    	if ( plugin == null || (Collection)plugins.get(plugin) == null ) {
    		return null;
    	}
    	return toStringArray((Collection)plugins.get(plugin));
    }

    public String getDescription(String fullyQualifiedGoalName) {
    	return (String) descriptions.get(fullyQualifiedGoalName);
    }

    public String[] getPrereqs(String fullyQualifiedGoalName) {
        return (String[]) prereqs.get(fullyQualifiedGoalName);
    }

    protected String[] toStringArray(Collection stringCollection) {
    	Object[] obj = stringCollection.toArray();
    	String[] strg = new String[obj.length];
    	for (int i = 0; i < strg.length; i++) {
    		strg[i] = (String) obj[i];
        } 
    	return strg;
    }
}
