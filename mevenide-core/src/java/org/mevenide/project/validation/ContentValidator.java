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
package org.mevenide.project.validation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.project.Branch;
import org.apache.maven.project.Build;
import org.apache.maven.project.Contributor;
import org.apache.maven.project.Developer;
import org.apache.maven.project.MailingList;
import org.apache.maven.project.Organization;
import org.apache.maven.project.Project;
import org.apache.maven.project.Repository;
import org.apache.maven.project.Version;
import org.mevenide.util.ResolverUtils;
import org.mevenide.util.StringUtils;



/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ContentValidator implements IProjectValidator {
    
    private List validationWarnings = new ArrayList();
    private List validationErrors = new ArrayList();

    public void validate(Project project) throws ValidationException {
        validatePomVersion(project.getPomVersion());
        validateSimpleElement(project.getName(), "name");
        validateSimpleElement(project.getGroupId(), "groupId");
        validateSimpleElement(project.getArtifactId(), "artifactId");
        validateSimpleElement(project.getCurrentVersion(), "currentVersion");
        validateOrganization(project.getOrganization());
        validateSimpleElement(project.getInceptionYear(), "inceptionYear");
        validatePackage(project.getPackage());
        validateFileElement(project, project.getLogo(), "logo");
        validateSimpleElement(project.getShortDescription(), "shortDescription");
        validateRepository(project.getRepository()); //@todo
        validateVersions(project.getVersions());  //@todo
        validateBranches(project.getBranches());  //@todo
        validateMailingLists(project.getMailingLists());  //@todo
        validateDevelopers(project.getDevelopers());  
        validateContributors(project.getContributors());
        validateLicenses(project.getLicenses());  //@todo
        validateDependencies(project.getDependencies());  //@todo
        validateBuild(project.getBuild());  //@todo
        validateReports(project.getReports());  //@todo
    }
    
    private void validateReports(List reports) {
        
    }

    private void validateBuild(Build build) {
        
    }

    private void validateDependencies(List dependencies) {
     
    }

    private void validateLicenses(List licenses) {
        
    }

    private void validatePackage(String packageList) {
        
    }
    
    private void validateFileElement(Project project, String file, String elementName) {
        if ( file != null ) {
            if ( !new File(project.getFile().getParent(), ResolverUtils.getInstance().resolve(project, file)).exists() ) {
                validationWarnings.add(elementName + " file doesnot exist.");
            }
        }
    }
    
    private void validateSimpleElement(String pomElement, String elementName) {
        if ( StringUtils.isNull(pomElement) ) {
            validationErrors.add(elementName + " must be defined");
        }
    }
    
    private void validatePomVersion(String pomVersion) {
        if ( !"3".equals(pomVersion) ) {
            validationErrors.add("pomVersion must be '3'");
        }
    }
    
    private void validateDevelopers(List developers) {
        if ( developers == null || developers.size() == 0 ) {
            validationErrors.add("developers must be defined");
        }
        else {
            for (int i = 0; i < developers.size(); i++) {
                validateDeveloper((Developer) developers.get(i), i);
            }
        }
    }
    
    private void validateDeveloper(Developer developer, int index) {
        validateSimpleElement(developer.getName(), "developer[" + index + "].name must be defined");
        validateSimpleElement(developer.getId(), "developer[" + index + "].id must be defined");
        validateContactDetails(developer, "developer", index);
    }
    
    private void validateContactDetails(Contributor contributor, String prefix, int index) {
        validateSimpleElement(contributor.getEmail(), prefix + "[" + index + "].email must be defined"); 
        validateTimezone(contributor.getTimezone(), prefix, index);
    }

    private void validateTimezone(String timezone, String prefix, int index) {
        if ( !StringUtils.isNull(timezone) ) {
	        try {
	            int tz = Integer.parseInt(timezone);
	            if ( tz > 14 || tz < - 14 ) {
	                validationErrors.add(prefix + "[" + index + "].timezone should be less than 14 and greater than -14");
	            }
	        }
	        catch ( NumberFormatException nfe ) {
	            validationErrors.add(prefix + "[" + index + "].timezone should be an integer");
	        }
        }
    }

    private void validateContributors(List contributors) {
        if ( contributors != null ) {
            for (int i = 0; i < contributors.size(); i++) {
                validateContributor((Contributor) contributors.get(i), i);
            }
        }
    }
    
    private void validateContributor(Contributor contributor, int index) {
        validateSimpleElement(contributor.getName(), "contributor[" + index + "].name must be defined");
        validateContactDetails(contributor, "contributor", index);
    }
    
    private void validateOrganization(Organization organization) {
        
    }
    
    private void validateRepository(Repository repository) {
        
    }
    
    private void validateVersions(List versions) {
        
    }
    
    private void validateVersion(Version version) {
        
    }
    
    private void validateBranches(List branches) {
        
    }
    
    private void validateBranch(Branch branch) {
        
    }
    
    private void validateMailingLists(List mailingLists) {
        
    }
    
    private void validateMailingList(MailingList mailingList) {
        
    }

}

