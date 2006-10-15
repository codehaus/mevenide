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
package org.codehaus.mevenide.indexer;

import java.awt.Image;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import org.openide.ErrorManager;

public class MavenIndexSettingsBeanInfo extends SimpleBeanInfo {

    public PropertyDescriptor[] getPropertyDescriptors () {
        try {
            PropertyDescriptor indexUpdateFrequency = new PropertyDescriptor ("indexUpdateFrequency", MavenIndexSettings.class); //NOI18N
            indexUpdateFrequency.setDisplayName ("indexUpdateFrequency"); //NOI18N
            indexUpdateFrequency.setHidden(true);
            PropertyDescriptor lastIndexUpdate = new PropertyDescriptor ("lastIndexUpdate", MavenIndexSettings.class); //NOI18N
            lastIndexUpdate.setDisplayName ("lastIndexUpdate"); //NOI18N
            lastIndexUpdate.setHidden(true);
            PropertyDescriptor collectedRepositories = new PropertyDescriptor ("collectedReposAsStrings", MavenIndexSettings.class, "getCollectedReposAsStrings", "setCollectedReposAsStrings"); //NOI18N
            collectedRepositories.setDisplayName ("collectedReposAsStrings"); //NOI18N
            collectedRepositories.setHidden(true);
            PropertyDescriptor includeSnapshots = new PropertyDescriptor ("includeSnapshots", MavenIndexSettings.class); //NOI18N
            includeSnapshots.setDisplayName ("includeSnapshots"); //NOI18N
            includeSnapshots.setHidden(true);
            
            return new PropertyDescriptor[] { indexUpdateFrequency, lastIndexUpdate, 
                                              collectedRepositories, includeSnapshots };
        } catch (IntrospectionException ie) {
            ErrorManager.getDefault().notify(ie);
            return new PropertyDescriptor[0];
        }
    }

    public Image getIcon (int type) {
        return null;
    }

}
