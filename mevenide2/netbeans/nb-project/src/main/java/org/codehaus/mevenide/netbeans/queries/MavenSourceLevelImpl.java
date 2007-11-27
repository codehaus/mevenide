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

import java.net.URI;
import java.util.logging.Logger;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.Constants;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.netbeans.spi.java.queries.SourceLevelQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * maven implementation of SourceLevelQueryImplementation.
 * checks a property of maven-compiler-plugin
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class MavenSourceLevelImpl implements SourceLevelQueryImplementation {
    private NbMavenProject project;
    private static Logger LOG = Logger.getLogger(MavenSourceLevelImpl.class.getName());
    /** Creates a new instance of MavenSourceLevelImpl */
    public MavenSourceLevelImpl(NbMavenProject proj) {
        project = proj;
    }
    
    public String getSourceLevel(FileObject javaFile) {
//        LOG.info("SLQ for " + javaFile);
        //TODO generated source are now assumed to be the same level as sources.
        // that's the most common scenario, not sure if sources are generated for tests that often..
        
        //MEVENIDE-573
        assert javaFile != null;
        if (javaFile == null) {
            return null;
        }
        URI[] tests = project.getSourceRoots(true);
        URI uri = FileUtil.toFile(javaFile).toURI();
        assert "file".equals(uri.getScheme());
        String goal = "compile"; //NOI18N
        for (URI testuri : tests) {
            if (uri.getPath().startsWith(testuri.getPath())) {
                goal = "testCompile"; //NOI18N
            } 
        }
        String toRet = PluginPropertyUtils.getPluginProperty(project, Constants.GROUP_APACHE_PLUGINS,  //NOI18N
                                                              Constants.PLUGIN_COMPILER,  //NOI18N
                                                              "source",  //NOI18N
                                                              goal);
        //null is allowed to be returned but junit tests module asserts not null
//        LOG.info("  returning " + toRet);
        return toRet == null ? "1.4" : toRet; //NOI18N
    }
    
}