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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mevenide.netbeans.project.MavenProject;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class TestOutputListenerProvider extends AbstractOutputProcessor {
    private static final String[] TESTGOALS = new String[] {
        "test:test:",
        "test:single:",
        "test:match:"
    };
    private Pattern failPattern;
    private MavenProject project;
    
    /** Creates a new instance of TestOutputListenerProvider */
    public TestOutputListenerProvider(MavenProject proj) {
        failPattern = failPattern.compile("\\s*\\[junit\\] \\[ERROR\\] TEST (.*) FAILED.*");
        project = proj;
    }
    
    protected String[] getWatchedGoals() {
        return TESTGOALS;
    }
    
    public void processLine(String line, OutputVisitor visitor) {
        if (isInWatchedGoals(line)) {
            Matcher match = failPattern.matcher(line);
            if (match.matches()) {
                String test = match.group(1);
                //TODO just one instance and reuse..
                visitor.setOutputListener(new TestOutputListener(project, test));
            }
        }
    }
    
    private static class TestOutputListener implements OutputListener {
        private MavenProject project;
        private String testname;
        public TestOutputListener(MavenProject proj, String test) {
            testname = test;
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
            String repDir = project.getPropertyResolver().getResolvedValue("maven.test.reportsDirectory");
            File dir = new File(repDir);
            if (dir.exists()) {
                File testResult = new File(dir, "TEST-" + testname + ".txt");
                FileObject fo = FileUtil.toFileObject(testResult);
                if (fo != null) {
                    InputOutput io = IOProvider.getDefault().getIO("Test " + testname, false);
                    io.getOut().flush();
                    io.select();
                    BufferedReader reader = null;
                    OutputWriter writer = io.getOut();
                    String line = null;
                    try {
                         reader = new BufferedReader(new InputStreamReader(fo.getInputStream()));
                        while ((line = reader.readLine()) != null) {
                            writer.println(line);
                        }
                    } catch (IOException exc) {
                        
                    } finally {
                        try {
                            if (reader != null) {
                                reader.close();
                            }
                            writer.close();
                        } catch (IOException ex) {
                            
                        }
                    }
                }
            }
            File testDir = new File(project.getTestSrcDirectory());
            String replace = testname.replace('.', '/');
            File testFile = new File(testDir, replace + ".java");
            if (testFile.exists()) {
                FileObject fo = FileUtil.toFileObject(testFile);
                try {
                    DataObject obj = DataObject.find(fo);
                    OpenCookie cook = (OpenCookie)obj.getCookie(OpenCookie.class);
                    if (cook != null) {
                        cook.open();
                    }
                } catch (DataObjectNotFoundException exc) {
                    
                }
            }
        }
        
        /** Called when a line is cleared from the buffer of known lines.
         * @param ev the event describing the line
         */
        public void outputLineCleared(OutputEvent ev) {
        }
    }
}
