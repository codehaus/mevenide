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
package org.mevenide.ui.eclipse.goals.model;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: Element.java,v 1.1 7 sept. 2003 Exp gdodinet 
 * 
 */
public class Element {
    public static final Element NULL_ROOT = new Element();
    
	private String name = "";
    
	private Element[] prereqs;
	
	    
    
	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String toString() {
        return name;
    }
    
    public Element[] getPrereqs() {
        return prereqs;
    }

    public void setPrereqs(Element[] prereqs) {
        this.prereqs = prereqs;
    }
	
	public String getFullyQualifiedName() {
		return name;
	}
	
	public boolean equals(Object obj) {
		return (obj instanceof Element) 
				&& ((Element) obj).getFullyQualifiedName().equals(getFullyQualifiedName());
	}
}
