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
package org.mevenide.ui.eclipse.sync.model;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.MavenUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

/**
 * 
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 *
 */
public abstract class SourceFolder extends ArtifactWrapper {
   private static Log log = LogFactory.getLog(SourceFolder.class);


   protected IClasspathEntry newSourceEntry(String path, IProject project) throws Exception {
         String basedir = project.getLocation().toOSString();
       log.debug("basedir = " + basedir);
             if ( new File(path).exists() ) {
           path = MavenUtils.makeRelativePath(new File(basedir), path);
       }

       if ( !project.getFolder(path).exists() ) {
           createFolder(path, project);
       }
       IClasspathEntry srcEntry = JavaCore.newSourceEntry(new Path("/" + project.getName() + "/" + path));
       return srcEntry;
   }

   private void createFolder(String path, IProject project) throws Exception {
       log.debug("creating src folder : " + path);
       IContainer container = project.getFolder(path).getParent();
       if ( !container.exists() ) {
           createFolder(container.getProjectRelativePath().toString(), project);
       }
       project.getFolder(path).create(true, true, null);
   }
}



