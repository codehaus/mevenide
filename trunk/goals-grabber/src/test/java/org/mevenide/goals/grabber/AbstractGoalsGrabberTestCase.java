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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collection;

import org.mevenide.Environment;

import junit.framework.TestCase;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: AbstractGoalsGrabberTestCase.java 5 sept. 2003 Exp gdodinet 
 * 
 */
public abstract class AbstractGoalsGrabberTestCase extends TestCase {
	protected IGoalsGrabber goalsGrabber;
	protected File mavenHomeLocal;
	protected File pluginsLocal;
	protected File goalsFile;
	
	protected void setUp() throws Exception {
		mavenHomeLocal = new File(System.getProperty("user.home"), ".mevenide");
		if (!mavenHomeLocal.exists()) {
			mavenHomeLocal.mkdirs();
		}
		Environment.setMavenHome(mavenHomeLocal.getAbsolutePath());
		
		pluginsLocal = new File(mavenHomeLocal, "plugins");
		Environment.setMavenPluginsInstallDir(pluginsLocal.getAbsolutePath());
		
		if (!pluginsLocal.exists()) {
			pluginsLocal.mkdir();
		}
		
		File src = new File(AbstractGoalsGrabberTestCase.class.getResource("/goals.cache").getFile());
		goalsFile = new File(pluginsLocal, "goals.cache") ; 
		copy(src.getAbsolutePath(), goalsFile.getAbsolutePath());

		goalsGrabber = getGoalsGrabber();
		goalsGrabber.refresh();
	}

	protected void tearDown() throws Exception {
        goalsGrabber = null;
        delete(mavenHomeLocal);
    }
    
	protected void delete(File file) {
		if ( file.isFile() ) {
			file.delete();
		}
		else {
			File[] files = file.listFiles();
			if ( files != null ) {
				for (int i = 0; i < files.length; i++) {
					delete(files[i]);
				}
			}
			file.delete();
		}
	}
	
	private void copy(String sourceFile, String destFile) throws Exception {

		FileInputStream from = new FileInputStream(sourceFile);
		FileOutputStream to = new FileOutputStream(destFile);
		try {
			byte[] buffer = new byte[4096]; 
			int bytes_read; 
			while ((bytes_read = from.read(buffer)) != -1) {
				to.write(buffer, 0, bytes_read);
			}
		} 
		finally {
			if (from != null) {
				from.close();
			}
			if (to != null) {
				to.close();
			}
		}

	}

	public void testGetPlugins() {
		Collection plugins = Arrays.asList(goalsGrabber.getPlugins());
		for (int i = 0; i < getGetPluginsResults().length; i++) {
			assertTrue(plugins.contains(getGetPluginsResults()[i]));    
        }
	}

	public void testGetGoals() {
		for (int i = 0; i < getGetGoalsParameters().length; i++) {
			String[] goals = goalsGrabber.getGoals(getGetGoalsParameters()[i]);
			Collection goalsCollection = Arrays.asList(goals);
			assertEquals(getGetGoalsResults()[i].length, goals.length);
			for (int j = 0; j < getGetGoalsResults()[i].length; j++) {
				assertTrue(goalsCollection.contains(getGetGoalsResults()[i][j]));
            }
        }
		goalsGrabber.getGoals(null);
	}

	public void testGetDescription() {
		for (int i = 0; i < getGetDescriptionParameters().length; i++) {
			assertEquals(getGetDescriptionResults()[i], goalsGrabber.getDescription(getGetDescriptionParameters()[i]));
        }
	}

	public void testGetPrereqs() {
		for (int i = 0; i < getGetPrereqsParameters().length; i++) {
			String[] prereqs = goalsGrabber.getPrereqs(getGetPrereqsParameters()[i]);
			Collection prereqsCollection = Arrays.asList(prereqs);
			assertEquals(getGetPrereqsResults()[i].length, prereqs.length);
			for (int j = 0; j < getGetPrereqsResults()[i].length; j++) {
                assertTrue(prereqsCollection.contains(getGetPrereqsResults()[i][j]));
            }
        }
	}
	
	
	protected abstract IGoalsGrabber getGoalsGrabber() throws Exception  ;

	protected abstract String[] getGetPluginsResults() ;

	protected abstract String[] getGetGoalsParameters() ;
	protected abstract String[][] getGetGoalsResults() ;

	protected abstract String[] getGetDescriptionParameters() ;
	protected abstract String[] getGetDescriptionResults() ;

	protected abstract String[] getGetPrereqsParameters() ;
	protected abstract String[][] getGetPrereqsResults() ;
}
