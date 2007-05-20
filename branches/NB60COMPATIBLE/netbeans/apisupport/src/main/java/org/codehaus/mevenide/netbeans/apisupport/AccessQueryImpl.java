/* ==========================================================================
 * Copyright 2007 Mevenide Team
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

package org.codehaus.mevenide.netbeans.apisupport;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.spi.java.queries.AccessibilityQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author mkleint
 */
public class AccessQueryImpl implements AccessibilityQueryImplementation {
    private NbMavenProject project;
    private WeakReference<List<Pattern>> ref;
    
    private static final String MANIFEST_PATH = "src/main/nbm/manifest.mf"; //NOI18N
    private static final String ATTR_PUBLIC_PACKAGE = "OpenIDE-Module-Public-Packages"; //NOI18N
    
    public AccessQueryImpl(NbMavenProject prj) {
        project = prj;
    }
    
    /**
     *
     * @param pkg
     * @return
     */
    public Boolean isPubliclyAccessible(FileObject pkg) {
        FileObject srcdir = org.codehaus.mevenide.netbeans.FileUtilities.convertStringToFileObject(project.getOriginalMavenProject().getBuild().getSourceDirectory());
        if (srcdir != null) {
            String path = FileUtil.getRelativePath(srcdir, pkg);
            if (path != null) {
                String name = path.replace('/', '.');
                //TODO cache somehow..
                List<Pattern> pp = getPublicPackagesPatterns();
                if (pp.size() > 0) {
                    return check(pp, name);
                }
            }
        }
        
        return null;
    }
    
    private boolean check(List<Pattern> patt, String value) {
        boolean matches = false;
        for (Pattern pattern : patt) {
            matches = pattern.matcher(value).matches();
            if (matches) break;
        }
        return matches;
    }
    
    
    List<Pattern> getPublicPackagesPatterns() {
        if (ref != null) {
            List<Pattern> patterns = ref.get();
            if (patterns != null) {
                return patterns;
            }
        }
        List<Pattern> toRet = new ArrayList<Pattern>();
        FileObject obj = project.getProjectDirectory().getFileObject(MANIFEST_PATH);
        if (obj != null) {
            try {
                Manifest man = new Manifest(obj.getInputStream());
                String value = man.getMainAttributes().getValue(ATTR_PUBLIC_PACKAGE);
                toRet = preparePublicPackagesPatterns(value);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        ref = new WeakReference<List<Pattern>>(toRet);
        return toRet;
    }
    
    static List<Pattern> preparePublicPackagesPatterns(String value) {
        List<Pattern> toRet = new ArrayList<Pattern>();
        if (value != null) {
            StringTokenizer tok = new StringTokenizer(value, " ,", false);
            while (tok.hasMoreTokens()) {
                String token = tok.nextToken();
                token = token.trim();
                boolean recursive = false;
                if (token.endsWith(".*")) {
                    token = token.substring(0, token.length() - ".*".length());
                    recursive = false;
                } else if (token.endsWith(".**")) {
                    token = token.substring(0, token.length() - ".**".length());
                    recursive = true;
                }
                token = token.replace(".","\\.");
                if (recursive) {
                    token = token + ".*";
                }
                toRet.add(Pattern.compile(token));
            }
        }
        return toRet;
    }
    
}
