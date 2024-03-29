/* ==========================================================================
 * Copyright 2003-2005 Mevenide Team
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

package org.mevenide.environment;

/**
 * An implementation of ILocationFinder that returns <tt>null</tt>
 * for every value. In other words, this is an implementation of
 * the <tt>Null Object Pattern</tt>.
 * @author fdutton
 */
public class MissingLocationFinder extends AbstractLocationFinder {

    /* (non-Javadoc)
     * @see org.mevenide.environment.ILocationFinder#getMavenHome()
     */
    public String getMavenHome() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.ILocationFinder#getJavaHome()
     */
    public String getJavaHome() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.ILocationFinder#getUserHome()
     */
    public String getUserHome() {
        return null;
    }

}
