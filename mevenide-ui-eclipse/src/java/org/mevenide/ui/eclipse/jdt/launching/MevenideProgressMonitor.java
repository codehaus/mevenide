/* 
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 */
package org.mevenide.ui.eclipse.jdt.launching;


import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @todo IMPLEMENTME
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: MevenideProgressMonitor.java 27 avr. 2003 14:12:1913:34:35 Exp gdodinet 
 * 
 */
public class MevenideProgressMonitor extends NullProgressMonitor {
    
	/** 
	 * @see org.eclipse.core.runtime.IProgressMonitor#worked(int)
	 */
	public void worked(int work) {
		super.worked(work);
	}

}
