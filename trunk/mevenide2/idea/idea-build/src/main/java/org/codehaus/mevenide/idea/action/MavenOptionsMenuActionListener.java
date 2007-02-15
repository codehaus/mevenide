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



package org.codehaus.mevenide.idea.action;

import org.apache.log4j.Logger;

import org.codehaus.mevenide.idea.build.MavenOptions;
import org.codehaus.mevenide.idea.build.util.BuildConstants;
import org.codehaus.mevenide.idea.helper.ActionContext;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenOptionsMenuActionListener extends AbstractBaseActionListener implements ActionListener {
    private static final Logger LOG = Logger.getLogger(MavenOptionsMenuActionListener.class);

    /**
     * Constructs ...
     *
     * @param context Document me!
     */
    public MavenOptionsMenuActionListener(ActionContext context) {
        this.context = context;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        JCheckBoxMenuItem source = (JCheckBoxMenuItem) actionEvent.getSource();
        MavenOptions options = context.getProjectPluginSettings().getMavenOptions();

        if (source.getActionCommand().equals(BuildConstants.MAVEN_OPTION_STRICT_CHECKSUM)) {
            options.setStrictChecksums(getMavenOptionValue(source));
        } else if (source.getActionCommand().equals(BuildConstants.MAVEN_OPTION_LAX_CHECKSUM)) {
            options.setLaxChecksums(getMavenOptionValue(source));
        } else if (source.getActionCommand().equals(BuildConstants.MAVEN_OPTION_BATCH_MODE)) {
            options.setBatchMode(getMavenOptionValue(source));
        } else if (source.getActionCommand().equals(BuildConstants.MAVEN_OPTION_CHECK_PLUGIN_UPDATES)) {
            options.setCheckPluginUpdates(getMavenOptionValue(source));
        } else if (source.getActionCommand().equals(BuildConstants.MAVEN_OPTION_DEBUG)) {
            options.setDebug(getMavenOptionValue(source));
        } else if (source.getActionCommand().equals(BuildConstants.MAVEN_OPTION_ERRORS)) {
            options.setErrors(getMavenOptionValue(source));
        } else if (source.getActionCommand().equals(BuildConstants.MAVEN_OPTION_FAIL_AT_END)) {
            options.setFailAtEnd(getMavenOptionValue(source));
        } else if (source.getActionCommand().equals(BuildConstants.MAVEN_OPTION_FAIL_FAST)) {
            options.setFailFast(getMavenOptionValue(source));
        } else if (source.getActionCommand().equals(BuildConstants.MAVEN_OPTION_FAIL_NEVER)) {
            options.setFailNever(getMavenOptionValue(source));
        } else if (source.getActionCommand().equals(BuildConstants.MAVEN_OPTION_NO_PLUGIN_REGISTRY)) {
            options.setNoPluginRegistry(getMavenOptionValue(source));
        } else if (source.getActionCommand().equals(BuildConstants.MAVEN_OPTION_NO_PLUGIN_UPDATES)) {
            options.setNoPluginUpdates(getMavenOptionValue(source));
        } else if (source.getActionCommand().equals(BuildConstants.MAVEN_OPTION_NON_RECURSIVE)) {
            options.setNonRecursive(getMavenOptionValue(source));
        } else if (source.getActionCommand().equals(BuildConstants.MAVEN_OPTION_OFFLINE)) {
            options.setOffline(getMavenOptionValue(source));
        } else if (source.getActionCommand().equals(BuildConstants.MAVEN_OPTION_REACTOR)) {
            options.setReactor(getMavenOptionValue(source));
        } else if (source.getActionCommand().equals(BuildConstants.MAVEN_OPTION_UPDATE_PLUGINS)) {
            options.setUpdatePlugins(getMavenOptionValue(source));
        } else if (source.getActionCommand().equals(BuildConstants.MAVEN_OPTION_UPDATE_SNAPSHOTS)) {
            options.setUpdateSnapshots(getMavenOptionValue(source));
        } else if (source.getActionCommand().equals(BuildConstants.MAVEN_OPTION_SKIP_TESTS)) {
            options.setSkipTests(getMavenOptionValue(source));
        }
    }

    private boolean getMavenOptionValue(JCheckBoxMenuItem menuItem) {
        return menuItem.isSelected();
    }
}
