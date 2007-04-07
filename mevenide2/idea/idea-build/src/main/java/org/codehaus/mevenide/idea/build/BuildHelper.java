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

import com.intellij.openapi.projectRoots.ProjectJdkTable;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.apache.maven.execution.MavenExecutionRequest;
import org.codehaus.mevenide.idea.build.util.BuildConstants;
import org.codehaus.mevenide.idea.model.MavenConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class BuildHelper {
    static final Logger LOG = Logger.getLogger(BuildHelper.class);

    /**
     * Determines, whether the given path to the maven settings file is the default file.
     *
     * @param settingsFile path to settings file.
     * @return true in case the settings file is the default one, false otherwise.
     */
    private static boolean isDefaultSettingsFile(String settingsFile) {
        return settingsFile.equals(System.getProperty("user.home") + System.getProperty("file.separator") + ".m2"
                + System.getProperty("file.separator")
                + BuildConstants.FILENAME_MAVEN_SETTINGS_FILE);
    }

    public static List<String> createMavenExecutionCommand(IBuildEnvironment buildEnvironment)
            throws MavenBuildException {
        String mavenHome;
        String classpathSeparator = null;

        if (SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_WINDOWS_2000 || SystemUtils.IS_OS_WINDOWS_95
                || SystemUtils.IS_OS_WINDOWS_98 || SystemUtils.IS_OS_WINDOWS_ME || SystemUtils.IS_OS_WINDOWS_NT
                || SystemUtils.IS_OS_WINDOWS_XP) {
            classpathSeparator = ";";
        } else if (SystemUtils.IS_OS_UNIX) {
            classpathSeparator = ":";
        }

        mavenHome = getMavenHome(buildEnvironment.getMavenBuildConfiguration().getMavenHome());

        String classpathEntries = getMavenClasspathEntries(mavenHome, classpathSeparator);
        String vmOptions = buildEnvironment.getMavenBuildConfiguration().getVmOptions();
        String mavenSystemOptions = StringUtils.defaultString(System.getenv("MAVEN_OPTS"));
        List<String> cmdList = new ArrayList<String>();

        cmdList.add(getRealJdkPath(buildEnvironment.getMavenBuildConfiguration().getJdkPath()));

        if (!StringUtils.isBlank(vmOptions)) {
            StringTokenizer tokenizer = new StringTokenizer(vmOptions);

            while (tokenizer.hasMoreTokens()) {
                cmdList.add(tokenizer.nextToken());
            }
        }

        if (!StringUtils.isBlank(mavenSystemOptions)) {
            StringTokenizer tokenizer = new StringTokenizer(mavenSystemOptions);

            while (tokenizer.hasMoreTokens()) {
                cmdList.add(tokenizer.nextToken());
            }
        }

        cmdList.add("-classpath");
        cmdList.add(classpathEntries);
        cmdList.add("-Dclassworlds.conf=" + mavenHome + System.getProperty("file.separator") + "bin"
                + System.getProperty("file.separator") + "m2.conf");
        cmdList.add("-Dmaven.home=" + mavenHome);
        cmdList.add("org.codehaus.classworlds.Launcher");

        addMavenSwitchesFromCoreConfiguration(buildEnvironment, cmdList);

        if (!StringUtils.isBlank(buildEnvironment.getMavenBuildConfiguration().getMavenSettingsFile())
                && !isDefaultSettingsFile(buildEnvironment.getMavenBuildConfiguration().getMavenSettingsFile())) {
            cmdList.add("-s");
            cmdList.add(buildEnvironment.getMavenBuildConfiguration().getMavenSettingsFile());
        }
        if (buildEnvironment.getMavenBuildConfiguration().isSkipTests()) {
            cmdList.add("-Dtest=skip");
        }
        Map mavenProperties = buildEnvironment.getMavenBuildConfiguration().getMavenProperties();
        Set keys = mavenProperties.keySet();
        for (Object key : keys) {
            String propertyName = (String) key;
            String propertyValue = (String) mavenProperties.get(propertyName);
            cmdList.add("-D" + propertyName + "=" + propertyValue);
        }
        cmdList.add("-f");
        cmdList.add(buildEnvironment.getPomFile());

        List<String> goals = buildEnvironment.getGoals();

        for (String goal : goals) {
            cmdList.add(goal);
        }

        return cmdList;
    }

    private static void addMavenSwitchesFromCoreConfiguration(IBuildEnvironment buildEnvironment,
                                                              List<String> cmdList) {
        MavenConfiguration mavenConfiguration = buildEnvironment.getMavenBuildConfiguration().getMavenConfiguration();
        if (mavenConfiguration.isWorkOffline()) {
            cmdList.add("--offline");
        }
        if (mavenConfiguration.isNonRecursive()) {
            cmdList.add("--non-recursive");
        }
        if (mavenConfiguration.isProduceExceptionErrorMessages()) {
            cmdList.add("--errors");
        }
        if (!mavenConfiguration.isUsePluginRegistry()) {
            cmdList.add("--no-plugin-registry");
        }
        if (mavenConfiguration.getOutputLevel() == MavenExecutionRequest.LOGGING_LEVEL_DEBUG) {
            cmdList.add("--debug");
        }
        if (mavenConfiguration.isPluginUpdatePolicy()) {
            cmdList.add("--check-plugin-updates");
        } else {
            cmdList.add("--no-plugin-updates");
        }
        cmdList.add("--" + mavenConfiguration.getFailureBehavior());
        if (!StringUtils.isEmpty(mavenConfiguration.getChecksumPolicy())) {
            if (mavenConfiguration.getChecksumPolicy().equals(MavenExecutionRequest.CHECKSUM_POLICY_FAIL)) {
                cmdList.add("--strict-checksums");
            } else {
                cmdList.add("--lax-checksums");
            }
        }
    }

    private static String getMavenClasspathEntries(String mavenHome, String classpathSeparator) {
        File mavenHomeBootAsFile = new File(mavenHome + System.getProperty("file.separator") + "core"
                + System.getProperty("file.separator") + "boot");
        String classpathEntries = "";

        if (mavenHomeBootAsFile.isDirectory()) {
            File[] files = mavenHomeBootAsFile.listFiles();

            for (File file : files) {
                if (file.getName().startsWith("classworlds-")) {
                    classpathEntries = classpathEntries + file.getAbsolutePath() + classpathSeparator;
                }
            }

            classpathEntries = StringUtils.chop(classpathEntries);
        }

        LOG.info("Classpath for Maven-2 call is: " + classpathEntries);

        return classpathEntries;
    }

    private static String getMavenHome(String mavenHome) throws MavenBuildException {
        String mavenSystemHome = System.getenv("M2_HOME");

        if (StringUtils.isBlank(mavenHome)) {
            LOG.info("Maven-2 Home not specified. Trying M2_HOME env variable!");

            if (!StringUtils.isBlank(mavenSystemHome)) {
                LOG.info("M2_HOME property found: " + mavenSystemHome);
                mavenHome = mavenSystemHome;
            } else {
                throw new MavenBuildException("Unable to execute Maven-2. No Maven-2 home directory specified!"
                        + System.getProperty("line.separator")
                        + "Either set the home directory in the configuration dialogs"
                        + System.getProperty("line.separator")
                        + "or set the M2_HOME environment variable on your system!");
            }
        }

        File mavenHomeAsFile = new File(mavenHome);
        File mavenConfFile = new File(mavenHome + System.getProperty("file.separator") + "bin"
                + System.getProperty("file.separator") + "m2.conf");

        if (!mavenConfFile.exists()) {
            throw new MavenBuildException("Specified Maven-2 home directory " + mavenHome + " is not a"
                    + System.getProperty("line.separator") + "Maven-2 installation directory!");
        }

        try {
            mavenHome = mavenHomeAsFile.getCanonicalPath();
            LOG.debug("Maven home as canonical path: " + mavenHome);
            LOG.debug("Maven home as absolute path: " + mavenHomeAsFile.getAbsolutePath());
            LOG.debug("Maven home as path: " + mavenHomeAsFile.getPath());
        } catch (IOException e) {
            throw new MavenBuildException(e);
        }

        return mavenHome;
    }

    private static String getRealJdkPath(String jdkPath) {
        String javaExecutable;    // if JAVA_HOME is specified use it as executable
        String javaHome = System.getenv("JAVA_HOME");
        if (StringUtils.isBlank(jdkPath)) {
            // in case the user did not select any of the JDKs configured in IDEA but choose to use
            // the system JDK accessible under the JAVA_HOME variable.
            if (!StringUtils.isBlank(javaHome)) {
                javaExecutable = javaHome + System.getProperty("file.separator") + "bin"
                        + System.getProperty("file.separator");
                LOG.info("Using JAVA_HOME variable. Java executable is in directory: " + javaExecutable);

                // if JAVA_HOME is not specified try to use the internal JDK from IDEA
            } else {
                ProjectJdkTable projectJdkTable = ProjectJdkTable.getInstance();

                javaExecutable = projectJdkTable.getInternalJdk().getBinPath() + System.getProperty("file.separator");
                LOG.info("JAVA_HOME not specified. Using internal IDEA JDK with bin path: " + javaExecutable);
                javaHome = projectJdkTable.getInternalJdk().getHomePath();
            }

            if (StringUtils.isBlank(javaHome)) {
                throw new MavenBuildException(
                        "Unable to locate JDK home directory!" + System.getProperty("line.separator")
                                + "Either set the home directory in the configuration dialogs"
                                + System.getProperty("line.separator")
                                + "or set the JAVA_HOME environment variable on your system!");
            }

            if (SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_WINDOWS_2000 || SystemUtils.IS_OS_WINDOWS_95
                    || SystemUtils.IS_OS_WINDOWS_98 || SystemUtils.IS_OS_WINDOWS_ME || SystemUtils.IS_OS_WINDOWS_NT
                    || SystemUtils.IS_OS_WINDOWS_XP) {
                if (!StringUtils.isBlank(javaExecutable)) {
                    javaExecutable = javaExecutable + "java.exe";
                }
            } else if (SystemUtils.IS_OS_UNIX) {
                if (!StringUtils.isBlank(javaExecutable)) {
                    javaExecutable = javaExecutable + "java";
                }
            }

            return javaExecutable;
        } else {
            return jdkPath;
        }
    }
}
