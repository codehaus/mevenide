/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Milos Kleint (ca206216@tiscali.cz).  All rights
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
package org.mevenide.ui.netbeans;

import java.io.File;
import org.mevenide.ui.netbeans.exec.MavenExecutor;
import org.mevenide.Environment;
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
    
    private static final long serialVersionUID = -4857548488373547L;
    
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
    
    public File getMavenHome()
    {
        File f = (File)getProperty(PROP_MAVEN_HOME);
        if (f == null)
        {
            String home = Environment.getMavenHome();
            if (home != null)
            {
                f = new File(home);
            } else {
                //DEBUG
//                System.out.println("maven home env. variable not set.");
            }
//            f = InstalledFileLocator.getDefault().locate("maven", "org.mevenide.ui.netbeans", false); // NOI18N
//            putProperty(PROP_MAVEN_HOME, f, false);
        } else {
                //DEBUG
//            System.out.println("has a mavenhome value defined.");
        }
        return f;
    }
    
    public void setMavenHome(File f)
    {
        putProperty(PROP_MAVEN_HOME, f, true);
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
        putProperty(PROP_TOP_GOALS, topGoals);
    }
    
}
