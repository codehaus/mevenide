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

package org.codehaus.mevenide.netbeans.queries;

import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.netbeans.spi.java.queries.SourceLevelQueryImplementation;
import org.openide.filesystems.FileObject;

/**
 * maven implementation of SourceLevelQueryImplementation.
 * checks a property of maven-compiler-plugin
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class MavenSourceLevelImpl implements SourceLevelQueryImplementation {
    private NbMavenProject project;
    /** Creates a new instance of MavenSourceLevelImpl */
    public MavenSourceLevelImpl(NbMavenProject proj) {
        project = proj;
    }
    
    public String getSourceLevel(FileObject javaFile) {
        //TODO differenciate between test sources and main sources
        return PluginPropertyUtils.getPluginProperty(project, "org.apache.maven.plugins", 
                                                              "maven-compiler-plugin", 
                                                              "source", 
                                                              "compile");
    }
    
}