/*
 * SrcClassPathImpl.java
 *
 * Created on April 3, 2004, 1:41 PM
 */

package org.mevenide.netbeans.project.classpath;

import java.net.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.project.MavenProject;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class SrcClassPathImpl extends AbstractProjectClassPathImpl {
    private static final Log logger = LogFactory.getLog(SrcClassPathImpl.class);
    
    /** Creates a new instance of SrcClassPathImpl */
    public SrcClassPathImpl(MavenProject proj) {
        super(proj);
    }
    
    URI[] createPath() {
        logger.debug("path=" + getMavenProject().getSrcDirectory());
        return new URI[] { getMavenProject().getSrcDirectory() };
    }
    
}
