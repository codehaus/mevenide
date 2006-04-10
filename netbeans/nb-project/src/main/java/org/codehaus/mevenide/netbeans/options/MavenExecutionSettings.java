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

import org.apache.maven.execution.MavenExecutionRequest;
import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * a netbeans settings for global options that cannot be put into the settings file.
 * @author mkleint
 */
public class MavenExecutionSettings extends SystemOption {
    public static final String PROP_DEBUG = "showDebug"; // NOI18N
    public static final String PROP_ERRORS = "showErrors"; //NOI18N
    public static final String PROP_CHECKSUM_POLICY = "checksumPolicy"; //NOI18N
    public static final String PROP_PLUGIN_POLICY = "pluginUpdatePolicy"; //NOI18N
    public static final String PROP_FAILURE_BEHAVIOUR = "failureBehaviour"; //NOI18N
    
    private static final long serialVersionUID = -4857548487373437L;

    
    protected void initialize() {
        super.initialize();
        setChecksumPolicy(null);
        setPluginUpdatePolicy(null);
        setShowDebug(false);
        setShowErrors(false);
        setFailureBehaviour(MavenExecutionRequest.REACTOR_FAIL_FAST);
    }
    
    public String displayName() {
        return "ExecutionSettings"; //NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public static MavenExecutionSettings getDefault() {
        return (MavenExecutionSettings) findObject(MavenExecutionSettings.class, true);
    }

    public boolean isShowDebug() {
        return ((Boolean)getProperty(PROP_DEBUG)).booleanValue();
    }

    public void setShowDebug(boolean showDebug) {
        putProperty(PROP_DEBUG, Boolean.valueOf(showDebug), true);
    }

    public boolean isShowErrors() {
        return ((Boolean)getProperty(PROP_ERRORS)).booleanValue();
    }

    public void setShowErrors(boolean showErrors) {
        putProperty(PROP_ERRORS, Boolean.valueOf(showErrors), true);
    }

    public String getChecksumPolicy() {
        return (String)getProperty(PROP_CHECKSUM_POLICY);
    }

    public void setChecksumPolicy(String checksumPolicy) {
        putProperty(PROP_CHECKSUM_POLICY, checksumPolicy, true);
    }

    public Boolean getPluginUpdatePolicy() {
        return (Boolean)getProperty(PROP_PLUGIN_POLICY);
    }

    public void setPluginUpdatePolicy(Boolean pluginUpdatePolicy) {
        putProperty(PROP_PLUGIN_POLICY, pluginUpdatePolicy, true);
    }

    public String getFailureBehaviour() {
        return (String)getProperty(PROP_FAILURE_BEHAVIOUR);
    }

    public void setFailureBehaviour(String failureBehaviour) {
        putProperty(PROP_FAILURE_BEHAVIOUR, failureBehaviour, true);
    }
    
}
