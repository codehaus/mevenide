/*
 * SubprojectProviderImpl.java
 *
 * Created on April 19, 2004, 8:43 PM
 */

package org.mevenide.netbeans.project;

import java.util.Collections;
import java.util.Set;
import org.netbeans.spi.project.SubprojectProvider;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class SubprojectProviderImpl implements SubprojectProvider {

    private MavenProject project;
    /** Creates a new instance of SubprojectProviderImpl */
    public SubprojectProviderImpl(MavenProject proj) {
        project = proj;
    }
    
    public Set getSubProjects() {
        return Collections.EMPTY_SET;
    }
    
}
