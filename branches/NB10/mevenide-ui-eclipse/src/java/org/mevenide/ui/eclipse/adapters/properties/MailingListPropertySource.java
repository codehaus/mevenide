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
package org.mevenide.ui.eclipse.adapters.properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.MailingList;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.util.MevenideUtils;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class MailingListPropertySource extends AbstractPomPropertySource {

	private static final Log log = LogFactory.getLog(MailingListPropertySource.class);

	private static final String MAILINGLIST_NAME = "name"; //$NON-NLS-1$
	private static final String MAILINGLIST_SUBSCRIBE = "subscribe"; //$NON-NLS-1$
	private static final String MAILINGLIST_UNSUBSCRIBE = "unsubscribe"; //$NON-NLS-1$
	private static final String MAILINGLIST_ARCHIVE = "archive"; //$NON-NLS-1$

	private MailingList mailingList;
	
	private IPropertyDescriptor[] descriptors = new IPropertyDescriptor[4];
	{
		descriptors[0] = new TextPropertyDescriptor(
			MAILINGLIST_NAME,
			MAILINGLIST_NAME
		);
		descriptors[1] = new TextPropertyDescriptor(
			MAILINGLIST_SUBSCRIBE,
			MAILINGLIST_SUBSCRIBE
		);
		descriptors[2] = new TextPropertyDescriptor(
			MAILINGLIST_UNSUBSCRIBE,
			MAILINGLIST_UNSUBSCRIBE
		);
		descriptors[3] = new TextPropertyDescriptor(
			MAILINGLIST_ARCHIVE,
			MAILINGLIST_ARCHIVE
		);
	}

	public MailingListPropertySource(MailingList mailingList) {
		this.mailingList = mailingList;
	}

	public Object getEditableValue() {
		return mailingList.getName();
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (log.isDebugEnabled()) {
			log.debug("getPropertyValue called: " + id); //$NON-NLS-1$
		}
		if (MAILINGLIST_NAME.equals(id)) {
			return valueOrEmptyString(mailingList.getName());
		}
		else if (MAILINGLIST_SUBSCRIBE.equals(id)) {
			return valueOrEmptyString(mailingList.getSubscribe());
		}
		else if (MAILINGLIST_UNSUBSCRIBE.equals(id)) {
			return valueOrEmptyString(mailingList.getUnsubscribe());
		}
		else if (MAILINGLIST_ARCHIVE.equals(id)) {
			return valueOrEmptyString(mailingList.getArchive());
		}
		return null;
	}
	
	public boolean isPropertySet(Object id) {
		if (MAILINGLIST_NAME.equals(id)) {
			return !isEmpty(mailingList.getName());
		}
		else if (MAILINGLIST_SUBSCRIBE.equals(id)) {
			return !isEmpty(mailingList.getSubscribe());
		}
		else if (MAILINGLIST_UNSUBSCRIBE.equals(id)) {
			return !isEmpty(mailingList.getUnsubscribe());
		}
		else if (MAILINGLIST_ARCHIVE.equals(id)) {
			return !isEmpty(mailingList.getArchive());
		}
		return false;
	}
	
	public void resetPropertyValue(Object id) {
		setPropertyValue(id, EMPTY_STR);
	}

	public void setPropertyValue(Object id, Object value) {
		if (log.isDebugEnabled()) {
			log.debug("setPropertyValue called: " + id + " = " + value); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (value == null) return;
		
		String newValue = value.toString();
		String oldValue = null;
		boolean changed = false;
		if (MAILINGLIST_NAME.equals(id)) {
			oldValue = mailingList.getName();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				mailingList.setName(newValue);
				changed = true;
			}
		}
		else if (MAILINGLIST_SUBSCRIBE.equals(id)) {
			oldValue = mailingList.getSubscribe();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				mailingList.setSubscribe(newValue);
				changed = true;
			}
		}
		else if (MAILINGLIST_UNSUBSCRIBE.equals(id)) {
			oldValue = mailingList.getUnsubscribe();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				mailingList.setUnsubscribe(newValue);
				changed = true;
			}
		}
		else if (MAILINGLIST_ARCHIVE.equals(id)) {
			oldValue = mailingList.getArchive();
			if (MevenideUtils.notEquivalent(newValue, oldValue)) {
				mailingList.setArchive(newValue);
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
			log.debug("getLabel called for " + o); //$NON-NLS-1$
		}
		return mailingList.getName() != null ? mailingList.getName() : Mevenide.getResourceString("AbstractPropertySource.Element.Unknown"); //$NON-NLS-1$
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.AbstractPomPropertySource#getSource()
	 */
	public Object getSource() {
		return mailingList;
	}
}
