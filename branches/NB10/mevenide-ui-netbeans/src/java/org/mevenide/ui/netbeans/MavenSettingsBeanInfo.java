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
package org.mevenide.ui.netbeans;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import org.mevenide.ui.netbeans.exec.GoalsListPropEditor;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;


public class MavenSettingsBeanInfo extends SimpleBeanInfo {

    public PropertyDescriptor[] getPropertyDescriptors () {
        try {
            PropertyDescriptor executor = new PropertyDescriptor ("executor", MavenSettings.class); //NOI18N
            executor.setDisplayName (NbBundle.getMessage (MavenSettingsBeanInfo.class, "PROP_executor")); //NOI18N
            executor.setShortDescription (NbBundle.getMessage (MavenSettingsBeanInfo.class, "HINT_executor")); //NOI18N
            PropertyDescriptor mavenHome = new PropertyDescriptor("mavenHome", MavenSettings.class); //NOI18N
            mavenHome.setDisplayName(NbBundle.getMessage(MavenSettingsBeanInfo.class, "PROP_mavenHome")); //NOI18N
            mavenHome.setShortDescription(NbBundle.getMessage(MavenSettingsBeanInfo.class, "HINT_mavenHome")); //NOI18N
            PropertyDescriptor topGoals = new PropertyDescriptor ("topGoals", MavenSettings.class); //NOI18N
            topGoals.setDisplayName (NbBundle.getMessage (MavenSettingsBeanInfo.class, "PROP_topGoals")); //NOI18N
            topGoals.setShortDescription (NbBundle.getMessage (MavenSettingsBeanInfo.class, "HINT_topGoals")); //NOI18N
            topGoals.setPropertyEditorClass(GoalsListPropEditor.class);
            PropertyDescriptor hint = new PropertyDescriptor ("showAddFavouriteHint", MavenSettings.class); //NOI18N
            hint.setHidden(true);
            PropertyDescriptor current = new PropertyDescriptor ("currentProject", MavenSettings.class); //NOI18N
            current.setHidden(true);
            return new PropertyDescriptor[] { mavenHome, executor, topGoals, hint, current };
        } catch (IntrospectionException ie) {
            ErrorManager.getDefault().notify(ie);
            return new PropertyDescriptor[0];
        }
    }

    public Image getIcon (int type) {
        if (type == BeanInfo.ICON_COLOR_16x16 || type == BeanInfo.ICON_MONO_16x16) {
            return Utilities.loadImage ("org/mevenide/ui/netbeans/resources/MavenIcon.gif"); //NOI18N
        } else {
            return null;
        }
    }

}
