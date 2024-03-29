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



package org.codehaus.mevenide.idea.build;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public interface LogListener {
    public static final int OUTPUT_TYPE_USER = 3;
    public static final int OUTPUT_TYPE_SYSTEM = 1;
    public static final int OUTPUT_TYPE_ERROR = 2;
    public static final int OUTPUT_TYPE_NORMAL = 0;

    public void printMessage(String message, String throwable, int outputType);

    public void raiseFatalErrorMessage(String message, String throwable);
}
