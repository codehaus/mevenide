/*
 *  Copyright 2007 Mevenide Team
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

package org.netbeans.modules.maven.spi.debug;

import java.util.List;
import org.netbeans.api.project.Project;

/**
 * Implementations to be found in project lookup.
 * Will be used by the debugger when preparing binaries/sources for the debugging
 * engine. By default the project's dependencies are included. For "pom" packaged
 * projects also all modules are iterated and their classpath is added. 
 * This interface servers for project to provide additional list of projects that will be 
 * iterated and their respective classpath added to debugger.
 *
 * The primary usecase is ear debugging where wars/ejbjars needs to be added as well.
 * @author mkleint
 */
public interface AdditionalDebuggedProjects {

    List<Project> getProjects();
}
