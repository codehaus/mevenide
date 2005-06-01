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
package org.mevenide.ui.eclipse.template.view;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.template.model.Template;


public class TemplateLabelProvider extends LabelProvider {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object obj) {
		if (obj instanceof Template) {
			return ((Template)obj).getTemplateName();
		}
		return super.getText(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object obj) {
		if (obj instanceof Template) {
			return Mevenide.getInstance().getImageRegistry().get(IImageRegistry.MAVEN_POM_OBJ);
		}
		return super.getImage(obj);
	}

}