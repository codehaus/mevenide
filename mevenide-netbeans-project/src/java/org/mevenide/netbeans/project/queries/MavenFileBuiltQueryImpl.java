/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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

import javax.swing.event.ChangeListener;
import org.netbeans.api.queries.FileBuiltQuery;
import org.netbeans.spi.queries.FileBuiltQueryImplementation;
import org.openide.filesystems.FileObject;

/**
 * TODO
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenFileBuiltQueryImpl implements FileBuiltQueryImplementation {
    
    /** Creates a new instance of MavenFileBuiltQueryImpl */
    public MavenFileBuiltQueryImpl() {
    }
    
    public FileBuiltQuery.Status getStatus(FileObject fileObject) {
        return new Status();
    }
    
    
    private class Status implements FileBuiltQuery.Status {
        
        public void addChangeListener(ChangeListener changeListener) {
        }
        
        public boolean isBuilt() {
            return false;
        }
        
        public void removeChangeListener(ChangeListener changeListener) {
        }
        
    }
}
