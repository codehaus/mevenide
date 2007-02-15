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



package org.codehaus.mevenide.idea.build;

import com.intellij.openapi.progress.ProgressManager;

import org.codehaus.mevenide.idea.build.util.BuildConstants;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenBuildManager extends AbstractMavenBuildManager {
    public MavenBuildManager(IBuildEnvironment buildEnvironment) {
        super(buildEnvironment);
    }

    public AbstractMavenBuildTask execute() {
        MavenBuildTask mavenTask;

        mavenTask = new MavenBuildTask(buildEnvironment);

        ProgressManager progressManager = ProgressManager.getInstance();

        progressManager.runProcessWithProgressAsynchronously(buildEnvironment.getProject(),
                BuildConstants.MESSAGE_EXECUTING_MAVEN + BuildConstants.MESSAGE_USING_EXTERNAL_MAVEN, mavenTask, null,
                null);

        return mavenTask;
    }
}
