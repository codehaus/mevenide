package org.codehaus.mevenide.idea.build;

import org.apache.commons.lang.builder.ToStringBuilder;

public class MavenConfiguration {
    private boolean workOffline;
    private String localRepository;
    private boolean produceExceptionErrorMessages;
    private boolean usePluginRegistry;
    private boolean skipTests;
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

    public boolean isSkipTests() {
        return skipTests;
    }

    public void setSkipTests(final boolean skipTests) {
        this.skipTests = skipTests;
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
        sb.append(", skipTests=").append(skipTests);
        sb.append(", nonRecursive=").append(nonRecursive);
        sb.append(", outputLevel=").append(outputLevel);
        sb.append(", checksumPolicy='").append(checksumPolicy).append('\'');
        sb.append(", failureBehavior='").append(failureBehavior).append('\'');
        sb.append(", pluginUpdatePolicy=").append(pluginUpdatePolicy);
        sb.append('}');
        return sb.toString();
    }
}