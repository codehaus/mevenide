/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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
package org.mevenide.ui.eclipse.editors.pom;

/**
 * A page within the Mevenide POM Editor.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public interface IPomEditorPage {
	/**
	 * Access to the parent Mevenide POM editor instance.
	 */
	public MevenidePomEditor getPomEditor();
	
	/**
	 * Callback message sent with this page is activated (i.e.
	 * user selects this page in the multi-page editor).
	 */
	public void pageActivated(IPomEditorPage oldPage);
	
	/**
	 * Callback message sent with this page is de-activated (i.e.
	 * user selects another page in the multi-page editor).
	 */
	public void pageDeactivated(IPomEditorPage newPage);
	
	/**
	 * Callback message sent to allow the page to dispose any
	 * resources it has allocated.
	 */
	public void dispose();
	
	/**
	 * Message indicating whether this page presents components
	 * editable in the properties editor view.
	 */
	public boolean isPropertySourceSupplier();
	
	/**
	 * Message indicating whether this page is the current active page
	 * in the multi-page editor.
	 */
	public boolean isActive();
}