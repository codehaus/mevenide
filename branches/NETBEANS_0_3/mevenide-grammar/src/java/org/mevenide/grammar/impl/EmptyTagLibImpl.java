/*
 * ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * =========================================================================
 */
package org.mevenide.grammar.impl;

import java.util.Collection;
import java.util.Collections;
import org.mevenide.grammar.TagLib;

/**
 * Empty implementation of a taglib. A fallback impl.
 * 
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class EmptyTagLibImpl implements TagLib {

    private String name;

    /** Creates a new instance of EmptyTagLibImpl */
    public EmptyTagLibImpl(String tagLibName) {
        name = tagLibName;
    }

    public String getName() {
        return name;
    }

    public Collection getRootTags() {
        return Collections.EMPTY_LIST;
    }

    public Collection getSubTags(String tagName) {
        return Collections.EMPTY_LIST;
    }

    public Collection getTagAttrs(String tag) {
        return Collections.EMPTY_LIST;
    }

    public Collection getAttrCompletionTypes(String tag, String attribute) {
        return Collections.EMPTY_LIST;
    }
}