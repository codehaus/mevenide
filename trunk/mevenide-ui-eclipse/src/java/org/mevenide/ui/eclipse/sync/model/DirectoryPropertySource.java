/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software contributord under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.ui.eclipse.sync.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mevenide.project.ProjectConstants;
import org.mevenide.ui.eclipse.editors.properties.AbstractPomPropertySource;
import org.mevenide.util.MevenideUtils;

/**
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 */
public class DirectoryPropertySource extends AbstractPomPropertySource {

	private static final Log log = LogFactory.getLog(DirectoryPropertySource.class);

	private static final String DIRECTORY_PATH = "path";
	private static final String DIRECTORY_TYPE = "type";

	private static final String[] DIRECTORY_TYPE_VALUES = new String[] {
		ProjectConstants.MAVEN_SRC_DIRECTORY,
		ProjectConstants.MAVEN_TEST_DIRECTORY,
		ProjectConstants.MAVEN_ASPECT_DIRECTORY,
		ProjectConstants.MAVEN_RESOURCE,
		ProjectConstants.MAVEN_TEST_RESOURCE,
	};
	
	private Directory directory;
	
	private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[2];
	{
		descriptors[0] = new TextPropertyDescriptor(
			DIRECTORY_PATH,
			DIRECTORY_PATH
		);
		descriptors[1] = new TextPropertyDescriptor(
			DIRECTORY_TYPE,
			DIRECTORY_TYPE
		);
		descriptors[1] = new ComboBoxPropertyDescriptor(
				DIRECTORY_TYPE,
				DIRECTORY_TYPE,
				DIRECTORY_TYPE_VALUES
		);
		((ComboBoxPropertyDescriptor) descriptors[1]).setLabelProvider(
				new LabelProvider() {
					public String getText(Object element) {
						if (element instanceof Integer) {
							log.debug("element instance of Integer : " + ((Integer) element).intValue());
							return DIRECTORY_TYPE_VALUES[((Integer) element).intValue()];
						}
						return super.getText(element);
					}
				}
		);
	}

	public DirectoryPropertySource(Directory directory) {
		this.directory = directory;
	}

	public Object getEditableValue() {
		return directory.getType();
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (log.isDebugEnabled()) {
			log.debug("getPropertyValue called: " + id);
		}
		if (DIRECTORY_PATH.equals(id)) {
			return valueOrEmptyString(directory.getPath());
		}
		else if (DIRECTORY_TYPE.equals(id)) {
			return getTypeIndex();
		}
		return null;
	}
	
	private Integer getTypeIndex() {
		for ( int u = 0; u < DIRECTORY_TYPE_VALUES.length; u++ ) {
			if ( DIRECTORY_TYPE_VALUES[u].equals(directory.getType()) ) {
				return new Integer(u);
			}	
		}
		//should not happen
		return new Integer(0);
	}
	
	public boolean isPropertySet(Object id) {
		if (DIRECTORY_PATH.equals(id)) {
			return !isEmpty(directory.getPath());
		}
		else if (DIRECTORY_TYPE.equals(id)) {
			return !isEmpty(directory.getType());
		}
		return false;
	}
	
	public void resetPropertyValue(Object id) {
		setPropertyValue(id, EMPTY_STR);
	}

	public void setPropertyValue(Object id, Object value) {
		if (log.isDebugEnabled()) {
			log.debug("setPropertyValue called: " + id + " = " + value);
		}
		if (value == null) return;
		
		String newValue = value.toString();
		String oldValue = null;
		boolean changed = false;
		if (DIRECTORY_PATH.equals(id)) {
			oldValue = directory.getPath();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				directory.setPath(newValue);
				changed = true;
			}
		}
		else if (DIRECTORY_TYPE.equals(id)) {
			newValue = DIRECTORY_TYPE_VALUES[(((Integer) value).intValue())];
			oldValue = directory.getPath();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				directory.setType(newValue);
				changed = true;
			}
		}
		if (changed) {
			firePropertyChangeEvent(id.toString(), oldValue, newValue);
		}
	}

	public String getLabel(Object o) {
		if (log.isDebugEnabled()) {
			log.debug("getLabel called for " + o);
		}
		return directory.getPath() != null ? directory.getType() : "[unknown]";
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return directory;
	}
}
