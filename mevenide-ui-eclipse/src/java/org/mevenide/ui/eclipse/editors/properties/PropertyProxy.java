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
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mevenide.util.MevenideUtils;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PropertyProxy extends AbstractPomPropertySource {

	private static final Log log = LogFactory.getLog(PropertyProxy.class);
	
	private static final String PROPERTY_NAME = "name";
	private static final String PROPERTY_VALUE = "value";

	private String name;
	private String value;
	
	private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[2];
	{
		descriptors[0] = new TextPropertyDescriptor(
			PROPERTY_NAME,
			PROPERTY_NAME
		);
		descriptors[1] = new TextPropertyDescriptor(
			PROPERTY_VALUE,
			PROPERTY_VALUE
		);
	}

	public PropertyProxy(String property) {
		this(MevenideUtils.resolveProperty(property));
	}

	private PropertyProxy(String[] property) {
		this(property[0], property[1]);
	}

	public PropertyProxy(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public Object getEditableValue() {
		return value;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (log.isDebugEnabled()) {
			log.debug("getPropertyValue called: " + id);
		}
		if (PROPERTY_NAME.equals(id)) {
			return valueOrEmptyString(name);
		}
		else if (PROPERTY_VALUE.equals(id)) {
			return valueOrEmptyString(value);
		}
		return null;
	}
	
	public boolean isPropertySet(Object id) {
		if (PROPERTY_NAME.equals(id)) {
			return !isEmpty(name);
		}
		else if (PROPERTY_VALUE.equals(id)) {
			return !isEmpty(value);
		}
		return false;
	}
	
	public void resetPropertyValue(Object id) {
		setPropertyValue(id, EMPTY_STR);
	}

	public void setPropertyValue(Object id, Object valueObj) {
		if (log.isDebugEnabled()) {
			log.debug("setPropertyValue called: " + id + " = " + valueObj);
		}
		if (valueObj == null) return;
		
		String newValue = valueObj.toString();
		String oldValue = null;
		boolean changed = false;
		if (PROPERTY_NAME.equals(id)) {
			oldValue = name;
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				this.name = newValue;
				changed = true;
			}
		}
		else if (PROPERTY_VALUE.equals(id)) {
			oldValue = value;
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				this.value = newValue;
				changed = true;
			}
		}
		if (changed)
		{
			firePropertyChangeEvent(id.toString(), oldValue, newValue);
		}
	}

	public String getLabel(Object o) {
		if (log.isDebugEnabled()) {
			log.debug("getLabel called for " + o);
		}
		return name != null ? name : "[unknown]";
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return name + MevenideUtils.PROPERTY_SEPARATOR + value;
	}
}
