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
package org.mevenide.netbeans.project;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import org.mevenide.netbeans.project.editors.DownloadMeterEditor;
import org.mevenide.netbeans.project.exec.GoalsListPropEditor;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;


public class MavenSettingsBeanInfo extends SimpleBeanInfo {

    public PropertyDescriptor[] getPropertyDescriptors () {
        try {
            PropertyDescriptor topGoals = new PropertyDescriptor ("topGoals", MavenSettings.class); //NOI18N
            topGoals.setDisplayName (NbBundle.getMessage (MavenSettingsBeanInfo.class, "PROP_topGoals")); //NOI18N
            topGoals.setShortDescription (NbBundle.getMessage (MavenSettingsBeanInfo.class, "HINT_topGoals")); //NOI18N
            topGoals.setPropertyEditorClass(GoalsListPropEditor.class);
            PropertyDescriptor offline = new PropertyDescriptor ("offline", MavenSettings.class); //NOI18N
            offline.setDisplayName (NbBundle.getMessage (MavenSettingsBeanInfo.class, "PROP_offline")); //NOI18N
            offline.setShortDescription (NbBundle.getMessage (MavenSettingsBeanInfo.class, "HINT_offline")); //NOI18N
            PropertyDescriptor noBanner = new PropertyDescriptor ("noBanner", MavenSettings.class); //NOI18N
            noBanner.setDisplayName (NbBundle.getMessage (MavenSettingsBeanInfo.class, "PROP_noBanner")); //NOI18N
            noBanner.setShortDescription (NbBundle.getMessage (MavenSettingsBeanInfo.class, "HINT_noBanner")); //NOI18N
            PropertyDescriptor debug = new PropertyDescriptor ("debug", MavenSettings.class); //NOI18N
            debug.setDisplayName (NbBundle.getMessage (MavenSettingsBeanInfo.class, "PROP_debug")); //NOI18N
            debug.setShortDescription (NbBundle.getMessage (MavenSettingsBeanInfo.class, "HINT_debug")); //NOI18N
            PropertyDescriptor exceptions = new PropertyDescriptor ("exceptions", MavenSettings.class); //NOI18N
            exceptions.setDisplayName (NbBundle.getMessage (MavenSettingsBeanInfo.class, "PROP_exceptions")); //NOI18N
            exceptions.setShortDescription (NbBundle.getMessage (MavenSettingsBeanInfo.class, "HINT_exceptions")); //NOI18N
            PropertyDescriptor nonverbose = new PropertyDescriptor ("nonverbose", MavenSettings.class); //NOI18N
            nonverbose.setDisplayName (NbBundle.getMessage (MavenSettingsBeanInfo.class, "PROP_nonverbose")); //NOI18N
            nonverbose.setShortDescription (NbBundle.getMessage (MavenSettingsBeanInfo.class, "HINT_nonverbose")); //NOI18N
            PropertyDescriptor downloader = new PropertyDescriptor ("downloader", MavenSettings.class); //NOI18N
            downloader.setDisplayName (NbBundle.getMessage (MavenSettingsBeanInfo.class, "PROP_downloader")); //NOI18N
            downloader.setShortDescription (NbBundle.getMessage (MavenSettingsBeanInfo.class, "HINT_downloader")); //NOI18N
            downloader.setPropertyEditorClass(DownloadMeterEditor.class);
            PropertyDescriptor home = new PropertyDescriptor ("mavenHome", MavenSettings.class); //NOI18N
            home.setDisplayName (NbBundle.getMessage (MavenSettingsBeanInfo.class, "PROP_home")); //NOI18N
            home.setShortDescription (NbBundle.getMessage (MavenSettingsBeanInfo.class, "HINT_home")); //NOI18N
            
            PropertyDescriptor hint = new PropertyDescriptor ("showAddFavouriteHint", MavenSettings.class); //NOI18N
            hint.setHidden(true);
            
            return new PropertyDescriptor[] { home, topGoals, hint, noBanner, offline, debug, exceptions, nonverbose, downloader};
        } catch (IntrospectionException ie) {
            ErrorManager.getDefault().notify(ie);
            return new PropertyDescriptor[0];
        }
    }

    public Image getIcon (int type) {
        if (type == BeanInfo.ICON_COLOR_16x16 || type == BeanInfo.ICON_MONO_16x16) {
            return Utilities.loadImage ("org/mevenide/netbeans/project/resources/MavenIcon.gif"); //NOI18N
        } else {
            return null;
        }
    }

}
