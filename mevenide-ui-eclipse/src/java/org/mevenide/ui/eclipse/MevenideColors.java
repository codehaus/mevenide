/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
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

