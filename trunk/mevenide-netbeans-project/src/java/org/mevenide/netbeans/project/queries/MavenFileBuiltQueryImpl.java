/*
 * MavenFileBuiltQueryImpl.java
 *
 * Created on April 19, 2004, 7:57 PM
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
