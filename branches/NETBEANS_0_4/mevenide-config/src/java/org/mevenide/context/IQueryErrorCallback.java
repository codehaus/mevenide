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

package org.mevenide.context;

/**
 * an error callback which implementations can be passed to IqueryContext
 * to get notified of errors in reading and parsing. 
 * The IQueryContext doesn't propagate these errors on purpose but one can be notified here
 * and handle the UI changes appropriately.
 * @author  <a href="mailto:mkleint@gmail.com">Milos Kleint</a>
 */
public interface IQueryErrorCallback {
   
    public static final int ERROR_UNPARSABLE_POM = -1;
    public static final int ERROR_UNREADABLE_PROP_FILE = -2;
    public static final int ERROR_CANNOT_FIND_POM = -3;
    public static final int ERROR_CANNOT_FIND_PARENT_POM = -4;
    
    void handleError(int errorNumber, Exception exception);
    
    void discardError(int errorNumber);
}
