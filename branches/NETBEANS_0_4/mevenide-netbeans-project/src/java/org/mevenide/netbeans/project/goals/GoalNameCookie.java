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
package org.mevenide.netbeans.project.goals;

import org.openide.nodes.Node;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public interface GoalNameCookie extends Node.Cookie
{
    // for plugins returns the name of plugin, for goals returns plugin:goal value
    String getGoalName();
}
