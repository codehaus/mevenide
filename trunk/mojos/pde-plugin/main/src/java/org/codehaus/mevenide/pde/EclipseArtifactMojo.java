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
package org.codehaus.mevenide.pde;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.maven.plugin.Plugin;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.codehaus.mevenide.pde.resources.Messages;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;


/**  
 * 
 * base pde-mojo class 
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class EclipseArtifactMojo implements Plugin {
    
    /** eclipse home directory */
    protected File eclipseHome;  
    
    /** Eclipse configuration Folder. It is exposed as a property because it is user configurable through the <code>-configuration</code> flag */
    protected File configurationFolder;
    
    /** The workspace un which Eclipse project exists. Required if project has not been created in the 'default location' */
    protected File workspace;
    
    /** directory where generated artifacts are outputted */
    protected File outputDirectory;
    
    /** base working directory */
    protected File basedir;
    
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
    protected void checkBuildId(long minBuildId, long maxBuildId) throws ConfigurationException {
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
    protected void checkMaxBuildId(long maxBuildId) throws ConfigurationException {
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
    protected void checkMinBuildId(long minBuildId) throws ConfigurationException {
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
    protected long getBuildId() throws ConfigurationException {
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
            long id = Long.parseLong(buildId.substring(1));
            return id;
        }
        catch (NumberFormatException e) {
            String message = Messages.get("Configuration.BuildId.Invalid", buildId != null ? buildId : "null", eclipseConfigFile);
            throw new ConfigurationException(message, e);
        }
    }
    
    /**
     * extract the eclipse dependencies from the .classpath. 
     * 
     * if "org.eclipse.pde.core.requiredPlugins" container is found  then dependencies will be extracted from the plugin descriptor 
     * else dependencies will be grabbed directly from .classpath   
     * 
     * @return  the list of eclipse dependencies
     * @throws PdePluginException
     */
    protected List extractEclipseDependencies() throws PdePluginException {
        Document document = null; 
            
        try {
            document = new SAXBuilder().build(new File(basedir, ".classpath"));
        }
        catch (Exception e) {
            String message = Messages.get("BuildArtifact.CannotExtractDependencies", e.getMessage()); 
            throw new PdePluginException(message, e);
        }    
 
        Element classpath = document.getRootElement();
            
        boolean useDependenciesContainer = useEclipseDependenciesContainer(classpath);
            
        return useDependenciesContainer ? extractDependenciesFromDescriptor() 
                                        : extractDependenciesFromClasspath(classpath);
        
    }

    /**
     * try to extract other Eclipse dependencies from explicit declaration in .classpath
     * 
     * here some use cases : 
     * 
     * 1- libraries are referenced using a Variable, e.g. : 
     *   	MAVEN_REPO/eclipse/jars/jface-3.1.0.jar
     *   	ECLIPSE_HOME/plugins/org.eclipse.jface_3.1.0/jface.jar
     *     
     * 2- libraries are referenced directly  
     * 
     * in the first case we should try to grab the variable value from .metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.jdt.core.prefs
     * variables are referenced as this : org.eclipse.jdt.core.classpathVariable.<code>VARIABLE_NAME</code>.
     * 
     * thus we need to know the location of the workspace.. in most case it is expected to be the parent of basedir, we're not assured this is always 
     * the case because project may have not been created in the 'default location'  
     * 
     * @return  the list of eclipse dependencies
     * @throws PdePluginException
     */
    List extractDependenciesFromClasspath(Element classpath) throws PdePluginException {
        return null;
    }

    /**
     * extract the dependencies using the requires/import elements in plugin.xml file.
     * 
     * @return  the list of eclipse dependencies
     * @throws PdePluginException
     */
    List extractDependenciesFromDescriptor() throws PdePluginException {
        List dependencies = new ArrayList();

        File pluginDescriptor = new File(basedir, "plugin.xml");
        
        Document document = null;
        
        try {
            document = new SAXBuilder().build(pluginDescriptor);
        }
        catch (Exception e) {
            String message = Messages.get("BuildArtifact.CannotExtractDependencies", e.getMessage()); 
            throw new PdePluginException(message, e);
        }
        
        Element requiresElement = document.getRootElement().getChild("requires");
        
        if ( requiresElement != null ) {
            List importElements = requiresElement.getChildren("import");
            for ( Iterator it = importElements.iterator(); it.hasNext(); ) {
                Element importElement = (Element) it.next();
                String dependency = importElement.getAttributeValue("plugin");
                
                String library = dependency.indexOf("org.eclipse.swt") == -1 ? getNonSwtLibrary(dependency) 
                                                                             : getSwtLibrary(dependency);
                dependencies.add(library);
		    }
        }
        
        return dependencies;
    }

    private String getSwtLibrary(String dependency) {
        //example
        //org.eclipse.swt.win32_${maven.eclipse.plugin.swt.version}/ws/win32"
	    //file="${eclipse.home}/plugins/org.eclipse.swt.win32_${maven.eclipse.plugin.swt.version}/ws/win32/swt.jar"/
        return null;
    }

    private String getNonSwtLibrary(final String dependency) throws PdePluginException {
        String library = null;
        
        File[] parentNames = new File(eclipseHome, "plugins").listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory() && pathname.getName().indexOf(dependency) >= 0;
            }
        });
        
        if ( parentNames == null ) {
            String message = Messages.get("BuildArtifact.ComputeDependencyParent", dependency);
            throw new PdePluginException(message);
        }
        File dependencyHome = parentNames[0];
        
        File[] jars = dependencyHome.listFiles(new FileFilter(){
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".jar");
            } 
        });
        
        if ( jars != null && jars.length > 0 ) {
            library = jars[0].getAbsolutePath();
        }
        
        return library;
    }

    /**
     * @param classpath toplevel &lt;classpath&gt; element 
     * @return true if .classpath uses the <code>org.eclipse.pde.core.requiredPlugins</code> container to reference other plugin libraries false otherwise
     */
    boolean useEclipseDependenciesContainer(Element classpath) {
        Iterator elems = classpath.getDescendants(new Filter() {
            public boolean matches(Object obj) {
                if ( obj instanceof Element ) {
                    Element descendant = (Element) obj;
                    return  "classpathentry".equals(descendant.getName()) && 
                    		"con".equals(descendant.getAttributeValue("kind")) &&
                    		"org.eclipse.pde.core.requiredPlugins".equals(descendant.getAttributeValue("path"));
                }
                return false;
            }
        });
        
        return elems != null && elems.hasNext();
    }

    /**
     * extract common parameters from request
     * @param request 
     */
    protected void initialize(PluginExecutionRequest request) throws ConfigurationException {
        String eclipseHomeLocation = (String) request.getParameter("eclipseHome");
        eclipseHome = new File(eclipseHomeLocation);
        
        String eclipseConfigurationFolder = (String) request.getParameter("eclipseConfigurationFolder");
        configurationFolder = new File(eclipseConfigurationFolder);
        
        long maxBuildId = ((Long) request.getParameter("maxBuildId")).longValue();
        long minBuildId = ((Long) request.getParameter("minBuildId")).longValue();
        checkBuildId(minBuildId, maxBuildId);
        
        String outputDirectoryLocation = (String) request.getParameter("outputDirectory");
        outputDirectory = new File(outputDirectoryLocation);
        
        String basedirLocation = (String) request.getParameter("basedir");
        basedir = new File(basedirLocation);
        
        String workspaceLocation = (String) request.getParameter("outputDirectory");
        workspace = new File(workspaceLocation);
    }
}
