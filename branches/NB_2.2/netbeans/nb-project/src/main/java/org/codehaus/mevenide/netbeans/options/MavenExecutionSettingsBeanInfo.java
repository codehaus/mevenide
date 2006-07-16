/* ==========================================================================
 * Copyright 2006 Mevenide Team
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
package org.codehaus.mevenide.netbeans.options;

import java.awt.Image;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import org.openide.ErrorManager;

public class MavenExecutionSettingsBeanInfo extends SimpleBeanInfo {

    public PropertyDescriptor[] getPropertyDescriptors () {
        try {
            PropertyDescriptor showDebug = new PropertyDescriptor ("showDebug", MavenExecutionSettings.class); //NOI18N
            showDebug.setDisplayName ("ShowDebug"); //NOI18N
            showDebug.setHidden(true);
            PropertyDescriptor showError = new PropertyDescriptor ("showErrors", MavenExecutionSettings.class); //NOI18N
            showError.setDisplayName ("ShowError"); //NOI18N
            showError.setHidden(true);
            PropertyDescriptor checksum = new PropertyDescriptor ("checksumPolicy", MavenExecutionSettings.class); //NOI18N
            checksum.setDisplayName ("checksumPolicy"); //NOI18N
            checksum.setHidden(true);
            PropertyDescriptor updates = new PropertyDescriptor ("pluginUpdatePolicy", MavenExecutionSettings.class); //NOI18N
            updates.setDisplayName ("pluginUpdatePolicy"); //NOI18N
            updates.setHidden(true);
            PropertyDescriptor failure = new PropertyDescriptor ("failureBehaviour", MavenExecutionSettings.class); //NOI18N
            failure.setDisplayName ("failureBehaviour"); //NOI18N
            failure.setHidden(true);
            PropertyDescriptor useRegistry = new PropertyDescriptor ("usePluginRegistry", MavenExecutionSettings.class); //NOI18N
            useRegistry.setDisplayName ("usePluginRegistry"); //NOI18N
            useRegistry.setHidden(true);
            
            
            return new PropertyDescriptor[] { showDebug, showError, checksum, updates, failure, useRegistry };
        } catch (IntrospectionException ie) {
            ErrorManager.getDefault().notify(ie);
            return new PropertyDescriptor[0];
        }
    }

    public Image getIcon (int type) {
        return null;
    }

}
