/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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

package org.mevenide.netbeans.project.output;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.project.MavenProject;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;



/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class JavaOutputListenerProvider extends AbstractOutputProcessor {
    private static final Log logger = LogFactory.getLog(JavaOutputListenerProvider.class);
    
    private static final String[] JAVAGOALS = new String[] {
        "java:compile:",
        "test:compile:"
    };
    private Pattern failPattern;
    private MavenProject project;
    
    /** Creates a new instance of TestOutputListenerProvider */
    public JavaOutputListenerProvider(MavenProject proj) {
        failPattern = failPattern.compile("(.*)\\.java\\:([0-9]*)\\: (.*)");
        project = proj;
    }
    
    protected String[] getWatchedGoals() {
        return JAVAGOALS;
    }
    
    public void processLine(String line, OutputVisitor visitor) {
        if (isInWatchedGoals(line)) {
            Matcher match = failPattern.matcher(line);
            if (match.matches()) {
                String clazz = match.group(1);
                String lineNum = match.group(2);
                //TODO just one instance and reuse..
                visitor.setOutputListener(new JavaOutputListener(project, clazz, lineNum));
            }
        }
    }
    
    private static class JavaOutputListener implements OutputListener {
        private MavenProject project;
        private File clazzfile;
        private int lineNum;
        public JavaOutputListener(MavenProject proj, String clazz, String line) {
            clazzfile = new File(clazz + ".java");
            project = proj;
            try {
                lineNum = Integer.parseInt(line);
            } catch (NumberFormatException exc) {
                lineNum = -1;
            }
        }
        /** Called when a line is selected.
         * @param ev the event describing the line
         */
        public void outputLineSelected(OutputEvent ev) {
        }
        
        /** Called when some sort of action is performed on a line.
         * @param ev the event describing the line
         */
        public void outputLineAction(OutputEvent ev) {
            FileObject file = FileUtil.toFileObject(clazzfile);
            if (file == null) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            try {
                DataObject dob = DataObject.find(file);
                EditorCookie ed = (EditorCookie) dob.getCookie(EditorCookie.class);
                if (ed != null && file == dob.getPrimaryFile()) {
                    if (lineNum == -1) {
                        ed.open();
                    } else {
                        ed.openDocument();
                        try {
                            Line l = ed.getLineSet().getOriginal(lineNum - 1);
                            if (! l.isDeleted()) {
                                l.show(Line.SHOW_GOTO);
                            }
                        } catch (IndexOutOfBoundsException ioobe) {
                            // Probably harmless. Bogus line number.
                            ed.open();
                        }
                    }
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            } catch (DataObjectNotFoundException donfe) {
                logger.warn("DO not found.", donfe);
            } catch (IOException ioe) {
                logger.warn(ioe);
            }
        }
        
        /** Called when a line is cleared from the buffer of known lines.
         * @param ev the event describing the line
         */
        public void outputLineCleared(OutputEvent ev) {
        }
    }
}
