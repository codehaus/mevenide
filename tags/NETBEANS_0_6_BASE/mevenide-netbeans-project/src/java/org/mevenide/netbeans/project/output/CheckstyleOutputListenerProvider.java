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

package org.mevenide.netbeans.project.output;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.project.MavenProject;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;
import org.mevenide.reports.CheckstyleResult;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class CheckstyleOutputListenerProvider extends AbstractOutputProcessor {
    private static final Log logger = LogFactory.getLog(CheckstyleOutputListenerProvider.class);
    
    private static final String[] CHECKSTYLEGOALS = new String[] {
        "checkstyle:run:"
    };
    private Pattern failPattern;
    private MavenProject project;
    
    /** Creates a new instance of TestOutputListenerProvider */
    public CheckstyleOutputListenerProvider(MavenProject proj) {
        failPattern = Pattern.compile("checkstyle\\:run\\:");
        project = proj;
    }
    
    protected String[] getWatchedGoals() {
        return CHECKSTYLEGOALS;
    }
    
    public void processLine(String line, OutputVisitor visitor) {
        if (isInWatchedGoals(line)) {
            Matcher match = failPattern.matcher(line);
            if (match.matches()) {
                visitor.setOutputListener(new CheckStyleOutputListener(project));
            }
        }
    }
    
    private static class CheckStyleOutputListener implements OutputListener {
        private MavenProject project;
        public CheckStyleOutputListener(MavenProject proj) {
            project = proj;
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
            openReport();
        }
        
        /** Called when a line is cleared from the buffer of known lines.
         * @param ev the event describing the line
         */
        public void outputLineCleared(OutputEvent ev) {
        }
        
        private void openReport() {
            CheckstyleResult result = new CheckstyleResult(project.getContext());
            String title = "Checkstyle: " + project.getDisplayName();
            try {
                IOProvider.getDefault().getIO(title, false).getOut().reset();
            } catch (Exception exc) {
                logger.error("Exception while resetting output", exc);
            }
            InputOutput io = IOProvider.getDefault().getIO(title, false);
            io.select();
            OutputWriter writer = io.getOut();
            try {
                File[] files = result.getFiles();
                File srcRoot = new File(project.getSrcDirectory());
                FileObject rootFo = FileUtil.toFileObject(srcRoot);
                if (files.length == 0) {
                    writer.println("No files violating the defined checkstyle rules.");
                } else {
                    writer.println("Number of violating files: " + files.length);
                    writer.println(" ");
                }
                for (int i = 0; i < files.length; i++) {
                    FileObject file = FileUtil.toFileObject(files[i]);
                    String relative = FileUtil.getRelativePath(rootFo, file);
                    relative = relative == null ? files[i].toString() : relative.replace('\\', '/').replace('/', '.');
                    writer.println(relative);
                    List viols = result.getViolationsForFile(files[i]);
                    Iterator it = viols.iterator();
                    while (it.hasNext()) {
                        CheckstyleResult.Violation v = (CheckstyleResult.Violation)it.next();
                        CheckstyleAnnotation list = new CheckstyleAnnotation(v);
                        writer.println("   Line:" + v.getLine() + " " + v.getMessage(), list);
                    }
                    writer.println(" ");
                }
            } catch (IOException exc) {
                logger.error("Exception while writing output", exc);
            } finally {
                writer.close();
            }
        }
    }
    
}
