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
package org.mevenide.project.dependency;

import java.io.File;

import org.mevenide.AbstractMevenideTestCase;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class AbstractDependencyResolverTest extends AbstractMevenideTestCase {

	public void testNewInstance() throws Exception {
		
		File tmp = new File(System.getProperty("user.home"), ".mevenide/tests/bleah/bouh/");
		tmp.mkdirs();
		File f = new File(tmp, "pyo.jar");
		f.createNewFile();
		assertNotNull(AbstractDependencyResolver.newInstance(f.getAbsolutePath()));
	}

}
