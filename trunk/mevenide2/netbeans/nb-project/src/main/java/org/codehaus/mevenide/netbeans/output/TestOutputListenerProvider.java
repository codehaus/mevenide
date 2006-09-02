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
package org.codehaus.mevenide.netbeans.output;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessor;
import org.codehaus.mevenide.netbeans.api.output.OutputVisitor;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;


/**
 * processing test (surefire) output
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class TestOutputListenerProvider implements OutputProcessor {
    private static final String[] TESTGOALS = new String[] {
        "mojo-execute#surefire:test" //NOI18N
    };
    private Pattern failSeparatePattern;
    private Pattern failInlinedPattern;
    private Pattern outDirPattern;
    private Pattern runningPattern;
    
    private NbMavenProject project;
    String outputDir;
    String runningTestClass;
    
    /** Creates a new instance of TestOutputListenerProvider */
    public TestOutputListenerProvider(NbMavenProject proj) {
        failSeparatePattern = Pattern.compile("Tests run.*[<]* FAILURE[!]*[\\s]*"); //NOI18N
        failInlinedPattern = Pattern.compile(".*\\(.*\\).*[<]* FAILURE[!]*"); //NOI18N
        runningPattern = Pattern.compile("Running (.*)"); //NOI18N
        outDirPattern = Pattern.compile("Surefire report directory\\: (.*)"); //NOI18N
        project = proj;
    }
    
    public String[] getWatchedGoals() {
        return TESTGOALS;
    }
    
    public void processLine(String line, OutputVisitor visitor) {
        if (outputDir == null) {
            Matcher match = outDirPattern.matcher(line);
            if (match.matches()) {
                outputDir = match.group(1);
                return;
            }
        }
        Matcher match = runningPattern.matcher(line);
        if (match.matches()) {
            runningTestClass = match.group(1);
            return;
        }
        
        match = failSeparatePattern.matcher(line);
        if (match.matches()) {
            visitor.setOutputListener(new TestOutputListener(project, runningTestClass, outputDir), true);
        }
    }
    
    public String[] getRegisteredOutputSequences() {
        return TESTGOALS;
    }
    
    public void sequenceStart(String sequenceId, OutputVisitor visitor) {
    }
    
    public void sequenceEnd(String sequenceId, OutputVisitor visitor) {
    }
    
    public void sequenceFail(String sequenceId, OutputVisitor visitor) {
    }
    
    private static class TestOutputListener implements OutputListener {
        private NbMavenProject project;
        private String testname;
        private String outputDir;
        public TestOutputListener(NbMavenProject proj, String test, String outDir) {
            testname = test;
            project = proj;
            outputDir = outDir;
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
            File testDir = new File(project.getOriginalMavenProject().getBuild().getTestSourceDirectory());
            String replace = testname.replace('.', '/');
            File testFile = new File(testDir, replace + ".java"); //NOI18N
            FileObject fo = FileUtil.toFileObject(testFile);
            FileObject outDir;
            if (outputDir != null) {
                outDir = FileUtil.toFileObject(new File(outputDir));
            } else {
                //TODO how to report..
                return;
            }
            outDir.refresh();
            FileObject report = outDir.getFileObject(testname + ".txt");
            if (report != null) {
                String nm = testname.lastIndexOf('.') > -1 
                        ? testname.substring(testname.lastIndexOf('.')) 
                        : testname;
                openLog(report, nm, fo);
            } else {
                //TODO how to report..
            }
        }
        
        /** Called when a line is cleared from the buffer of known lines.
         * @param ev the event describing the line
         */
        public void outputLineCleared(OutputEvent ev) {
        }
        
        private void openLog(FileObject fo, String title, FileObject testFile) {
            try {
                IOProvider.getDefault().getIO(title, false).getOut().reset();
            } catch (Exception exc) {
                ErrorManager.getDefault().notify(exc);
            }
            InputOutput io = IOProvider.getDefault().getIO(title, false);
            io.select();
            BufferedReader reader = null;
            OutputWriter writer = io.getOut();
            String line = null;
            try {
                reader = new BufferedReader(new InputStreamReader(fo.getInputStream()));
                Pattern linePattern = Pattern.compile("\\sat (.*)\\((.*)\\.java\\:(.*)\\)"); //NOI18N
                ClassPath classPath = ClassPath.getClassPath(testFile, ClassPath.EXECUTE);
                while ((line = reader.readLine()) != null) {
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
                                    File rootFile = FileUtil.toFile(rootz[i]);
                                    File java = new File(rootFile, packageName + file + ".java");
                                    FileObject javaFo = FileUtil.toFileObject(java);
                                    if (javaFo != null) {
                                        DataObject obj = DataObject.find(javaFo);
                                        EditorCookie cook = (EditorCookie)obj.getCookie(EditorCookie.class);
                                        int lineInt = Integer.parseInt(lineNum);
                                        list = new StacktraceOutputListener(cook, lineInt);
                                    }
                                }
                            }
                        } else {
                            //weird..
                        }
                    }
                    if (list != null) {
                        writer.println(line, list);
                    } else {
                        writer.println(line);
                    }
                }
            } catch (IOException exc) {
                ErrorManager.getDefault().notify(exc);
            } finally {
                writer.close();
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        }
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
