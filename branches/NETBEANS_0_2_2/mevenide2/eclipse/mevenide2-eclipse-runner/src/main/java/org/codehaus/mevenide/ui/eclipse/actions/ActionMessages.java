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
package org.codehaus.mevenide.ui.eclipse.actions;

import java.util.ResourceBundle;
import org.codehaus.mevenide.ui.eclipse.AbstractMessagesProvider;
import org.codehaus.mevenide.ui.eclipse.IMessagesProvider;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ActionMessages extends AbstractMessagesProvider {
    public static final String KEY = ActionMessages.class.getName();
    
    private static ResourceBundle bundle = ResourceBundle.getBundle(ActionMessages.class.getPackage().getName() + ".ActionMessages"); //$NON-NLS-1$;
    
    protected ResourceBundle getBundle() {
        return null;
    }
    
    private static IMessagesProvider instance = new ActionMessages();
    
    public static IMessagesProvider instance() {
        return instance;
    }
    
}
