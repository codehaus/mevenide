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
package org.mevenide.runner;

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

	public String getToolsJar() {
		return null;
	}

	protected void initEnvironment() {
	}

	protected String getBasedir() {
		return null;
	}

	protected void launchVM(String[] options, String[] goals) throws Exception {
	}

}
