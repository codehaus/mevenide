/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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

package org.mevenide.properties;

import java.io.File;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.properties.resolver.DefaultsResolver;
import org.mevenide.properties.resolver.PropertyFilesAggregator;

/**
 *
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 */
public final class ProjectPropFactory {
    
    private static ProjectPropFactory factory;
    /** Creates a new instance of ProjectPropFactory */
    ProjectPropFactory() {
    }
    
    public static ProjectPropFactory getInstance() {
        if (factory == null) {
            factory = new ProjectPropFactory();
        }
        return factory;
    }
    /**
     * returns a property resolver (IPropertyResolver) for given project directory.
     */
    public IPropertyResolver createResolver(File projectDir) {
        String userHome = System.getProperty("user.home"); //NOI18N
        File userFile = new File(userHome);
        LocationFinderAggregator finder = new LocationFinderAggregator();
        finder.setEffectiveWorkingDirectory(projectDir.getAbsolutePath());
        return new PropertyFilesAggregator(projectDir, userFile, new DefaultsResolver(projectDir, userFile, finder));
    }
    
}
