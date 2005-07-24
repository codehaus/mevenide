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
package org.mevenide.netbeans.project.customizer;


/**
 *
 * Holder and resolver of changes of a POM field
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public interface MavenPOMChange extends MavenChange {
    /**
     * getPath in pom file, serves as ID.
     * eg. pom.artifactId or pom.organization.name
     */
    String getPath();
    /**
     * original location of the the property definition.
     */
    int getOldLocation();
    /**
     * new location of the the property definition.
     */
    int getNewLocation();
 
}
