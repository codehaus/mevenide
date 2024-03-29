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
package org.mevenide.ui.netbeans.exec;

import java.awt.Image;
import java.beans.*;
import org.openide.ErrorManager;

import org.openide.execution.Executor;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;


public class MavenExecutorBeanInfo extends SimpleBeanInfo {

    public BeanInfo[] getAdditionalBeanInfo () {
        try {
            return new BeanInfo[] {
                       Introspector.getBeanInfo (Executor.class)
                   };
        } catch (IntrospectionException ie) {
            ErrorManager.getDefault().notify(ie);
            return null;
        }
    }

    public BeanDescriptor getBeanDescriptor () {
        BeanDescriptor desc = new BeanDescriptor (MavenExecutor.class);
        desc.setDisplayName (NbBundle.getMessage (MavenExecutorBeanInfo.class, "LBL_executor"));
        desc.setShortDescription (NbBundle.getMessage (MavenExecutorBeanInfo.class, "HINT_executor"));
        return desc;
    }

    public PropertyDescriptor[] getPropertyDescriptors () {
        try {
            PropertyDescriptor target = new PropertyDescriptor ("goal", MavenExecutor.class);
            target.setDisplayName (NbBundle.getMessage (MavenExecutorBeanInfo.class, "PROP_goal"));
            target.setShortDescription (NbBundle.getMessage (MavenExecutorBeanInfo.class, "HINT_goal"));
            target.setPropertyEditorClass(GoalPropEditor.class);
            PropertyDescriptor executor = new PropertyDescriptor ("externalExecutor", MavenExecutor.class, "getExternalExecutor", "setExternalExecutor"); // NOI18N
            executor.setDisplayName (NbBundle.getMessage (MavenExecutorBeanInfo.class, "PROP_externalExecutor"));
            executor.setShortDescription (NbBundle.getMessage (MavenExecutorBeanInfo.class, "HINT_externalExecutor"));
            PropertyDescriptor offline = new PropertyDescriptor ("offline", MavenExecutor.class, "isOffline", "setOffline"); // NOI18N
            offline.setDisplayName (NbBundle.getMessage (MavenExecutorBeanInfo.class, "PROP_offline"));
            offline.setShortDescription (NbBundle.getMessage (MavenExecutorBeanInfo.class, "HINT_offline"));
            PropertyDescriptor nobanner = new PropertyDescriptor ("nobanner", MavenExecutor.class, "isNoBanner", "setNoBanner"); // NOI18N
            nobanner.setDisplayName (NbBundle.getMessage (MavenExecutorBeanInfo.class, "PROP_nobanner"));
            nobanner.setShortDescription (NbBundle.getMessage (MavenExecutorBeanInfo.class, "HINT_nobanner"));
            
            return new PropertyDescriptor[] { target, executor, offline, nobanner };
        } catch (IntrospectionException ie) {
            ErrorManager.getDefault().notify(ie);
            return null;
        }
    }

    public Image getIcon (int type) {
        if (type == BeanInfo.ICON_COLOR_16x16 || type == BeanInfo.ICON_MONO_16x16) {
            return Utilities.loadImage ("org/mevenide/ui/netbeans/resources/MavenIcon.gif");
        } else {
            return null;
        }
    }

}
