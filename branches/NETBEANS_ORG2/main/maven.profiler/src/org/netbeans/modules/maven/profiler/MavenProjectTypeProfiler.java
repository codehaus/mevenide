/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.profiler;

import java.util.Properties;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.project.Project;
import org.netbeans.lib.profiler.common.ProfilingSettings;
import org.netbeans.lib.profiler.common.SessionSettings;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.profiler.AbstractProjectTypeProfiler;
import org.netbeans.modules.profiler.NetBeansProfiler;
import org.netbeans.modules.profiler.utils.ProjectUtilities;
import org.openide.filesystems.FileObject;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Jiri Sedlacek
 */
public class MavenProjectTypeProfiler extends AbstractProjectTypeProfiler {
    
    private ProfilingSettings lastProfilingSettings;
    private SessionSettings lastSessionSettings;
    private Properties lastSessionProperties;
    
    
    ProfilingSettings getLastProfilingSettings() {
        return lastProfilingSettings;
    }
    
    SessionSettings getLastSessionSettings() {
        return lastSessionSettings;
    }
    
    Properties getLastSessionProperties() {
        return lastSessionProperties;
    }
    
    
    public boolean isFileObjectSupported(Project project, FileObject fo) {
        // Profile File, Profile Test not supported in this version
        return false;
    }

    public String getProfilerTargetName(Project project, FileObject buildScript, int type, FileObject profiledClassFile) {
        throw new UnsupportedOperationException("Not supported"); // NOI18N
    }
    
    public JavaPlatform getProjectJavaPlatform(Project project) {
        return JavaPlatform.getDefault();
    }

    public boolean isProfilingSupported(Project project) {
        NbMavenProject mproject = project.getLookup().lookup(NbMavenProject.class);
        return mproject == null ? false :
            NbMavenProject.TYPE_JAR.equals(mproject.getPackagingType());
    }

    public boolean checkProjectCanBeProfiled(Project project, FileObject profiledClassFile) {
        return true;
    }

    public boolean checkProjectIsModifiedForProfiler(Project project) {
        return true;
    }
    
    public boolean startProfilingSession(final Project project, final FileObject profiledClassFile, final boolean isTest, final Properties properties) {
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() { startMaven(project, profiledClassFile, isTest, properties); }
        });
        
        return true;
    }
    
    private void startMaven(Project project, FileObject profiledClassFile, boolean isTest, Properties properties) {
        lastProfilingSettings = new ProfilingSettings();
        lastSessionSettings = new SessionSettings();
        lastSessionProperties = new Properties(properties);
        
        lastProfilingSettings.load(properties);
        lastSessionSettings.load(properties);
        
        NetBeansProfiler.getDefaultNB().setProfiledProject(project, profiledClassFile);
        
        if (profiledClassFile != null) ProjectUtilities.invokeAction(project, "profile-single"); //NOI18N
        else ProjectUtilities.invokeAction(project, isTest ? "profile-tests" : "profile"); //NOI18N
    }

}
