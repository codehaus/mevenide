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
package org.mevenide.tags.netbeans;

import org.apache.commons.jelly.TagLibrary;
import org.mevenide.tags.AdaptNbVersionTag;
import org.mevenide.tags.FindLicenseTag;

/**
 * 
 * @author <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 * @version $Id$
 * 
 */
public class MevenideNbTagLibrary extends TagLibrary {
    public MevenideNbTagLibrary() {
        registerTag("adapt-version", AdaptNbVersionTag.class);
        registerTag("examine-manifest", ExamineManifestTag.class);
        registerTag("find-license", FindLicenseTag.class);
        registerTag("check-dependency-property", CheckDependencyTypeTag.class);
        registerTag("check-module-name", CheckModuleNameTag.class);
    }
}
