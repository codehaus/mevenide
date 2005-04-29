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
package org.mevenide.idea;

/**
 * @author Arik
 */
public class MavenHomeNotDefinedException extends Exception {
    private static final Res RES = Res.getInstance(MavenHomeNotDefinedException.class);
    private static final String KEY = "maven.home.not.defined";
    private static final String MSG = RES.get(KEY);

    public MavenHomeNotDefinedException() {
        super(MSG);
    }

    public MavenHomeNotDefinedException(final Throwable pCause) {
        super(MSG, pCause);
    }

    public MavenHomeNotDefinedException(final String pMsg) {
        super(pMsg);
    }

    public MavenHomeNotDefinedException(final String pMsg, final Throwable pCause) {
        super(pMsg, pCause);
    }
}
