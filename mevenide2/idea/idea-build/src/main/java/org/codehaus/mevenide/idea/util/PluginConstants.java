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



package org.codehaus.mevenide.idea.util;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class PluginConstants {

    /**
     * Field description
     */
    public static final String PLUGIN_PROJECT_DISPLAY_NAME = "Build";

    /**
     * Field description
     */
    public static final String PLUGIN_GLOBAL_DISPLAY_NAME = "Maven-2 Integration";

    /**
     * Field description
     */
    public static final String APPLICATION_COMPONENT_NAME = "MavenBuildApplicationComponent";

    /**
     * Field description
     */
    public static final String POM_FILE_NAME = "pom.xml";

    /**
     * Field description
     */
    public static final String TREE_ROOT_NODE_TITLE = "Available POMs";

    /**
     * Field description
     */
    public static final String TOOLBAR_ACTION_COMMAND_OPEN_SETTINGS = "Open Settings";

    /**
     * Field description
     */
    public static final String PROJECT_COMPONENT_NAME = "MavenBuildProjectComponent";

    /**
     * Field description
     */
    public static final String PLUGIN_CONFIG_FILENAME = "/plugin-config.xml";

    /**
     * Field description
     */
    public static final String OUTPUT_TOOL_WINDOW_ID = "Mevenide2 Build Output";

    /**
     * Field description
     */
    public static final String NODE_POMTREE_PHASES = "Phases";

    /**
     * Field description
     */
    public static final String MAVEN_POM_FILENAME = "pom.xml";

    /**
     * Field description
     */
    public static final String MAVEN_PLUGIN_DESCRIPTOR = "META-INF/maven/plugin.xml";

    /**
     * Field description
     */
    public static final String ICON_SORT_ASC = "/objectBrowser/sorted.png";

    /**
     * Field description
     */
    public static final String ICON_RUN = "/actions/execute.png";

    /**
     * Field description
     */
    public static final String ICON_REMOVE_POM = "/general/remove.png";

    /**
     * Field description
     */
    public static final String ICON_FILTER = "/filter.png";

    /**
     * Field description
     */
    public static final String ICON_FILTER_APPLIED = "/filter-applied.png";

    /**
     * Field description
     */
    public static final String ICON_SHOW_MAVEN_OPTIONS = "/actions/quickList.png";

    /**
     * Field description
     */
    public static final String ICON_REMOVE_PLUGIN = "/list-remove.png";

    /**
     * Field description
     */
    public static final String ICON_POM_SMALL = "/modulesNode.png";

    /**
     * Field description
     */
    public static final String ICON_APPLICATION_SMALL = "/applications-system.png";

    /**
     * Field description
     */
    public static final String ICON_APPLICATION_SETTINGS = "/general/applicationSettings.png";

    /**
     * Field description
     */
    public static final String ICON_APPLICATION_EMBLEM_SMALL = "/emblem-system.png";

    /**
     * Field description
     */
    public static final String ICON_APPLICATION_BIG = "/applications-system-22.png";

    /**
     * Field description
     */
    public static final String ICON_ADD_POM = "/general/add.png";

    /**
     * Field description
     */
    public static final String ICON_RERUN = "/actions/refreshUsages.png";

    /**
     * Field description
     */
    public static final String ICON_STOP = "/actions/suspend.png";

    /**
     * Field description
     */
    public static final String ICON_CLOSE = "/actions/cancel.png";

    /**
     * Field description
     */
    public static final String ICON_PAUSE = "/actions/pause.png";

    /**
     * Field description
     */
    public static final String ICON_ADD_PLUGIN = "/list-add.png";

    /**
     * Field description
     */
    public static final String ACTION_COMMAND_SORT_ASC = "Sort ascending";

    /**
     * Field description
     */
    public static final String ACTION_COMMAND_RUN_GOALS = "Run selected goal(s)";

    /**
     * Field description
     */
    public static final String ACTION_COMMAND_REMOVE_POM = "Remove POM";

    /**
     * Field description
     */
    public static final String ACTION_COMMAND_OPEN_POM = "Open POM";

    /**
     * Field description
     */
    public static final String ACTION_COMMAND_PAUSE_OUTPUT = "Pause Output";

    /**
     * Field description
     */
    public static final String ACTION_COMMAND_CONTINUE_OUTPUT = "Continue Output";

    /**
     * Field description
     */
    public static final String ACTION_COMMAND_RERUN_MAVEN = "Rerun Mevenide2";

    /**
     * Field description
     */
    public static final String ACTION_COMMAND_CLOSE_OUTPUT_PANEL = "Close Output";

    /**
     * Field description
     */
    public static final String ACTION_COMMAND_REMOVE_PLUGIN = "Remove plugin";

    /**
     * Field description
     */
    public static final String ACTION_COMMAND_ADD_POM = "Add POM";

    /**
     * Field description
     */
    public static final String ACTION_COMMAND_STOP_PROCESS = "Stop Mevenide2 Build";

    /**
     * Field description
     */
    public static final String ACTION_COMMAND_ADD_PLUGIN = "Add plugin";

    /**
     * Field description
     */
    public static final String ACTION_COMMAND_SET_MAVEN_HOME = "Set Maven Home";

    /**
     * Field description
     */
    public static final String ACTION_COMMAND_SET_ALTERNATE_SETTINGS = "Set Alternate Settings";

    /**
     * Field description
     */
    public static final String ACTION_COMMAND_SHOW_MAVEN_OPTIONS = "Show Maven Options";

    /**
     * Field description
     */
    public static final String ACTION_COMMAND_FILTER = "Filter Phases";

    /**
     * Field description
     */
    public static final String BUILD_TOOL_WINDOW_ID = "Mevenide2 Build";

    /**
     * Field description
     */
    public static final String CONFIG_DIALOG_LABEL_SETTINGS_FILE = "Settings File";
    public static final String CONFIG_DIALOG_LABEL_MAVEN_HOME_DIRECTORY = "Maven-2 Home Directory";
    public static final String CONFIG_DIALOG_LABEL_VM_OPTIONS = "VM Parameters";
    public static final String CONFIG_DIALOG_LABEL_MAVEN_GLOBAL_COMMAND_LINE_ARGUMENTS =
        "Maven-2 command line arguments";
    public static final String CONFIG_DIALOG_TAB_MAVEN_SETUP = "Maven Setup";
    public static final String CONFIG_DIALOG_TAB_MAVEN_OPTIONS = "Maven Options";
    public static final String CONFIG_DIALOG_TAB_GENERAL = "General";
    public static final String CONFIG_DIALOG_CHECKBOX_LABEL_SCAN_POMS = "Scan for existing POMs on project startup";
    public static final String CONFIG_DIALOG_CHECKBOX_LABEL_USE_MAVEN_EMBEDDER = "Use Maven Embedder (Experimental)";
    public static final String CONFIG_ELEMENT_MAVEN_EXECUTABLE = "mavenExecutable";
    public static final String CONFIG_ELEMENT_MAVEN_COMMAND_LINE = "mavenCommandLineParams";
    public static final String CONFIG_ELEMENT_VM_OPTIONS = "vmOptions";
    public static final String CONFIG_ELEMENT_MAVEN_REPOSITORY = "mavenRepository";
    public static final String CONFIG_ELEMENT_SCAN_FOR_POMS = "scanForExistingPoms";
    public static final String CONFIG_ELEMENT_USE_MAVEN_EMBEDDER = "useMavenEmbedder";
    public static final String CONFIG_ELEMENT_USE_FILTER = "useFilter";
    public static final String ACTION_COMMAND_EDIT_MAVEN_COMMAND_LINE = "EditMavenCommandLine";
    public static final String ACTION_COMMAND_FILTER_RELEASE = "Release Filter";
}
