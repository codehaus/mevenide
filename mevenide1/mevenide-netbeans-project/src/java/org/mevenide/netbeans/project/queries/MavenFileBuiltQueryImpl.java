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

import java.util.logging.Logger;
import javax.swing.event.ChangeListener;

import org.mevenide.netbeans.api.project.MavenProject;
import org.netbeans.api.queries.FileBuiltQuery;
import org.netbeans.spi.queries.FileBuiltQueryImplementation;
import org.openide.filesystems.FileObject;

/**
 * TODO
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenFileBuiltQueryImpl implements FileBuiltQueryImplementation {
     private static final Logger logger = Logger.getLogger(MavenFileBuiltQueryImpl.class.getName());
   
    private MavenProject project;
    /** Creates a new instance of MavenFileBuiltQueryImpl */
    public MavenFileBuiltQueryImpl(MavenProject proj) {
        project = proj;
    }
    
    /**
     * Check whether a (source) file has been <em>somehow</em> built
     * or processed.
     * This would typically mean that at least its syntax has been
     * validated by a build system, some conventional output file exists
     * and is at least as new as the source file, etc.
     * For example, for a <samp>Foo.java</samp> source file, this could
     * check whether <samp>Foo.class</samp> exists (in the appropriate
     * build directory) with at least as new a timestamp.
     * @param file a source file which can be built to a direct product
     * @return a status object that can be queries and listened to,
     *         or null for no answer
     */   
    public FileBuiltQuery.Status getStatus(FileObject fileObject) {
        logger.fine("status for=" + fileObject); //NOI18N
        return new Status();
    }
    
    
    private class Status implements FileBuiltQuery.Status {
        
        public void addChangeListener(ChangeListener changeListener) {
            logger.fine("adding listener=" + changeListener.getClass()); //NOI18N
        }
        
        public boolean isBuilt() {
            return false;
        }
        
        public void removeChangeListener(ChangeListener changeListener) {
            logger.fine("removing listener=" + changeListener.getClass()); //NOI18N
        }
        
    }
}
