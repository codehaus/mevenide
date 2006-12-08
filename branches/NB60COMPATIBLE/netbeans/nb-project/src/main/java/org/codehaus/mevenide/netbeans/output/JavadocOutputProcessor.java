/* ==========================================================================
 * Copyright 2006 Mevenide Team
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessor;
import org.codehaus.mevenide.netbeans.api.output.OutputVisitor;
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
public class JavadocOutputProcessor implements OutputProcessor {
    
    private static final String[] JAVADOCGOALS = new String[] {
        "mojo-execute#javadoc:javadoc"
    };
    private Pattern index;
    private String path;
    
    /** Creates a new instance of JavadocOutputProcessor */
    public JavadocOutputProcessor() {
        index = Pattern.compile("Generating (.*)index\\.html.*", Pattern.DOTALL);
    }
    
    public String[] getRegisteredOutputSequences() {
        return JAVADOCGOALS;
    }
    
    public void processLine(String line, OutputVisitor visitor) {
        Matcher match = index.matcher(line);
        if (match.matches()) {
            path = match.group(1);
        }
    }
    
    public void sequenceStart(String sequenceId, OutputVisitor visitor) {
        path = null;
    }
    
    public void sequenceEnd(String sequenceId, OutputVisitor visitor) {
        if (path != null) {
            visitor.setLine("View Generated javadoc at " + path);
            visitor.setOutputListener(new Listener(path), false);
        }
    }
    
    public void sequenceFail(String sequenceId, OutputVisitor visitor) {
    }
    
    private static class Listener implements OutputListener {
        private String root;
        private Listener(String path) {
            root = path;
        }
        public void outputLineSelected(OutputEvent arg0) {
            
        }
        
        public void outputLineAction(OutputEvent arg0) {
            File javadoc = FileUtil.normalizeFile(new File(root));
            FileObject fo = FileUtil.toFileObject(javadoc);
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
