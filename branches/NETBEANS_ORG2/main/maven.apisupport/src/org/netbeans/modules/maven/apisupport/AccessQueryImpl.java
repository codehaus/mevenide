/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.apisupport;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import org.netbeans.modules.maven.api.NbMavenProject;
import hidden.org.codehaus.plexus.util.IOUtil;
import org.netbeans.api.project.Project;
import org.netbeans.spi.java.queries.AccessibilityQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author mkleint
 */
public class AccessQueryImpl implements AccessibilityQueryImplementation {
    private NbMavenProject mavenProject;
    private Project project;
    private WeakReference<List<Pattern>> ref;
    
    private static final String MANIFEST_PATH = "src/main/nbm/manifest.mf"; //NOI18N
    private static final String ATTR_PUBLIC_PACKAGE = "OpenIDE-Module-Public-Packages"; //NOI18N
    
    public AccessQueryImpl(Project prj) {
        project = prj;
        mavenProject = prj.getLookup().lookup(NbMavenProject.class);
        
    }
    
    /**
     *
     * @param pkg
     * @return
     */
    public Boolean isPubliclyAccessible(FileObject pkg) {
        FileObject srcdir = org.netbeans.modules.maven.api.FileUtilities.convertStringToFileObject(mavenProject.getMavenProject().getBuild().getSourceDirectory());
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
            if (matches) {
                break;
            }
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
            InputStream in = null;
            try {
                in = obj.getInputStream();
                Manifest man = new Manifest();
                man.read(in);
                String value = man.getMainAttributes().getValue(ATTR_PUBLIC_PACKAGE);
                toRet = preparePublicPackagesPatterns(value);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                IOUtil.close(in);
            }
        }
        ref = new WeakReference<List<Pattern>>(toRet);
        return toRet;
    }
    
    static List<Pattern> preparePublicPackagesPatterns(String value) {
        List<Pattern> toRet = new ArrayList<Pattern>();
        if (value != null) {
            StringTokenizer tok = new StringTokenizer(value, " ,", false); //NOI18N
            while (tok.hasMoreTokens()) {
                String token = tok.nextToken();
                token = token.trim();
                boolean recursive = false;
                if (token.endsWith(".*")) { //NOI18N
                    token = token.substring(0, token.length() - ".*".length()); //NOI18N
                    recursive = false;
                } else if (token.endsWith(".**")) { //NOI18N
                    token = token.substring(0, token.length() - ".**".length()); //NOI18N
                    recursive = true;
                }
                token = token.replace(".","\\."); //NOI18N
                if (recursive) {
                    token = token + ".*"; //NOI18N
                }
                toRet.add(Pattern.compile(token));
            }
        }
        return toRet;
    }
    
}
