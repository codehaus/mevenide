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
package org.mevenide.core;

import org.mevenide.MevenideException;


/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: AbstractRunnerStub.java 8 mai 2003 14:59:0213:34:35 Exp gdodinet 
 * 
 */
public class AbstractRunnerStub extends AbstractRunner {

	public AbstractRunnerStub() throws MevenideException {
		super();
	}

	protected void initEnvironment() {
	}

	protected String getBasedir() {
		return null;
	}

	protected void launchVM(String[] options, String[] goals) throws Exception {
	}

}
