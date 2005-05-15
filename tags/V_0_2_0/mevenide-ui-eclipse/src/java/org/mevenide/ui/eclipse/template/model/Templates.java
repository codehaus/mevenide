/*
 * ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * =========================================================================
 */
package org.mevenide.ui.eclipse.template.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.eclipse.core.runtime.IAdaptable;

/**
 * A Templates object is a container for Templates
 * @author	<a href="mailto:jens@iostream.net">Jens Andersen</a>, Last updated by $Author$
 * @version $Id$
 */
public class Templates  extends Observable implements IAdaptable {
	private List fTemplates;
	/**
	 * 
	 */
	public Templates() {
		fTemplates = new ArrayList();
	}
	/**
	 * Add a template to the Templates container
	 * @param template - template to be added
	 */
	public void addTemplate(Template template)
	{
		fTemplates.add(template);
		setChanged();
		notifyObservers();
	}
	/**
	 * Remove a template from the Templates container
	 * @param template - template to be removed
	 */
	public void removeTemplate(Template template)
	{
		fTemplates.remove(template);
		setChanged();
		notifyObservers();
	}
	/**
	 * Get all templates as an array
	 * @return a template array
	 */
	public Object[] getTemplates()
	{
		return fTemplates.toArray();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
