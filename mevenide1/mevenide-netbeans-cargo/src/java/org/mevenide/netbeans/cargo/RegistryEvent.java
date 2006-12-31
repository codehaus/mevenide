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


package org.mevenide.netbeans.cargo;

import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.State;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class RegistryEvent {
    private Container cont;
    private State futureState;
    /** Creates a new instance of RegistryEvent */
    RegistryEvent(Container container) {
        cont = container;
    }
    
    RegistryEvent(Container container, State futureState) {
        this(container);
        this.futureState = futureState;
    }
    
    public Container getContainer() {
        return cont;
    }

    public State getFutureState() {
        return futureState;
    }
    
}
