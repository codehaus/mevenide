/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.ui.eclipse.sync.model;

import java.io.File;




/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class AbstractArtifactMappingNode implements IArtifactMappingNode {
    /** may be instance of IProject or IClasspathEntry */
    protected Object ideEntry;
    protected Object resolvedArtifact;
    protected Object artifact;
    protected File declaringPom; 
    
    protected IArtifactMappingNodeContainer parent;
    
    public Object getIdeEntry() {
        return ideEntry;
    }
    public Object getArtifact() {
        return artifact;
    }
    public Object getResolvedArtifact() {
        return resolvedArtifact;
    }
    public IArtifactMappingNodeContainer getParent() {
        return parent;
    }
    public void setParent(IArtifactMappingNodeContainer parent) {
        this.parent = parent;
    }
    public File getDeclaringPom() {
        return declaringPom;
    }
    public void setDeclaringPom(File declaringPom) {
        this.declaringPom = declaringPom;
    }
    
    public Object getWrappedObject() {
    	return getArtifact() != null ? getArtifact() : getResolvedArtifact();
    }
}
