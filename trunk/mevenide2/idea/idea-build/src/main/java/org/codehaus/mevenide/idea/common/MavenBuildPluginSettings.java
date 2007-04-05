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

import org.apache.commons.lang.StringUtils;

import org.codehaus.mevenide.idea.build.util.BuildConstants;
import org.codehaus.mevenide.idea.model.MavenConfiguration;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenBuildPluginSettings implements org.codehaus.mevenide.idea.build.IMavenBuildConfiguration {
    private String mavenCommandLineParams;
    private String mavenExecutable;
    private String mavenRepository;
    private Properties mavenProperties = new Properties();
    private String vmOptions;
    private String mavenSettingsFile;
    private boolean scanForExistingPoms;
    private boolean useFilter;
    private boolean useMavenEmbedder;
    private boolean skipTests;
    private String jdkPath;
    private List<String> standardPhasesList = new ArrayList<String>();
    private MavenConfiguration mavenConfiguration = new MavenConfiguration();

    public MavenBuildPluginSettings() {
        standardPhasesList.add("clean");
        standardPhasesList.add("compile");
        standardPhasesList.add("test");
        standardPhasesList.add("package");
        standardPhasesList.add("install");
        standardPhasesList.add("site");
    }

    public MavenConfiguration getMavenConfiguration() {
        return mavenConfiguration;
    }

    public Properties getMavenProperties() {
        return this.mavenProperties;
    }

    public void setMavenProperties(Properties mavenProperties) {
        this.mavenProperties = mavenProperties;
    }

    public void setMavenConfiguration(MavenConfiguration mavenConfiguration) {
        this.mavenConfiguration = mavenConfiguration;
    }


    public String getJdkPath() {
        return jdkPath;
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

    public boolean isUseFilter() {
        return useFilter;
    }

    public void setUseFilter(boolean useFilter) {
        this.useFilter = useFilter;
    }

    public String getVmOptions() {
        return vmOptions;
    }

    public List<String> getStandardPhasesList() {
        return standardPhasesList;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public String getMavenCommandLineParams() {
        return mavenCommandLineParams;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public String getMavenHome() {
        return mavenExecutable;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public String getMavenRepository() {
        return mavenRepository;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public boolean isScanForExistingPoms() {
        return scanForExistingPoms;
    }

    /**
     * Method description
     *
     * @param mavenCommandLineParams Document me!
     */
    public void setMavenCommandLineParams(String mavenCommandLineParams) {
        this.mavenCommandLineParams = mavenCommandLineParams;
    }

    /**
     * Method description
     *
     * @param mavenExecutable Document me!
     */
    public void setMavenHome(String mavenExecutable) {
        this.mavenExecutable = mavenExecutable;
    }

    /**
     * Method description
     *
     * @param mavenRepository Document me!
     */
    public void setMavenRepository(String mavenRepository) {
        this.mavenRepository = mavenRepository;
    }

    public void setVmOptions(String vmOptions) {
        this.vmOptions = vmOptions;
    }

    /**
     * Method description
     *
     * @param scanForExistingPoms Document me!
     */
    public void setScanForExistingPoms(boolean scanForExistingPoms) {
        this.scanForExistingPoms = scanForExistingPoms;
    }

    public boolean isUseMavenEmbedder() {
        return useMavenEmbedder;
    }

    public void setUseMavenEmbedder(boolean useMavenEmbedder) {
        this.useMavenEmbedder = useMavenEmbedder;
    }
}
