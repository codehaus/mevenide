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
 */
package org.mevenide.project.dependency;

import java.io.File;

import org.mevenide.AbstractMevenideTestCase;
import org.mevenide.Environment;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DependencyResolverTest extends AbstractMevenideTestCase {
	
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGuessExtension() throws Exception {
		IDependencyResolver resolver = AbstractDependencyResolver.newInstance("/home/bleah/bouh/foo+joe-test2.-bar-1.0.7-beta-1.txt");
		String ext = resolver.guessExtension();
		assertEquals("txt", ext);
		
		resolver = AbstractDependencyResolver.newInstance("/home/bleah/bouh/rt.jar");
		ext = resolver.guessExtension();
		assertEquals("jar", ext);
		
		//BUG-DefaultDependencyResolver_DEP_guessVersion $DEP-3 depends on $DEP-1
		//assertEquals("tar.gz", ext);
		
	}

	public void testGuess() throws Exception {
		IDependencyResolver resolver = AbstractDependencyResolver.newInstance(new File(Environment.getMavenRepository(), "commons-httpclient\\jars\\commons-httpclient-2.0alpha1-20020829.jar").getAbsolutePath());
		assertEquals("2.0alpha1-20020829", resolver.guessVersion());
		assertEquals("commons-httpclient", resolver.guessArtifactId());
		assertEquals("commons-httpclient", resolver.guessGroupId());
		
	}
	
	
	
}
