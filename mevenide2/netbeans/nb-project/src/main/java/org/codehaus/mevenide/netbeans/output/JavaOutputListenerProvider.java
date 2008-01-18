/* ==========================================================================
 * Copyright 2005 Mevenide Team
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.mevenide.netbeans.FileUtilities;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessor;
import org.codehaus.mevenide.netbeans.api.output.OutputVisitor;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;




/**
 * compilation output processing
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class JavaOutputListenerProvider implements OutputProcessor {
    
    private static final String[] JAVAGOALS = new String[] {
        "mojo-execute#compiler:compile", //NOI18N
        "mojo-execute#compiler:testCompile" //NOI18N
    };
    private Pattern failPattern;
    
    /** Creates a new instance of JavaOutputListenerProvider */
    public JavaOutputListenerProvider() {
        //[javac] required because of forked compilation
        //DOTALL seems to fix MEVENIDE-455 on windows. one of the characters seems to be a some kind of newline and that's why the line doesnt' get matched otherwise.
        failPattern = Pattern.compile("\\s*(?:\\[javac\\])?(?:Compilation failure)?\\s*(.*)\\.java\\:\\[([0-9]*),([0-9]*)\\] (.*)", Pattern.DOTALL); //NOI18N
    }
    
    public void processLine(String line, OutputVisitor visitor) {
            Matcher match = failPattern.matcher(line);
            if (match.matches()) {
                String clazz = match.group(1);
                String lineNum = match.group(2);
                String text = match.group(4);
                visitor.setOutputListener(new CompileAnnotation(clazz, lineNum, 
                        text), text.indexOf("[deprecation]") < 0); //NOI18N
                File clazzfile = new File(clazz + ".java"); //NOI18N
                FileObject file = FileUtilities.toFileObject(FileUtil.normalizeFile(clazzfile));
                String newclazz = clazz;
                if (file != null) {
                    Project prj = FileOwnerQuery.getOwner(file);
                    if (prj != null) {
                        Sources srcs = prj.getLookup().lookup(Sources.class);
                        if (srcs != null) {
                            for (SourceGroup grp : srcs.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA)) {
                                if (FileUtil.isParentOf(grp.getRootFolder(), file)) {
                                    newclazz = FileUtil.getRelativePath(grp.getRootFolder(), file);
                                }
                            }
                        }
                    }
                }
                line = line.replace(clazz, newclazz);
                visitor.setLine(line);
            }
    }

    public String[] getRegisteredOutputSequences() {
        return JAVAGOALS;
    }

    public void sequenceStart(String sequenceId, OutputVisitor visitor) {
    }

    public void sequenceEnd(String sequenceId, OutputVisitor visitor) {
    }
    
    public void sequenceFail(String sequenceId, OutputVisitor visitor) {
    }
    
}
