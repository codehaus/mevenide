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
package org.mevenide.idea.util.ui.images;

import javax.swing.Icon;

import com.intellij.openapi.util.IconLoader;

/**
 * @author Arik
 */
public abstract class Icons {
    public static final Icon BACK = IconLoader.getIcon("back.png", Icons.class);
    public static final Icon CANCEL = IconLoader.getIcon("cancel.png", Icons.class);
    public static final Icon EXECUTE = IconLoader.getIcon("execute.png", Icons.class);
    public static final Icon FORWARD = IconLoader.getIcon("forward.png", Icons.class);
    public static final Icon GOAL = IconLoader.getIcon("goal.png", Icons.class);
    public static final Icon MAVEN = IconLoader.getIcon("maven-icon.png", Icons.class);
    public static final Icon MAVEN_SETTINGS = IconLoader.getIcon("maven-settings.png", Icons.class);
    public static final Icon MAVEN_SETTINGS_SMALL = IconLoader.getIcon("maven-settings-small.png", Icons.class);
    public static final Icon PAUSE = IconLoader.getIcon("pause.png", Icons.class);
    public static final Icon PLUGIN = IconLoader.getIcon("plugin.png", Icons.class);
    public static final Icon PROPERTIES = IconLoader.getIcon("properties.png", Icons.class);
    public static final Icon RERUN = IconLoader.getIcon("rerun.png", Icons.class);
    public static final Icon REPOSITORY = IconLoader.getIcon("repository.png", Icons.class);
    public static final Icon SUSPEND = IconLoader.getIcon("suspend.png", Icons.class);
    public static final Icon SYNC = IconLoader.getIcon("sync.png", Icons.class);

}
