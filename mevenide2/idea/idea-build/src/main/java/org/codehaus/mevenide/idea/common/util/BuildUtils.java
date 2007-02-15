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



package org.codehaus.mevenide.idea.common.util;

import org.apache.commons.lang.StringUtils;

import org.codehaus.mevenide.idea.build.IMavenBuildSettings;
import org.codehaus.mevenide.idea.build.MavenOptions;
import org.codehaus.mevenide.idea.common.MavenBuildProjectPluginSettings;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class BuildUtils {
    public static IMavenBuildSettings createMavenBuildSettings(MavenBuildProjectPluginSettings projectSettings) {
        IMavenBuildSettings buildSettings = new MavenBuildProjectPluginSettings();
        String mavenHome = null;
        MavenOptions mavenOptions = null;
        String mavenCommandLineParams = null;
        String vmOptions = null;
        String mavenRepository = null;
        String mavenSettingsFile = null;

        if (StringUtils.isNotEmpty(projectSettings.getMavenHome())) {
            mavenHome = StringUtils.defaultString(projectSettings.getMavenHome());
        }

        if (StringUtils.isNotEmpty(projectSettings.getMavenCommandLineParams())) {
            mavenCommandLineParams = StringUtils.defaultString(projectSettings.getMavenCommandLineParams());
        }

        if (StringUtils.isNotEmpty(projectSettings.getVmOptions())) {
            vmOptions = StringUtils.defaultString(projectSettings.getVmOptions());
        }

        if (StringUtils.isNotEmpty(projectSettings.getMavenOptions().toString())) {
            mavenOptions = projectSettings.getMavenOptions();
        }

        if (StringUtils.isNotEmpty(projectSettings.getMavenRepository())) {
            mavenRepository = StringUtils.defaultString(projectSettings.getMavenRepository());
        }

        if (StringUtils.isNotEmpty(projectSettings.getMavenSettingsFile())) {
            mavenSettingsFile = StringUtils.defaultString(projectSettings.getMavenSettingsFile());
        }

        buildSettings.setMavenCommandLineParams(mavenCommandLineParams);
        buildSettings.setMavenRepository(mavenRepository);
        buildSettings.setMavenSettingsFile(mavenSettingsFile);
        buildSettings.setMavenHome(mavenHome);
        buildSettings.setVmOptions(vmOptions);
        buildSettings.setMavenOptions(mavenOptions);

        return buildSettings;
    }
}
