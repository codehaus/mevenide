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
package org.mevenide.netbeans.project;

import java.io.File;
import org.openide.ServiceType;
import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class MavenSettings extends SystemOption {
    public static final String PROP_TOP_GOALS = "topGoals"; // NOI18N
    public static final String PROP_SHOW_FAVOURITE_HINT = "showAddFavouriteHint"; //NOI18N
    
    public static final String PROP_OFFLINE = "offline"; //NOI18N
    public static final String PROP_NOBANNER = "nobanner"; //NOI18N
    
    
    private static final long serialVersionUID = -4857548488373547L;
    
    protected void initialize() {
        super.initialize();
        String[] defaultGoals = new String[] {
            "clean",
            "java",
            "jar",
            "dist",
            "javadoc"
        };
        setTopGoals(defaultGoals);
        setShowAddFavouriteHint(true);
    }
    
    public String displayName() {
        return NbBundle.getMessage(MavenSettings.class, "LBL_settings"); //NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx("org.mevenide.netbeans.projects"); // TODO helpset //NOI18N
    }
    
    public static MavenSettings getDefault() {
        return (MavenSettings) findObject(MavenSettings.class, true);
    }
    
    public static File getUserHome() {
        String home = System.getProperty("user.home");
        return new File(home);
    }
    
    /** will get the favourite maven goals, used in executor action
     * @return Value of property topGoals.
     *
     */
    public String[] getTopGoals() {
        return (String[])getProperty(PROP_TOP_GOALS);
    }
    
    /** will set the favourite maven goals, used in executor action
     * @param topGoals New value of property topGoals.
     *
     */
    public void setTopGoals(String[] topGoals) {
        putProperty(PROP_TOP_GOALS, topGoals, true);
    }
    
    /** Getter for property showAddFavouriteHint.
     * @return Value of property showAddFavouriteHint.
     *
     */
    public boolean isShowAddFavouriteHint() {
        Boolean obj = (Boolean)getProperty(PROP_SHOW_FAVOURITE_HINT);
        return obj == null ? true : obj.booleanValue();
    }
    
    /** Setter for property showAddFavouriteHint.
     * @param showAddFavouriteHint New value of property showAddFavouriteHint.
     *
     */
    public void setShowAddFavouriteHint(boolean showHint) {
        putProperty(PROP_SHOW_FAVOURITE_HINT, Boolean.valueOf(showHint), true);
    }
    
    public boolean isOffline() {
        Boolean obj = (Boolean)getProperty(PROP_OFFLINE);
        return obj == null ? true : obj.booleanValue();
    }
    
    public void setOffline(boolean offline) {
        putProperty(PROP_OFFLINE, Boolean.valueOf(offline), true);
    }
    
    public boolean isNoBanner() {
        Boolean obj = (Boolean)getProperty(PROP_NOBANNER);
        return obj == null ? true : obj.booleanValue();
    }
    
    public void setNoBanner(boolean nb) {
        putProperty(PROP_NOBANNER, Boolean.valueOf(nb), true);
    }
    
}
