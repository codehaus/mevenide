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

import org.mevenide.AbstractMevenideTestCase;

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
		System.err.println(resolver.guessArtifactId());
		System.err.println(resolver.guessGroupId());
		System.err.println(resolver.guessVersion());
		
		
		resolver = AbstractDependencyResolver.newInstance("/home/bleah/bouh/junit-3.8.1.jar");
		ext = resolver.guessExtension();
		assertEquals("jar", ext);
		System.err.println(resolver.guessArtifactId());
		System.err.println(resolver.guessGroupId());
		System.err.println(resolver.guessVersion());

		resolver = AbstractDependencyResolver.newInstance("/home/bleah/bouh/rt.jar");
		ext = resolver.guessExtension();
		assertEquals("jar", ext);
		System.err.println(resolver.guessArtifactId());
		System.err.println(resolver.guessGroupId());
		System.err.println(resolver.guessVersion());		
			
		//BUG-DefaultDependencyResolver_DEP_guessVersion $DEP-3 depends on $DEP-1
		//assertEquals("tar.gz", ext);
	}
	
	
	
}
