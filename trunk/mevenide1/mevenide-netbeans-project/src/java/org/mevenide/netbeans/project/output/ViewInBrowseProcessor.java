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
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;

import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileUtil;
import org.mevenide.netbeans.api.output.OutputVisitor;
import org.mevenide.netbeans.api.output.OutputProcessor;
import org.mevenide.netbeans.api.output.AbstractOutputProcessor;

/**
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class ViewInBrowseProcessor extends AbstractOutputProcessor implements OutputProcessor {
    private static final Logger logger = Logger.getLogger(ViewInBrowseProcessor.class.getName());
    
    private String[] goalsToWatch;
    private File file;
    private String questionMessage;
    private int priority;
    
    /** Creates a new instance of ViewInBrowseProcessor */
    public ViewInBrowseProcessor(String[] goals, File fileToOpen, String question, int prior) {
        goalsToWatch = goals;
        file = fileToOpen;
        questionMessage = question;
        priority = prior;
    }
    
    
    public void processLine(String line, OutputVisitor visitor) {
        if (isWatchedGoalLine(line)) {
            visitor.setSuccessAction(new ViewAction());
        }
    }
    
   public String[] getWatchedGoals() {
        return goalsToWatch;
    }    
    
    private class ViewAction extends AbstractAction {
        
        public ViewAction() {
            putValue(OutputVisitor.ACTION_QUESTION, questionMessage);
            putValue(OutputVisitor.ACTION_PRIORITY, new Integer(priority));
        }
        
        public void actionPerformed(java.awt.event.ActionEvent event) {
            
            File fil = FileUtil.normalizeFile(file);
            if (fil.exists()) {
                try {
                    HtmlBrowser.URLDisplayer.getDefault().showURL(fil.toURI().toURL());
                } catch (MalformedURLException exc) {
                    logger.log(Level.SEVERE, "MalformedURLException", exc);
                }
            }
            
        }
        
    }
}
