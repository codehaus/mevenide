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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  <a href="mailto:mkleint@gmail.com">Milos Kleint</a>
 */
class LoggerErrorHandlerCallback implements IQueryErrorCallback {
    private static final Log logger = LogFactory.getLog(LoggerErrorHandlerCallback.class);
    
    /** Creates a new instance of NoopErrorHandlerCallback */
    public LoggerErrorHandlerCallback() {
    }

    public void handleError(int errorNumber, Exception exception) {
        logger.error("exception while reading IQueryContext", exception);
    }
    
    public void discardError(int errorNumber) {
        
    }
    
}
