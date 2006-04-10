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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.spi.java.queries.UnitTestForSourceQueryImplementation;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;

/**
 * JUnit tests queries.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class MavenTestForSourceImpl implements UnitTestForSourceQueryImplementation {
    
                                                          
    private NbMavenProject project;
    /** Creates a new instance of MavenTestForSourceImpl */
    public MavenTestForSourceImpl(NbMavenProject proj) {
        project = proj;
    }


    public URL findUnitTest(FileObject fileObject) {
        try {
            String str = project.getOriginalMavenProject().getBuild().getTestSourceDirectory();
            if (str != null) {
                URI uri = new File(str).toURI();
                return uri.toURL();
            }
        } catch (MalformedURLException exc) {
            ErrorManager.getDefault().notify(exc);
        }
        return null;
    }

    public URL findSource(FileObject fileObject) {
        try {
            String str = project.getOriginalMavenProject().getBuild().getSourceDirectory();
            if (str != null) {
                URI uri = new File(str).toURI();
                return uri.toURL();
            }
        } catch (MalformedURLException exc) {
            ErrorManager.getDefault().notify(exc);
        }
        return null;
    }
    
}
