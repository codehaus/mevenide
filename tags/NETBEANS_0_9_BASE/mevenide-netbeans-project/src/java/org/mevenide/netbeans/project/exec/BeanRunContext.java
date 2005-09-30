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
package org.mevenide.netbeans.project.exec;

import java.io.File;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class BeanRunContext implements RunContext {
    private String name;
    private String homeDir;
    private String localHomeDir;
    private File directory;
    private String[] additionalParams;
    /** Creates a new instance of ProjectRunContext */
    public BeanRunContext(String nm, String home, String localHome, File dir, String[] params) {
        name = nm;
        homeDir = home;
        directory = dir;
        additionalParams = params;
        localHomeDir = localHome;
    }

    public File getExecutionDirectory() {
        return directory;
    }

    public String getExecutionName() {
        return name;
    }

    public String getMavenHome() {
        return homeDir;
    }
    
    public String getMavenLocalHome() {
        return localHomeDir;
    }
    
    public String[] getAdditionalParams() {
        return additionalParams;
    }
    
}
