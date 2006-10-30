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

package org.codehaus.mevenide.netbeans.options;

import java.util.prefs.Preferences;
import org.apache.maven.execution.MavenExecutionRequest;
import org.openide.util.NbPreferences;

/**
 * a netbeans settings for global options that cannot be put into the settings file.
 * @author mkleint
 */
public class MavenExecutionSettings  {
    public static final String PROP_DEBUG = "showDebug"; // NOI18N
    public static final String PROP_ERRORS = "showErrors"; //NOI18N
    public static final String PROP_CHECKSUM_POLICY = "checksumPolicy"; //NOI18N
    public static final String PROP_PLUGIN_POLICY = "pluginUpdatePolicy"; //NOI18N
    public static final String PROP_FAILURE_BEHAVIOUR = "failureBehaviour"; //NOI18N
    public static final String PROP_USE_REGISTRY = "usePluginRegistry"; //NOI18N
    
    private static final MavenExecutionSettings INSTANCE = new MavenExecutionSettings();
    
    public static MavenExecutionSettings getDefault() {
        return INSTANCE;
    }
    
    protected final Preferences getPreferences() {
        return NbPreferences.forModule(MavenExecutionSettings.class);
    }
    
    protected final String putProperty(String key, String value) {
        String retval = getProperty(key);
        if (value != null) {
            getPreferences().put(key, value);
        } else {
            getPreferences().remove(key);
        }
        return retval;
    }

    protected final String getProperty(String key) {
        return getPreferences().get(key, null);
    }    
    
    private MavenExecutionSettings() {
    }
    

    public boolean isShowDebug() {
        return getPreferences().getBoolean(PROP_DEBUG, false);
    }

    public void setShowDebug(boolean showDebug) {
        getPreferences().putBoolean(PROP_DEBUG, showDebug);
    }

    public boolean isShowErrors() {
        return getPreferences().getBoolean(PROP_ERRORS, false);
    }

    public void setShowErrors(boolean showErrors) {
        getPreferences().putBoolean(PROP_ERRORS, showErrors);
    }

    public String getChecksumPolicy() {
        return getPreferences().get(PROP_CHECKSUM_POLICY, null);
    }

    public void setChecksumPolicy(String checksumPolicy) {
        putProperty(PROP_CHECKSUM_POLICY, checksumPolicy);
    }

    public Boolean getPluginUpdatePolicy() {
        String prop = getProperty(PROP_PLUGIN_POLICY);
        return prop == null ? null : Boolean.parseBoolean(prop);
    }

    public void setPluginUpdatePolicy(Boolean pluginUpdatePolicy) {
        if (pluginUpdatePolicy == null) {
            getPreferences().remove(PROP_PLUGIN_POLICY);
        } else {
            putProperty(PROP_PLUGIN_POLICY, pluginUpdatePolicy.toString());
        }
    }

    public String getFailureBehaviour() {
        return getPreferences().get(PROP_FAILURE_BEHAVIOUR, MavenExecutionRequest.REACTOR_FAIL_FAST);
    }

    public void setFailureBehaviour(String failureBehaviour) {
        putProperty(PROP_FAILURE_BEHAVIOUR, failureBehaviour);
    }

    public boolean isUsePluginRegistry() {
        return getPreferences().getBoolean(PROP_USE_REGISTRY, true);
    }

    public void setUsePluginRegistry(boolean usePluginRegistry) {
        getPreferences().putBoolean(PROP_USE_REGISTRY, usePluginRegistry);
    }
    
    
}
