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

package org.mevenide.grammar;

import java.util.Collection;

/**
 * Container for attribute content code completion data.
 * Implementation can/should read the data lazily and cache it once read.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public interface AttributeCompletion
{

    /**
     * Unique Identifier
     */
    String getName(); 
    
    /**
     * Collection of <String>, all possible attribute values that match 
     * the start parameter [name.startsWith(start)]
     * @work-in-progress
     *@param start - start of the word that should be matched, if null, everything matches.
     */
    Collection getValueHints(String start);
    
}
