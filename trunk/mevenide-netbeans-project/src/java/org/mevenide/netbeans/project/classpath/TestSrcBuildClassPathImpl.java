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
public class TestSrcBuildClassPathImpl extends AbstractProjectClassPathImpl {
    
    /** Creates a new instance of SrcClassPathImpl */
    public TestSrcBuildClassPathImpl(MavenProject proj) {
        super(proj);
    }
    
    URI[] createPath() {
        //TODO add intgeration test build dir?
        return new URI[] { getMavenProject().getTestBuildClassesDir() };
    }
    
}
