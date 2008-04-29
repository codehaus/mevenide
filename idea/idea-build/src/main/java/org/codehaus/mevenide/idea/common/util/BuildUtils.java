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
import org.codehaus.mevenide.idea.build.IMavenBuildConfiguration;
import org.codehaus.mevenide.idea.common.MavenBuildPluginSettings;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class BuildUtils {
    public static IMavenBuildConfiguration createMavenBuildSettings(IMavenBuildConfiguration buildConfiguration) {
        IMavenBuildConfiguration configuration = new MavenBuildPluginSettings();
        String mavenHome = null;
        String mavenCommandLineParams = null;
        String vmOptions = null;
        String mavenRepository = null;
        String mavenSettingsFile = null;

        if (StringUtils.isNotEmpty(buildConfiguration.getMavenHome())) {
            mavenHome = StringUtils.defaultString(buildConfiguration.getMavenHome());
        }

        if (StringUtils.isNotEmpty(buildConfiguration.getMavenCommandLineParams())) {
            mavenCommandLineParams = StringUtils.defaultString(buildConfiguration.getMavenCommandLineParams());
        }

        if (StringUtils.isNotEmpty(buildConfiguration.getVmOptions())) {
            vmOptions = StringUtils.defaultString(buildConfiguration.getVmOptions());
        }

        if (StringUtils.isNotEmpty(buildConfiguration.getMavenRepository())) {
            mavenRepository = StringUtils.defaultString(buildConfiguration.getMavenRepository());
        }

        if (StringUtils.isNotEmpty(buildConfiguration.getMavenSettingsFile())) {
            mavenSettingsFile = StringUtils.defaultString(buildConfiguration.getMavenSettingsFile());
        }

        configuration.setMavenCommandLineParams(mavenCommandLineParams);
        configuration.setMavenRepository(mavenRepository);
        configuration.setMavenSettingsFile(mavenSettingsFile);
        configuration.setMavenHome(mavenHome);
        configuration.setVmOptions(vmOptions);
        configuration.setMavenConfiguration(buildConfiguration.getMavenConfiguration());
        configuration.setSkipTests(buildConfiguration.isSkipTests());
        configuration.setMavenProperties(buildConfiguration.getMavenProperties());
        configuration.setJdkPath(buildConfiguration.getJdkPath());
        return configuration;
    }
}
