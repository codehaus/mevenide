/* ==========================================================================
 * Copyright 2007 Mevenide Team
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
package org.codehaus.mevenide.netbeans.queries;

import java.io.File;
import java.util.Collection;
import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.configurations.M2ConfigProvider;
import org.codehaus.mevenide.netbeans.configurations.M2Configuration;
import org.netbeans.api.queries.SharabilityQuery;
import org.netbeans.spi.queries.SharabilityQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class MavenSharabilityQueryImpl implements SharabilityQueryImplementation {
    
    private NbMavenProject project;
    /** Creates a new instance of MavenSharabilityQueryImpl */
    public MavenSharabilityQueryImpl(NbMavenProject proj) {
        project = proj;
    }
    
    private Boolean checkShare(File file) {
        File basedir = FileUtil.toFile(project.getProjectDirectory());
        // is this condition necessary?
        if (!file.getAbsolutePath().startsWith(basedir.getAbsolutePath())) {
            return null;
        }
        if (basedir.equals(file.getParentFile()) && "nbproject".equals(file.getName())) { //NOI18N
            // screw the netbeans profiler directory creation.
            // #98662
            return false;
        }
        MavenProject proj = project.getOriginalMavenProject();
        Build build = proj.getBuild();
        if (build != null && build.getDirectory() != null) {
            File target = new File(build.getDirectory());
            if (target.equals(file) || file.getAbsolutePath().startsWith(target.getAbsolutePath())) {
                return false;
            }
        }
        if (file.equals(new File(basedir, "profiles.xml"))) { //NOI18N
            //profiles.xml are not meant to be put in version control.
            return false;
        }
        if (file.getName().startsWith("nbactions") && file.getParentFile().equals(basedir)) { //NOI18N
            //non shared custom configurations shall not be added to version control.
            M2ConfigProvider configs = project.getLookup().lookup(M2ConfigProvider.class);
            if (configs != null) {
                Collection<M2Configuration> col = configs.getNonSharedConfigurations();
                for (M2Configuration conf : col) {
                    if (file.getName().equals(M2Configuration.getFileNameExt(conf.getId()))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public int getSharability(File file) {
        //#119541 for the project's root, return MIXED right away.
        FileObject fo = FileUtil.toFileObject(file);
        if (fo != null && fo.equals(project.getProjectDirectory())) {
            return SharabilityQuery.MIXED;
        }
        Boolean check = checkShare(file);
        if (check == null) {
            return SharabilityQuery.UNKNOWN;
        }
        if (Boolean.TRUE.equals(check)) {
            if (file.isDirectory()) {
                //#119541 let's play safe  here and always return MIXED for directories.
                //consider this setup:
                // project root
                //     -- modules
                //            -- subproject1
                //            -- subproject2
                // The "modules" folder itself doesn't contain a project, therefore belongs to root project
                // however it cannot be marked as SHARABLE because the subproject1+2 folder would be added automatically then.
                
                return SharabilityQuery.MIXED;
            }
            return SharabilityQuery.SHARABLE;
        }
        return SharabilityQuery.NOT_SHARABLE;
    }
    
}
