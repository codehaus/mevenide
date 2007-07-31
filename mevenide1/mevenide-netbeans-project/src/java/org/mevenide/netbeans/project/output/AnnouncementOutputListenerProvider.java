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

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mevenide.netbeans.api.project.MavenProject;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.mevenide.netbeans.api.output.OutputVisitor;
import org.mevenide.netbeans.api.output.AbstractOutputProcessor;



/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class AnnouncementOutputListenerProvider extends AbstractOutputProcessor {
    private static final Logger LOGGER = Logger.getLogger(AnnouncementOutputListenerProvider.class.getName());
    
    private static final String[] ANNOUNCEGOALS = new String[] {
        "announcement:generate:",
        "announcement:generate-all:"
    };
    private Pattern pattern;
    private MavenProject project;
    
    /** Creates a new instance of TestOutputListenerProvider */
    public AnnouncementOutputListenerProvider(MavenProject proj) {
        pattern = pattern.compile(".*\\[echo\\] Generating announcement for .* in (.*)\\.\\.\\.");
        project = proj;
    }
    
   public String[] getWatchedGoals() {
        return ANNOUNCEGOALS;
    }
    
    public void processLine(String line, OutputVisitor visitor) {
        if (isInWatchedGoals(line)) {
            Matcher match = pattern.matcher(line);
            if (match.matches()) {
                String file = match.group(1);
                //TODO just one instance and reuse..
                visitor.setOutputListener(new AnnOutputListener(project, file));
            }
        }
    }
    
    private static class AnnOutputListener implements OutputListener {
        private MavenProject project;
        private File file;
        public AnnOutputListener(MavenProject proj, String fileStr) {
            file = new File(fileStr);
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
            FileObject fo = FileUtil.toFileObject(file);
            if (fo == null) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            try {
                DataObject dob = DataObject.find(fo);
                OpenCookie ed = (OpenCookie) dob.getCookie(OpenCookie.class);
                if (ed != null) {
                    ed.open();
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            } catch (DataObjectNotFoundException donfe) {
                LOGGER.log(Level.WARNING, "DO not found.", donfe);
            } catch (IOException ioe) {
                LOGGER.log(Level.WARNING, "IOException", ioe);
            }
        }
        
        /** Called when a line is cleared from the buffer of known lines.
         * @param ev the event describing the line
         */
        public void outputLineCleared(OutputEvent ev) {
        }
    }
}
