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

package org.mevenide.netbeans.project.nodes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Resource;
import org.apache.tools.ant.DirectoryScanner;
import org.mevenide.util.MevenideUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

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
    
    public static boolean checkIncluded(FileObject file, FileObject rootFile, String includes, String excludes) {
        List includeList = new ArrayList();
        if (includes != null) {
            StringTokenizer tok = new StringTokenizer(includes, " ,", false);
            while (tok.hasMoreTokens()) {
                includeList.add(tok.nextToken());
            }
        }
        List excludeList = new ArrayList();
        if (excludes != null) {
            StringTokenizer tok = new StringTokenizer(excludes, " ,", false);
            while (tok.hasMoreTokens()) {
                excludeList.add(tok.nextToken());
            }
        }
        return checkIncludedImpl(file, rootFile, includeList, excludeList);
    }
    
    public static boolean checkIncluded(FileObject file, FileObject rootFile, Resource resource) {
        logger.debug("chceckIncluded");
        return checkIncludedImpl(file, rootFile, resource.getIncludes(), resource.getExcludes());
    }
    
    /**
     * package private because of tests.
     */
    static boolean checkIncludedImpl(FileObject file, FileObject rootFile, List includes, List excludes) {
        String relPath = "";
        relPath = FileUtil.getRelativePath(rootFile, file);
        boolean doInclude = false;
        if (includes != null && includes.size() != 0) {
            Iterator it = includes.iterator();
            while (it.hasNext()) {
                String pattern = (String)it.next();
                logger.debug("include=" + pattern);
                // exact match or pattern match
                if (pattern.equals(relPath) || DirectoryScanner.match(pattern, relPath)) {
                    doInclude = true;
                    break;
                }
                if (file.isFolder()) {
                    int lastSlash = pattern.lastIndexOf("/");
                    if (lastSlash > -1) {
                        String dirPath = pattern.substring(0, lastSlash);
                        // needs to match something like templates/**/*.*
                        if (dirPath.startsWith(relPath) || DirectoryScanner.match(dirPath, relPath)) {
                           doInclude = true;
                           break;
                        }
                    }
                }
            }
        } else {
            String pattern = "**";
            if (pattern.equals(relPath) || DirectoryScanner.match(pattern, relPath)) {
                doInclude = true;
            }
        }
        
        if (!doInclude) {
            logger.debug("do not include");
            return false;
        }
        
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
        return true;
    }
    
    public static boolean checkVisible(File file, File rootFile) {

        String relPath = file.getAbsolutePath().replace('\\', '/');
        String[] defaults = DirScannerSubClass.getDefaultExcludesHack();
        if (defaults != null) {
            for (int i =0; i < defaults.length; i++) {
                if (DirectoryScanner.match(defaults[i], relPath)) {
                    return false;
                }
            }
        }
        return true;
    }
    
}
