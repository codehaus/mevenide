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
package org.mevenide.goals.manager;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.goals.grabber.DefaultGoalsGrabber;
import org.mevenide.goals.grabber.GoalsGrabbersAggregator;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.goals.grabber.ProjectGoalsGrabber;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: GoalsManager.java,v 1.1 7 sept. 2003 Exp gdodinet 
 * 
 */
public final class GoalsGrabbersManager {
	private static Log log = LogFactory.getLog(GoalsGrabbersManager.class);
		
	private static Map goalsGrabbers = new HashMap();
	
	private static DefaultGoalsGrabber defaultGoalsGrabber;
	
	private GoalsGrabbersManager() { }
	
	public static synchronized IGoalsGrabber getGoalsGrabber(String projectDescriptorPath) throws Exception {
//		if ( defaultGoalsGrabber == null ) {
//			defaultGoalsGrabber = new DefaultGoalsGrabber();
//		}
		if ( goalsGrabbers.get(projectDescriptorPath) == null ) {
			GoalsGrabbersAggregator aggregator = new GoalsGrabbersAggregator();
			aggregator.addGoalsGrabber(getDefaultGoalsGrabber());
			
			String mavenXmlPath = new File(new File(projectDescriptorPath).getParent(), "maven.xml").getAbsolutePath();
			
			if ( new File(mavenXmlPath).exists() ) {
				ProjectGoalsGrabber projectGoalsGrabber = new ProjectGoalsGrabber();
				projectGoalsGrabber.setMavenXmlFile(mavenXmlPath);
				aggregator.addGoalsGrabber(projectGoalsGrabber);
				log.debug("maven.xml not found. aggregator only aggregates defaultGoalsGrabber.");
			}
			
			goalsGrabbers.put(projectDescriptorPath, aggregator);
		}
		
		IGoalsGrabber aggregator = (IGoalsGrabber) goalsGrabbers.get(projectDescriptorPath);
		aggregator.refresh();
		
		return aggregator;
	}
	
	public static synchronized IGoalsGrabber getDefaultGoalsGrabber() throws Exception {
		if ( defaultGoalsGrabber == null ) {
			defaultGoalsGrabber = new DefaultGoalsGrabber();
		}
                return defaultGoalsGrabber;
	}
}
