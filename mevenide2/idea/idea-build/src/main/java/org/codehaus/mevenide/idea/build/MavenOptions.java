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

import org.apache.commons.lang.StringUtils;

import org.codehaus.mevenide.idea.build.util.BuildConstants;

import java.util.Hashtable;

/**
 * Container for Maven options.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenOptions {
    private boolean strictChecksums;
    private boolean laxChecksums;
    private boolean failFast;
    private boolean failAtEnd;
    private boolean batchMode;
    private String activateProfiles;
    private boolean failNever;
    private boolean updatePlugins;
    private boolean nonRecursive;
    private boolean noPluginRegistry;
    private boolean updateSnapshots;
    private boolean checkPluginUpdates;
    private boolean noPluginUpdates;
    private String defineSystemProperty;
    private boolean debug;
    private boolean errors;
    private String file;
    private boolean help;
    private boolean offline;
    private boolean reactor;
    private String settingsFile;
    private boolean version;
    private boolean skipTests;
    private Hashtable<String, Boolean> mavenOptionList = new Hashtable<String, Boolean>();

    public Hashtable<String, Boolean> getMavenOptionList() {
        return mavenOptionList;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        if (isStrictChecksums()) {
            buffer.append("-C ");
        }

        if (isLaxChecksums()) {
            buffer.append("-c ");
        }

        if (isFailFast()) {
            buffer.append("-ff ");
        }

        if (isFailAtEnd()) {
            buffer.append("-fae ");
        }

        if (isBatchMode()) {
            buffer.append("-B ");
        }

        if (isFailNever()) {
            buffer.append("-fn ");
        }

        if (isUpdatePlugins()) {
            buffer.append("-up ");
        }

        if (isNonRecursive()) {
            buffer.append("-N ");
        }

        if (isNoPluginRegistry()) {
            buffer.append("-npr ");
        }

        if (isUpdateSnapshots()) {
            buffer.append("-U ");
        }

        if (isCheckPluginUpdates()) {
            buffer.append("-cpu ");
        }

        if (isNoPluginUpdates()) {
            buffer.append("-npu ");
        }

        if (isDebug()) {
            buffer.append("-X ");
        }

        if (isErrors()) {
            buffer.append("-e ");
        }

        if (isOffline()) {
            buffer.append("-o ");
        }

        if (isReactor()) {
            buffer.append("-r ");
        }

        if (isSkipTests()) {
            buffer.append("-Dtest=skip ");
        }

        return buffer.toString();
    }

    public String[] toStringArray() {
        return StringUtils.split(this.toString());
    }

    public boolean isStrictChecksums() {
        return strictChecksums;
    }

    public void setStrictChecksums(boolean strictChecksums) {
        this.strictChecksums = strictChecksums;
        mavenOptionList.put(BuildConstants.MAVEN_OPTION_STRICT_CHECKSUM, strictChecksums);
    }

    public boolean isLaxChecksums() {
        return laxChecksums;
    }

    public void setLaxChecksums(boolean laxChecksums) {
        this.laxChecksums = laxChecksums;
        mavenOptionList.put(BuildConstants.MAVEN_OPTION_LAX_CHECKSUM, laxChecksums);
    }

    public boolean isSkipTests() {
        return skipTests;
    }

    public void setSkipTests(boolean skipTests) {
        this.skipTests = skipTests;
        mavenOptionList.put(BuildConstants.MAVEN_OPTION_SKIP_TESTS, skipTests);
    }

    public boolean isFailFast() {
        return failFast;
    }

    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
        mavenOptionList.put(BuildConstants.MAVEN_OPTION_FAIL_FAST, failFast);
    }

    public boolean isFailAtEnd() {
        return failAtEnd;
    }

    public void setFailAtEnd(boolean failAtEnd) {
        this.failAtEnd = failAtEnd;
        mavenOptionList.put(BuildConstants.MAVEN_OPTION_FAIL_AT_END, failAtEnd);
    }

    public boolean isBatchMode() {
        return batchMode;
    }

    public void setBatchMode(boolean batchMode) {
        this.batchMode = batchMode;
        mavenOptionList.put(BuildConstants.MAVEN_OPTION_BATCH_MODE, batchMode);
    }

    public String getActivateProfiles() {
        return activateProfiles;
    }

    public void setActivateProfiles(String activateProfiles) {
        this.activateProfiles = activateProfiles;
    }

    public boolean isFailNever() {
        return failNever;
    }

    public void setFailNever(boolean failNever) {
        this.failNever = failNever;
        mavenOptionList.put(BuildConstants.MAVEN_OPTION_FAIL_NEVER, failNever);
    }

    public boolean isUpdatePlugins() {
        return updatePlugins;
    }

    public void setUpdatePlugins(boolean updatePlugins) {
        this.updatePlugins = updatePlugins;
        mavenOptionList.put(BuildConstants.MAVEN_OPTION_UPDATE_PLUGINS, updatePlugins);
    }

    public boolean isNonRecursive() {
        return nonRecursive;
    }

    public void setNonRecursive(boolean nonRecursive) {
        this.nonRecursive = nonRecursive;
        mavenOptionList.put(BuildConstants.MAVEN_OPTION_NON_RECURSIVE, nonRecursive);
    }

    public boolean isNoPluginRegistry() {
        return noPluginRegistry;
    }

    public void setNoPluginRegistry(boolean noPluginRegistry) {
        this.noPluginRegistry = noPluginRegistry;
        mavenOptionList.put(BuildConstants.MAVEN_OPTION_NO_PLUGIN_REGISTRY, noPluginRegistry);
    }

    public boolean isUpdateSnapshots() {
        return updateSnapshots;
    }

    public void setUpdateSnapshots(boolean updateSnapshots) {
        this.updateSnapshots = updateSnapshots;
        mavenOptionList.put(BuildConstants.MAVEN_OPTION_UPDATE_SNAPSHOTS, updateSnapshots);
    }

    public boolean isCheckPluginUpdates() {
        return checkPluginUpdates;
    }

    public void setCheckPluginUpdates(boolean checkPluginUpdates) {
        this.checkPluginUpdates = checkPluginUpdates;
        mavenOptionList.put(BuildConstants.MAVEN_OPTION_CHECK_PLUGIN_UPDATES, checkPluginUpdates);
    }

    public boolean isNoPluginUpdates() {
        return noPluginUpdates;
    }

    public void setNoPluginUpdates(boolean noPluginUpdates) {
        this.noPluginUpdates = noPluginUpdates;
        mavenOptionList.put(BuildConstants.MAVEN_OPTION_NO_PLUGIN_UPDATES, noPluginUpdates);
    }

    public String getDefineSystemProperty() {
        return defineSystemProperty;
    }

    public void setDefineSystemProperty(String defineSystemProperty) {
        this.defineSystemProperty = defineSystemProperty;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
        mavenOptionList.put(BuildConstants.MAVEN_OPTION_DEBUG, debug);
    }

    public boolean isErrors() {
        return errors;
    }

    public void setErrors(boolean errors) {
        this.errors = errors;
        mavenOptionList.put(BuildConstants.MAVEN_OPTION_ERRORS, errors);
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
        mavenOptionList.put(BuildConstants.MAVEN_OPTION_OFFLINE, offline);
    }

    public boolean isReactor() {
        return reactor;
    }

    public void setReactor(boolean reactor) {
        this.reactor = reactor;
        mavenOptionList.put(BuildConstants.MAVEN_OPTION_REACTOR, reactor);
    }

    public String getSettingsFile() {
        return settingsFile;
    }

    public void setSettingsFile(String settingsFile) {
        this.settingsFile = settingsFile;
    }

    public boolean isVersion() {
        return version;
    }

    public void setVersion(boolean version) {
        this.version = version;
    }
}
