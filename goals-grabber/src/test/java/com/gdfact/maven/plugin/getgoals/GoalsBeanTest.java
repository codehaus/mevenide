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
package com.gdfact.maven.plugin.getgoals;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import junit.framework.TestCase;

public class GoalsBeanTest extends TestCase {

	private GoalsBean goalsBean;

    public GoalsBeanTest(String arg0) {
        super(arg0);
    }
    
    protected void setUp() throws Exception {
        goalsBean = new GoalsBean(); 
    }
    
    protected void tearDown() throws Exception {
        goalsBean = null;
    }

    public void testGetPlugin() {
        assertEquals("prefix", goalsBean.getPlugin("prefix:goal"));
        assertEquals("prefix", goalsBean.getPlugin("prefix"));
    }
    
    public void testGetShortGoalName() {
        assertEquals("goal", goalsBean.getShortGoalName("prefix:goal"));
        assertEquals("(default)", goalsBean.getShortGoalName("prefix"));
    }
    
    public void testAddGoal() {
        goalsBean.addGoal("prefix:goal1", "testGoal");
        goalsBean.addGoal("prefix:goal2", "testGoal2");
        
		Map map = (Map)goalsBean.getGoals().get("prefix");
        assertEquals("testGoal", map.get("goal1"));
        assertEquals("testGoal2", map.get("goal2"));
        
        goalsBean.addGoal("goal", "testGoal3");
        map = (Map)goalsBean.getGoals().get("goal");
        assertEquals("testGoal3", map.get("(default)"));
        
        //test that duplicate values are managed
        goalsBean.addGoal("prefix:goal1", "testGoal");
        assertEquals(2, ((Map)goalsBean.getGoals().get("prefix")).size());
       	
        goalsBean.addGoal("prefix:goal3", "testGoal");
		assertEquals(3, ((Map)goalsBean.getGoals().get("prefix")).size());
       
        goalsBean.addGoal("goal", "testGoal3");
        assertEquals(1, ((Map)goalsBean.getGoals().get("goal")).size());
        
    }
    
    public void testUnmarshall() throws Exception {
        unmarshallGoalBean();
		Map map = (Map)goalsBean.getGoals().get("prefix");
        
        assertEquals("testGoal", map.get("goal1"));
        assertEquals("testGoal2", map.get("goal2"));
        assertEquals("testGoal",  map.get("goal3"));
        
        assertEquals(1, ((Map)goalsBean.getGoals().get("goal")).size());
        assertEquals(3, ((Map)goalsBean.getGoals().get("prefix")).size());
        
        map = (Map)goalsBean.getGoals().get("goal");
        assertEquals("testGoal3", map.get("(default)"));
        //System.out.println(goalsBean);
    }

    private void unmarshallGoalBean() throws Exception {
        File tmpFile = createTmpGoalFile("testGoals.xml");
        String tmpFilePath = tmpFile.getAbsolutePath();
        goalsBean.unMarshall(tmpFilePath);
    }
    
    private File createTmpGoalFile(String xmlOutputFilename) throws Exception {
        String outputString = 
			"<?xml version=\"1.0\" standalone=\"yes\"?>" +
        	"<!DOCTYPE goals [\n" +
        	"    <!ELEMENT goals (category*)>" +
        	"    <!ELEMENT category (goal+)>" +
        	"    <!ATTLIST category name CDATA #REQUIRED>" +
        	"    <!ELEMENT goal (description?, prereqs?)>" +
        	"    <!ATTLIST goal name CDATA #REQUIRED>" +
        	"    <!ELEMENT description (#PCDATA)>" +
        	"    <!ELEMENT prereqs (prereq*)>" +
        	"    <!ELEMENT prereq (#PCDATA)>" +
        	"    <!ATTLIST prereq goal CDATA #REQUIRED>" +
        	" ]> " +
			"<goals>" +
        	"	<category name=\"goal\">" +
        	"		<goal name=\"(default)\">" +
        	"			<description>testGoal3</description>" +
        	"			<prereqs/>" +
        	"		</goal>" +
        	"	</category>" +
        	"	<category name=\"prefix\">" +
        	"		<goal name=\"goal3\">" +
        	"			<description>testGoal</description>" +
        	"			<prereqs>" +
        	"				<prereq goal=\"prefix:goal1\"/>" +
        	"			</prereqs>" +	
        	"		</goal>" +
        	"		<goal name=\"goal2\">" +
        	"			<description>testGoal2</description>" +
        	"			<prereqs/>" +
        	"		</goal>" +
        	"		<goal name=\"goal1\">" +
        	"			<description>testGoal</description>" +
        	"			<prereqs/>" +
        	"		</goal>" +
        	"	</category>" +
        	"</goals>";
        File output = File.createTempFile("tmp", xmlOutputFilename);
        FileOutputStream fileOutputStream = new FileOutputStream(output);
        fileOutputStream.write(outputString.getBytes());
        return output;
    }
    
    public void testGetGoalCategories() throws Exception {
        unmarshallGoalBean();
        Collection categories = goalsBean.getPlugins();
        ArrayList expectedCategories = new ArrayList();
        expectedCategories.add("goal");
        expectedCategories.add("prefix");
        assertTrue(categories.containsAll(expectedCategories));
    }
    
    public void testGetGoals() throws Exception {
        unmarshallGoalBean();
        Collection goals = goalsBean.getGoals("prefix");
        ArrayList expectedGoals = new ArrayList();
        expectedGoals.add("goal1"); 
        expectedGoals.add("goal2"); 
        expectedGoals.add("goal3");  
        assertTrue(goals.containsAll(expectedGoals));
    }
    
    public void testGetDescription() throws Exception {
        unmarshallGoalBean();
        String description = goalsBean.getDescription("prefix", "goal3");
        assertEquals("testGoal", description);
    }
    
    
   
}
