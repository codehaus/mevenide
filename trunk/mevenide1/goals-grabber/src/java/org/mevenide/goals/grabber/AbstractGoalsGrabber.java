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
package org.mevenide.goals.grabber;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

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
        StringTokenizer splittedGoal = new StringTokenizer(fullyQualifiedGoalName, ":", false);
    	String plugin = splittedGoal.nextToken();
    
    	String goalName = "(default)";
    	if (splittedGoal.hasMoreTokens()) {
    		goalName = splittedGoal.nextToken();
    	}
    	Set goals = (Set) plugins.get(plugin);
    	if ( goals == null ) { 
    		goals = new TreeSet();
        	plugins.put(plugin, goals);
    	}
  		goals.add(goalName);
    }

    protected void registerGoalProperties(String fullyQualifiedGoalName, String properties) {
        int index1 = (properties == null ? -1 : properties.indexOf('>'));
        if (properties != null) {
            String description = "";
        	if ( index1 > 0) {
        		description = properties.substring(0, index1);
        		descriptions.put(fullyQualifiedGoalName, description);
        	}
        	if ( index1 > -1  && index1 < properties.length() - 1) {
                String prereqsString = properties.substring(index1 + 1);
                StringTokenizer tok = new StringTokenizer(prereqsString, ",", false);
                String[] commaSeparatedPrereqs = new String[tok.countTokens()];
                int count = 0;
                while (tok.hasMoreTokens()) {
                    commaSeparatedPrereqs[count] = tok.nextToken();
                    count = count + 1;
                }
        		prereqs.put(fullyQualifiedGoalName, commaSeparatedPrereqs);
        	}
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

     protected  boolean containsGoal(String fullyQualifiedGoalName) {
        StringTokenizer splittedGoal = new StringTokenizer(fullyQualifiedGoalName, ":", false);
    	String plugin = splittedGoal.nextToken();
      
    	String goalName = "(default)";
    	if (splittedGoal.hasMoreTokens()) {
    		goalName = splittedGoal.nextToken();
    	}
    	Collection goals = (Collection) plugins.get(plugin);
    	if ( goals == null) {
    		return false;
    	}
        if (goalName != null && !goals.contains(goalName)) {
            return false;
        }
        return true;
    }
     
    public String getOrigin(String fullyQualifiedGoalName)
    {
        return containsGoal(fullyQualifiedGoalName) ? getName() : null;
    }
    
    public String getDescription(String fullyQualifiedGoalName) {
    	return (String) descriptions.get(fullyQualifiedGoalName);
    }

    public String[] getPrereqs(String fullyQualifiedGoalName) {
        return (String[]) prereqs.get(fullyQualifiedGoalName);
    }
	
	
	/**
	 * 
	 * convert a Collection of Strings into an String[]
	 * The resulted array is also sorted according to 
	 * the natural order 
	 * 
	 * @see {@link java.util.TreeSet}   
	 * 
	 */
    protected String[] toStringArray(Collection stringCollection) {
    	Object[] obj = new TreeSet(stringCollection).toArray();
    	String[] strg = new String[obj.length];
    	for (int i = 0; i < strg.length; i++) {
    		strg[i] = (String) obj[i];
        } 
    	return strg;
    }
}
