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

import java.io.File;




public class AbstractGoalsManagerStub extends AbstractGoalsManager {
	
	public AbstractGoalsManagerStub() {
	
	}
    public void initialize() { }
    public void load() { }
    public void save() { }
    public File getXmlGoals() { 
        String xmlGoals = 
            AbstractGoalsManagerTest.class
                                    .getResource("/maven-goals.xml")
                                    .getFile();
        return new File(xmlGoals);
    }
    
}