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
package org.mevenide.environment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: LocationFinderAggregator.java,v 1.1 15 nov. 2003 Exp gdodinet 
 * 
 */
public class LocationFinderAggregator implements ILocationFinder {
    
    private static Log log = LogFactory.getLog(LocationFinderAggregator.class);
    
    private UserRefinedPropertiesLocationFinder userRefinedPropertiesLocationFinder;
    private ProjectPropertiesLocationFinder projectPropertiesLocationFinder;
    private BuildPropertiesLocationFinder buildPropertiesLocationFinder;
    private SysEnvLocationFinder sysEnvLocationFinder;
    private DefaultLocationFinder defaultLocationFinder;
    
    
    public LocationFinderAggregator(String effectiveWorkingDirectory) {
        sysEnvLocationFinder = SysEnvLocationFinder.getInstance();
        defaultLocationFinder = DefaultLocationFinder.getInstance();
        try {
            buildPropertiesLocationFinder = BuildPropertiesLocationFinder.getInstance();
        }
        catch ( Exception e ) { 
            log.debug("BuildPropertiesLocationFinder not created");
        }
        try {
	        userRefinedPropertiesLocationFinder = new UserRefinedPropertiesLocationFinder(effectiveWorkingDirectory);
        }
		catch ( Exception e ) { 
			log.debug("UserRefinedPropertiesLocationFinder not created");
		}
        try {
            projectPropertiesLocationFinder = new ProjectPropertiesLocationFinder(effectiveWorkingDirectory);
        }
		catch ( Exception e ) { 
			log.debug("ProjectPropertiesLocationFinder not created");
		}
    }
    
    public String getConfigurationFileLocation() {
        // @todo Auto-generated method stub
        return null;
    }
    public String getJavaHome() {
        // @todo Auto-generated method stub
        return null;
    }
    public String getMavenHome() {
        // @todo Auto-generated method stub
        return null;
    }
    public String getMavenLocalHome() {
        // @todo Auto-generated method stub
        return null;
    }
    public String getMavenLocalRepository() {
        // @todo Auto-generated method stub
        return null;
    }
    public String getMavenPluginsDir() {
        // @todo Auto-generated method stub
        return null;
    }
}
