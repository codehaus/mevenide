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
package org.mevenide.ui.eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

/**  
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$ 
 * 
 */
public abstract class MevenideFonts {
	
	private MevenideFonts() { }
	
	protected void finalize() throws Throwable {
		EDITOR_HEADER.dispose();
    }
	
	public static final Font EDITOR_HEADER = new Font(null, "Courier", 14, SWT.BOLD);
}

