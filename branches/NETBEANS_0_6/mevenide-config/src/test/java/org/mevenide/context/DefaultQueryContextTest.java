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
package org.mevenide.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import junit.framework.TestCase;
import org.apache.maven.project.Project;
import org.jdom.Element;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class DefaultQueryContextTest extends TestCase {
    
    protected File userHomeDir;
    protected File projectDir;
    protected File parentProjectDir;
    protected String originalUserHome;
    private ProjectContext context;
    /** Creates a new instance of DefaultsResolverTest */
    public DefaultQueryContextTest() {
        originalUserHome = System.getProperty("user.home"); //NOI18N
        userHomeDir  = new File(originalUserHome, ".mevenide_test");
    }
    
    protected void setUp() throws Exception {
        System.setProperty("user.home", userHomeDir.getAbsolutePath());
        if (!userHomeDir.exists()) {
            userHomeDir.mkdir();
        }
        File userprop = new File(DefaultQueryContextTest.class.getResource("/org/mevenide/properties/user.properties").getFile());
        File copyTo = new File(userHomeDir, "build.properties");
        copy(userprop.getAbsolutePath(), copyTo.getAbsolutePath());
        projectDir = new File (userHomeDir, "test_project");
        if (!projectDir.exists()) {
            projectDir.mkdir();
        }
        File buildprop = new File(DefaultQueryContextTest.class.getResource("/org/mevenide/properties/build.properties").getFile());
        copyTo = new File(projectDir, "build.properties");
        copy(buildprop.getAbsolutePath(), copyTo.getAbsolutePath());

        File projectprop = new File(DefaultQueryContextTest.class.getResource("/org/mevenide/properties/project.properties").getFile());
        copyTo = new File(projectDir, "project.properties");
        copy(projectprop.getAbsolutePath(), copyTo.getAbsolutePath());
        
        parentProjectDir = new File (userHomeDir, "test_project_parent");
        if (!parentProjectDir.exists()) {
            parentProjectDir.mkdir();
        }        
        buildprop = new File(DefaultQueryContextTest.class.getResource("/org/mevenide/properties/parent/build.properties").getFile());
        copyTo = new File(parentProjectDir, "build.properties");
        copy(buildprop.getAbsolutePath(), copyTo.getAbsolutePath());

        projectprop = new File(DefaultQueryContextTest.class.getResource("/org/mevenide/properties/parent/project.properties").getFile());
        copyTo = new File(parentProjectDir, "project.properties");
        copy(projectprop.getAbsolutePath(), copyTo.getAbsolutePath());
        
        Project project = new Project();
        context = new ProjectContext(project, new File("test"), parentProjectDir);
    }

    protected void tearDown() throws Exception {
        delete(userHomeDir);
        System.setProperty("user.home", originalUserHome);
    }
    
    
    public void testNonProjectBased() {
        IQueryContext query = DefaultQueryContext.getNonProjectContextInstance();
        assertNull(query.getProjectDirectory());
        assertNotNull(query.getUserDirectory());
        assertNotNull(query.getUserPropertyValue("maven.repo.remote"));
        assertNotNull(query.getPropertyValue("maven.repo.remote"));
    }
    
    public void testProjectBased() {
        DefaultQueryContext query = new DefaultQueryContext(projectDir, context);
        assertNotNull(query.getProjectDirectory());
        assertNotNull(query.getUserDirectory());
        assertNull(query.getUserPropertyValue("maven.build.dir"));
        assertNotNull(query.getUserPropertyValue("maven.repo.remote"));
        
        assertNotNull(query.getBuildPropertyValue("maven.build.dir"));
        assertNull(query.getBuildPropertyValue("maven.repo.remote"));
        
        assertNotNull(query.getProjectPropertyValue("maven.conf.dir"));
        assertNull(query.getProjectPropertyValue("maven.repo.remote"));
        
        assertNotNull(query.getPropertyValue("maven.conf.dir"));
        assertNotNull(query.getPropertyValue("maven.repo.remote"));
        
        assertNotNull(query.getPOMContext().getFinalProject());
        assertNotNull(query.getPOMContext().getProjectLayers());
        assertEquals(2, query.getPOMContext().getProjectLayers().length);
        assertEquals(2, query.getPOMContext().getProjectFiles().length);
    }
    
    public void testRefresh() throws Exception {
        IQueryContext query = DefaultQueryContext.getNonProjectContextInstance();
        assertNull(query.getProjectDirectory());
        assertNotNull(query.getUserDirectory());
        assertNotNull(query.getUserPropertyValue("maven.repo.remote"));
        assertNotNull(query.getPropertyValue("maven.repo.remote"));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException exc) {
            
        }
        File changes = new File(DefaultQueryContextTest.class.getResource("/org/mevenide/properties/build.properties").getFile());
        File copyTo = new File(userHomeDir, "build.properties");
        copy(changes.getAbsolutePath(), copyTo.getAbsolutePath());

        assertNull(query.getUserPropertyValue("maven.repo.remote"));
        assertNull(query.getPropertyValue("maven.repo.remote"));
        assertNotNull(query.getUserPropertyValue("maven.build.dir"));
    } 
    
    
	protected void delete(File file) {
		if ( file.isFile() ) {
			file.delete();
		}
		else {
			File[] files = file.listFiles();
			if ( files != null ) {
				for (int i = 0; i < files.length; i++) {
    	            delete(files[i]);
        	    }
			}
            file.delete();
		}
		
	}    
    
    
	protected void copy(String sourceFile, String destFile) throws Exception {

		FileInputStream from = new FileInputStream(sourceFile);
		FileOutputStream to = new FileOutputStream(destFile);
		try {
			byte[] buffer = new byte[4096]; 
			int bytes_read; 
			while ((bytes_read = from.read(buffer)) != -1) {
				to.write(buffer, 0, bytes_read);
			}
		} 
		finally {
			if (from != null) {
				from.close();
			}
			if (to != null) {
				to.close();
			}
		}

	}    
        
        private class ProjectContext implements IProjectContext {
            private Project project;
            private File prFile;
            private File parent;
            public ProjectContext(Project proj, File file, File par) {
                project = proj;
                prFile = file;
                parent = par;
            }
            
            public Project getFinalProject() {
                return project;
            }
            
            public File[] getProjectFiles() {
                return new File[] {prFile, parent};
            }
            
            public Project[] getProjectLayers() {
                return new Project[] { project, project};
            }
            
            public org.jdom.Element[] getRootElementLayers() {
                return new Element[0];
            }
            
            public org.jdom.Element getRootProjectElement() {
                return null;
            }
            
        }
}
