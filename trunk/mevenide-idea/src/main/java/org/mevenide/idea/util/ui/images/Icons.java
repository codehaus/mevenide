/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.idea.util.ui.images;

import com.intellij.openapi.util.IconLoader;
import javax.swing.*;

/**
 * @author Arik
 */
public abstract class Icons {
    public static final Icon BACK = IconLoader.getIcon("back.png", Icons.class);
    public static final Icon CANCEL = IconLoader.getIcon("cancel.png", Icons.class);
    public static final Icon DOWNLOAD = IconLoader.getIcon("download.png", Icons.class);
    public static final Icon EXECUTE = IconLoader.getIcon("execute.png", Icons.class);
    public static final Icon FORWARD = IconLoader.getIcon("forward.png", Icons.class);
    public static final Icon GOAL = IconLoader.getIcon("goal.png", Icons.class);
    public static final Icon MAVEN = IconLoader.getIcon("maven-icon.png", Icons.class);
    public static final Icon MAVEN_SETTINGS = IconLoader.getIcon("maven-settings.png",
                                                                 Icons.class);
    public static final Icon MAVEN_SETTINGS_SMALL = IconLoader.getIcon(
        "maven-settings-small.png",
        Icons.class);
    public static final Icon PAUSE = IconLoader.getIcon("pause.png", Icons.class);
    public static final Icon PLUGIN = IconLoader.getIcon("plugin.png", Icons.class);
    public static final Icon PROPERTIES = IconLoader.getIcon("properties.png",
                                                             Icons.class);
    public static final Icon RERUN = IconLoader.getIcon("rerun.png", Icons.class);
    public static final Icon REPOSITORY = IconLoader.getIcon("repository.png",
                                                             Icons.class);
    public static final Icon SUSPEND = IconLoader.getIcon("suspend.png", Icons.class);
    public static final Icon SYNC = IconLoader.getIcon("sync.png", Icons.class);

    public static final Icon REPO_GROUP_OPEN = IconLoader.getIcon("repoGroupOpen.png",
                                                                  Icons.class);
    public static final Icon REPO_GROUP_CLOSED = IconLoader.getIcon("repoGroupClosed.png",
                                                                    Icons.class);

    public static final Icon REPO_ARTIFACT = IconLoader.getIcon("repoArtifact.png",
                                                                Icons.class);
    public static final Icon REPO_VERSION = IconLoader.getIcon("repoVersion.png",
                                                               Icons.class);
    public static final Icon REPO_MSG = IconLoader.getIcon("repoMsg.png", Icons.class);

    public static final Icon REPO_FOLDER_OPEN = IconLoader.getIcon("repoFolderOpen.png",
                                                                   Icons.class);
    public static final Icon REPO_FOLDER_CLOSED = IconLoader.getIcon(
        "repoFolderClosed.png",
        Icons.class);

    public static final Icon REPO_TYPE_ARCHIVE = IconLoader.getIcon(
        "repoArtifactArchive.png",
        Icons.class);
    public static final Icon REPO_TYPE_POM = IconLoader.getIcon("repoArtifactPom.png",
                                                                Icons.class);
    public static final Icon REPO_TYPE_LICENSE = IconLoader.getIcon(
        "repoArtifactLicense.png",
        Icons.class);

    public static final Icon REPO_TYPE_JAR_OPEN = IconLoader.getIcon("repoTypeJarOpen.png",
                                                                     Icons.class);
    public static final Icon REPO_TYPE_JAR_CLOSED = IconLoader.getIcon(
        "repoTypeJarClosed.png",
        Icons.class);
    public static final Icon REPO_TYPE_SRC_JAR_OPEN = IconLoader.getIcon(
        "repoTypeSrcJarOpen.png",
        Icons.class);
    public static final Icon REPO_TYPE_SRC_JAR_CLOSED = IconLoader.getIcon(
        "repoTypeSrcJarClosed.png",
        Icons.class);
    public static final Icon REPO_TYPE_JAVADOC_JAR_OPEN = IconLoader.getIcon(
        "repoTypeJavadocJarOpen.png",
        Icons.class);
    public static final Icon REPO_TYPE_JAVADOC_JAR_CLOSED = IconLoader.getIcon(
        "repoTypeJavadocJarClosed.png",
        Icons.class);
    public static final Icon REPO_TYPE_EJB_OPEN = IconLoader.getIcon("repoTypeEjbOpen.png",
                                                                     Icons.class);
    public static final Icon REPO_TYPE_EJB_CLOSED = IconLoader.getIcon(
        "repoTypeEjbClosed.png",
        Icons.class);
    public static final Icon REPO_TYPE_WAR_OPEN = IconLoader.getIcon("repoTypeWarOpen.png",
                                                                     Icons.class);
    public static final Icon REPO_TYPE_WAR_CLOSED = IconLoader.getIcon(
        "repoTypeWarClosed.png",
        Icons.class);
    public static final Icon REPO_TYPE_LICENSE_OPEN = IconLoader.getIcon(
        "repoTypeLicenseOpen.png",
        Icons.class);
    public static final Icon REPO_TYPE_LICENSE_CLOSED = IconLoader.getIcon(
        "repoTypeLicenseClosed.png",
        Icons.class);
    public static final Icon REPO_TYPE_PLUGIN_OPEN = IconLoader.getIcon(
        "repoTypePluginOpen.png",
        Icons.class);
    public static final Icon REPO_TYPE_PLUGIN_CLOSED = IconLoader.getIcon(
        "repoTypePluginClosed.png",
        Icons.class);
    public static final Icon REPO_TYPE_DIST_OPEN = IconLoader.getIcon(
        "repoTypeDistOpen.png",
        Icons.class);
    public static final Icon REPO_TYPE_DIST_CLOSED = IconLoader.getIcon(
        "repoTypeDistClosed.png",
        Icons.class);
    public static final Icon REPO_TYPE_POM_OPEN = IconLoader.getIcon("repoTypePomOpen.png",
                                                                     Icons.class);
    public static final Icon REPO_TYPE_POM_CLOSED = IconLoader.getIcon(
        "repoTypePomClosed.png",
        Icons.class);
    public static final Icon REPO_TYPE_EAR_OPEN = IconLoader.getIcon("repoTypeEarOpen.png",
                                                                     Icons.class);
    public static final Icon REPO_TYPE_EAR_CLOSED = IconLoader.getIcon(
        "repoTypeEarClosed.png",
        Icons.class);

    public static final Icon REPO_FILTER_LOCAL = IconLoader.getIcon("repoFilterLocal.png",
                                                                    Icons.class);
    public static final Icon REPO_FILTER_REMOTE = IconLoader.getIcon(
        "repoFilterRemote.png",
        Icons.class);

    public static final Icon FIX_PROBLEMS = IconLoader.getIcon("fixProblems.png",
                                                               Icons.class);
    public static final Icon PROBLEM = IconLoader.getIcon("problem.png", Icons.class);
    public static final Icon UNKNOWN_PROBLEM = IconLoader.getIcon("problemUnknown.png",
                                                                  Icons.class);
    public static final Icon WARNING = IconLoader.getIcon("warning.png", Icons.class);
    public static final Icon PROBLEM_FIXED = IconLoader.getIcon("problemOk.png",
                                                                Icons.class);
}
