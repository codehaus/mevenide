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

import junit.framework.TestCase;


/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class AbstractRunnerTest extends TestCase {

	private AbstractRunner runnerNullBaseDir;
	private AbstractRunner runnerNotNullBaseDir;
	private AbstractRunner runner ;
	
	protected void setUp() throws Exception {
		runnerNullBaseDir = new AbstractRunnerStub();
		runnerNotNullBaseDir = 
			new AbstractRunnerStub() { 
				protected String getBasedir() {return "someBasedir";} 
			};
			
		final File f = new File(AbstractRunner.class.getResource("/project.xml").getFile());
		
		runner = new MyAbstractRunnerStub(f);
			
	}

	protected void tearDown() throws Exception {
		runnerNullBaseDir = null;
		runnerNotNullBaseDir = null;
		runner = null;
	}

	public void testGetMavenArgs() {
		String[] options = runnerNullBaseDir.getMavenArgs(new String[]{"-X", "-e", "-DsomeProperty=someValue"}, new String[]{"somePlugin:someGoal", "somePlugin:anotherGoal"});
		String[] expected = new String[]{"-b", "-f", "project.xml", "-X", "-e", "-DsomeProperty=someValue", "somePlugin:someGoal", "somePlugin:anotherGoal"};
		
		//assertEquality(expected, options);
		
		options = runnerNotNullBaseDir.getMavenArgs(new String[]{"-X", "-e", "-DsomeProperty=someValue"}, new String[]{"somePlugin:someGoal", "somePlugin:anotherGoal"});
		expected = new String[]{"-b", "-f", "someBasedir" + File.separator + "project.xml", "-X", "-e", "-DsomeProperty=someValue", "somePlugin:someGoal", "somePlugin:anotherGoal"};
		//assertEquality(expected, options);
	}

	private void assertEquality(String[] expected, String[] options) {
		assertEquals(expected.length, options.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], options[i]);
		}
	}
	
	public void testRun() throws Exception {
		String[] options = runner.getMavenArgs(new String[] {"-X"}, new String[] {"-Dname=value"});
		//runner.run(options, new String[] {"-arf"});
	}

}


final class MyAbstractRunnerStub extends AbstractRunnerStub {
	private File projectFile;
	
	MyAbstractRunnerStub(File f) throws Exception {
		System.out.println("[WARNING] MyAbstractRunnerStub - Crappy Trick : Setting Java home and Maven Home. Please update those values according to your configuration");
		System.setProperty("java.home", "C:/jdk1.4.1");
		System.setProperty("maven.home", "E:/maven");
		projectFile = f;
	}
	
	protected String getBasedir() {
		return projectFile.getParent();
	} 
	
	protected void initEnvironment() {
        //MILOS: not needed anymore, is done in abstracttestcase setup..
//		Environment.setMavenHome(System.getProperty("maven.home")); 
//		Environment.setJavaHome(System.getProperty("java.home"));
	}
		
	protected void launchVM(String[] options, String[] goals) throws Exception {
		String[] newArray = new String[options.length + goals.length + 1];
		newArray[0] = "com.werken.forehead.Forehead";
		System.arraycopy(options, 0, newArray, 1, options.length);
		System.arraycopy(goals, 0, newArray, options.length + 1, goals.length);
	
		String[] cmdLine = new String[newArray.length + 4];
		cmdLine[0] = System.getProperty("java.home")
					 + File.separator 
					 + "bin"
					 + File.separator
					 + "javaw";
		cmdLine[1] = "-Dforehead.conf.file="
					 + System.getProperty("maven.home")
					 + File.separator 
					 + "bin"
					 + File.separator
					 + "forehead.conf";
		cmdLine[2] = "-classpath";
		cmdLine[3] = System.getProperty("maven.home")
					 + File.separator
					 + "repository"
					 + File.separator
					 + "forehead"
					 + File.separator
					 + "jars"
					 + File.separator
					 + "forehead-1.0-beta-4.jar";

		for (int i = 4; i < cmdLine.length; i++) {
			cmdLine[i] = newArray[i-4];
		}
	
//		for (int i = 0; i < cmdLine.length; i++) {
//			System.out.println(cmdLine[i]);
//		}
						
		Runtime.getRuntime().exec(cmdLine, null);
	
	}
}