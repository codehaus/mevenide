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
package org.mevenide.ui.eclipse.editors.pom.entries;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

/**
 * Wrapper for the SWT Text widgets that are overridable in the POM editor.
 * Accepts a companion checkbox to toggle between overriding a parent POM
 * value and accepting the parent value.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class OverridableTextEntry extends TextEntry {

    private static final Log log = LogFactory.getLog(OverridableTextEntry.class);

    private Button overrideToggle;
	private Button browseButton;
    private boolean inherited;

    private class OverridableSelectionAdapter extends SelectionAdapter {
    	private IOverrideAdaptor adaptor;
    	
    	private OverridableSelectionAdapter(IOverrideAdaptor adaptor) {
    		this.adaptor = adaptor;
    	}
    	
        public void widgetSelected(SelectionEvent e) {
            toggleOverride();
            if (isInherited()) {
                setText((String) adaptor.acceptParent());
				adaptor.overrideParent(null);
            }
            else {
                setText(null);
				adaptor.overrideParent(""); //$NON-NLS-1$
            }
			adaptor.refreshUI();
        }
    }

    public OverridableTextEntry(Text text, Button overrideToggle) {
        this(text, overrideToggle, null);
    }

	public OverridableTextEntry(Text text, Button overrideToggle, Button browseButton) {
		super(text);
		this.overrideToggle = overrideToggle;
		this.browseButton = browseButton;
	}

    public void addOverrideAdaptor(IOverrideAdaptor adaptor) {
        if (overrideToggle != null) {
            overrideToggle.addSelectionListener(new OverridableSelectionAdapter(adaptor));
        }
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        if (log.isDebugEnabled()) {
            log.debug("field changed to inherited = " + inherited); //$NON-NLS-1$
        }
        this.inherited = inherited;
        setEnabled(!inherited);
        setFocus();
        if (overrideToggle != null) {
            overrideToggle.setToolTipText(inherited ? INHERITED_TOOLTIP : OVERRIDEN_TOOLTIP);
        }
    }

    public void setEnabled(boolean enable) {
        super.setEnabled(enable);
        if (overrideToggle != null && !overrideToggle.isDisposed()) {
            overrideToggle.setSelection(!enable);
        }
        if (browseButton != null && !browseButton.isDisposed()) {
        	browseButton.setEnabled(enable);
        }
    }

    protected void toggleOverride() {
        setInherited(!inherited);
    }

    public void addBrowseButtonListener(SelectionListener listener) {
    	if (browseButton != null) {
    		browseButton.addSelectionListener(listener);
    	}
    }

}
