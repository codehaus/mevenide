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
package org.apache.maven.plugin.nbm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Tag examines the manifest of a jar file and retrieves netbeans specific information.
 * @author <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 *
 */
public class ExamineManifest  {
    
    private File jarFile;
    private File manifestFile;
    // package private to simplify testing
    private boolean netbeansModule;
    private boolean localized;
    private String specVersion;
    private String implVersion;
    private String module;
    private String moduleDeps;
    private String locBundle;
    
    
    public void checkFile() throws MojoExecutionException {
        
        resetExamination();
        
        Manifest mf = null;
        if (jarFile != null) {
            JarFile jar = null;
            try {
                jar = new JarFile(jarFile);
                mf = jar.getManifest();
            } catch (Exception exc) {
                throw new MojoExecutionException( exc.getMessage(), exc );
            } finally {
                if (jar != null) {
                    try {
                        jar.close();
                    } catch (IOException io) {
                        throw new MojoExecutionException( io.getMessage(), io );
                    }
                }
            }
        } else if (manifestFile != null) {
            InputStream stream = null;
            try {
                stream = new FileInputStream(manifestFile);
                mf = new Manifest(stream);
            } catch (Exception exc) {
                throw new MojoExecutionException( exc.getMessage(), exc );
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException io) {
                        throw new MojoExecutionException( io.getMessage(), io );
                    }
                }
            }
        }
        if (mf != null) {
            processManifest(mf);
        } else {
            throw new MojoExecutionException("Cannot read jar file or manifest file");
        }
    }
    
    void resetExamination() {
        setNetbeansModule(false);
        setLocalized(false);
        setSpecVersion(null);
        setImplVersion(null);
        setModule(null);
        setModuleDeps(null);
        setLocBundle(null);
    }
    
    void processManifest(Manifest mf) {
        Attributes attrs = mf.getMainAttributes();
        setModule(attrs.getValue("OpenIDE-Module"));
        setNetbeansModule((getModule() == null ? false : true));
        if (isNetbeansModule()) {
            setLocBundle(attrs.getValue("OpenIDE-Module-Localizing-Bundle"));
            setLocalized((getLocBundle() == null ? false : true));
            setSpecVersion(attrs.getValue("OpenIDE-Module-Specification-Version"));
            setImplVersion(attrs.getValue("OpenIDE-Module-Implementation-Version"));
            setModuleDeps(attrs.getValue("OpenIDE-Module-Module-Dependencies"));
        } else {
            // for non-netbeans jars.
            setSpecVersion(attrs.getValue("Specification-Version"));
            setImplVersion(attrs.getValue("Implementation-Version"));
            setModule(attrs.getValue("Package"));
        /*    if (module != null) {
                // now we have the package to make it a module definition, add the version there..
                module = module + "/1"; 
            }
         */
            if (getModule() == null) {
                // do we want to do that?
                setModule(attrs.getValue("Extension-Name"));
            }
        }
        
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

    public boolean isNetbeansModule() {
        return netbeansModule;
    }

    public void setNetbeansModule(boolean netbeansModule) {
        this.netbeansModule = netbeansModule;
    }

    public boolean isLocalized() {
        return localized;
    }

    public void setLocalized(boolean localized) {
        this.localized = localized;
    }

    public String getSpecVersion() {
        return specVersion;
    }

    public void setSpecVersion(String specVersion) {
        this.specVersion = specVersion;
    }

    public String getImplVersion() {
        return implVersion;
    }

    public void setImplVersion(String implVersion) {
        this.implVersion = implVersion;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getModuleDeps() {
        return moduleDeps;
    }

    public void setModuleDeps(String moduleDeps) {
        this.moduleDeps = moduleDeps;
    }

    public String getLocBundle() {
        return locBundle;
    }

    public void setLocBundle(String locBundle) {
        this.locBundle = locBundle;
    }
    
}
