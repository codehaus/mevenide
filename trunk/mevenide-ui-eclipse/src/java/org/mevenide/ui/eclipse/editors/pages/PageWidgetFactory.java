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
package org.mevenide.ui.eclipse.editors.pages;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.parts.FormToolkit;

/**
 * Factory for creating SWT widgets for the POM editor ui.  Merely adds some
 * dressing to the basic FormToolkit.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PageWidgetFactory extends FormToolkit{

	public PageWidgetFactory() {
	}

	public static ScrolledComposite getScrolledComposite(Control c) {
		Composite parent = c.getParent();

		while (parent != null) {
			if (parent instanceof ScrolledComposite) {
				return (ScrolledComposite) parent;
			}
			parent = parent.getParent();
		}
		return null;
	}

	public Label createSpacer(Composite parent) {
		return createLabel(parent, " ");
	}

}
