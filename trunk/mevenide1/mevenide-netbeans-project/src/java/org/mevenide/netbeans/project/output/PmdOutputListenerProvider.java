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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mevenide.netbeans.api.project.MavenProject;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;
import org.mevenide.reports.PmdResult;
import org.mevenide.netbeans.api.output.OutputVisitor;
import org.mevenide.netbeans.api.output.AbstractOutputProcessor;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class PmdOutputListenerProvider extends AbstractOutputProcessor {
    private static final Logger LOGGER = Logger.getLogger(PmdOutputListenerProvider.class.getName());
    
    private static final String[] PMDGOALS = new String[] {
        "pmd:report:"
    };
    private Pattern failPattern;
    private MavenProject project;
    
    /** Creates a new instance of TestOutputListenerProvider */
    public PmdOutputListenerProvider(MavenProject proj) {
        failPattern = Pattern.compile("pmd\\:report\\:");
        project = proj;
    }
    
    public String[] getWatchedGoals() {
        return PMDGOALS;
    }
    
    public void processLine(String line, OutputVisitor visitor) {
        if (isInWatchedGoals(line)) {
            Matcher match = failPattern.matcher(line);
            if (match.matches()) {
                visitor.setOutputListener(new PmdOutputListener(project));
            }
        }
    }
    
    private static class PmdOutputListener implements OutputListener {
        private MavenProject project;
        public PmdOutputListener(MavenProject proj) {
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
            PmdResult result = new PmdResult(project.getContext());
            String title = "PMD: " + project.getDisplayName();
            try {
                IOProvider.getDefault().getIO(title, false).getOut().reset();
            } catch (Exception exc) {
                LOGGER.log(Level.SEVERE, "Exception while resetting output", exc);
            }
            InputOutput io = IOProvider.getDefault().getIO(title, false);
            io.select();
            OutputWriter writer = io.getOut();
            try {
                File[] files = result.getFiles();
                File srcRoot = new File(project.getSrcDirectory());
                FileObject rootFo = FileUtil.toFileObject(srcRoot);
                if (files.length == 0) {
                    writer.println("No files matched the defined pmd rules.");
                } else {
                    writer.println("Number of matched files: " + files.length);
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
                        PmdResult.Violation v = (PmdResult.Violation)it.next();
                        PmdAnnotation list = new PmdAnnotation(v);
                        writer.println("   Line:" + v.getLine() + " " + v.getViolationText().trim(), list);
                    }
                    writer.println(" ");
                }
            } catch (IOException exc) {
                LOGGER.log(Level.SEVERE, "Exception while writing output", exc);
            } finally {
                writer.close();
            }
        }
    }
    
}
