package org.codehaus.mevenide.idea.build;

public class MavenConfiguration {
    private boolean workOffline;
    private String localRepository;
    private boolean produceExceptionErrorMessages;
    private boolean usePluginRegistry;
    private boolean skipTests;
    private boolean nonRecursive;
    private int outputLevel;

    public MavenConfiguration() {
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
}