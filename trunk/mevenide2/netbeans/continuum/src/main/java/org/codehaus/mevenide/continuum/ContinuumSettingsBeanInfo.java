/* ==========================================================================
 * Copyright 2005 Mevenide Team
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
package org.codehaus.mevenide.continuum;

import java.awt.Image;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import org.openide.ErrorManager;

public class ContinuumSettingsBeanInfo extends SimpleBeanInfo {

    public PropertyDescriptor[] getPropertyDescriptors () {
        try {
            PropertyDescriptor servers = new PropertyDescriptor ("servers", ContinuumSettings.class); //NOI18N
            servers.setDisplayName ("Servers"); //NOI18N
            servers.setShortDescription (""); //NOI18N
            servers.setHidden(true);
            PropertyDescriptor outputs = new PropertyDescriptor ("outputs", ContinuumSettings.class); //NOI18N
            outputs.setDisplayName ("Log Outputs"); //NOI18N
            outputs.setShortDescription (""); //NOI18N
            outputs.setHidden(true);
            
            return new PropertyDescriptor[] { servers, outputs };
        } catch (IntrospectionException ie) {
            ErrorManager.getDefault().notify(ie);
            return new PropertyDescriptor[0];
        }
    }

    public Image getIcon (int type) {
        return null;
    }

}
