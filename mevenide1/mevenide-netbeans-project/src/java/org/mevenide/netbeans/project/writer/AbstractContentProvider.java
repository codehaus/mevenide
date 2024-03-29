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
package org.mevenide.netbeans.project.writer;

import java.util.Collections;
import java.util.List;
import org.mevenide.project.io.IContentProvider;

/**
 * utility content provider for easy subclassing.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public abstract  class AbstractContentProvider implements IContentProvider {
    
    public List getProperties() {
        return Collections.EMPTY_LIST;
    }
    
    public IContentProvider getSubContentProvider(String key) {
        return null;
    }
    
    public List getSubContentProviderList(String parentKey, String childKey) {
        return null;
    }
    
    public String getValue(String key) {
        return null;
    }
    
    public List getValueList(String parentKey, String childKey) {
        return Collections.EMPTY_LIST;
    }
    
}

