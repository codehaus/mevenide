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
package org.mevenide.project.validation;

import org.xml.sax.SAXParseException;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ValidationProblem {
    private SAXParseException exception;
    
    public ValidationProblem(SAXParseException exception) {
        this.exception = exception;
    }
    
    public SAXParseException getException() {
        return exception;
    }
    
    public void setException(SAXParseException exception) {
        this.exception = exception;
    }
    
    
    public boolean equals(Object obj) {
        return obj instanceof ValidationProblem && 
               exception.getMessage().equals(((ValidationProblem) obj).exception.getMessage());
    }
}
