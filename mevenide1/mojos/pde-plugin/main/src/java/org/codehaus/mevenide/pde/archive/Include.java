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
package org.codehaus.mevenide.pde.archive;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class Include {

    /** path of the file to include */
    private String absolutePath;
    
    /** entry's path - if not null, it should begin with a '/' (zipEntry) */
    private String targetPath;
    
    public Include(String absolutePath, String targetPath) {
        this.absolutePath = absolutePath;
        this.targetPath = targetPath;
    }
    
    public Include() {
    }
    
    public String getAbsolutePath() { return absolutePath; }
    public void setAbsolutePath(String absolutePath) { this.absolutePath = absolutePath; }
    
    public String getTargetPath() { return targetPath; }
    public void setTargetPath(String targetPath) { this.targetPath = targetPath; }
}
