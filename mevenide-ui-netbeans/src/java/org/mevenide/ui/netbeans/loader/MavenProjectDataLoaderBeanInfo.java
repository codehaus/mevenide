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
package org.mevenide.ui.netbeans.loader;

import java.awt.Image;
import java.beans.*;

import org.openide.ErrorManager;
import org.openide.util.Utilities;

/** Description of {@link MyDataLoader}.
 *
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenProjectDataLoaderBeanInfo extends SimpleBeanInfo {
    
    public BeanInfo[] getAdditionalBeanInfo() {
        try {
            // I.e. MultiFileLoader.class or UniFileLoader.class.
            return new BeanInfo[] {Introspector.getBeanInfo(MavenProjectDataLoader.class.getSuperclass())};
        } catch (IntrospectionException ie) {
            ErrorManager.getDefault().notify(ie);
            return null;
        }
    }
    
    public Image getIcon(int type) {
        if (type == BeanInfo.ICON_COLOR_16x16 || type == BeanInfo.ICON_MONO_16x16) {
            return Utilities.loadImage("org/mevenide/ui/netbeans/resources/MyDataIcon.gif", true);
        } else {
            return Utilities.loadImage("org/mevenide/ui/netbeans/resources/MyDataIcon32.gif", true);
        }
    }
    
}
