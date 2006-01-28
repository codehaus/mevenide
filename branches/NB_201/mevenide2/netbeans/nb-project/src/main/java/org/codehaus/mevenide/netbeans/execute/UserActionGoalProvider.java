/* ==========================================================================
 * Copyright 2005-2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.execute;

import java.io.FileNotFoundException;
import java.io.InputStream;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.openide.filesystems.FileObject;

/**
 * user defined definitions, to be found in the project directory in the nbactions.xml file.
 * @author mkleint
 */
public class UserActionGoalProvider extends AbstractActionGoalProvider {
    /** Creates a new instance of UserActionGoalProvider */
    public UserActionGoalProvider() {
    }
    
    public InputStream getActionDefinitionStream(NbMavenProject project) {
        FileObject fo = project.getProjectDirectory().getFileObject("nbactions.xml");
        if (fo != null) {
            try {
                return fo.getInputStream();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    
}
