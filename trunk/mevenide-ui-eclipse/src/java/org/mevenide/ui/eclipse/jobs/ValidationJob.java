/*
 * ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * =========================================================================
 */
package org.mevenide.ui.eclipse.jobs;

import java.io.File;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.PlatformUI;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.project.validation.ContentValidator;
import org.mevenide.project.validation.ValidationException;
import org.mevenide.ui.eclipse.Mevenide;


/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet </a>
 * @version $Id$
 *  
 */
public class ValidationJob extends Job {
    private static final Log log = LogFactory.getLog(ValidationJob.class);
    
    private IFile pomFile;
    
    public ValidationJob(IFile pomFile) {
        super(Mevenide.getResourceString("ValidationJob.Name"));
        this.pomFile = pomFile;
        setPriority(Job.SHORT);
    }
    
    protected IStatus run(IProgressMonitor monitor) {
        ContentValidator contentValidator = new ContentValidator();
        try {
            File file = pomFile.getRawLocation().toFile();
            ProjectReader reader = ProjectReader.getReader();
            
            Project pom = reader.read(file);
            if ( pom != null ) {
                contentValidator.validate(pom);
            }
            return new Status(Status.OK, "org.mevenide.ui", 0, null, null);
        }
        catch ( ValidationException e ) {
            List errors = e.getErrors();
            List warnings = e.getWarnings();
            try {
                for (int i = 0; i < errors.size(); i++) {
                    IMarker marker = pomFile.createMarker(IMarker.TASK);
                    marker.setAttribute(IMarker.MESSAGE, errors.get(i));
                } 
                for (int i = 0; i < warnings.size(); i++) {
                    IMarker marker = pomFile.createMarker(IMarker.TASK);
                    marker.setAttribute(IMarker.MESSAGE, warnings.get(i));
                }
	            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.views.TaskList");
	            return new Status(Status.WARNING, "org.mevenide.ui", 0, null, null);
            }
            catch ( Exception ex ) {
                String message = "Uanble to create markers"; 
                log.error(message, e);
                return new Status(Status.WARNING, "org.mevenide.ui", 0, message, e);
            }
        }
        catch (Exception e) {
            String message = Mevenide.getResourceString("MevenidePomEditorContributor.Validation.Error"); 
            log.error(message, e);
            return new Status(Status.INFO, "org.mevenide.ui", 0, message, e);
        }
    }
}
