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
package org.mevenide.ui.jbuilder;

import com.borland.primetime.properties.NodeBooleanProperty;
import com.borland.primetime.properties.PropertyGroup;
import com.borland.primetime.properties.PropertyManager;
import com.borland.primetime.properties.PropertyPage;
import com.borland.primetime.properties.PropertyPageFactory;

/**
 * @author Serge Huber
 * @version 1.0
 */
public class MavenPropertyGroup
    implements PropertyGroup {

    private static final String CATEGORY = "maven";
    public static final NodeBooleanProperty DEBUG_MODE = new
        NodeBooleanProperty(
        CATEGORY, "mavenDebugMode", false);
    public static final NodeBooleanProperty OFFLINE_MODE = new
        NodeBooleanProperty(
        CATEGORY, "mavenOffLineMode", false);

    public static final NodeBooleanProperty QUIET_MODE = new
        NodeBooleanProperty(
        CATEGORY, "mavenQuietMode", false);

    public static void initOpenTool (byte majorVersion, byte minorVersion) {

        PropertyManager.registerPropertyGroup(new MavenPropertyGroup());
    }

    public void initializeProperties () {
    }

    public PropertyPageFactory getPageFactory (Object topic) {
        if (topic instanceof MavenFileNode) {
            final MavenFileNode mavenFileNode = (MavenFileNode) topic;
            if (mavenFileNode.isMavenFile()) {
                return new PropertyPageFactory("Maven") {
                    public PropertyPage createPropertyPage () {
                        return new MavenPropertyPage(mavenFileNode);
                    }
                };
            } else {
                return null;
            }
        }
        return null;
    }

}
