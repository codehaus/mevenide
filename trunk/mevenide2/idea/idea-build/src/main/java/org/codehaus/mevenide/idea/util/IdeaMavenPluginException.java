/* ==========================================================================
 * Copyright 2006 Mevenide Team
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



package org.codehaus.mevenide.idea.util;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class IdeaMavenPluginException extends Exception {

    /**
     * Constructs ...
     */
    public IdeaMavenPluginException() {
        super();
    }

    /**
     * Constructs ...
     *
     * @param string Document me!
     */
    public IdeaMavenPluginException(String string) {
        super(string);    // To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Constructs ...
     *
     * @param throwable Document me!
     */
    public IdeaMavenPluginException(Throwable throwable) {
        super(throwable);    // To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Constructs ...
     *
     * @param string    Document me!
     * @param throwable Document me!
     */
    public IdeaMavenPluginException(String string, Throwable throwable) {
        super(string, throwable);    // To change body of overridden methods use File | Settings | File Templates.
    }
}
