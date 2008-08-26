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

package org.netbeans.modules.maven.api.output;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;
import org.openide.util.Exceptions;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;

/**
 *
 * @author mkleint
 */
public final class OutputUtils {
    static final Pattern linePattern = Pattern.compile("(?:\\[catch\\])?\\sat (.*)\\((.*)\\.java\\:(.*)\\)"); //NOI18N
    
    /** Creates a new instance of OutputUtils */
    private OutputUtils() {
    }
    
    public static OutputListener matchStackTraceLine(String line, ClassPath classPath) {
        Matcher match = linePattern.matcher(line);
        OutputListener list = null;
        if (match.matches()) {
            String method = match.group(1);
            String file = match.group(2);
            String lineNum = match.group(3);
            int index = method.indexOf(file);
            if (index > -1) {
                String packageName = method.substring(0, index).replace('.', '/'); //NOI18N
                String resourceName = packageName  + file + ".class"; //NOI18N
                FileObject resource = classPath.findResource(resourceName);
                if (resource != null) {
                    FileObject root = classPath.findOwnerRoot(resource);
                    URL url = URLMapper.findURL(root, URLMapper.INTERNAL);
                    SourceForBinaryQuery.Result res = SourceForBinaryQuery.findSourceRoots(url);
                    FileObject[] rootz = res.getRoots();
                    for (int i = 0; i < rootz.length; i++) {
                        String path = packageName + file + ".java"; //NOI18N
                        FileObject javaFo = rootz[i].getFileObject(path);
                        if (javaFo != null) {
                            try {
                                DataObject obj = DataObject.find(javaFo);
                                EditorCookie cook = obj.getCookie(EditorCookie.class);
                                int lineInt = Integer.parseInt(lineNum);
                                list = new OutputUtils.StacktraceOutputListener(cook, lineInt);
                            }
                            catch (DataObjectNotFoundException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                    }
                }
            }
        }
        return list;
    }
    
    
    private static class StacktraceOutputListener implements OutputListener {
        
        private EditorCookie cookie;
        private int line;
        public StacktraceOutputListener(EditorCookie cook, int ln) {
            cookie = cook;
            line = ln - 1;
        }
        public void outputLineSelected(OutputEvent ev) {
//            cookie.getLineSet().getCurrent(line).show(Line.SHOW_SHOW);
        }
        
        /** Called when some sort of action is performed on a line.
         * @param ev the event describing the line
         */
        public void outputLineAction(OutputEvent ev) {
            cookie.getLineSet().getCurrent(line).show(Line.SHOW_GOTO);
        }
        
        /** Called when a line is cleared from the buffer of known lines.
         * @param ev the event describing the line
         */
        public void outputLineCleared(OutputEvent ev) {
        }
        
    }
    
}
