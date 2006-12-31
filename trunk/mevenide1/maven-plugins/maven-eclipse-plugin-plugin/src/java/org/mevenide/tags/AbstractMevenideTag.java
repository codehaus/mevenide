/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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
package org.mevenide.tags;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class AbstractMevenideTag extends TagSupport {
    protected void checkAttribute(Object attribute, String attributeName) throws MissingAttributeException {
        if (attribute == null ) {
            throw new MissingAttributeException(attributeName + " should be defined.");
        }
    }

    protected void checkAttribute(String attribute, String attributeName) throws MissingAttributeException {
        checkAttribute((Object) attribute, attributeName);
        if (attribute.trim().equals("") ) {
            throw new MissingAttributeException(attributeName + " should not be empty.");
        }
    }
}
