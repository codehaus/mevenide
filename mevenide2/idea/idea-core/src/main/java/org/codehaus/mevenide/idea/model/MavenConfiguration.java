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

package org.codehaus.mevenide.idea.model;

public class MavenConfiguration {
    public static final String CONFIG_ELEMENT_WORK_OFFLINE = "workOffline";
    public static final String CONFIG_ELEMENT_LOCAL_REPOSITORY = "localRepository";
    public static final String CONFIG_ELEMENT_EXCEPTION_ERROR_MESSAGES = "exceptionErrorMessages";
    public static final String CONFIG_ELEMENT_USE_PLUGIN_REGISTRY = "usePluginRegistry";
    public static final String CONFIG_ELEMENT_NON_RECURSIVE = "nonRecursive";
    public static final String CONFIG_ELEMENT_OUTPUT_LEVEL = "outputLevel";
    public static final String CONFIG_ELEMENT_CHECKSUM_POLICY = "checksumPolicy";
    public static final String CONFIG_ELEMENT_FAILURE_BEHAVIOR = "failureBehavior";
    public static final String CONFIG_ELEMENT_PLUGIN_UPDATE_POLICY = "updatePolicy";

    private boolean workOffline;
    private String localRepository;
    private boolean produceExceptionErrorMessages;
    private boolean usePluginRegistry;
    private boolean nonRecursive;
    private int outputLevel;
    private String checksumPolicy;
    private String failureBehavior;
    private Boolean pluginUpdatePolicy;

    public MavenConfiguration() {
    }


    public Boolean isPluginUpdatePolicy() {
        return pluginUpdatePolicy;
    }

    public void setPluginUpdatePolicy(Boolean pluginUpdatePolicy) {
        this.pluginUpdatePolicy = pluginUpdatePolicy;
    }

    public String getChecksumPolicy() {
        return checksumPolicy;
    }

    public void setChecksumPolicy(String checksumPolicy) {
        this.checksumPolicy = checksumPolicy;
    }

    public String getFailureBehavior() {
        return failureBehavior;
    }

    public void setFailureBehavior(String failureBehavior) {
        this.failureBehavior = failureBehavior;
    }

    public boolean isWorkOffline() {
        return workOffline;
    }

    public void setWorkOffline(final boolean workOffline) {
        this.workOffline = workOffline;
    }

    public int getOutputLevel() {
        return outputLevel;
    }

    public void setOutputLevel(int outputLevel) {
        this.outputLevel = outputLevel;
    }

    public String getLocalRepository() {
        return localRepository;
    }

    public void setLocalRepository(final String localRepository) {
        this.localRepository = localRepository;
    }

    public boolean isProduceExceptionErrorMessages() {
        return produceExceptionErrorMessages;
    }

    public void setProduceExceptionErrorMessages(final boolean produceExceptionErrorMessages) {
        this.produceExceptionErrorMessages = produceExceptionErrorMessages;
    }

    public boolean isUsePluginRegistry() {
        return usePluginRegistry;
    }

    public void setUsePluginRegistry(final boolean usePluginRegistry) {
        this.usePluginRegistry = usePluginRegistry;
    }

    public boolean isNonRecursive() {
        return nonRecursive;
    }

    public void setNonRecursive(final boolean nonRecursive) {
        this.nonRecursive = nonRecursive;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("MavenConfiguration");
        sb.append("{workOffline=").append(workOffline);
        sb.append(", localRepository='").append(localRepository).append('\'');
        sb.append(", produceExceptionErrorMessages=").append(produceExceptionErrorMessages);
        sb.append(", usePluginRegistry=").append(usePluginRegistry);
        sb.append(", nonRecursive=").append(nonRecursive);
        sb.append(", outputLevel=").append(outputLevel);
        sb.append(", checksumPolicy='").append(checksumPolicy).append('\'');
        sb.append(", failureBehavior='").append(failureBehavior).append('\'');
        sb.append(", pluginUpdatePolicy=").append(pluginUpdatePolicy);
        sb.append('}');
        return sb.toString();
    }
}
