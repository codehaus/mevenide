/*
 * ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * =========================================================================
 */
package org.codehaus.mevenide.provider.tag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.mevenide.provider.GrabberException;
import org.codehaus.mevenide.provider.PropertyGrabber;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet </a>
 * @version $Id$
 *  
 */
public class ExtractPropertiesTag extends TagSupport {

    private static final Log log = LogFactory.getLog(ExtractPropertiesTag.class);
    
    /**
     * name of the plugin. plugin.properties will then be looked in
     * ${maven.plugins.unpacked.dir}/${pluginName}/plugin.properties
     */
    private String pluginName;
    
    /**
     * version of the plugin. plugin.properties will then be looked in
     * ${maven.plugins.unpacked.dir}/${pluginName}/plugin.properties
     */
    private String pluginVersion;
    
    /**
     * name of the destination file. if null output will be redirected to System.err
     */
    private String destFile;
    
    /** name of the optional input file */
    private String inputFile;

    /** fully qualified class name of the IElementHandler to use */
    private String elementHandlerClassName; 
    
    private PropertyGrabber grabber;
    
    public void doTag(XMLOutput arg0) throws MissingAttributeException, JellyTagException {
        try {
            validate();
            initialize();
            grabber.grab();
            outputDescription();
        }
        catch (Exception e) {
            throw new JellyTagException(e);
        }
        
    }

    private void validate() throws Exception {
        checkAttribute(pluginVersion, "pluginVersion");
        checkAttribute(pluginName, "pluginName");
        
        if ( !isNull(inputFile) && !new File(inputFile).exists() ) {
            //we should have another exception for this error
            throw new Exception("Input file : " + inputFile + " doesnot exist");
        }
    }

    private void initialize() {
        grabber = new PropertyGrabber();
        grabber.setPluginName(pluginName);
        grabber.setPluginVersion(pluginVersion);
        grabber.setElementHandlerClassName(elementHandlerClassName);
        ILocationFinder finder = new LocationFinderAggregator();
        if ( isNull(inputFile) ) {
            File pluginDir = new File(finder.getMavenPluginsDir(), pluginName + "-" + pluginVersion); //$NON-NLS-1$
            grabber.setPropertyFile(new File(pluginDir, "plugin.properties")); //$NON-NLS-1$
        }
        else {
            grabber.setPropertyFile(new File(inputFile));
        }
    }

    private void outputDescription() throws IOException {
        String propertyDescription = grabber.getPropertyDescription();
        if ( destFile != null ) {
            FileOutputStream fos = null;
            try {
                File destination = new File(destFile);
                destination.getParentFile().mkdirs();
                fos = new FileOutputStream(destination);
                fos.write(propertyDescription.getBytes());
            }
            catch (IOException e) {
                String message = "Unable to write propertiesDescription to file " + destFile + ". redirecting to System.err";
                log.error(message, e);
                System.err.println(message + "\n" + propertyDescription);
            }
            finally {
                if ( fos != null ) {
                    fos.close();
                }
            }
        }
        else {
            System.out.println("Attribute destFile not specified. Redirecting output to console \n" + propertyDescription);
        }
    }

    protected void checkAttribute(String attribute, String attributeName) throws MissingAttributeException {
        if ( isNull(attribute) ) {
            throw new MissingAttributeException(attributeName);
        }
    }

    private boolean isNull(String strg) {
        return strg == null || strg.trim().length() == 0;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    public String getDestFile() {
        return destFile;
    }

    public void setDestFile(String destFile) {
        this.destFile = destFile;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }
    
    public String getElementHandlerClassName() {
        return elementHandlerClassName;
    }
    
    public void setElementHandlerClassName(String elementHandlerClassName) {
        this.elementHandlerClassName = elementHandlerClassName;
    }
}