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
package org.mevenide.grammar.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.mevenide.grammar.TagLib;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class EmptyTagLib implements TagLib {
    
    public Collection getAttrCompletionTypes(String tag, String attribute) {
        return new ArrayList();
    }
    
    public String getName() {
        return "undefined";
    }
    
    public Collection getRootTags() {
        return new ArrayList();
    }
    
    public Collection getSubTags(String tagName) {
        return new ArrayList();
    }
    
    public Collection getTagAttrs(String tag) {
        return new ArrayList();
    }
}
