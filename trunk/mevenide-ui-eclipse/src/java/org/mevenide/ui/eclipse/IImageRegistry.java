/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.ui.eclipse;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public interface IImageRegistry {
    public static final String CLEAR_VALIDATE_TOOL = "etool16/clr_validate.gif"; //$NON-NLS-1$
    public static final String VALIDATE_TOOL = "etool16/validate_pom.gif"; //$NON-NLS-1$
    public static final String REFRESH_TOOL = "etool16/refresh.gif"; //$NON-NLS-1$
    public static final String COPY_TOOL = "etool16/copy.gif"; //$NON-NLS-1$
    
    public static final String SYNC_CONFLICT = "elcl16/synch_conflicting.gif"; //$NON-NLS-1$
    public static final String SYNC_OUTGOING = "elcl16/synch_outgoing.gif"; //$NON-NLS-1$
    public static final String SYNC_INCOMING = "elcl16/synch_incoming.gif"; //$NON-NLS-1$
    public static final String SYNC_PROPERTIES = "elcl16/synch_wprop.gif"; //$NON-NLS-1$
    public static final String FILTER_GOALS = "elcl16/goals_filter.gif"; //$NON-NLS-1$
    public static final String CUSTOM_FILTER = "elcl16/regex_filter.gif"; //$NON-NLS-1$
    public static final String OPEN_FILTER_DIALOG = "elcl16/filter_def.gif"; //$NON-NLS-1$
    public static final String OFFLINE = "elcl16/run_offline.gif"; //$NON-NLS-1$
    public static final String RUN_GOAL_ENABLED = "elcl16/run_goal.gif"; //$NON-NLS-1$
    public static final String RUN_GOAL_DISABLED = "dlcl16/run_goal.gif"; //$NON-NLS-1$
    public static final String ARGUMENTS_TAB_ICON = "eview16/variable_tab.gif"; //$NON-NLS-1$
    public static final String PATTERN_SEARCH_ICON = "eview16/patterns_srch.gif"; //$NON-NLS-1$
    
    public static final String MAVEN_PROJECT_WIZ = "wizban/newmprj_wiz.gif"; //$NON-NLS-1$
    public static final String MAVEN_POM_WIZ = "wizban/newmfile_wiz.gif"; //$NON-NLS-1$
    public static final String POM_CHOICE_WIZ = "wizban/mfile_choose.gif"; //$NON-NLS-1$
    public static final String EXT_TOOLS_WIZ = "wizban/ext_tools_wiz.gif"; //$NON-NLS-1$
    
    public static final String XML_ATTR_OBJ = "obj16/xmlattr_obj.gif"; //$NON-NLS-1$
    public static final String XML_TAG_OBJ = "obj16/xmltag_obj.gif"; //$NON-NLS-1$
    public static final String XML_END_TAG_OBJ = "obj16/xmlendtag_obj.gif"; //$NON-NLS-1$
    public static final String GOAL_OBJ = "obj16/mgoal_obj.gif"; //$NON-NLS-1$
    public static final String PLUGIN_OBJ = "obj16/mplugin_obj.gif"; //$NON-NLS-1$
    public static final String DEPENDENCY_OBJ = "obj16/mdep_obj.gif"; //$NON-NLS-1$
    public static final String PROPERTY_OBJ = "obj16/mprop_attr.gif"; //$NON-NLS-1$
    public static final String MAVEN_PROJECT_OBJ = "obj16/mprj_obj.gif"; //$NON-NLS-1$
    public static final String MAVEN_POM_OBJ = "obj16/mfile_obj.gif"; //$NON-NLS-1$
    public static final String FOLDER_UNDEF_OBJ = "obj16/mundefined_obj.gif"; //$NON-NLS-1$
    public static final String FOLDER_SRC_OBJ = "obj16/msources_obj.gif"; //$NON-NLS-1$
    public static final String FOLDER_TEST_OBJ = "obj16/mutests_obj.gif"; //$NON-NLS-1$
    public static final String FOLDER_ASPECTS_OBJ = "obj16/maspects_obj.gif"; //$NON-NLS-1$
    public static final String FOLDER_OUTPUT_OBJ = "obj16/moutput_obj.gif"; //$NON-NLS-1$
    public static final String FOLDER_RESOURCE_OBJ = "obj16/mresources_obj.gif"; //$NON-NLS-1$
    public static final String EXCLUSION_OBJ = "obj16/mdir_excl_attr.gif"; //$NON-NLS-1$
    
    public static final String NEW_REPO_DEFINITION = "etool16/newmrepo.gif"; //$NON-NLS-1$
    public static final String REMOVE_REPO_DEFINITION = "etool16/delmrepo.gif"; //$NON-NLS-1$
    public static final String MAVEN_REPO = "obj16/maven_repo.gif"; //$NON-NLS-1$
    public static final String MAVEN_REPO_GROUP = "obj16/mrepo_group.gif"; //$NON-NLS-1$
    public static final String MAVEN_REPO_TYPE = "obj16/mtype.gif"; //$NON-NLS-1$
    public static final String SEARCH_BUTTON_ICON = "etool16/search_8x8.gif"; //$NON-NLS-1$
    
    public static final String NEW_MAVEN_REPO_WIZ = "wizban/newmrepo.gif"; //$NON-NLS-1$
    
    public static final String FILE_OBJ = "obj16/file_obj.gif"; //$NON-NLS-1$
    
    static final String[] IMAGE_KEYS = new String[] {
            CLEAR_VALIDATE_TOOL,
            VALIDATE_TOOL,
            REFRESH_TOOL,
            SYNC_CONFLICT,
            SYNC_OUTGOING,
            SYNC_INCOMING,
            SYNC_PROPERTIES,
            FILTER_GOALS,
            CUSTOM_FILTER,
            OPEN_FILTER_DIALOG,
            OFFLINE,
            RUN_GOAL_ENABLED,
            RUN_GOAL_DISABLED,
            ARGUMENTS_TAB_ICON,            
            MAVEN_PROJECT_WIZ,
            MAVEN_POM_WIZ,
            POM_CHOICE_WIZ,
            XML_ATTR_OBJ,
            XML_TAG_OBJ,
            XML_END_TAG_OBJ,
            GOAL_OBJ,
            PLUGIN_OBJ,
            NEW_REPO_DEFINITION,
            REMOVE_REPO_DEFINITION,
            DEPENDENCY_OBJ,
            PROPERTY_OBJ,
            MAVEN_PROJECT_OBJ,
            MAVEN_POM_OBJ,
            FOLDER_UNDEF_OBJ,
            FOLDER_SRC_OBJ,
            FOLDER_TEST_OBJ,
            FOLDER_ASPECTS_OBJ,
            FOLDER_OUTPUT_OBJ,
            FOLDER_RESOURCE_OBJ,
            EXCLUSION_OBJ,
            COPY_TOOL,
            EXT_TOOLS_WIZ,
            PATTERN_SEARCH_ICON,
            MAVEN_REPO,
            MAVEN_REPO_GROUP,
            SEARCH_BUTTON_ICON,
            MAVEN_REPO_TYPE,
            FILE_OBJ,
            NEW_MAVEN_REPO_WIZ,
    };
}
