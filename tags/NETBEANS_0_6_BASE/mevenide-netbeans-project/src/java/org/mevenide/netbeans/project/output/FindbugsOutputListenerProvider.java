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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.reports.FindbugsResult;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;



/**
 * findbugs report output listener.
 * works with 0.8 series of the plugin.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class FindbugsOutputListenerProvider extends AbstractOutputProcessor {
    private static Log logger = LogFactory.getLog(FindbugsOutputListenerProvider.class);
    
    private static final String[] FINDBUGSGOALS = new String[] {
        "maven-findbugs-plugin:report:"
    };
    private Pattern failPattern;
    private MavenProject project;
    private boolean workNow;
    /** Creates a new instance of TestOutputListenerProvider */
    public FindbugsOutputListenerProvider(MavenProject proj) {
        project = proj;
        workNow = false;
        failPattern = Pattern.compile(".*\\[findbugs\\] Running FindBugs.*");
    }
    
    protected String[] getWatchedGoals() {
        return FINDBUGSGOALS;
    }
    
    public void processLine(String line, OutputVisitor visitor) {
        if (isWatchedGoalLine(line)) {
            workNow = true;
        }
        if (workNow) { 
            Matcher match = failPattern.matcher(line);
            if (match.matches()) {
                visitor.setOutputListener(new FindBugsOutputListener(project));
                workNow = false;
            }
        }
    }
    
    private static class FindBugsOutputListener implements OutputListener {
        private MavenProject project;
        public FindBugsOutputListener(MavenProject proj) {
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
            FindbugsResult result = new FindbugsResult(project.getContext());
            String title = "Findbugs: " + project.getDisplayName();
            try {
                IOProvider.getDefault().getIO(title, false).getOut().reset();
            } catch (Exception exc) {
                logger.error("Exception while resetting output", exc);
            }
            InputOutput io = IOProvider.getDefault().getIO(title, false);
            io.select();
            OutputWriter writer = io.getOut();
            try {
                String[] classes = result.getClassNames();
                File srcRoot = new File(project.getSrcDirectory());
                FileObject rootFo = FileUtil.toFileObject(srcRoot);
                if (classes.length == 0) {
                    writer.println("No bugs found.");
                } else {
                    writer.println("Number of matched classes: " + classes.length);
                    writer.println(" ");
                }
                for (int i = 0; i < classes.length; i++) {
                    
                    writer.println(classes[i]);
                    List viols = result.getViolationsForClass(classes[i]);
                    Iterator it = viols.iterator();
                    while (it.hasNext()) {
                        FindbugsResult.Violation v = (FindbugsResult.Violation)it.next();
                        FindbugsAnnotation list = new FindbugsAnnotation(v, rootFo);
                        writer.println("   Line:" + v.getLine() + " " + v.getMessage().trim(), list);
                        writer.println("                             (View detailed bug description in browser)", 
                                                new HtmlLinkListener(v.getType()));
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
    
    private static class HtmlLinkListener implements OutputListener {
        private String errorId;
        public HtmlLinkListener(String id) {
            errorId = id;
        }
        public void outputLineAction(OutputEvent ev) {
            try {
                URL link = new URL("http://findbugs.sourceforge.net/bugDescriptions.html#" + errorId);
                HtmlBrowser.URLDisplayer.getDefault().showURL(link);
            } catch (MalformedURLException exc) {
                NotifyDescriptor error = new NotifyDescriptor.Message("Is not a valid URL.", NotifyDescriptor.WARNING_MESSAGE);
                DialogDisplayer.getDefault().notify(error);
            }
            
        }

        public void outputLineCleared(OutputEvent ev) {
        }

        public void outputLineSelected(OutputEvent ev) {
        }
        
    }
    
}
