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
package org.mevenide.idea.global;

import org.mevenide.idea.Res;

/**
 * @author Arik
 */
public class MavenHomeUndefinedException extends IllegalMavenHomeException {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(MavenHomeUndefinedException.class);

    public MavenHomeUndefinedException() {
        super(RES.get("maven.home.not.defined"));
    }

    public MavenHomeUndefinedException(final Throwable pCause) {
        super(RES.get("maven.home.not.defined"), pCause);
    }
}
