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
package com.gdfact.maven.plugin.getgoals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/**
 * This class is a long-live container for the Maven.cache goals
 * @todo use Jdom to marshall the bean and generate the DTD
 * @todo need SERIOUS refactoring !
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: GoalsBean.java 3 mai 2003 Exp gdodinet 
 *
 */
public class GoalsBean {

	//goals->(prefix, unqualifiedGoalNames->(unqualifiedGoalName, goalDescription))
   	private Map goals ;
   	
    //prefix:goal->{prereq} 
   	private Map prereqs;
   	
    //prefix:goal->ID
   	private Map goalIds;
   	
    //shortcut to the last inserted id
   	private int maxId = 0;
   	
    /**
   	 * 
     * <code>goalBean.add("war:war", "bleah")</code> 
     * <code>goalBean.add("war", "bleah")</code> 
     * in the later case <code>war</code> will be expanded to <code>war:(default)</war>
     * 
   	 * @param goalName <i>fully qualified</i> name of the goal to add (e.g. java:compile)
   	 * @param goalDescription description of the goal to add
   	 * 
   	 */
   	public void addGoal(String goalName, String goalDescription) {
   	   	if ( goals == null )  {
   	        goals = new HashMap();
   	    }
   	    if ( goalIds == null )  {
			goalIds = new HashMap();
   	    }
   	    String plugin = getPlugin(goalName);
   	    String shortGoalName = getShortGoalName(goalName);
   	    if ( goals.containsKey(plugin) ) {
			Map descriptions = (Map) goals.get(plugin);
            if ( !descriptions.containsKey(shortGoalName) ) {
                descriptions.put(shortGoalName, goalDescription);
                goalIds.put(goalName, new Integer(maxId + 1));
                maxId++;
            }
   	    }
   	    else  {
			Map descriptions = new HashMap();
            descriptions.put(shortGoalName, goalDescription);
   	        goals.put(plugin, descriptions);
			goalIds.put(goalName, new Integer(maxId + 1));
			maxId++;
   	    }
   	}

	public void addPrereq(String fullyQualifiedGoalName, String prereq) {
        if ( prereq != null && !prereq.equals("") && !prereq.equals("gt;") ){
	        if ( prereqs == null )  {
	            prereqs = new Hashtable();
	        }
	        if ( prereq.startsWith("gt;") ) {
	        	prereq = prereq.substring(3, prereq.length());  
	        }
	        ArrayList list = new ArrayList(); 
	        if ( prereqs.containsKey(fullyQualifiedGoalName) ) {
	        	list = (ArrayList) prereqs.get(fullyQualifiedGoalName);
	        	prereqs.remove(fullyQualifiedGoalName);
	        }
	        list.add(prereq);
	        prereqs.put(fullyQualifiedGoalName, list);
        }
	}
	
	/**
	 * 
	 * package-protected <i>for testing purpose</i>
	 * 
	 */
	String getPlugin(String goalName) {
	    StringTokenizer tokenizer = new StringTokenizer(goalName,":");
	    return tokenizer.nextToken();
	}
	
    /**
     * 
     * package-protected <i>for testing purpose</i>
     * 
     */
    String getShortGoalName(String goalName) {
        StringTokenizer tokenizer = new StringTokenizer(goalName,":");
        if ( tokenizer.countTokens() > 1 ) {
            tokenizer.nextToken();
            return tokenizer.nextToken();
        }
        else return "(default)";
    }

    /**
     * 
     * package-protected <i>for testing purpose</i>
     * 
     */
    Map getGoals() {
        return goals;
    }
    
    /**
     * 
     * @todo allow to customize the cache storage of the goals
     * 
     */
    public void store(String outputFile) throws IOException {
        File output = new File(outputFile);
        FileOutputStream fileOutputStream = new FileOutputStream(output);
        fileOutputStream.write(toString().getBytes());
    }
    
    /**
     * 
     * return XML-ized list of the goals the generated document is valid against
     * this dtd :
     * 
     * <[CDATA[
     * 	<!DOCTYPE goals [\n");
     *   	<!ELEMENT goals (category*)>
     *   	<!ELEMENT category (goal+)>
     *   	<!ATTLIST category name NMTOKEN #REQUIRED>
     *   	<!ELEMENT goal (description?, prereqs?)>
     *   	<!ATTLIST goal name CDATA #REQUIRED>
     *   	<!ELEMENT description (#PCDATA)>
     * 		<!ELEMENT prereqs (prereq*)>
     * 		<!ELEMENT prereq (#PCDATA)>
     * 		<!ATTLIST prereq goal CDATA #REQUIRED>
     * 
     * 	]> 
     * ]]>
     * 
     * also the generated document is standalone. 
     */
    public String toString() {
        return toXMLString();
    }
    
    /**
     * @todo use JDOM (or any Java2Xml mapping tool) to generate the xml document  
     * @todo use ID / IDREF in place of the &lt;[CDATA[&lt;prereq goal=".."/&gt;]]&gt; element  
     * @todo refactor : this method has a way too high cyclomatic complexity (>>4)
     * 
     * @return String
     */
    private String toXMLString() {
        StringBuffer buffer = getXmlHead();
        buffer.append("<goals>\n");
        if ( goals != null ) {
            Set keySet = goals.keySet();
            Iterator keys = keySet.iterator();
            while ( keys.hasNext() ) {
                String prefix = (String) keys.next();
                buffer.append("\t<category name=\"" + prefix + "\">\n");
				Hashtable unqualifedGoalNames = (Hashtable) goals.get(prefix);
                Set unqualifiedGoalNamesKeySet = unqualifedGoalNames.keySet();
                Iterator unqualifiedGoalNamesKeys = unqualifiedGoalNamesKeySet.iterator();
                while ( unqualifiedGoalNamesKeys.hasNext() ) {
                    String unqualifiedGoalName = (String) unqualifiedGoalNamesKeys.next();
                    String description = (String) unqualifedGoalNames.get(unqualifiedGoalName);
                    if ( description == null || description.equals("null") ) {
                    	description = "No available description";
                    }
                    buffer.append("\t\t<goal name=\""+ unqualifiedGoalName + "\">\n");
                    buffer.append("\t\t\t<description>" + description + "</description>\n");
                    if ( prereqs != null ) {
                    	buffer.append("\t\t\t<prereqs");
	                    ArrayList goalPrereqs= null;
	                    if ( !unqualifiedGoalName.equals("(default)")) {
			                goalPrereqs = (ArrayList) prereqs.get(prefix + ":" + unqualifiedGoalName);
	                    }
	                    else {
	                        goalPrereqs = (ArrayList) prereqs.get(prefix);
	                    }
	                    boolean hasPrereqs = false;
	                    if ( goalPrereqs != null ) {
	                        buffer.append(">\n");
	                        hasPrereqs = true;
	                        for (int i = 0; i < goalPrereqs.size(); i++) {
	                            buffer.append("\t\t\t\t<prereq");    
								buffer.append(" goal=\"" + goalPrereqs.get(i) + "\"/>\n");   
		                    }
	                    }
	                	if ( hasPrereqs ){ 
	                    	buffer.append("\t\t\t</prereqs>\n");
	                	}
	                	else {
	                        buffer.append("/>\n");
	                	}
                    }
                    buffer.append("\t\t</goal>\n");
                }
                buffer.append("\t</category>\n");
            }
            
        }
        buffer.append("</goals>");
        return buffer.toString();
    }

	private StringBuffer getXmlHead() {
        StringBuffer buffer = new StringBuffer("<?xml version=\"1.0\" standalone=\"yes\"?>\n");
      	buffer.append("<!DOCTYPE goals [\n");
        buffer.append("\t<!ELEMENT goals (category*)>\n");
        buffer.append("\t<!ELEMENT category (goal+)>\n");
        buffer.append("\t<!ATTLIST category name NMTOKEN #REQUIRED>\n");
        buffer.append("\t<!ELEMENT goal (description?, prereqs?)>\n");
        buffer.append("\t<!ATTLIST goal name CDATA #REQUIRED>\n");
        buffer.append("\t<!ELEMENT description (#PCDATA)>\n");
        buffer.append("\t<!ELEMENT prereqs (prereq*)>\n");
        buffer.append("\t<!ELEMENT prereq (#PCDATA)>\n");
        buffer.append("\t<!ATTLIST prereq goal CDATA #REQUIRED>\n");
        buffer.append("]>\n");
        return buffer;
    }
	
	
	public void unMarshall(String xmlInputFilename) throws Exception {
	    System.setProperty("org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser");
	    InputSource inputSource = new InputSource(new FileReader(xmlInputFilename));	
        SAXBuilder saxBuilder = new SAXBuilder(false);
        
        Document document = saxBuilder.build(new File(xmlInputFilename));
        Element goalsElement = document.getRootElement();
        List categories = goalsElement.getChildren("category");
        Iterator categoriesIterator = categories.iterator();
        while ( categoriesIterator.hasNext() ) {
             Element category = (Element) categoriesIterator.next(); 
             String categoryName = category.getAttribute("name").getValue();
             List categoryGoals = category.getChildren("goal");
             Iterator goalsIterator = categoryGoals.iterator();
             while ( goalsIterator.hasNext() ) {
                 Element goal = (Element) goalsIterator.next();
                 String goalName = goal.getAttribute("name").getValue();
                 String goalDescription = goal.getChildText("description");
                 addGoal(categoryName + ":" + goalName, goalDescription );
                 Element prereqsElem = goal.getChild("prereqs");
                 if ( prereqsElem != null ) {
                 	List prereqs = prereqsElem.getChildren("prereq");
                 	for (int i = 0; i < prereqs.size(); i++) {
						Element prereq = (Element) prereqs.get(i);
						addPrereq(categoryName + ":" + goalName, prereq.getAttributeValue("goal"));
					}
                 }
             }
        }
	}
	
	
	public Collection getPlugins() {
        return goals.keySet();
	}
	
	/**
	 * 
	 * @param String category
	 * @return Set the set of goals associated with the category parameter
	 * 			return null if the specified category cannot found
	 * 		 
	 * 
	 */
	public Collection getGoals(String plugin) {
        if ( goals != null ) {
		    if ( !goals.containsKey(plugin) ) {
		     	return null;
		    }
		    Set set = ((Map)goals.get(plugin)).keySet();
	        return set;
        }
        return null;
	}
  
   /**
	 * 
	 * @param category
	 * @param goal
	 * @return String the description of the specified goal
	 * 			return null if either the category or the goal 
	 * 			inside that category cannot be found
	 */
	public String getDescription(String category, String goal) {
        if ( goals != null ) {
		    if ( !goals.containsKey(category) || !((Map) goals.get(category)).containsKey(goal)) {
	        	return null;
	    	}
			Map map = (Map) goals.get(category);
	        return (String) map.get(goal);	   
        }
        return null;
	}
	
	
	
    

}


