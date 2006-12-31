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
package org.mevenide.project.validation;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.project.Project;
import org.apache.maven.util.StringInputStream;
import org.mevenide.project.io.CarefulProjectMarshaller;
import org.mevenide.util.ProjectUtils;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.SinglePropertyMap;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;



/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SchemaValidator implements IProjectValidator {
    
    private List errors;
    private List warnings;
    
    public SchemaValidator() {
        initialize();
    }
    
    public void validate(File file) throws ValidationException {
        ErrorHandler err = new ErrorHandler() {
            public void error(SAXParseException exception) throws SAXException {
                ValidationProblem problem = new ValidationProblem(exception);
	            if ( !errors.contains(problem) ) {
	                errors.add(problem);
	            }
	        }
	        public void fatalError(SAXParseException exception) throws SAXParseException {
	            ValidationProblem problem = new ValidationProblem(exception);
	            if ( !errors.contains(problem) ) {
	                errors.add(problem);
	            }
	        }
	        public void warning(SAXParseException exception) throws SAXParseException {
	            ValidationProblem problem = new ValidationProblem(exception);
	            if ( !warnings.contains(problem) ) {
	                warnings.add(problem);
	            }
	        }
        };
      
        PropertyMap map = new SinglePropertyMap(ValidateProperty.ERROR_HANDLER, err);
        ValidationDriver driver = new ValidationDriver(map);
      
        try {
	        InputStream schema = getClass().getResourceAsStream("/maven-project.xsd");
	        driver.loadSchema(new InputSource(schema));
	        Project pom = ProjectUtils.resolveProjectTree(file);
	      
	        Writer stringWriter = new StringWriter();
	        new CarefulProjectMarshaller().marshall(stringWriter, pom);
	        System.out.println(stringWriter);
	        driver.validate(new InputSource(new StringInputStream(stringWriter.toString())));
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            warnings.add("Unable to validate pom against schema due to : " + e);
            throw new ValidationException(errors, warnings);
        }
	    if ( !errors.isEmpty() || !warnings.isEmpty() ) {
	        throw new ValidationException(errors, warnings);
	    }   
    }

    private void initialize() {
        errors = new ArrayList();
        warnings = new ArrayList();
    }
}

