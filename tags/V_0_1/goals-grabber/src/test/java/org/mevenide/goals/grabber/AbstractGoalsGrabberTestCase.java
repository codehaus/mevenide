/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
import java.util.Arrays;
import java.util.Collection;

import org.mevenide.goals.AbstractTestCase;
import org.mevenide.goals.test.util.TestUtils;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: AbstractGoalsGrabberTestCase.java 5 sept. 2003 Exp gdodinet 
 * 
 */
public abstract class AbstractGoalsGrabberTestCase extends AbstractTestCase {
	protected IGoalsGrabber goalsGrabber;

	protected File goalsFile;
	
	protected void setUp() throws Exception {
		super.setUp();
		File src = new File(AbstractGoalsGrabberTestCase.class.getResource("/goals.cache").getFile());
		goalsFile = new File(pluginsLocal, "goals.cache") ; 
		TestUtils.copy(src.getAbsolutePath(), goalsFile.getAbsolutePath());

		goalsGrabber = getGoalsGrabber();
		goalsGrabber.refresh();
	}

	protected void tearDown() throws Exception {
        goalsGrabber = null;
        super.tearDown();
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

	public void testGetOrigin() {
		for (int i = 0; i < getGetOriginParameters().length; i++) {
			String origin = goalsGrabber.getOrigin(getGetOriginParameters()[i]);
			assertEquals(getGetOriginResults()[i], origin);
        }
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
    
    protected abstract String[] getGetOriginParameters();
    protected abstract String[] getGetOriginResults();
    
}
