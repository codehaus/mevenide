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
 *       "This product includes software licensed under 
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
package org.mevenide.ui.eclipse.preferences;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.preference.PreferenceStore;

import org.mevenide.ui.eclipse.Mevenide;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class PreferencesManager {
	private static Log log = LogFactory.getLog(PreferencesManager.class);
	
	private PreferenceStore preferenceStore;
	
	public void loadPreferences() {
		preferenceStore = new PreferenceStore(getPreferenceStoreFilename());
		try {
			preferenceStore.load();
		}
		catch ( Exception ex ) {
			log.debug("Unable to load preferences from file '" + getPreferenceStoreFilename() + "' due to : " + ex);
		}
	}
	
	public boolean store() {
		try {
			preferenceStore.save();		
			return true;
		}
		catch (IOException e) {
			log.debug("Unable to save preferences to file '" + getPreferenceStoreFilename() + "' due to : " + e);
			return false;
		}
	}
	
	private String getPreferenceStoreFilename() {
		return Mevenide.getPlugin().getPreferencesFilename() ; 
	}
	
	public String getValue(String property) {
		return preferenceStore.getString(property);
	}
	
	public void setValue(String property, String value) {
		preferenceStore.setValue(property, value);
	}
	
	public boolean getBooleanValue(String property) {
		return preferenceStore.getBoolean(property);
	}
	
	public int getIntValue(String property) {
		return preferenceStore.getInt(property);
	}
	
	public void setBooleanValue(String property, boolean value) {
		preferenceStore.setValue(property, value);
	}
	
	public void setIntValue(String property, int value) {
		preferenceStore.setValue(property, value);
	}
		
	public PreferenceStore getPreferenceStore() {
		return preferenceStore;
	}

	public void setPreferenceStore(PreferenceStore store) {
		preferenceStore = store;
	}

}
