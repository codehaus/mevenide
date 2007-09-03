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
import org.netbeans.spi.java.queries.MultipleRootsUnitTestForSourceQueryImplementation;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * JUnit tests queries.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class MavenTestForSourceImpl implements MultipleRootsUnitTestForSourceQueryImplementation {
    
                                                          
    private NbMavenProject project;
    /** Creates a new instance of MavenTestForSourceImpl */
    public MavenTestForSourceImpl(NbMavenProject proj) {
        project = proj;
    }


    public URL[] findUnitTests(FileObject fileObject) {
        try {
            String str = project.getOriginalMavenProject().getBuild().getTestSourceDirectory();
            if (str != null) {
                URI uri = FileUtil.normalizeFile(new File(str)).toURI();
                URL entry = uri.toURL();
                if  (!entry.toExternalForm().endsWith("/")) { //NOI18N
                    entry = new URL(entry.toExternalForm() + "/"); //NOI18N
                }
                return new URL[] { entry };
            }
        } catch (MalformedURLException exc) {
            ErrorManager.getDefault().notify(exc);
        }
        return null;
    }

    public URL[] findSources(FileObject fileObject) {
        try {
            String str = project.getOriginalMavenProject().getBuild().getSourceDirectory();
            if (str != null) {
                URI uri = FileUtil.normalizeFile(new File(str)).toURI();
                URL entry = uri.toURL();
                if  (!entry.toExternalForm().endsWith("/")) { //NOI18N
                    entry = new URL(entry.toExternalForm() + "/"); //NOI18N
                }
                
                return new URL[] { entry };
            }
        } catch (MalformedURLException exc) {
            ErrorManager.getDefault().notify(exc);
        }
        return null;
    }
    
}
