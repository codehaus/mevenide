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

package org.mevenide.netbeans.project.nodes;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.MavenUtils;
import org.apache.maven.project.Resource;
import org.apache.tools.ant.DirectoryScanner;

/**
 * a directory scanner subclass which only purposes is to return the default excludes..
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
// was made public just because of MavenSourcesImpl, prolly better to move there..
public final class DirScannerSubClass extends DirectoryScanner {
    private static Log logger = LogFactory.getLog(DirScannerSubClass.class);
    /** Creates a new instance of DirScannerSubClass */
    public DirScannerSubClass() {
        super();
    }
    
    public static String[] getDefaultExcludesHack() {
        return DEFAULTEXCLUDES;
    }
    
    
    
    public static boolean checkIncluded(File file, File rootFile, Resource resource) {
        logger.debug("chceckIncluded");
        String relPath = "";
        try {
            relPath = MavenUtils.makeRelativePath(rootFile, file.getAbsolutePath());
        } catch (IOException exc) {
            logger.info(exc);
            return false;
        }
        logger.debug("chceckIncluded relpath=" + relPath);
        List includes = resource.getIncludes();
        boolean doInclude = false;
        if (includes != null && includes.size() != 0) {
            Iterator it = includes.iterator();
            while (it.hasNext()) {
                String pattern = (String)it.next();
                logger.debug("include=" + pattern);
                // exact match or pattern match
                if (pattern.equals(relPath) || DirectoryScanner.match(pattern, file.getAbsolutePath())) {
                    doInclude = true;
                    break;
                }
                //                if (file.isDirectory() && pattern.startsWith(relPath)) {
                //                    doInclude = true;
                //                    break;
                //                }
            }
        } else {
            String pattern = "**";
            if (pattern.equals(relPath) || DirectoryScanner.match(pattern, file.getAbsolutePath())) {
                doInclude = true;
            }
        }
        
        if (!doInclude) {
            logger.debug("do not include");
            return false;
        }
        
        List excludes = resource.getExcludes();
        if (excludes != null) {
            Iterator it = excludes.iterator();
            while (it.hasNext()) {
                String pattern = (String)it.next();
                logger.debug("exclude=" + pattern);
                if (pattern.equals(relPath) || DirectoryScanner.match(pattern, relPath)) {
                    return false;
                }
            }
        }
        //        String[] defaults = DirScannerSubClass.getDefaultExcludesHack();
        //        if (defaults != null) {
        //            for (int i =0; i < defaults.length; i++) {
        //                if (DirectoryScanner.match(defaults[i], relPath)) {
        //                    return false;
        //                }
        //            }
        //        }
        return true;
    }
    
    public static boolean checkVisible(File file, File rootFile) {
        String relPath = "";
        try {
            relPath = MavenUtils.makeRelativePath(rootFile, file.getAbsolutePath());
        } catch (IOException exc) {
            logger.info(exc);
            return false;
        }
        String[] defaults = DirScannerSubClass.getDefaultExcludesHack();
        if (defaults != null) {
            for (int i =0; i < defaults.length; i++) {
                if (DirectoryScanner.match(defaults[i], file.getAbsolutePath())) {
                    System.out.println("kick out=" + relPath + " because of=" + defaults[i]);
                    return false;
                }
            }
        }
        return true;
    }
    
}
