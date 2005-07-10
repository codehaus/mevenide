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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.maven.project.Dependency;
import org.mevenide.properties.IPropertyLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;
import org.mevenide.properties.IPropertyResolver;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.SubprojectProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.apache.tools.ant.DirectoryScanner;



/**
 * finds subprojects (projects this one depends on) that are locally available
 * and can be build as one unit. Uses maven multiproject infrastructure. (maven.multiproject.includes)
 * TODO: maybe could also need non-maven style of dependency linking.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class SubprojectProviderImpl implements SubprojectProvider {
    private static final Log logger = LogFactory.getLog(SubprojectProviderImpl.class);
    
    private static final String MULTIPROJECT_INCLUDES = "maven.multiproject.includes"; //NOI18N
    private static final String MULTIPROJECT_EXCLUDES = "maven.multiproject.excludes"; //NOI18N
    private static final String MULTIPROJECT_BASEDIR = "maven.multiproject.basedir"; //NOI18N
    
    private MavenProject project;
    /** Creates a new instance of SubprojectProviderImpl */
    public SubprojectProviderImpl(MavenProject proj) {
        project = proj;
    }
    
    public Set getSubprojects() {
        logger.debug("getSubProjects()");
        String includes = project.getPropertyResolver().getResolvedValue(MULTIPROJECT_INCLUDES);
        String excludes = project.getPropertyResolver().getResolvedValue(MULTIPROJECT_EXCLUDES);
        String basedir = project.getPropertyResolver().getResolvedValue(MULTIPROJECT_BASEDIR);
        int includesLocation = project.getPropertyLocator().getPropertyLocation(MULTIPROJECT_INCLUDES);
        if (includes != null && includesLocation > IPropertyLocator.LOCATION_DEFAULTS) {
            Set toReturn = new HashSet();
            File basefile = FileUtil.normalizeFile(new File(basedir));
            if (basefile.exists()) {
                DirectoryScanner scanner = new DirectoryScanner();
                scanner.setBasedir(basefile);
                if (excludes != null) {
                    String[] exc = StringUtils.split(excludes, ",");
                    scanner.setExcludes(exc);
                }
                String[] inc = StringUtils.split(includes, ",");
                scanner.setIncludes(inc);
                scanner.scan();
                String[] results = scanner.getIncludedFiles();
                for (int i = 0; i < results.length; i++) {
                    Project proj = processOneSubproject(basefile, results[i]);
                    if (proj != null && proj instanceof MavenProject) {
						//#MEVENIDE-287
                        MavenProject prj = (MavenProject)proj;
                        IPropertyResolver prjRes = prj.getPropertyResolver();
                        String version = prjRes.resolveString(prj.getOriginalMavenProject().getCurrentVersion());
                        String id = prjRes.resolveString(prj.getOriginalMavenProject().getId());
                        List deps = project.getOriginalMavenProject().getDependencies();
                        Dependency dp = null;
                        if (deps != null) {
                            Iterator it = deps.iterator();
                            while (it.hasNext()) {
                                Dependency xx = (Dependency)it.next();
                                if (id.equals(xx.getId())) {
                                    dp = xx;
                                    break;
                                }
                                
                            }
                        }
                        if (dp != null) {
                            String dpVersion = project.getPropertyResolver().resolveString(dp.getVersion());
                            if (dpVersion != null && (dpVersion.equals(version) || "SNAPSHOT".equals(dpVersion))) {
                                toReturn.add(proj);
                            }
                        }
                    }
                }
            } else {
                logger.debug("basefile not found=" + basedir);
            }
            return toReturn;
        }
        return Collections.EMPTY_SET;
    }
    
    private Project processOneSubproject(File basefile, String relPath) {
        File projectFile = FileUtil.normalizeFile(new File(basefile.getAbsolutePath() + File.separator + relPath));
        if (projectFile.exists()) {
            FileObject projectDir = FileUtil.toFileObject(projectFile);
            if (projectDir != null) {
                projectDir = projectDir.getParent();
                if (project.getProjectDirectory().equals(projectDir)) {
                    // don't include itself into the list of subprojects..
                    return null;
                }
                if (ProjectManager.getDefault().isProject(projectDir)) {
                    try {
                        Project proj = ProjectManager.getDefault().findProject(projectDir);
                        return proj;
                    } catch (IOException exc) {
                        logger.debug("IO exc. while loading project", exc);
                    }
                }
            } else {
                // HUH?
                logger.debug("fileobject not found=" + relPath + " in basedir=" + basefile);
            }
            
        } else {
            logger.debug("project file not found=" + relPath + " in basedir=" + basefile);
        }
        return null;
    }
    
    public void addChangeListener(javax.swing.event.ChangeListener changeListener) {
    }
    
    public void removeChangeListener(javax.swing.event.ChangeListener changeListener) {
    }
    
}
