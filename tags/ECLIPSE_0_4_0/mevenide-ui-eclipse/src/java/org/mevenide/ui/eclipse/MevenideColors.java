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

import org.eclipse.swt.graphics.Color;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: MevenideColors.java 30 août 2003 Exp gdodinet 
 * 
 */
public abstract class MevenideColors {
	
	private MevenideColors() { }
	
	protected void finalize() throws Throwable {
        WHITE.dispose();
		GREY.dispose();
		LIGHT_GRAY.dispose();
		BLACK.dispose();
		ORANGE.dispose();
		GREEN.dispose();
		BLUE_GRAY.dispose();
		BLUE.dispose();
		DARK_BLUE.dispose();
		DARK_RED.dispose();
    }
	
	public static final Color WHITE = new Color(null, 255, 255, 255);
	public static final Color GREY = new Color(null, 156, 156, 156);
	public static final Color LIGHT_GRAY = new Color(null, 127, 127, 127);
	public static final Color BLACK = new Color(null, 0, 0, 0);
	public static final Color ORANGE = new Color(null, 255, 178, 0);
	public static final Color GREEN = new Color(null, 0, 127, 0);
	public static final Color BLUE_GRAY = new Color(null, 156, 170, 197);
	public static final Color BLUE = new Color(null, 0, 0, 255);
	public static final Color DARK_BLUE = new Color(null, 0, 0, 127);
	public static final Color DARK_RED = new Color(null, 127, 0, 0);
	public static final Color RED = new Color(null, 255, 0, 0);
}

