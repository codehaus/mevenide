/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.api.archetype;

import java.util.List;

/**
 * Componentized provider of list of available archetypes.
 * It is used in New Maven project wizard to populate the list of available archetypes.
 * The providers are expected to be registered in META-INF/services of the module.
 * There are 2 default implementations registered: One lists 3 basic archetypes 
 * (simple, webapp and mojo) and the other lists all archetypes it find in local repository index.
 * @author mkleint
 */
public interface ArchetypeProvider {

    /**
     * return Archetype instances known to this provider. Is called once per
     * New Maven Project wizard invokation.
     */ 
    List<Archetype> getArchetypes();
}
