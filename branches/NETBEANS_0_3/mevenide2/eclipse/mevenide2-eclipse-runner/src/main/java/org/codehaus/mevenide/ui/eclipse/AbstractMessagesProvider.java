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
package org.codehaus.mevenide.ui.eclipse;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.codehaus.mevenide.m2.logging.Logger;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class AbstractMessagesProvider implements IMessagesProvider {
    private static Logger logger = Logger.getLogger(AbstractMessagesProvider.class);
    
    public String getString(String key) {
        ResourceBundle bundle = getBundle();
        
		try {
			return bundle.getString(key);
		} 
		catch (MissingResourceException e) {
			logger.error("Cannot find Bundle Key '" + key + "'", e);  //$NON-NLS-1$//$NON-NLS-2$
			return key;
		}
	}

	public String getString(String key, String param) {
		return getString(key, new String[] {param});
	}

	public String getString(String key, String[] params) {
		return MessageFormat.format(getString(key), params);
	}
	
	protected abstract ResourceBundle getBundle();
}
