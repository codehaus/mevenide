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
package org.mevenide.netbeans.project.exec;

import org.openide.windows.OutputListener;

/**
 * implementors of this interface will check the line and attach listener to it
 * if  the line is important. Used for hyperlinking files in output etc..
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public interface OutputListenerProvider {
   
    OutputListener recognizeLine(String line);
    
}
