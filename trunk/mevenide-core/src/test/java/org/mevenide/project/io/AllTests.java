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
package org.mevenide.project.io;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: AllTests.java 8 mai 2003 15:32:4913:34:35 Exp gdodinet 
 * 
 */
public class AllTests  {
    public static Test suite() {
        TestSuite suite = new TestSuite();
        
        suite.addTestSuite(ProjectReaderTest.class);
		suite.addTestSuite(ProjectWriterTest.class);
		
        return suite;
    }
}
