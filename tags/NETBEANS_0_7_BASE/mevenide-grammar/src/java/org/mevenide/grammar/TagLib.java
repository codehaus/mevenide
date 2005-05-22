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
 * Container for tag library code completion data.
 * Implementation can/should read the data lazily and cache it once read.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public interface TagLib
{

    /**
     * Name of the tag library, eg. 'jelly:util' or 'artifact'
     */
    String getName(); 
    
    /**
     * Collection of {@see String}, names of tags that are not context sensitive and can be used
     * everywhere.
     */
    Collection getRootTags();
    
    /**
     * Collection of {@see String} names of attributes withing the given tag name.
     */
    Collection getTagAttrs(String tag);
    
    /**
     * Collection of {@see String}, names of tags that can be only applied within the tag passes as paramater.
     */
    Collection getSubTags(String tagName);
    
    /**
     * Collection of {@see String}, AttributeCompletion identifiers that denote what 
     * types of completion apply to the given attribute.
     */
    Collection getAttrCompletionTypes(String tag, String attribute);
    
}
