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

import org.codehaus.mevenide.idea.model.MavenConfiguration;

import java.util.Map;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public interface IMavenBuildConfiguration {
    MavenConfiguration getMavenConfiguration();

    Map<String, String> getMavenProperties();

    void setMavenProperties(Map<String, String> mavenProperties);

    void setMavenConfiguration(MavenConfiguration mavenConfiguration);

    String getMavenCommandLineParams();

    String getMavenHome();

    String getMavenSettingsFile();

    String getMavenRepository();

    String getVmOptions();

    String getJdkPath();

    void setJdkPath(String jdkPath);

    boolean isSkipTests();

    void setSkipTests(boolean skipTests);

    void setMavenCommandLineParams(String mavenCommandLineParams);

    void setMavenHome(String mavenHome);

    void setMavenSettingsFile(String mavenAdditionalOptions);

    void setMavenRepository(String mavenRepository);

    void setVmOptions(String vmOptions);

    public boolean isUseMavenEmbedder();

    public void setUseMavenEmbedder(boolean useMavenEmbedder);
}
