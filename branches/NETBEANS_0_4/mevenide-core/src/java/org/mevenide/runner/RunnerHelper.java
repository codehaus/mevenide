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

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: RunnerHelper.java,v 1.1 12 oct. 2003 Exp gdodinet 
 * 
 */
public abstract class RunnerHelper {
	private static RunnerHelper helper;
	
	
    protected RunnerHelper() { }
    
    public static RunnerHelper getHelper() {
    	return helper;	
    }
    
    public static synchronized void setHelper(RunnerHelper runnerHelper) {
    	RunnerHelper.helper = runnerHelper;
    }
    
    public abstract String getForeheadLibrary();
}
