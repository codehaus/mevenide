/*
 * ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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
package org.mevenide.ui.eclipse.pom.validation;

import java.io.File;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.mevenide.project.validation.IProjectValidator;
import org.mevenide.project.validation.SchemaValidator;
import org.mevenide.project.validation.ValidationException;
import org.mevenide.project.validation.ValidationProblem;
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
        super(Mevenide.getResourceString("ValidationJob.Name")); //$NON-NLS-1$
        this.pomFile = pomFile;
        setPriority(Job.SHORT);
    }
    
    protected IStatus run(IProgressMonitor monitor) {
        IProjectValidator projectValidator = new SchemaValidator();
        try {
            MarkerHelper.deleteMarkers(pomFile);
            
            File file = pomFile.getRawLocation().toFile();
            
            if ( file != null ) {
                projectValidator.validate(file);
            }
            return new Status(Status.OK, "org.mevenide.ui", 0, Mevenide.getResourceString("ValidationJob.IsValid"), null); //$NON-NLS-1$ //$NON-NLS-2$
        }
        catch ( ValidationException e ) {
            List errors = e.getErrors();
            List warnings = e.getWarnings();
            try {
                for (int i = 0; i < errors.size(); i++) {
                    ValidationProblem problem = (ValidationProblem) errors.get(i);
                    createMarker(problem, IMarker.SEVERITY_ERROR);
                } 
                for (int i = 0; i < warnings.size(); i++) {
                    ValidationProblem problem = (ValidationProblem) errors.get(i);
                    createMarker(problem, IMarker.SEVERITY_WARNING);
                }
	            //PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.views.TaskList");
	            return new Status(Status.OK, "org.mevenide.ui", 0, Mevenide.getResourceString("ValidationJob.HasErrors"), null); //$NON-NLS-1$ //$NON-NLS-2$
            }
            catch ( Exception ex ) {
                String message = Mevenide.getResourceString("Validation.CreateMarker.Error", pomFile.getFullPath().toString());  //$NON-NLS-1$
                log.error(message, e);
                return new Status(Status.WARNING, "org.mevenide.ui", 0, message, e); //$NON-NLS-1$
            }
        }
        catch (Exception e) {
            String message = Mevenide.getResourceString("MevenidePomEditorContributor.Validation.Error");  //$NON-NLS-1$
            log.error(message, e);
            return new Status(Status.INFO, "org.mevenide.ui", 0, message, e); //$NON-NLS-1$
        }
    }

    private void createMarker(ValidationProblem problem, int severity) throws CoreException {
        IMarker marker = pomFile.createMarker(IPomValidationMarker.VALIDATION_ERROR_MARKER);
        marker.setAttribute(IMarker.MESSAGE, problem.getException().getMessage());
        marker.setAttribute(IMarker.SEVERITY, severity);
        marker.setAttribute(IMarker.LINE_NUMBER, problem.getException().getLineNumber());
        //marker.setAttribute(IMarker.USER_EDITABLE, Boolean.FALSE);
    }
}
