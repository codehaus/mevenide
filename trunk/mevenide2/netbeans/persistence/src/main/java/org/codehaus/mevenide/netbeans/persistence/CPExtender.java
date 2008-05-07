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

package org.codehaus.mevenide.netbeans.persistence;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.codehaus.mevenide.netbeans.api.customizer.ModelHandle;
import org.codehaus.mevenide.netbeans.spi.archetype.WizardExtenderUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.spi.java.project.classpath.ProjectClassPathExtender;
import org.netbeans.spi.java.project.classpath.ProjectClassPathModifierImplementation;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author mkleint
 */
public class CPExtender extends ProjectClassPathModifierImplementation implements ProjectClassPathExtender {
    private static final String SL_15 = "1.5"; //NOI18N
    private Project project;
    /** Creates a new instance of CPExtender */
    public CPExtender(Project project) {
        this.project = project;
    }
    
    protected SourceGroup[] getExtensibleSourceGroups() {
        //the default one privides them.
        return new SourceGroup[0];
    }

    protected String[] getExtensibleClassPathTypes(SourceGroup arg0) {
        return new String[0];
    }

    protected boolean addLibraries(Library[] libs, SourceGroup arg1, String arg2) throws IOException,
                                                                                         UnsupportedOperationException {
        boolean added = false;
        for (Library l : libs) {
            added = added || addLibrary(l);
        }
        return added;
    }

    protected boolean removeLibraries(Library[] arg0, SourceGroup arg1,
                                      String arg2) throws IOException,
                                                          UnsupportedOperationException {
        return false;
    }

    protected boolean addRoots(URL[] arg0, SourceGroup arg1, String arg2) throws IOException,
                                                                                 UnsupportedOperationException {
        return false;
    }

    protected boolean removeRoots(URL[] arg0, SourceGroup arg1, String arg2) throws IOException,
                                                                                    UnsupportedOperationException {
        return false;
    }

    protected boolean addAntArtifacts(AntArtifact[] arg0, URI[] arg1,
                                      SourceGroup arg2, String arg3) throws IOException,
                                                                            UnsupportedOperationException {
        return false;
    }

    protected boolean removeAntArtifacts(AntArtifact[] arg0, URI[] arg1,
                                         SourceGroup arg2, String arg3) throws IOException,
                                                                               UnsupportedOperationException {
        return false;
    }

    public boolean addLibrary(Library library) throws IOException {
        if ("toplink".equals(library.getName())) { //NOI18N
            //TODO would be nice if the toplink lib shipping with netbeans be the same binary
            // then we could just copy the pieces to local repo.
            try {
                //not necessary any more. toplink will be handled by default library impl..            
                //TODO would be nice if the toplink lib shipping with netbeans be the same binary
                // then we could just copy the pieces to local repo.
                ModelHandle handle = WizardExtenderUtils.createModelHandle(project);
                
                // checking source doesn't work anymore, the wizard requires the level to be 1.5 up front.
                PluginPropertyUtils.checkSourceLevel(handle, SL_15);
                WizardExtenderUtils.writeModelHandle(handle, project);
                
                //shall not return true, needs processing by the fallback impl as well.
                return false;
            } catch (XmlPullParserException ex) {
                //not going to happen XmlPull for nbactions.xml parsing.
                Exceptions.printStackTrace(ex);
            }
        }
        return false;
    }
    
    public boolean addArchiveFile(FileObject arg0) throws IOException {
        return false;
    }
    
    public boolean addAntArtifact(AntArtifact arg0, URI arg1) throws IOException {
        return false;
    }
    
}
