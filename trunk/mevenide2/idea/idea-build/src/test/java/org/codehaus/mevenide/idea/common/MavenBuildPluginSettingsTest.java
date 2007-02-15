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



package org.codehaus.mevenide.idea.common;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

import org.codehaus.mevenide.idea.build.MavenOptions;
import org.codehaus.mevenide.idea.build.util.BuildConstants;

/**
 * MavenBuildPluginSettings Tester.
 *
 * @author peter
 * @version $Revision$, $Date: 2007-01-11 01:31:09 +0100 (Do, 11 Jan 2007) $
 * @created Dezember 26, 2006
 * @since 1.0
 */
public class MavenBuildPluginSettingsTest extends TestCase {
    private MavenBuildPluginSettings settings;

    public void setUp() {}

    public void testGetMavenOptions() {
        settings = new MavenBuildPluginSettings();
        assertNotNull(settings.getMavenOptions());

        MavenOptions options = new MavenOptions();

        options.setActivateProfiles("activateProfile");
        options.setBatchMode(true);
        assertNotNull(options);
        settings.setMavenOptions(options);
        assertNotNull(settings.getMavenOptions());
    }

    public void testSetMavenOptions() {
        settings = new MavenBuildPluginSettings();

        MavenOptions options = new MavenOptions();

        options.setActivateProfiles("activateProfile");
        options.setBatchMode(true);
        assertNotNull(options);
        settings.setMavenOptions(options);
        assertNotNull(settings.getMavenOptions());
    }

    public void testGetMavenSettingsFile() {
        settings = new MavenBuildPluginSettings();

        String systemProperty = System.getProperty("user.home") + System.getProperty("file.separator") + ".m2"
                                + System.getProperty("file.separator") + BuildConstants.FILENAME_MAVEN_SETTINGS_FILE;

        if (StringUtils.isBlank(systemProperty)) {
            assertNull(settings.getMavenSettingsFile());
        } else {
            assertNotNull(settings.getMavenSettingsFile());
        }
    }

    public void testSetMavenSettingsFile() {
        settings = new MavenBuildPluginSettings();
        settings.setMavenSettingsFile("A settings file");
        assertNotNull(settings.getMavenSettingsFile());
    }

    public void testGetMavenCommandLineParams() {
        settings = new MavenBuildPluginSettings();
        settings.setMavenCommandLineParams("A command line param");
        assertNotNull(settings.getMavenCommandLineParams());
    }

    public void testGetMavenHome() {
        settings = new MavenBuildPluginSettings();
        settings.setMavenHome("Maven Home");
        assertNotNull(settings.getMavenHome());
    }

    public void testGetMavenRepository() {
        settings = new MavenBuildPluginSettings();
        settings.setMavenRepository("Maven Repository");
        assertNotNull(settings.getMavenRepository());
    }

    public void testIsScanForExistingPoms() {
        settings = new MavenBuildPluginSettings();
        assertFalse(settings.isScanForExistingPoms());
        settings.setScanForExistingPoms(true);
        assertTrue(settings.isScanForExistingPoms());
    }

    public void testIsUseMavenEmbedder() {
        settings = new MavenBuildPluginSettings();
        assertFalse(settings.isUseMavenEmbedder());
        settings.setUseMavenEmbedder(true);
        assertTrue(settings.isUseMavenEmbedder());
    }
}
