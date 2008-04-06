/*
 * Copyright 2005-2006 Mevenide Team
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.codehaus.mevenide.netbeans.customizer;

import junit.framework.TestCase;

/**
 *
 * @author mkleint
 */
public class RunJarPanelTest extends TestCase {
    
    public RunJarPanelTest(String testName) {
        super(testName);
    }            

    /**
     * Test of split* method, of class RunJarPanel.
     */
    public void testParams() {
        String line = "-Xmx256m org.milos.Main arg1";
        assertEquals("-Xmx256m", RunJarPanel.splitJVMParams(line));
        assertEquals("org.milos.Main", RunJarPanel.splitMainClass(line));
        assertEquals("arg1", RunJarPanel.splitParams(line));
        
        line = "-Xdebug -Djava.compiler=none -Xnoagent -Xrunjdwp:transport=dt_socket,server=n,address=${jpda.address} -classpath %classpath ${packageClassName}";
        assertEquals("-Xdebug -Djava.compiler=none -Xnoagent -Xrunjdwp:transport=dt_socket,server=n,address=${jpda.address} -classpath %classpath", RunJarPanel.splitJVMParams(line));
        assertEquals("${packageClassName}", RunJarPanel.splitMainClass(line));
        assertEquals("", RunJarPanel.splitParams(line));
        
        line = "-classpath %classpath ${packageClassName} %classpath ${packageClassName}";
        assertEquals("-classpath %classpath", RunJarPanel.splitJVMParams(line));
        assertEquals("${packageClassName}", RunJarPanel.splitMainClass(line));
        assertEquals("%classpath ${packageClassName}", RunJarPanel.splitParams(line));
        
        line = "Main arg1 arg2.xsjs.xjsj.MainParam";
        assertEquals("", RunJarPanel.splitJVMParams(line));
        assertEquals("Main", RunJarPanel.splitMainClass(line));
        assertEquals("arg1 arg2.xsjs.xjsj.MainParam", RunJarPanel.splitParams(line));
        
    }


}
