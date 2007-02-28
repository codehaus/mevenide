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

package org.codehaus.mevenide.netbeans;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.spi.java.project.classpath.ProjectClassPathExtender;
import org.netbeans.spi.project.LookupMerger;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author mkleint
 */
public class CPExtenderLookupMerger implements LookupMerger<ProjectClassPathExtender> {
    
    private CPExtender fallback;
    private Extender instance;
    
    /** Creates a new instance of CPExtenderLookupMerger */
    public CPExtenderLookupMerger(CPExtender fallbck) {
        fallback = fallbck;
        assert fallback != null;
    }
    
    public Class<ProjectClassPathExtender> getMergeableClass() {
        return ProjectClassPathExtender.class;
    }

    public synchronized ProjectClassPathExtender merge(Lookup lookup) {
        if (instance == null) {
            instance =  new Extender();
        }
        instance.setLookup(lookup);
        return instance;
    }

    private class Extender implements ProjectClassPathExtender {
        
        private Lookup context;
        
        private Extender() {
            this.context = context;
        }
        private void setLookup(Lookup context) {
            this.context = context;
        }
    
        public boolean addLibrary(Library arg0) throws IOException {
            Collection<? extends ProjectClassPathExtender> list = context.lookupAll(ProjectClassPathExtender.class);
            for (ProjectClassPathExtender ext : list) {
                boolean added = ext.addLibrary(arg0);
                if (added) {
                    return added;
                }
            }
            return fallback.addLibrary(arg0);
        }

        public boolean addArchiveFile(FileObject arg0) throws IOException {
            Collection<? extends ProjectClassPathExtender> list = context.lookupAll(ProjectClassPathExtender.class);
            for (ProjectClassPathExtender ext : list) {
                boolean added = ext.addArchiveFile(arg0);
                if (added) {
                    return added;
                }
            }
            return fallback.addArchiveFile(arg0);
        }

        public boolean addAntArtifact(AntArtifact arg0, URI arg1) throws IOException {
            Collection<? extends ProjectClassPathExtender> list = context.lookupAll(ProjectClassPathExtender.class);
            for (ProjectClassPathExtender ext : list) {
                boolean added = ext.addAntArtifact(arg0, arg1);
                if (added) {
                    return added;
                }
            }
            return fallback.addAntArtifact(arg0, arg1);
        }

    }
}
