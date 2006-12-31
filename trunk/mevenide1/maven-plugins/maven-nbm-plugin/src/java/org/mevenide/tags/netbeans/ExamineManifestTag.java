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
 * Tag examines the manifest of a jar file and retrieves netbeans specific information.
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
        return isNetbeansModuleVar;
    }    
    
    /**
     * Variable which will hold information wheather the examined manifest is 
     * Netbeans-enhanced or not.
     */
    public void setIsNetbeansModuleVar(String isNetbeansModuleVariable) {
        isNetbeansModuleVar = isNetbeansModuleVariable;
    }    
    
    /**
     * Getter for property localized.
     * @return Value of property localized.
     */
    public String getIsLocalizedVar() {
        return isLocalizedVar;
    }
    
    /**
     * Variable holding information about wheather the Netbeans manifest is
     * localized or not. (Some UI related entries can be defined in properties file.)
     */
    public void setIsLocalizedVar(String isLocalizedVariable) {
        isLocalizedVar = isLocalizedVariable;
    }
    
    /**
     * Getter for property jarFile.
     * @return Value of property jarFile.
     */
    public java.io.File getJarFile() {
        return jarFile;
    }

    /**
     * The jar file to examine. It is exclusing with manifestFile.
     */
    public void setJarFile(java.io.File jarFileLoc) {
        jarFile = jarFileLoc;
    }

    /**
     * Getter for property specVersionVar.
     * @return Value of property specVersionVar.
     */
    public String getSpecVersionVar() {
        return specVersionVar;
    }

    /**
     * Variable holds the found specification version, either from 
     * OpenIDE-Module-Specification-Version for nb modules or from
     * Specification-Version attribute for other jars.
     */
    public void setSpecVersionVar(String specVersionVariable) {
        specVersionVar = specVersionVariable;
    }

    /**
     * Getter for property implVersionVar.
     * @return Value of property implVersionVar.
     */
    public String getImplVersionVar() {
        return implVersionVar;
    }

    /**
     * Variable holds the found implementation version., either from
     * OpenIDE-Module-Implementation-Version for nb modules or from
     * Implementation-Version attribute for other jars.
     */
    public void setImplVersionVar(String implVersionVariable) {
        implVersionVar = implVersionVariable;
    }
    
    /**
     * Getter for property dependVar.
     * @return Value of property dependVar.
     */
    public String getDependVar() {
        return dependVar;
    }
    
    /**
     * For Netbeans modules, this variable holds the module's dependencies 
     * (OpenIDE-Module-Module-Dependencies).
     * @param dependVar New value of property dependVar.
     */
    public void setDependVar(String dependVariable) {
        dependVar = dependVariable;
    }
    
    /**
     * Getter for property moduleVar.
     * @return Value of property moduleVar.
     */
    public String getModuleVar() {
        return moduleVar;
    }
    
    /**
     * Variable holds the Netbeans module value, for
     * Netbeans enhanced manifest it's the OpenIDE-Module attribute value,
     * for others, it's guessed.
     * @param moduleVar New value of property moduleVar.
     */
    public void setModuleVar(String moduleVariable) {
        moduleVar = moduleVariable;
    }
    
    /**
     * Getter for property locBundleVar.
     * @return Value of property locBundleVar.
     */
    public String getLocBundleVar() {
        return locBundleVar;
    }
    
    /**
     * Content of the OpenIDE-Module-Localizing-Bundle attribute, holds link
     * to the bundle file with localized information for the module.
     * @param locBundleVar New value of property locBundleVar.
     */
    public void setLocBundleVar(String localBundleVar) {
        locBundleVar = localBundleVar;
    }
    
    /** Getter for property manifestFile.
     * @return Value of property manifestFile.
     *
     */
    public File getManifestFile()
    {
        return manifestFile;
    }    
    
    /** 
     * Manifest file to be examined. It is exclusing with jarFile.
     */
    public void setManifestFile(File manifestFileLoc)
    {
        manifestFile = manifestFileLoc;
    }
    
}
