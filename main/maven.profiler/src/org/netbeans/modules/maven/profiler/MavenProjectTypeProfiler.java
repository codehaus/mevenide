/*
 * Copyright 2007-2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
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
