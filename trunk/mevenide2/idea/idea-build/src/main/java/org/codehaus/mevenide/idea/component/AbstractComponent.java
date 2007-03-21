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
import com.intellij.openapi.util.WriteExternalException;

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
/*
        pluginSettings.getMavenBuildConfiguration().setProduceExceptionErrorMessages(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
                BuildConstants.MAVEN_OPTION_ERRORS)));
*/
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
/*
        JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_ERRORS,
                                        Boolean.toString(pluginSettings.getMavenBuildConfiguration().isProduceExceptionErrorMessages()));
*/
    }
}
