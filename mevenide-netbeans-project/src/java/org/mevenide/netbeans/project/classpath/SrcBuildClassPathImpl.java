/*
 * SrcClassPathImpl.java
 *
 * Created on April 3, 2004, 1:41 PM
 */

package org.mevenide.netbeans.project.classpath;

import java.net.URI;
import org.mevenide.netbeans.project.MavenProject;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class SrcBuildClassPathImpl extends AbstractProjectClassPathImpl {
    
    /** Creates a new instance of SrcClassPathImpl */
    public SrcBuildClassPathImpl(MavenProject proj) {
        super(proj);
    }
    
    URI[] createPath() {
        return new URI[] { getMavenProject().getBuildClassesDir() };
    }
    
}
