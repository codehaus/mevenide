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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessor;
import org.codehaus.mevenide.netbeans.api.output.OutputVisitor;
import org.codehaus.mevenide.netbeans.nodes.DependenciesNode;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;

/**
 *
 * @author mkleint
 */
public class DependencyAnalyzeOutputProcessor implements OutputProcessor {
    
    private static final String[] DEPGOALS = new String[] {
        "mojo-execute#dependency:analyze" //NOI18N
    };
    private Pattern start;
    private boolean started;
    private Pattern dependency;
        private NbMavenProject project;
    
    /** Creates a new instance of JavadocOutputProcessor */
    DependencyAnalyzeOutputProcessor(NbMavenProject project) {
        started = false;
        start = Pattern.compile(".*Used undeclared dependencies.*", Pattern.DOTALL); //NOI18N
        dependency = Pattern.compile("\\s*(.*):(.*):(.*):(.*):(.*)", Pattern.DOTALL); //NOI18N
        this.project = project;
    }

    public String[] getRegisteredOutputSequences() {
        return DEPGOALS;
    }
    
    public void processLine(String line, OutputVisitor visitor) {
        if (started) {
            Matcher match = dependency.matcher(line);
            if (match.matches() && match.groupCount() >= 5) {
                String gr = match.group(1);
                String ar = match.group(2);
                String type = match.group(3);
                String ver = match.group(4);
                String sc = match.group(5);
                visitor.setLine(line + " (Click to add to pom.xml)"); //NOI18N - part of maven output
                visitor.setOutputListener(new Listener(project, gr, ar, type, ver, sc), false);
            } else {
                started = false;
            }
        }
        if (!started) {
            Matcher match = start.matcher(line);
            if (match.matches()) {
                started = true;
            }
        }
    }
    
    public void sequenceStart(String sequenceId, OutputVisitor visitor) {
        started = false;
    }
    
    public void sequenceEnd(String sequenceId, OutputVisitor visitor) {
    }
    
    public void sequenceFail(String sequenceId, OutputVisitor visitor) {
    }
    
    private static class Listener implements OutputListener {
        private String group;
        private String scope;
        private String version;
        private String type;
        private String artifact;
        private NbMavenProject project;
        
        private Listener(NbMavenProject prj, String gr, String ar, String type, String ver, String sc) {
            group = gr;
            artifact = ar;
            this.type = type;
            version = ver;
            scope = sc;
            project = prj;
        }
        public void outputLineSelected(OutputEvent arg0) {
        }
        
        public void outputLineAction(OutputEvent arg0) {
            DependenciesNode.addDependency(project, group, artifact, version, type, scope, null);
            NotifyDescriptor nd = new NotifyDescriptor.Message(org.openide.util.NbBundle.getMessage(DependencyAnalyzeOutputProcessor.class, "MSG_Dependency", group + ":" + artifact));
            DialogDisplayer.getDefault().notify(nd);
        }
        
        public void outputLineCleared(OutputEvent arg0) {
        }
    }
}
