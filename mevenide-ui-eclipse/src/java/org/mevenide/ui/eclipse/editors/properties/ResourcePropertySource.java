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
package org.mevenide.ui.eclipse.editors.properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Resource;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mevenide.util.MevenideUtils;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class ResourcePropertySource extends AbstractPomPropertySource {

	private static final Log log = LogFactory.getLog(ResourcePropertySource.class);

	private static final String RESOURCE_DIRECTORY = "directory";
	private static final String RESOURCE_TARGETPATH = "targetPath";
	private static final String RESOURCE_FILTERING = "filtering";

	private static final String RESOURCE_FILTERING_TRUE = "true";
	private static final String RESOURCE_FILTERING_FALSE = "false";
	
	private static final String[] RESOURCE_FILTERING_VALUES = new String[] {
		RESOURCE_FILTERING_TRUE,
		RESOURCE_FILTERING_FALSE
	};

	private Resource resource;
	
	private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[3];
	{
		descriptors[0] = new TextPropertyDescriptor(
			RESOURCE_DIRECTORY,
			RESOURCE_DIRECTORY
		);
		descriptors[1] = new TextPropertyDescriptor(
			RESOURCE_TARGETPATH,
			RESOURCE_TARGETPATH
		);
		descriptors[2] = new ComboBoxPropertyDescriptor(
			RESOURCE_FILTERING,
			RESOURCE_FILTERING,
			RESOURCE_FILTERING_VALUES
		);
		((ComboBoxPropertyDescriptor) descriptors[2]).setLabelProvider(
			new LabelProvider() {
				public String getText(Object element) {
					if (element instanceof Integer) {
						return getBooleanForIndex(((Integer) element).intValue());
					}
					return super.getText(element);
				}
			}
		);
	}

	public ResourcePropertySource(Resource resource) {
		this.resource = resource;
	}

	public Object getEditableValue() {
		return resource.getDirectory();
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (log.isDebugEnabled()) {
			log.debug("getPropertyValue called: " + id);
		}
		if (RESOURCE_DIRECTORY.equals(id)) {
			return valueOrEmptyString(resource.getDirectory());
		}
		if (RESOURCE_TARGETPATH.equals(id)) {
			return valueOrEmptyString(resource.getTargetPath());
		}
		if (RESOURCE_FILTERING.equals(id)) {
			return getIndexOfType();
		}
		return null;
	}
	
	public boolean isPropertySet(Object id) {
		if (RESOURCE_DIRECTORY.equals(id)) {
			return !isEmpty(resource.getDirectory());
		}
		if (RESOURCE_TARGETPATH.equals(id)) {
			return !isEmpty(resource.getTargetPath());
		}
		if (RESOURCE_FILTERING.equals(id)) {
			return resource.getFiltering();
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
		if (RESOURCE_DIRECTORY.equals(id)) {
			oldValue = resource.getDirectory();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				resource.setDirectory(newValue);
				changed = true;
			}
		}
		else if (RESOURCE_TARGETPATH.equals(id)) {
			oldValue = resource.getTargetPath();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				resource.setTargetPath(newValue);
				changed = true;
			}
		}
		else if (RESOURCE_FILTERING.equals(id)) {
			newValue = getBooleanForIndex(((Integer) value).intValue());
			oldValue = Boolean.toString(resource.getFiltering());
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				boolean filtering = Boolean.valueOf(newValue).booleanValue();
				resource.setFiltering(filtering);
				changed = true;
			}
		}
		if (changed)
		{
			firePropertyChangeEvent(id.toString(), oldValue, newValue);
		}
	}

	private Integer getIndexOfType() {
		return resource.getFiltering() ? new Integer(0) : new Integer(1);
	}
	
	private String getBooleanForIndex(int index) {
		return index == 0 ? RESOURCE_FILTERING_TRUE : RESOURCE_FILTERING_FALSE;	
	}

	public String getLabel(Object o) {
		if (log.isDebugEnabled()) {
			log.debug("getLabel called for " + o);
		}
		return resource.getDirectory() != null ? resource.getDirectory() : "[unknown]";
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return resource;
	}
}
