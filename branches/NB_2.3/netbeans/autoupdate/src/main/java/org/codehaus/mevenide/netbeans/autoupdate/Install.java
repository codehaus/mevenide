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

package org.codehaus.mevenide.netbeans.autoupdate;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.modules.ModuleInstall;

/**
 *
 * @author mkleint@codehaus.org
 */
public class Install extends ModuleInstall {
    
    /** Creates a new instance of Install */
    public Install() {
    }

    public void restored() {
        super.restored();
        FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("Services/AutoupdateType/maven2.settings");
        if (fo != null) {
            try {
                //delete the item from extraupdatecenters
                fo.delete();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}
