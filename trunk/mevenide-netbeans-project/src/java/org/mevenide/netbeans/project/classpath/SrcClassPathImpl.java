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
//        logger.debug("path=" + getMavenProject().getSrcDirectory());
        return new URI[] { getMavenProject().getSrcDirectory(),
                           getMavenProject().getGeneratedSourcesDir() };
    }
    
}
