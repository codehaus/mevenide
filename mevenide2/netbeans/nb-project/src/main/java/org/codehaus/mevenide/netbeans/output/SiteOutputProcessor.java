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

package org.codehaus.mevenide.netbeans.output;

import java.io.File;
import java.net.URL;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessor;
import org.codehaus.mevenide.netbeans.api.output.OutputVisitor;
import org.netbeans.api.project.Project;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;

/**
 *
 * @author mkleint
 */
public class SiteOutputProcessor implements OutputProcessor {
    
    private static final String[] SITEGOALS = new String[] {
        "mojo-execute#site:site" //NOI18N
    };
    private Project project;
    
    /** Creates a new instance of SiteOutputProcessor */
    public SiteOutputProcessor(Project prj) {
        this.project = prj;
    }
    
    public String[] getRegisteredOutputSequences() {
        return SITEGOALS;
    }
    
    public void processLine(String line, OutputVisitor visitor) {
    }
    
    public void sequenceStart(String sequenceId, OutputVisitor visitor) {
    }
    
    public void sequenceEnd(String sequenceId, OutputVisitor visitor) {
        visitor.setLine("     View Generated Project Site"); //NOI18N shows up in maven output.
        visitor.setOutputListener(new Listener(project), false);
    }
    
    public void sequenceFail(String sequenceId, OutputVisitor visitor) {
    }
    
    private static class Listener implements OutputListener {
        private File root;
        private Listener(Project prj) {
            File fl = FileUtil.toFile(prj.getProjectDirectory());
            root = new File(fl, "target" + File.separator + "site"); //NOI18N
        }
        public void outputLineSelected(OutputEvent arg0) {
            
        }
        
        public void outputLineAction(OutputEvent arg0) {
            File site = FileUtil.normalizeFile(root);
            FileObject fo = FileUtil.toFileObject(site);
            if (fo != null) {
                FileObject index = fo.getFileObject("index.html"); //NOI18N
                if (index != null) {
                    URL link = URLMapper.findURL(index, URLMapper.EXTERNAL);
                    HtmlBrowser.URLDisplayer.getDefault().showURL(link);
                }
            }
        }
        
        public void outputLineCleared(OutputEvent arg0) {
        }
    }
}
