/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
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
