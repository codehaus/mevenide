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
package org.codehaus.mevenide.pde.plugin;

import org.codehaus.mevenide.pde.PdePluginException;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class CollectException extends PdePluginException {
   
	private static final long serialVersionUID = 3257854276893226293L;

	public CollectException(String message) {
        super(message);
    }

    public CollectException(Throwable cause) {
        super(cause);
    }

    public CollectException(String message, Throwable cause) {
        super(message, cause);
    }
}
