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
package org.mevenide.tags;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.jelly.JellyTagException;

import org.apache.commons.jelly.XMLOutput;

/**
 * A tag trying to find the license for the specified jar.
 * A few known places in the jar are checked, like META-INF/license, license, or copying
 * @author <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 *
 */
public class FindLicenseTag extends AbstractNbMevenideTag {
    
    
    private String var;
    private File jarFile;
    
    /**
     * possible license file locations, experimentally found these.. 
     * if adding new, add in uppercase.
     */
    private static final String[] possibleLocations = new String[] {
        "META-INF/LICENSE",
        "LICENSE",
        "COPYING"
    };

    public void doTag(XMLOutput arg0) throws JellyTagException {
         
        checkAttribute(jarFile, "jarFile");
        checkAttribute(var, "var");
        
        String foundLicense = readLicense();
        context.setVariable(var, foundLicense);
    }
    
    
    public String readLicense() throws JellyTagException {
        StringBuffer toReturn = new StringBuffer();
        JarFile jar = null;
        try {
            jar = new JarFile(jarFile);
            Enumeration en = jar.entries();
            JarEntry found = null;
            while (en.hasMoreElements()) {
                JarEntry entry = (JarEntry)en.nextElement();
                if (!entry.isDirectory()) {
                    if (checkName(entry.getName())) {
                        found = entry;
                        break;
                    }
                }
            }
            if (found != null) {
                InputStream stream = jar.getInputStream(found);
                BufferedReader read = new BufferedReader(new InputStreamReader(stream));
                char[] chr = new char[1];
                int rd = read.read(chr);
                while (rd != -1) {
                    toReturn.append(chr);
                    rd = read.read(chr);
                }
                read.close();
            }
        } catch (Exception exc) {
            throw new JellyTagException("Error while reading license.", exc);
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException io) {
                    //JUST Ignore here?
                }
            }
        }
        if (toReturn.length() == 0) {
            return null;
        }
        return toReturn.toString();
    }

    
    private boolean checkName(String path) {
        String upperCase = path.toUpperCase();
        for (int i = 0; i < possibleLocations.length; i++) {
            if (upperCase.startsWith(possibleLocations[i])) {
                return true;
            }
        }
        return false;
    }
    
    
    public String getVar() {
        return var;
    }

    /**
     * Variable name that will be passed the found license, if any.
     */
    public void setVar(String variable) {
        this.var = variable;
    }
    
    /**
     * Getter for property jarFile.
     * @return Value of property jarFile.
     */
    public File getJarFile() {
        return jarFile;
    }
    
    /**
     * Variable contains the location of the jar file to check for license.
     * @param jarFile New value of property jarFile.
     */
    public void setJarFile(File jarFileLoc) {
        jarFile = jarFileLoc;
    }    
}
