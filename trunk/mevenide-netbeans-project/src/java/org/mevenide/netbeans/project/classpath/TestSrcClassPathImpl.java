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
public class TestSrcClassPathImpl extends AbstractProjectClassPathImpl {
    
    /** Creates a new instance of SrcClassPathImpl */
    public TestSrcClassPathImpl(MavenProject proj) {
        super(proj);
    }
    
    URI[] createPath() {
        //TODO add integration tests src dir as well?
        return new URI[] { getMavenProject().getTestSrcDirectory(),
                           getMavenProject().getSrcDirectory() };
    }
    
}
