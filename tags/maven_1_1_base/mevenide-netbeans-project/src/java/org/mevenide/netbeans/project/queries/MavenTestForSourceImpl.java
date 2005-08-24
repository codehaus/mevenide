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

package org.mevenide.netbeans.project.queries;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.api.project.MavenProject;
import org.netbeans.spi.java.queries.UnitTestForSourceQueryImplementation;
import org.openide.filesystems.FileObject;

/**
 * JUnit tests queries.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenTestForSourceImpl implements UnitTestForSourceQueryImplementation {
    
    private static final Log logger = LogFactory.getLog(MavenTestForSourceImpl.class);
                                                          
    private MavenProject project;
    /** Creates a new instance of MavenTestForSourceImpl */
    public MavenTestForSourceImpl(MavenProject proj) {
        project = proj;
    }


    public URL findUnitTest(FileObject fileObject) {
        try {
            URI uri = project.getTestSrcDirectory();
            if (uri != null) {
                return uri.toURL();
            }
        } catch (MalformedURLException exc) {
            logger.warn("wrong src->unit uri", exc);
        }
        return null;
    }

    public URL findSource(FileObject fileObject) {
        try {
            URI uri = project.getSrcDirectory();
            if (uri != null) {
                return uri.toURL();
            }
        } catch (MalformedURLException exc) {
            logger.warn("wrong unti->src uri", exc);
        }
        return null;
    }
    
}
