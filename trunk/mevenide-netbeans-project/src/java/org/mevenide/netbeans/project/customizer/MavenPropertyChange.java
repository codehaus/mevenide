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
package org.mevenide.netbeans.project.customizer;


/**
 *
 * Holder and resolver of changes of a property field.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public interface MavenPropertyChange {
    /**
     * property's key
     */
    String getKey();
    /**
     * original value
     */
    String getOldValue();
    /**
     * current, maybe changed value
     */
    String getNewValue();
    /**
     * original location of the the property definition.
     */
    int getOldLocation();
    /**
     * new location of the the property definition.
     */
    int getNewLocation();
    
    /**
     * check weather the proeprty changed or not. either value or location.
     */
    boolean hasChanged();
 
}
