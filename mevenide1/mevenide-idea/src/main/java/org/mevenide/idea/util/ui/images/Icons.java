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
    public static final Icon ADD_DEPENDENCY = icon("addExistingModule.png");
    public static final Icon REMOVE_DEPENDENCY = icon("removeModule.png");

    public static final Icon BACK = icon("back.png");
    public static final Icon CANCEL = icon("cancel.png");
    public static final Icon DOWNLOAD = icon("download.png");
    public static final Icon EXECUTE = icon("execute.png");
    public static final Icon FORWARD = icon("forward.png");
    public static final Icon GOAL = icon("goal.png");
    public static final Icon MAVEN = icon("maven-icon.png");
    public static final Icon MAVEN_SETTINGS = icon("maven-settings.png");
    public static final Icon MAVEN_SETTINGS_SMALL = icon("maven-settings-small.png");
    public static final Icon PAUSE = icon("pause.png");
    public static final Icon PLUGIN = icon("plugin.png");
    public static final Icon PROPERTIES = icon("properties.png");
    public static final Icon RERUN = icon("rerun.png");
    public static final Icon REPOSITORY = icon("repository.png");
    public static final Icon SUSPEND = icon("suspend.png");
    public static final Icon SYNC = icon("sync.png");
    public static final Icon WEB = icon("web.png");
    public static final Icon WEB_SERVER = icon("webServer.png");

    public static final Icon REPO_LOCAL = icon("repoLocal.png");
    public static final Icon REPO_GROUP_OPEN = icon("repoGroupOpen.png");
    public static final Icon REPO_GROUP_CLOSED = icon("repoGroupClosed.png");

    public static final Icon REPO_ARTIFACT = icon("repoArtifact.png");
    public static final Icon REPO_VERSION = icon("repoVersion.png");
    public static final Icon REPO_MSG = icon("repoMsg.png");

    public static final Icon REPO_FOLDER_OPEN = icon("repoFolderOpen.png");
    public static final Icon REPO_FOLDER_CLOSED = icon("repoFolderClosed.png");

    public static final Icon REPO_TYPE_ARCHIVE = icon("repoArtifactArchive.png");
    public static final Icon REPO_TYPE_POM = icon("repoArtifactPom.png");
    public static final Icon REPO_TYPE_LICENSE = icon("repoArtifactLicense.png");

    public static final Icon REPO_TYPE_JAR_OPEN = icon("repoTypeJarOpen.png");
    public static final Icon REPO_TYPE_JAR_CLOSED = icon("repoTypeJarClosed.png");
    public static final Icon REPO_TYPE_SRC_JAR_OPEN = icon("repoTypeSrcJarOpen.png");
    public static final Icon REPO_TYPE_SRC_JAR_CLOSED = icon("repoTypeSrcJarClosed.png");
    public static final Icon REPO_TYPE_JAVADOC_JAR_OPEN = icon("repoTypeJavadocJarOpen.png");
    public static final Icon REPO_TYPE_JAVADOC_JAR_CLOSED = icon("repoTypeJavadocJarClosed.png");
    public static final Icon REPO_TYPE_EJB_OPEN = icon("repoTypeEjbOpen.png");
    public static final Icon REPO_TYPE_EJB_CLOSED = icon("repoTypeEjbClosed.png");
    public static final Icon REPO_TYPE_WAR_OPEN = icon("repoTypeWarOpen.png");
    public static final Icon REPO_TYPE_WAR_CLOSED = icon("repoTypeWarClosed.png");
    public static final Icon REPO_TYPE_LICENSE_OPEN = icon("repoTypeLicenseOpen.png");
    public static final Icon REPO_TYPE_LICENSE_CLOSED = icon("repoTypeLicenseClosed.png");
    public static final Icon REPO_TYPE_PLUGIN_OPEN = icon("repoTypePluginOpen.png");
    public static final Icon REPO_TYPE_PLUGIN_CLOSED = icon("repoTypePluginClosed.png");
    public static final Icon REPO_TYPE_DIST_OPEN = icon("repoTypeDistOpen.png");
    public static final Icon REPO_TYPE_DIST_CLOSED = icon("repoTypeDistClosed.png");
    public static final Icon REPO_TYPE_POM_OPEN = icon("repoTypePomOpen.png");
    public static final Icon REPO_TYPE_POM_CLOSED = icon("repoTypePomClosed.png");
    public static final Icon REPO_TYPE_EAR_OPEN = icon("repoTypeEarOpen.png");
    public static final Icon REPO_TYPE_EAR_CLOSED = icon("repoTypeEarClosed.png");

    public static final Icon REPO_FILTER_LOCAL = icon("repoFilterLocal.png");
    public static final Icon REPO_FILTER_REMOTE = icon("repoFilterRemote.png");

    public static final Icon FIX_PROBLEMS = icon("fixProblems.png");
    public static final Icon PROBLEM = icon("problem.png");
    public static final Icon UNKNOWN_PROBLEM = icon("problemUnknown.png");
    public static final Icon WARNING = icon("warning.png");
    public static final Icon PROBLEM_FIXED = icon("problemOk.png");

    private static Icon icon(final String pLocation) {
        return IconLoader.getIcon(pLocation, Icons.class);
    }
}
