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

import org.eclipse.core.runtime.IAdaptable;


/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public interface IArtifactMappingNode extends IAdaptable {
    
    
    public static final int INHERITED = 0x1;
    public static final int INCOMPLETE = 0x2;
    
    /** 
     * @return the encapsulated Ide Entry 
     */
    Object getIdeEntry();
    
    /**
     * @return the artifact as resolved from the Ide Entry
     */
    Object getResolvedArtifact();
    
    /**
     * @return the artifact which maps the encapsulated File
     */
    Object getArtifact();

    /**
     * @return the POM which declares the Artifact - if not null
     */
    File getDeclaringPom();
    
    /**
     * 
     * @return the change direction for this Artifact. one of : 
     * ProjectContainer.OUTGOING, ProjectContainer.INCOMING, 
     * ProjectContainer.CONFLICTING - or ProjectContainer.NO_CHANGE
     */
    int getChangeDirection();
    
    /**
     * @return presentation Label
     */
    String getLabel();
    
    /**
     * @return parent container
     */
    IArtifactMappingNodeContainer getParent();
    
    void setParent(IArtifactMappingNodeContainer parent);
}
