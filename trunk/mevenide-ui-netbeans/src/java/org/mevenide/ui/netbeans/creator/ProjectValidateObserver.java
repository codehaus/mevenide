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
package org.mevenide.ui.netbeans.creator;


/**
 * Object implementing this interface will be notified of changes in the valid state of the ProjectPanel
 * once it's set as it's Observer in ProjectPanel.setValidateObserver()
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public interface ProjectValidateObserver
{
    /**
     * parameter hold the new value of validity for the ProjectPanel
     */
    void resetValidState(boolean valid, String errorMessage);
}
