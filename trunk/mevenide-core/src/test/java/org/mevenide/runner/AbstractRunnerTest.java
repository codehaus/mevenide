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
package org.mevenide.runner;

import java.io.File;

import junit.framework.TestCase;

import org.mevenide.Environment;

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
		Environment.setMavenHome(System.getProperty("maven.home")); 
		Environment.setJavaHome(System.getProperty("java.home"));
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