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

import org.mevenide.context.AbstractUnmarshallerTestCase;
import org.mevenide.context.IProjectUnmarshaller;
import org.mevenide.context.JDomProjectUnmarshaller;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: DefaultProjectUnmarshallerTest.java 8 mai 2003 15:32:4913:34:35 Exp gdodinet 
 * 
 */
public class JDomProjectUnmarshallerTest extends AbstractUnmarshallerTestCase {

	protected IProjectUnmarshaller getUnmarshaller() {
        return new JDomProjectUnmarshaller();
    }
	
}
