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

import org.mevenide.AbstractMevenideTestCase;
import org.mevenide.project.io.ProjectReader;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class AbstractProjectValidatorTest extends AbstractMevenideTestCase {
    public void testValidate() throws Exception  {
        try {
            getValidator().validate(ProjectReader.getReader().read(projectFile));
        }
        catch (ValidationException e) {
        }
    }
    
    protected abstract IProjectValidator getValidator();
}
