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
package org.mevenide.ui.netbeans;

import java.io.File;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.ui.netbeans.exec.MavenExecutor;
import org.openide.ServiceType;
import org.openide.execution.Executor;
import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class MavenSettings extends SystemOption
{
    public static final String PROP_EXECUTOR = "executor"; // NOI18N
    public static final String PROP_MAVEN_HOME = "MAVEN_HOME"; // NOI18N
    public static final String PROP_TOP_GOALS = "topGoals"; // NOI18N
    public static final String PROP_SHOW_FAVOURITE_HINT = "showAddFavouriteHint"; //NOI18N    
    public static final String PROP_CURRENT_PROJECT = "currentProject"; //NOI18N
    
    private static final long serialVersionUID = -4857548488373547L;
    
    /** Holds value of property currentProject. */
    private File currentProject;
    
    protected void initialize()
    {
        super.initialize();
        Executor exec = Executor.find("org-mevenide-ui-netbeans-exec-MavenExecutor");
        if (exec == null)
        {
            exec = new MavenExecutor();
        }
        setExecutor(exec);
        String[] defaultGoals = new String[]
        {
            "clean",
            "java",
            "jar",
            "dist",
            "javadoc"
        };
        setTopGoals(defaultGoals);
        setShowAddFavouriteHint(true);
    }
    
    public String displayName()
    {
        return NbBundle.getMessage(MavenSettings.class, "LBL_settings"); //NOI18N
    }
    
    public HelpCtx getHelpCtx()
    {
        return new HelpCtx("org.mevenide.ui.netbeans"); // TODO helpset //NOI18N
    }
    
    public static MavenSettings getDefault()
    {
        return (MavenSettings) findObject(MavenSettings.class, true);
    }
    
    /** sets executor */
    public void setExecutor(Executor ct)
    {
        putProperty(PROP_EXECUTOR, new ServiceType.Handle(ct), true);
    }
    
    public Executor getExecutor()
    {
        ServiceType.Handle handle =  (ServiceType.Handle)getProperty(PROP_EXECUTOR);
        return (Executor)handle.getServiceType();
    }
    
    /**
     * @deprecated remove this settings thing and use the ConfigUtils directly..
     */
    public File getMavenHome()
    {
        return new File(ConfigUtils.getDefaultLocationFinder().getMavenHome());
//        File f = (File)getProperty(PROP_MAVEN_HOME);
//        if (f == null)
//        {
//            String home = Environment.getMavenHome();
//            if (home != null)
//            {
//                f = new File(home);
//            } else {
//                //DEBUG
////                System.out.println("maven home env. variable not set.");
//            }
////            f = InstalledFileLocator.getDefault().locate("maven", "org.mevenide.ui.netbeans", false); // NOI18N
////            putProperty(PROP_MAVEN_HOME, f, false);
//        } else {
//                //DEBUG
////            System.out.println("has a mavenhome value defined.");
//        }
//        return f;
    }
    
    public void setMavenHome(File f)
    {
//        putProperty(PROP_MAVEN_HOME, f, true);
    }
    
    public static File getUserHome()
    {
         String home = System.getProperty("user.home");
         return new File(home);
    }
    
    /** will get the favourite maven goals, used in executor action
     * @return Value of property topGoals.
     *
     */
    public String[] getTopGoals()
    {
        return (String[])getProperty(PROP_TOP_GOALS);
    }
    
    /** will set the favourite maven goals, used in executor action
     * @param topGoals New value of property topGoals.
     *
     */
    public void setTopGoals(String[] topGoals)
    {
        putProperty(PROP_TOP_GOALS, topGoals, true);
    }
    
    /** Getter for property showAddFavouriteHint.
     * @return Value of property showAddFavouriteHint.
     *
     */
    public boolean isShowAddFavouriteHint()
    {
        Boolean obj = (Boolean)getProperty(PROP_SHOW_FAVOURITE_HINT);
        return obj == null ? true : obj.booleanValue();
    }
    
    /** Setter for property showAddFavouriteHint.
     * @param showAddFavouriteHint New value of property showAddFavouriteHint.
     *
     */
    public void setShowAddFavouriteHint(boolean showHint)
    {
        putProperty(PROP_SHOW_FAVOURITE_HINT, Boolean.valueOf(showHint), true);
    }
    
    /** Getter for property currentProject.
     * @return Value of property currentProject.
     *
     */
    public File getCurrentProject()
    {
        return (File)getProperty(PROP_CURRENT_PROJECT);
    }
    
    /** Setter for property currentProject.
     * @param currentProject New value of property currentProject.
     *
     */
    public void setCurrentProject(File currentProject)
    {
        putProperty(PROP_CURRENT_PROJECT, currentProject, true);
    }
    
}
