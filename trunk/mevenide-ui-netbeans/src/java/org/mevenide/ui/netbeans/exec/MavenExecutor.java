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
package org.mevenide.ui.netbeans.exec;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.mevenide.ui.netbeans.MavenProjectCookie;
import org.mevenide.ui.netbeans.MavenSettings;
import org.openide.execution.NbProcessDescriptor;
import org.openide.execution.ProcessExecutor;
import org.openide.loaders.DataObject;
import org.openide.util.HelpCtx;
import org.openide.util.MapFormat;
import org.openide.util.Utilities;

public class MavenExecutor extends ProcessExecutor
{
    public static final String FORMAT_MAVEN_HOME = "MAVEN_HOME"; //NOI18N
    public static final String FORMAT_GOAL = "goal"; //NOI18N
    public static final String FORMAT_OFFLINE = "offline"; //NOI18N
    public static final String FORMAT_NOBANNER = "nobanner"; //NOI18N
    
    // -- default value
    private String goal = "dist"; //NOI18N 
    private boolean offline = false;
    private boolean nobanner = false;
    
    private static final long serialVersionUID = 7564737833872873L;
    
    public MavenExecutor()
    {
        super();
        String mavenExe = "bin/maven"; //NOI18N
        if (Utilities.isWindows()) {
            mavenExe = "bin/maven.bat"; //NOI18N
        }
        setExternalExecutor(new NbProcessDescriptor(
                        "{" + FORMAT_MAVEN_HOME + "}/" + mavenExe, //NOI18N
                        "{" + FORMAT_NOBANNER + "} {" + FORMAT_OFFLINE + "} {" + FORMAT_GOAL + "}", //NOI18N
                        "info"));
    }
    
    public HelpCtx getHelpCtx()
    {
        //TODO
        return new HelpCtx("org.mevenide.ui.netbeans"); //NOI18N
    }

    public String getGoal()
    {
        return goal;
    }
    
    public synchronized void setGoal(String nue)
    {
        String old = goal;
        goal = nue;
        firePropertyChange("goal", old, nue); // NOI18N
    }
    
    public boolean isOffline()
    {
        return offline;
    }
    
    public void setOffline(boolean offline)
    {
        boolean old = this.offline;
        this.offline = offline;
        firePropertyChange("offline", Boolean.valueOf(old), Boolean.valueOf(this.offline)); // NOI18N
    }
    
    public boolean isNoBanner()
    {
        return nobanner;
    }
    
    public void setNoBanner(boolean nb)
    {
        boolean old = this.nobanner;
        nobanner = nb;
        firePropertyChange("nobanner", Boolean.valueOf(old), Boolean.valueOf(nobanner)); // NOI18N
    }    
    
    protected Process createProcess(DataObject obj) throws IOException
    {
//        Process retValue;
        MavenProjectCookie cook = (MavenProjectCookie)obj.getCookie(MavenProjectCookie.class);
        File execDir = cook.getProjectFile();
        if (execDir != null)
        {
            execDir = execDir.getParentFile();
        } else {
            throw new IOException("The project file is not set");
        }
        HashMap formats = new HashMap(5);
        formats.put(FORMAT_GOAL, getGoal());
        formats.put(FORMAT_MAVEN_HOME, MavenSettings.getDefault().getMavenHome());
        formats.put(FORMAT_OFFLINE, isOffline() ? "--offline" : ""); //NOI18N
        formats.put(FORMAT_NOBANNER, isNoBanner() ? "--nobanner" : ""); //NOI18N
        return getExternalExecutor().exec(new MapFormat(formats), null, execDir);
//        retValue = super.createProcess(obj);
//        return retValue;
    }
}
