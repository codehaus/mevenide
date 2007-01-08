/*
 * Copyright (c) 2006 Bryan Kate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package mavenreloaded.configuration;


import mavenreloaded.console.LogMessage;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.TreeMap;

import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;


/**
 * A bean that holds the configuration settings for the plugin. They are all grouped here to make binding them to a GUI
 * for that much easier.
 *
 * @author bkate
 */
public class ConfigurationBean implements JDOMExternalizable {

    // overall config
    public boolean pluginEnabled = false;

    // POM manager config
    public boolean updateClasspathsEnabled = true;
    public boolean sortDependenciesEnabled = false;
    public boolean removeDuplicateDependenciesEnabled = false;
    public boolean respondToPomChangesEnabled = true;
    public boolean manageSourceRootsEnabled = true;
    public boolean manageModuleInterdependenciesEnabled = true;
    public boolean downloadSourcesEnabled = false;
    public boolean downloadJavadocEnabled = false;
    public boolean generateSourcesEnabled = false;
    public String settingsPath = DEFAULT_MVN_HOME + "/settings.xml";
    public String searchFilter = "";
    public Set<String> disabledPoms = new TreeSet<String>();
    public Map<String, Set<String>> moduleInterDependencies = new TreeMap<String, Set<String>>();

    // logger config
    public int logLevel = LogMessage.INFO;

    // location of maven settings
    private static final String DEFAULT_MVN_HOME = System.getProperty("user.home") + "/.m2";


    /**
     * Default constructor.
     */
    public ConfigurationBean() {
    }


    public boolean isPluginEnabled() {
        return pluginEnabled;
    }


    public void setPluginEnabled(boolean pluginEnabled) {
        this.pluginEnabled = pluginEnabled;
    }


    public boolean isUpdateClasspathsEnabled() {
        return updateClasspathsEnabled;
    }


    public void setUpdateClasspathsEnabled(boolean updateClasspathsEnabled) {
        this.updateClasspathsEnabled = updateClasspathsEnabled;
    }


    public boolean isSortDependenciesEnabled() {
        return sortDependenciesEnabled;
    }


    public void setSortDependenciesEnabled(boolean sortDependenciesEnabled) {
        this.sortDependenciesEnabled = sortDependenciesEnabled;
    }


    public boolean isRemoveDuplicateDependenciesEnabled() {
        return removeDuplicateDependenciesEnabled;
    }


    public void setRemoveDuplicateDependenciesEnabled(boolean removeDuplicateDependenciesEnabled) {
        this.removeDuplicateDependenciesEnabled = removeDuplicateDependenciesEnabled;
    }


    public boolean isRespondToPomChangesEnabled() {
        return respondToPomChangesEnabled;
    }

    public void setRespondToPomChangesEnabled(boolean respondToPomChangesEnabled) {
        this.respondToPomChangesEnabled = respondToPomChangesEnabled;
    }


    public boolean isManageSourceRootsEnabled() {
        return manageSourceRootsEnabled;
    }


    public void setManageSourceRootsEnabled(boolean manageSourceRootsEnabled) {
        this.manageSourceRootsEnabled = manageSourceRootsEnabled;
    }


    public boolean isManageModuleInterdependenciesEnabled() {
        return manageModuleInterdependenciesEnabled;
    }


    public void setManageModuleInterdependenciesEnabled(boolean manageModuleInterdependenciesEnabled) {
        this.manageModuleInterdependenciesEnabled = manageModuleInterdependenciesEnabled;
    }


    public boolean isGenerateSourcesEnabled() {
        return generateSourcesEnabled;
    }


    public void setGenerateSourcesEnabled(boolean generateSourcesEnabled) {
        this.generateSourcesEnabled = generateSourcesEnabled;
    }


    public boolean isDownloadSourcesEnabled() {
        return downloadSourcesEnabled;
    }


    public void setDownloadSourcesEnabled(boolean downloadSourcesEnabled) {
        this.downloadSourcesEnabled = downloadSourcesEnabled;
    }


    public boolean isDownloadJavadocEnabled() {
        return downloadJavadocEnabled;
    }


    public void setDownloadJavadocEnabled(boolean downloadJavadocEnabled) {
        this.downloadJavadocEnabled = downloadJavadocEnabled;
    }


    public String getSettingsPath() {
        return settingsPath;
    }


    public void setSettingsPath(String settingsPath) {
        this.settingsPath = settingsPath;
    }


    public String getSearchFilter() {
        return searchFilter;
    }


    public void setSearchFilter(String searchFilter) {
        this.searchFilter = searchFilter;
    }


    public Set<String> getDisabledPoms() {
        return disabledPoms;
    }


    public void setDisabledPoms(Set<String> disabledPoms) {
        this.disabledPoms = disabledPoms;
    }


    public Map<String, Set<String>> getModuleInterDependencies() {
        return moduleInterDependencies;
    }


    public void setModuleInterDependencies(Map<String, Set<String>> moduleInterDependencies) {
        this.moduleInterDependencies = moduleInterDependencies;
    }


    public int getLogLevel() {
        return logLevel;
    }


    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }


    /** {@inheritDoc} */
    public void readExternal(Element element) throws InvalidDataException {
        PluginJDOMExternalizer.readExternal(this, element);
    }


    /** {@inheritDoc} */
    public void writeExternal(Element element) throws WriteExternalException {
        PluginJDOMExternalizer.writeExternal(this, element);
    }
}

