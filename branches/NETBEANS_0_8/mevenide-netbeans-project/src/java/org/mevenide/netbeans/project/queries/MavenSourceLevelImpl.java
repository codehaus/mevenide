/* ==========================================================================
 * Copyright 2003-2005 Mevenide Team
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

package org.mevenide.netbeans.project.queries;

import java.net.MalformedURLException;
import java.net.URI;
import org.mevenide.netbeans.project.MavenProject;
import org.netbeans.spi.java.queries.SourceLevelQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;

/**
 * maven implementation of SourceLevelQueryImplementation.
 * checks a property of maven-java-plugin and maven-test-plugin to decide the
 * source level.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class MavenSourceLevelImpl implements SourceLevelQueryImplementation {
    private MavenProject project;
    /** Creates a new instance of MavenSourceLevelImpl */
    public MavenSourceLevelImpl(MavenProject proj) {
        project = proj;
    }

    public String getSourceLevel(FileObject javaFile) {
        URI testuri = project.getTestSrcDirectory();
        if (testuri != null) {
            try {
                FileObject testRoot = URLMapper.findFileObject(testuri.toURL());
                if (testRoot != null && FileUtil.isParentOf(testRoot, javaFile)) {
                    String testlevel = project.getPropertyResolver().getResolvedValue("maven.test.source");
                    if (testlevel != null) {
                        return testlevel.trim();
                    } else {
                        return null;
                    }
                }
            } catch (MalformedURLException exc) {
                
            }
        }
        String source = project.getPropertyResolver().getResolvedValue("maven.compile.source");
        if (source != null) {
            return source.trim();
        }
        return null;
    }
    
}
