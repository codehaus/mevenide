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

package org.codehaus.mevenide.netbeans.api.output;

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
    static final Pattern linePattern = Pattern.compile("\\sat (.*)\\((.*)\\.java\\:(.*)\\)"); //NOI18N
    
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
                String packageName = method.substring(0, index).replace('.', '/');
                String resourceName = packageName  + file + ".class"; //NOI18N
                FileObject resource = classPath.findResource(resourceName);
                if (resource != null) {
                    FileObject root = classPath.findOwnerRoot(resource);
                    URL url = URLMapper.findURL(root, URLMapper.INTERNAL);
                    SourceForBinaryQuery.Result res = SourceForBinaryQuery.findSourceRoots(url);
                    FileObject[] rootz = res.getRoots();
                    for (int i = 0; i < rootz.length; i++) {
                        String path = packageName + file + ".java";
                        FileObject javaFo = rootz[i].getFileObject(path);
                        if (javaFo != null) {
                            try {
                                DataObject obj = DataObject.find(javaFo);
                                EditorCookie cook = (EditorCookie) obj.getCookie(EditorCookie.class);
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
            cookie.getLineSet().getCurrent(line).show(Line.SHOW_SHOW);
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
