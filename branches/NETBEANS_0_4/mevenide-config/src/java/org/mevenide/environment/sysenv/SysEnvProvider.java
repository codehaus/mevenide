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

package org.mevenide.environment.sysenv;

/**
 * Provider of System environment variables. DefaultSysEntProvider is the default implementation.
 * If the IDE provides the Environment vars itself, it's better to reuse it by
 * creating a custom impl.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public interface SysEnvProvider
{
    /**
     * get the environment variable by name.
     */
    String getProperty(String name);
    
}
