/*
 * SinglePropFile.java
 *
 * Created on April 1, 2004, 5:49 PM
 */

package org.mevenide.netbeans.project.propfiles;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.properties.KeyValuePair;
import org.mevenide.properties.PropertyModel;
import org.mevenide.properties.PropertyModelFactory;
import org.openide.filesystems.FileObject;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
class SinglePropFile {
    private static final Log logger = LogFactory.getLog(SinglePropFile.class);
    
    private FileObject propFileFO = null;
    private PropertyModel propModel = null;
    private boolean regenerate;
    private boolean skip = false;
    /** Creates a new instance of SinglePropFile */
    SinglePropFile(FileObject fo) {
        propFileFO = fo;
        regenerate = true;
        skip = false;
        //TODO add file change listening.
        
    }
    
    public String getValue(String key) {
        if (skip) {
            return null;
        }
        try {
            if (regenerate) {
                readModel();  
            }
            KeyValuePair pair = propModel.findByKey(key);
            if (pair != null) {
                return pair.getValue();
            }
        } catch (IOException exc) {
            logger.error("Cannot read file", exc);
            skip = true;
        }
        return null;
    }
    
    private void readModel() throws IOException {
        propModel = PropertyModelFactory.getFactory().newPropertyModel(propFileFO.getInputStream());
    }
    
    
}
