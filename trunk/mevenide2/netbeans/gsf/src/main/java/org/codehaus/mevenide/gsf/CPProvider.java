/*
 *  Copyright 2008 mkleint.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.codehaus.mevenide.gsf;

import org.netbeans.modules.gsfpath.api.classpath.ClassPath;
import org.netbeans.modules.gsfpath.spi.classpath.ClassPathProvider;
import org.openide.filesystems.FileObject;

/**
 *
 * @author mkleint
 */
public class CPProvider implements ClassPathProvider {

    public ClassPath findClassPath(FileObject file, String type) {
        //TBD
        return null;
    }

    ClassPath[] getProjectSourcesClassPaths(String type) {
        //TBD
        return new ClassPath[0];
    }

}
