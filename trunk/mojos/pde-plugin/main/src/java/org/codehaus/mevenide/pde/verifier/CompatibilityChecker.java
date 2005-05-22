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
package org.codehaus.mevenide.pde.verifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.codehaus.mevenide.pde.ConfigurationException;
import org.codehaus.mevenide.pde.ParameterException;
import org.codehaus.mevenide.pde.resources.Messages;
import org.codehaus.plexus.util.StringUtils;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class CompatibilityChecker {
    
    /** eclipse home directory */
    private File eclipseHome;  
    
    /** Eclipse configuration Folder. It is exposed as a property because it is user configurable through the <code>-configuration</code> flag */
    private File configurationFolder;
    
    public CompatibilityChecker(File eclipseHome, File configurationFolder) {
        this.eclipseHome = eclipseHome;
        this.configurationFolder = configurationFolder;
    }
    
	public void checkBuildId(String minBuildIdParameter, String maxBuildIdParameter) throws ConfigurationException {
		long maxBuildId = 0;
	    long minBuildId = 0;

		if ( !StringUtils.isEmpty(maxBuildIdParameter) ) {
	    	maxBuildId = Long.parseLong(maxBuildIdParameter);
	    }
		if ( !StringUtils.isEmpty(minBuildIdParameter) ) {
	        minBuildId = Long.parseLong(minBuildIdParameter);
	    }
	        
	    if ( maxBuildId != 0 || minBuildId != 0 ) {
	        checkBuildId(minBuildId, maxBuildId);
	    }        
	}
    
    /**
     * Because we build against a specific Eclipse platform version,
     * we need to check the prerequisites. 
     * 
     * This method allows to verify that the current Eclipse platform 
     * buildId is GE than <code>minBuildId</code> and LE than 
     * <code>maxBuildId</code>. 
     * 
     * buildIds are expected to be in the standard format, f.i : I200409240800
     *    	
     * if it appears than minBuildId is GT maxBuildId then they are swapped
     * 			
     * @param minBuildId the min expected buildId. If null no min check will take place
     * @param maxBuildId the max expected buildId. If null no max check will take place 
     * 
     * @throws ConfigurationException if the buildId constraints are respected 
     *                                or a problem occured while reading the current platform's buildId
     *                                or <code>minBuildId</code> id GT <code>maxBuildId</code>  
     */
    public void checkBuildId(long minBuildId, long maxBuildId) throws ConfigurationException {
        if ( minBuildId > maxBuildId ) {
            long temp = minBuildId;
            minBuildId = maxBuildId;
            maxBuildId = temp;
        }
        checkMinBuildId(minBuildId);
        checkMaxBuildId(maxBuildId);
    }

    /** 
     * check that the buildId of the platform we're building against isnot GT than the <code>maxBuildId</code> 
     * 
     * @param maxBuildId max compatible buildId
     * @throws ConfigurationException if the current buildId is GT than <code>maxBuildId</code>
     */
    public void checkMaxBuildId(long maxBuildId) throws ConfigurationException {
        long buildId = getBuildId();
        if ( buildId > maxBuildId  ) {
            throw new ParameterException("buildId", "Configuration.Constraints.MaxBuildId");
        }
    }
    
    /** 
     * check that the buildId of the platform we're building against isnot GT than the <code>minBuildId</code> 
     * 
     * @param minBuildId max compatible buildId
     * @throws ConfigurationException if the current buildId is GT than <code>minBuildId</code>
     */
    public void checkMinBuildId(long minBuildId) throws ConfigurationException {
        long buildId = getBuildId();
        if ( buildId < minBuildId  ) {
            throw new ParameterException("buildId", "Configuration.Constraints.MinBuildId");
        }
    }
	
    /**
     * extract the buildId of the platform we're building against from the config.ini file and parse it to long 
     * 
     * @throws ConfigurationException if unable to read config file or to parse the buildId
     */
    public long getBuildId() throws ConfigurationException {
		
		Properties eclipseConfig = new Properties();
		if ( configurationFolder == null )  {
            configurationFolder = new File(eclipseHome, "configuration");
        }
		File eclipseConfigFile = new File(configurationFolder, "config.ini");
		FileInputStream configStream = null;
		try {
            configStream = new FileInputStream(eclipseConfigFile);
            eclipseConfig.load(configStream);
        }
        catch (FileNotFoundException e) {
            String message = Messages.get("Configuration.ConfigFile.NotFound", eclipseConfigFile);
            throw new ConfigurationException(message, e);
        }
        catch (IOException e) {
            String message = Messages.get("Configuration.ConfigFile.NotReadable", eclipseConfigFile);
            throw new ConfigurationException(message, e);
        }
        finally {
            if ( configStream != null ) { 
                try { configStream.close(); }
                catch (IOException e) { } //silently ignore
            }
        }
		String buildId = (String) eclipseConfig.get("eclipse.buildId");
		try {
            long id = Long.parseLong(buildId.substring(1).replaceAll("-", ""));
            return id;
        }
        catch (NumberFormatException e) {
            String message = Messages.get("Configuration.BuildId.Invalid", buildId != null ? buildId : "null", eclipseConfigFile);
            throw new ConfigurationException(message, e);
        }
	}
    
    public File getConfigurationFolder() {
        return configurationFolder;
    }
    public void setConfigurationFolder(File configurationFolder) {
        this.configurationFolder = configurationFolder;
    }
    public File getEclipseHome() {
        return eclipseHome;
    }
    public void setEclipseHome(File eclipseHome) {
        this.eclipseHome = eclipseHome;
    }
}
