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

package org.mevenide.netbeans.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class MavenSettings extends SystemOption {
    public static final String PROP_TOP_GOALS = "topGoals"; // NOI18N
    public static final String PROP_REPOSITORIES = "repositories"; // NOI18N
    public static final String PROP_DEP_PROPS = "dependencyProperties";
    public static final String PROP_SHOW_FAVOURITE_HINT = "showAddFavouriteHint"; //NOI18N
    
    public static final String PROP_OFFLINE = "offline"; //NOI18N
    public static final String PROP_NOBANNER = "nobanner"; //NOI18N
    public static final String PROP_DEBUG = "debug"; //NOI18N
    public static final String PROP_EXCEPTIONS = "exceptions"; //NOI18N
    public static final String PROP_NONVERBOSE = "nonverbose"; //NOI18N
    public static final String PROP_DOWNLOADER = "downloader"; //NOI18N
    
    private static final long serialVersionUID = -4857548488373547L;
    
    protected void initialize() {
        super.initialize();
        String[] defaultGoals = new String[] {
            "pmd:report",
            "checkstyle:report",
            "dist"
        };
        setTopGoals(defaultGoals);
        setShowAddFavouriteHint(true);
        setOffline(false);
        setNoBanner(false);
        setNonverbose(false);
        setExceptions(false);
        setDebug(false);
        setDownloader("silent");
        setRepositories(new String[] {
            "http://www.ibiblio.org/maven/",
            "http://cvs.apache.org/repository/",
            "http://maven-plugins.sourceforge.net/maven/",
            "http://seasar.sourceforge.jp/maven/",
            "http://spring-ext.sourceforge.jp/maven/",
            "http://ibiblio.org/geotools",
            "http://www.codeczar.com/maven/"
        });
        setDependencyProperties(new String[] {
            "war.bundle",
            "war.target.path",
            "netbeans.module.ext",
            "netbeans.module.dep",
            "ear.bundle",
            "ear.bundle.dir",
            "ear.bundle.name",
            "ear.module",
            "ejb.bundle",
            "ejb.manifest.classpath"
        });
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
    
//    public static File getUserHome() {
//        String home = System.getProperty("user.home");
//        return new File(home);
//    }
    
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
    
    public boolean isDebug() {
        Boolean obj = (Boolean)getProperty(PROP_DEBUG);
        return obj == null ? true : obj.booleanValue();
    }
    
    public void setDebug(boolean debug) {
        putProperty(PROP_DEBUG, Boolean.valueOf(debug), true);
    }
    
    public boolean isExceptions() {
        Boolean obj = (Boolean)getProperty(PROP_EXCEPTIONS);
        return obj == null ? true : obj.booleanValue();
    }
    
    public void setExceptions(boolean exc) {
        putProperty(PROP_EXCEPTIONS, Boolean.valueOf(exc), true);
    }
    
    public boolean isNonverbose() {
        Boolean obj = (Boolean)getProperty(PROP_NONVERBOSE);
        return obj == null ? true : obj.booleanValue();
    }
    
    public void setNonverbose(boolean nonverbose) {
        putProperty(PROP_NONVERBOSE, Boolean.valueOf(nonverbose), true);
    }
    
   public String getDownloader() {
        return (String)getProperty(PROP_DOWNLOADER);
    }
    
    public void setDownloader(String downloader) {
        putProperty(PROP_DOWNLOADER, downloader, true);
    }    
    
    public String[] getRepositories() {
        return (String[])getProperty(PROP_REPOSITORIES);
    }
    
    public void setRepositories(String[] repos) {
        putProperty(PROP_REPOSITORIES, repos, true);
    }    
    
    public String[] getDependencyProperties() {
        return (String[])getProperty(PROP_DEP_PROPS);
    }
    
    public void setDependencyProperties(String[] repos) {
        putProperty(PROP_DEP_PROPS, repos, true);
    }
    
    /**
     * will add unknown dependency properties names to the list.
     */
    public void checkDependencyProperties(Collection newones) {
        List lst = new ArrayList(Arrays.asList(getDependencyProperties()));
        Iterator it = newones.iterator();
        boolean updated = false;
        while (it.hasNext()) {
            Object key = it.next();
            if (!lst.contains(key)) {
                updated = true;
                lst.add(key);
            }
        }
        if (updated) {
            setDependencyProperties((String[])lst.toArray(new String[lst.size()]));
        }
    }
    
}
