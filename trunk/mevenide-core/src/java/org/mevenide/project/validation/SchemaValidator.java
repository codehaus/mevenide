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

import java.io.FileInputStream;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.maven.project.Project;
import org.apache.maven.util.StringInputStream;
import org.mevenide.project.io.DefaultProjectMarshaller;
import org.xml.sax.InputSource;
import com.thaiopensource.validate.ValidationDriver;



/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SchemaValidator implements IProjectValidator {
    
    public void validate(Project project) throws ValidationException {
        validateAgainstSchema(project);
    }
    
    private void validateAgainstSchema(Project project) throws ValidationException {
      ValidationDriver driver = new ValidationDriver();
      
      try {
	      String schema = getClass().getResource("/maven-project.xsd").getFile();
	      driver.loadSchema(new InputSource(new FileInputStream(schema)));
	      
	      Writer stringWriter = new StringWriter();
	      new DefaultProjectMarshaller().marshall(stringWriter, project);
	      
	      driver.validate(new InputSource(new StringInputStream(stringWriter.toString())));
      }
      catch (Exception e) {
          throw new ValidationException("Unable to validate pom against schema", e);
      }
    }
}

