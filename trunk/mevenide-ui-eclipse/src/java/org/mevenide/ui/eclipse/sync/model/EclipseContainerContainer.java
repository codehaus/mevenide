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

import org.eclipse.core.resources.IContainer;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class EclipseContainerContainer {
    public static final int INCOMING = 8;
    public static final int OUTGOING = 16;
    public static final int CONFLICTING = 4;
    public static final int NO_CHANGE = 32;
    
    private IContainer project;
   
    private Object[] pomContainers;
    
    public EclipseContainerContainer(IContainer project) {
        this.project = project;
    }
    
    public IContainer getProject() {
        return project;
    }
    
    public void setContainer(IContainer project) {
        this.project = project;
    }

    
    public Object[] getPomContainers() {
        return pomContainers;
    }

    public void setPomContainers(Object[] containers) {
        this.pomContainers = containers;
    }

}
