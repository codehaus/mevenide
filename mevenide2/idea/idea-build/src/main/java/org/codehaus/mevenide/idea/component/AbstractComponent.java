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



package org.codehaus.mevenide.idea.component;

import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;

import org.codehaus.mevenide.idea.build.util.BuildConstants;
import org.codehaus.mevenide.idea.common.MavenBuildPluginSettings;

import org.jdom.Element;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class AbstractComponent {

    /**
     * Method description
     *
     * @param pluginSettings The plugin settings to read.
     * @param element        The DOM element.
     *
     * @throws com.intellij.openapi.util.InvalidDataException
     *          in case of invalid data.
     */
    public void readExternal(MavenBuildPluginSettings pluginSettings, Element element) throws InvalidDataException {
        pluginSettings.getMavenOptions().setBatchMode(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                BuildConstants.MAVEN_OPTION_BATCH_MODE)));
        pluginSettings.getMavenOptions().setCheckPluginUpdates(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                BuildConstants.MAVEN_OPTION_CHECK_PLUGIN_UPDATES)));
        pluginSettings.getMavenOptions().setDebug(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                BuildConstants.MAVEN_OPTION_DEBUG)));
        pluginSettings.getMavenOptions().setErrors(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                BuildConstants.MAVEN_OPTION_ERRORS)));
        pluginSettings.getMavenOptions().setFailAtEnd(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                BuildConstants.MAVEN_OPTION_FAIL_AT_END)));
        pluginSettings.getMavenOptions().setFailFast(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                BuildConstants.MAVEN_OPTION_FAIL_FAST)));
        pluginSettings.getMavenOptions().setFailNever(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                BuildConstants.MAVEN_OPTION_FAIL_NEVER)));
        pluginSettings.getMavenOptions().setLaxChecksums(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                BuildConstants.MAVEN_OPTION_LAX_CHECKSUM)));
        pluginSettings.getMavenOptions().setNonRecursive(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                BuildConstants.MAVEN_OPTION_NON_RECURSIVE)));
        pluginSettings.getMavenOptions().setNoPluginRegistry(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                BuildConstants.MAVEN_OPTION_NO_PLUGIN_REGISTRY)));
        pluginSettings.getMavenOptions().setNoPluginUpdates(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                BuildConstants.MAVEN_OPTION_NO_PLUGIN_UPDATES)));
        pluginSettings.getMavenOptions().setOffline(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                BuildConstants.MAVEN_OPTION_OFFLINE)));
        pluginSettings.getMavenOptions().setReactor(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                BuildConstants.MAVEN_OPTION_REACTOR)));
        pluginSettings.getMavenOptions().setStrictChecksums(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                BuildConstants.MAVEN_OPTION_STRICT_CHECKSUM)));
        pluginSettings.getMavenOptions().setUpdatePlugins(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                BuildConstants.MAVEN_OPTION_UPDATE_PLUGINS)));
        pluginSettings.getMavenOptions().setUpdateSnapshots(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                BuildConstants.MAVEN_OPTION_UPDATE_SNAPSHOTS)));
        pluginSettings.getMavenOptions().setSkipTests(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                BuildConstants.MAVEN_OPTION_SKIP_TESTS)));
    }

    /**
     * Method description
     *
     * @param pluginSettings The settings to write.
     * @param element The DOM element.
     *
     * @throws com.intellij.openapi.util.WriteExternalException in case of a external write error.
     *
     */
    public void writeExternal(MavenBuildPluginSettings pluginSettings, Element element) throws WriteExternalException {
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_BATCH_MODE,
                                        Boolean.toString(pluginSettings.getMavenOptions().isBatchMode()));
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_CHECK_PLUGIN_UPDATES,
                                        Boolean.toString(pluginSettings.getMavenOptions().isCheckPluginUpdates()));
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_DEBUG,
                                        Boolean.toString(pluginSettings.getMavenOptions().isDebug()));
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_ERRORS,
                                        Boolean.toString(pluginSettings.getMavenOptions().isErrors()));
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_FAIL_AT_END,
                                        Boolean.toString(pluginSettings.getMavenOptions().isFailAtEnd()));
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_FAIL_FAST,
                                        Boolean.toString(pluginSettings.getMavenOptions().isFailFast()));
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_FAIL_NEVER,
                                        Boolean.toString(pluginSettings.getMavenOptions().isFailNever()));
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_LAX_CHECKSUM,
                                        Boolean.toString(pluginSettings.getMavenOptions().isLaxChecksums()));
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_NO_PLUGIN_REGISTRY,
                                        Boolean.toString(pluginSettings.getMavenOptions().isNoPluginRegistry()));
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_NO_PLUGIN_UPDATES,
                                        Boolean.toString(pluginSettings.getMavenOptions().isNoPluginUpdates()));
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_NON_RECURSIVE,
                                        Boolean.toString(pluginSettings.getMavenOptions().isNonRecursive()));
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_OFFLINE,
                                        Boolean.toString(pluginSettings.getMavenOptions().isOffline()));
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_REACTOR,
                                        Boolean.toString(pluginSettings.getMavenOptions().isReactor()));
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_STRICT_CHECKSUM,
                                        Boolean.toString(pluginSettings.getMavenOptions().isStrictChecksums()));
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_UPDATE_PLUGINS,
                                        Boolean.toString(pluginSettings.getMavenOptions().isUpdatePlugins()));
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_UPDATE_SNAPSHOTS,
                                        Boolean.toString(pluginSettings.getMavenOptions().isUpdateSnapshots()));
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_SKIP_TESTS,
                                        Boolean.toString(pluginSettings.getMavenOptions().isSkipTests()));
    }
}
