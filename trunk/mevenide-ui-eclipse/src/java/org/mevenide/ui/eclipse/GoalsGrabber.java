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
package org.mevenide.ui.eclipse;

import java.io.File;

import org.mevenide.core.AbstractGoalsGrabber;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class GoalsGrabber extends AbstractGoalsGrabber {
    /**
     * @see org.mevenide.core.AbstractGoalsGrabber#load()
     */
	public void load() throws Exception {
		Mevenide plugin = Mevenide.getPlugin();
        
//        String foreHeadConfFile = plugin.getForeheadConf();
//        String mavenHome = plugin.getMavenHome();
//        String javaHome = plugin.getJavaHome();
        String effectiveDirectory = plugin.getEffectiveDirectory();

        String output = new File(effectiveDirectory, "maven-goals.xml").getAbsolutePath();
        createMavenXmlFile(effectiveDirectory, output);
        mavenRunner.run(new String[0], new String[] {"goals:grab"});
        goalsBean.unMarshall(output);
   }
   
   
}

