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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.project.MavenProject;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
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
import org.mevenide.netbeans.api.output.OutputVisitor;
import org.mevenide.netbeans.api.output.AbstractOutputProcessor;


/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class TestOutputListenerProvider extends AbstractOutputProcessor {
    private static final Log logger = LogFactory.getLog(TestOutputListenerProvider.class);    
    private static final String[] TESTGOALS = new String[] {
        "test:test:", //NOI18N
        "test:single:", //NOI18N
        "test:match:" //NOI18N
    };
    private Pattern failPattern;
    private MavenProject project;
    
    /** Creates a new instance of TestOutputListenerProvider */
    public TestOutputListenerProvider(MavenProject proj) {
        failPattern = Pattern.compile("\\s*\\[junit\\] \\[ERROR\\] TEST (.*) FAILED.*"); //NOI18N
        project = proj;
    }
    
    public String[] getWatchedGoals() {
        return TESTGOALS;
    }
    
    public void processLine(String line, OutputVisitor visitor) {
        if (isInWatchedGoals(line)) {
            Matcher match = failPattern.matcher(line);
            if (match.matches()) {
                String test = match.group(1);
                //TODO just one instance and reuse..
                visitor.setOutputListener(new TestOutputListener(project, test), true);
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
            File testDir = new File(project.getTestSrcDirectory());
            String replace = testname.replace('.', '/');
            File testFile = new File(testDir, replace + ".java"); //NOI18N
            FileObject fo = FileUtil.toFileObject(testFile);
//            try {
//                DataObject obj = DataObject.find(fo);
//                OpenCookie cook = (OpenCookie)obj.getCookie(OpenCookie.class);
//                if (cook != null) {
//                    cook.open();
//                }
//            } catch (DataObjectNotFoundException exc) {
//                
//            }
            String repDir = project.getPropertyResolver().getResolvedValue("maven.test.reportsDirectory"); //NOI18N
            if (repDir == null) {
                StatusDisplayer.getDefault().setStatusText("Maven: Cannot resolve property maven.test.reportsDirectory.");
                logger.error("Cannot resolve property maven.test.reportsDirectory.");
                return;
            }
            File dir = new File(repDir);
            if (dir.exists()) {
                File testResult = new File(dir, "TEST-" + testname + ".txt");
                FileObject fo2 = FileUtil.toFileObject(testResult);
                if (fo != null) {
                    String nm = testname.lastIndexOf('.') > -1 ? testname.substring(testname.lastIndexOf('.')) : testname;
                    openLog(fo2, "Test " + nm, fo);
                }
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
                logger.error("Exception while resetting output", exc);
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
                logger.warn("exception IO", exc);
            } finally {
                writer.close();
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException ex) {
                    logger.warn("exception IO", ex);
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
