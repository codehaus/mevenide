/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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
package org.mevenide.tags.netbeans;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;

import org.apache.commons.jelly.XMLOutput;
import org.mevenide.tags.AbstractNbMevenideTag;

/**
 *
 * @author <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 *
 */
public class ExamineManifestTag extends AbstractNbMevenideTag {
    
    private String isNetbeansModuleVar;    
    private String isLocalizedVar;
    private File jarFile;
    private File manifestFile;    
    private String specVersionVar;    
    private String implVersionVar;
    private String dependVar;
    private String moduleVar;
    private String locBundleVar;
    
    // package private to simplify testing
    boolean isNetbeansModule;
    boolean isLocalized;
    String specVersion;
    String implVersion;
    String module;
    String moduleDeps;
    String locBundle;
    
    
    public void doTag(XMLOutput arg0) throws MissingAttributeException, JellyTagException {
        
//        checkAttribute(jarFile, "jarFile");
        
        resetExamination();
        
        Manifest mf = null;
        if (jarFile != null) {
            JarFile jar = null;
            try {
                jar = new JarFile(jarFile);
                mf = jar.getManifest();
            } catch (Exception exc) {
                throw new JellyTagException(exc);
            } finally {
                if (jar != null) {
                    try {
                        jar.close();
                    } catch (IOException io) {
                        throw new JellyTagException(io);
                    }
                }
            }
        } else if (manifestFile != null) {
            InputStream stream = null;
            try {
                stream = new FileInputStream(manifestFile);
                mf = new Manifest(stream);
            } catch (Exception exc) {
                throw new JellyTagException(exc);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException io) {
                        throw new JellyTagException(io);
                    }
                }
            }
        }
        if (mf != null) {
            processManifest(mf);
        } else {
            throw new JellyTagException("Cannot read jar file or manifest file");
        }
        setContextVars();
    }
    
    void resetExamination() {
        isNetbeansModule = false;
        isLocalized = false;
        specVersion = null;
        implVersion = null;
        module = null;
        moduleDeps = null;
        locBundle = null;
    }
    
    private void setContextVars() {
        if (isNetbeansModuleVar != null) {
            context.setVariable(isNetbeansModuleVar, Boolean.valueOf(isNetbeansModule));
        }
        if (isLocalizedVar != null) {
            context.setVariable(isLocalizedVar, Boolean.valueOf(isLocalized));
        }
        if (specVersionVar != null) {
            context.setVariable(specVersionVar, specVersion);
        }
        if (implVersionVar != null) {
            context.setVariable(implVersionVar, implVersion);
        }
        if (dependVar != null) {
            context.setVariable(dependVar, moduleDeps);
        }
        if (moduleVar != null) {
            context.setVariable(moduleVar, module);
        }
        if (locBundleVar != null) {
            context.setVariable(locBundleVar, locBundle);
        }
    }
    
    void processManifest(Manifest mf) {
        Attributes attrs = mf.getMainAttributes();
        module = attrs.getValue("OpenIDE-Module");
        isNetbeansModule = (module == null ? false : true);
        if (isNetbeansModule) {
            locBundle = attrs.getValue("OpenIDE-Module-Localizing-Bundle");
            isLocalized = (locBundle == null ? false : true);
            specVersion = attrs.getValue("OpenIDE-Module-Specification-Version");
            implVersion = attrs.getValue("OpenIDE-Module-Implementation-Version");
            moduleDeps = attrs.getValue("OpenIDE-Module-Module-Dependencies");
        } else {
            // for non-netbeans jars.
            specVersion = attrs.getValue("Specification-Version");
            implVersion = attrs.getValue("Implementation-Version");
            module = attrs.getValue("Package");
        /*    if (module != null) {
                // now we have the package to make it a module definition, add the version there..
                module = module + "/1"; 
            }
         */
            if (module == null) {
                // do we want to do that?
                module = attrs.getValue("Extension-Name");
            }
        }
        
    }
    
    
    /**
     * Getter for property netbeansModule.
     * @return Value of property netbeansModule.
     */
    public String getIsNetbeansModuleVar() {
        return this.isNetbeansModuleVar;
    }    
    
    /**
     * Setter for property netbeansModule.
     * @param netbeansModule New value of property netbeansModule.
     */
    public void setIsNetbeansModuleVar(String isNetbeansModuleVar) {
        this.isNetbeansModuleVar = isNetbeansModuleVar;
    }    
    
    /**
     * Getter for property localized.
     * @return Value of property localized.
     */
    public String getIsLocalizedVar() {
        return this.isLocalizedVar;
    }
    
    /**
     * Setter for property localized.
     * @param localized New value of property localized.
     */
    public void setIsLocalizedVar(String isLocalizedVar) {
        this.isLocalizedVar = isLocalizedVar;
    }
    
    /**
     * Getter for property jarFile.
     * @return Value of property jarFile.
     */
    public java.io.File getJarFile() {
        return this.jarFile;
    }
    
    /**
     * Setter for property jarFile.
     * @param jarFile New value of property jarFile.
     */
    public void setJarFile(java.io.File jarFile) {
        this.jarFile = jarFile;
    }
    
    /**
     * Getter for property specVersionVar.
     * @return Value of property specVersionVar.
     */
    public String getSpecVersionVar() {
        return this.specVersionVar;
    }
    
    /**
     * Setter for property specVersionVar.
     * @param specVersionVar New value of property specVersionVar.
     */
    public void setSpecVersionVar(String specVersionVar) {
        this.specVersionVar = specVersionVar;
    }
    
    /**
     * Getter for property implVersionVar.
     * @return Value of property implVersionVar.
     */
    public String getImplVersionVar() {
        return this.implVersionVar;
    }
    
    /**
     * Setter for property implVersionVar.
     * @param implVersionVar New value of property implVersionVar.
     */
    public void setImplVersionVar(String implVersionVar) {
        this.implVersionVar = implVersionVar;
    }
    
    /**
     * Getter for property dependVar.
     * @return Value of property dependVar.
     */
    public String getDependVar() {
        return this.dependVar;
    }
    
    /**
     * Setter for property dependVar.
     * @param dependVar New value of property dependVar.
     */
    public void setDependVar(String dependVar) {
        this.dependVar = dependVar;
    }
    
    /**
     * Getter for property moduleVar.
     * @return Value of property moduleVar.
     */
    public String getModuleVar() {
        return this.moduleVar;
    }
    
    /**
     * Setter for property moduleVar.
     * @param moduleVar New value of property moduleVar.
     */
    public void setModuleVar(String moduleVar) {
        this.moduleVar = moduleVar;
    }
    
    /**
     * Getter for property locBundleVar.
     * @return Value of property locBundleVar.
     */
    public String getLocBundleVar() {
        return this.locBundleVar;
    }
    
    /**
     * Setter for property locBundleVar.
     * @param locBundleVar New value of property locBundleVar.
     */
    public void setLocBundleVar(String locBundleVar) {
        this.locBundleVar = locBundleVar;
    }
    
    /** Getter for property manifestFile.
     * @return Value of property manifestFile.
     *
     */
    public File getManifestFile()
    {
        return this.manifestFile;
    }    
    
    /** Setter for property manifestFile.
     * @param manifestFile New value of property manifestFile.
     *
     */
    public void setManifestFile(File manifestFile)
    {
        this.manifestFile = manifestFile;
    }
    
}
