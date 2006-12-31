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
package org.codehaus.mevenide.pde.license;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class LicenseReader {
    
    /** jarFile to explore */
    private File jarFile;
    
    /**
     * possible license file locations, experimentally found these.. 
     * if adding new, add in uppercase.
     */
    private final static String[] possibleLocations = new String[] {
        "META-INF/LICENSE",
        "LICENSE",
        "COPYING"
    };
    
    public String readLicense() throws LicenseException {
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
                BufferedReader read = null;
                try {
	                read = new BufferedReader(new InputStreamReader(stream));
	                char[] chr = new char[1];
	                int rd = read.read(chr);
	                while (rd != -1) {
	                    toReturn.append(chr);
	                    rd = read.read(chr);
	                }
                }
                finally {
                    if ( read != null ) { read.close(); }
                }
            }
        } 
        catch (Exception exc) {
            throw new LicenseException(exc);
        } 
        finally {
            if (jar != null) {
                try {
                    jar.close();
                } 
                catch (IOException io) {
                    throw new LicenseException(io);
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
    
    public void setJarFile(File jarFile) { this.jarFile = jarFile; }
}
