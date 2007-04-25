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


package org.codehaus.mevenide.idea.common;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.StringUtils;
import org.codehaus.mevenide.idea.build.IMavenBuildConfiguration;
import org.codehaus.mevenide.idea.build.util.BuildConstants;
import org.codehaus.mevenide.idea.model.MavenConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenBuildPluginSettings implements IMavenBuildConfiguration {
    private String mavenCommandLineParams;
    private String mavenExecutable;
    private String mavenRepository;
    private Map<String, String> mavenProperties = new LinkedMap();
    private String vmOptions;
    private String mavenSettingsFile;
    private boolean useMavenEmbedder;
    private boolean skipTests;
    private String jdkPath;
    private boolean runMavenInBackground = false;

    private MavenConfiguration mavenConfiguration = new MavenConfiguration();

    private final String [] standardPhases = {"clean", "compile", "test", "package", "install", "site"};
    private final String [] standardGoals =
            {"clean", "validate",
            "generate-sources", "process-sources", "generate-resources", "process-resources",
            "compile", "process-classes",
            "generate-test-sources", "process-test-sources", "generate-test-resources", "process-test-resources",
            "test-compile", "test",
            "package",
            "pre-integration-test", "integration-test", "post-integration-test", 
            "verify", "install", "site", "deploy"};

    public MavenBuildPluginSettings() {
    }

    public List<String> getStandardGoalsList() {
        return Arrays.asList(standardGoals);
    }

    public List<String> getStandardPhasesList() {
        return Arrays.asList(standardPhases);
    }

    public Map<String, String> getMavenProperties() {
        return this.mavenProperties;
    }

    public void setMavenProperties(Map<String, String> mavenProperties) {
        this.mavenProperties = mavenProperties;
    }

    public MavenConfiguration getMavenConfiguration() {
        return mavenConfiguration;
    }

    public void setMavenConfiguration(MavenConfiguration mavenConfiguration) {
        this.mavenConfiguration = mavenConfiguration;
    }

    public String getJdkPath() {
        return this.jdkPath;
    }

    public void setJdkPath(String jdkPath) {
        this.jdkPath = jdkPath;
    }

    public boolean isSkipTests() {
        return skipTests;
    }

    public void setSkipTests(boolean skipTests) {
        this.skipTests = skipTests;
    }

    public String getMavenSettingsFile() {
        if (StringUtils.isBlank(mavenSettingsFile)) {
            String settingsFile = System.getProperty("user.home") + System.getProperty("file.separator") + ".m2"
                    + System.getProperty("file.separator") + BuildConstants.FILENAME_MAVEN_SETTINGS_FILE;
            File settingsFileAsFile = new File(settingsFile);

            if (settingsFileAsFile.exists()) {
                return settingsFile;
            }
        }

        return mavenSettingsFile;
    }

    public void setMavenSettingsFile(String mavenSettingsFile) {
        this.mavenSettingsFile = mavenSettingsFile;
    }

    public String getMavenCommandLineParams() {
        return mavenCommandLineParams;
    }

    public void setMavenCommandLineParams(String mavenCommandLineParams) {
        this.mavenCommandLineParams = mavenCommandLineParams;
    }

    public String getMavenHome() {
        return mavenExecutable;
    }

    public void setMavenHome(String mavenExecutable) {
        this.mavenExecutable = mavenExecutable;
    }

    public String getMavenRepository() {
        return mavenRepository;
    }

    public void setMavenRepository(String mavenRepository) {
        this.mavenRepository = mavenRepository;
    }

    public String getVmOptions() {
        return vmOptions;
    }

    public void setVmOptions(String vmOptions) {
        this.vmOptions = vmOptions;
    }

    public boolean isUseMavenEmbedder() {
        return useMavenEmbedder;
    }

    public void setUseMavenEmbedder(boolean useMavenEmbedder) {
        this.useMavenEmbedder = useMavenEmbedder;
    }

    public boolean isRunMavenInBackground() {
        return runMavenInBackground;
    }

    public void setRunMavenInBackground(boolean runMavenInBackground) {
        this.runMavenInBackground = runMavenInBackground;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("MavenBuildPluginSettings");
        sb.append("{mavenCommandLineParams='").append(mavenCommandLineParams).append('\'');
        sb.append(", mavenExecutable='").append(mavenExecutable).append('\'');
        sb.append(", mavenRepository='").append(mavenRepository).append('\'');
        sb.append(", mavenProperties=").append(mavenProperties);
        sb.append(", vmOptions='").append(vmOptions).append('\'');
        sb.append(", mavenSettingsFile='").append(mavenSettingsFile).append('\'');
        sb.append(", useMavenEmbedder=").append(useMavenEmbedder);
        sb.append(", skipTests=").append(skipTests);
        sb.append(", jdkPath='").append(jdkPath).append('\'');
        sb.append(", standardPhasesList=").append(getStandardPhasesList());
        sb.append(", mavenConfiguration=").append(mavenConfiguration);
        sb.append('}');
        return sb.toString();
    }
}
