/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Jeffrey Bonevich (jeff@bonevich.com).  All rights
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
package org.mevenide.ui.eclipse.editors.pages;

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
				adaptor.overrideParent("");
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
            log.debug("field changed to inherited = " + inherited);
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
        if (overrideToggle != null) {
            overrideToggle.setSelection(!enable);
        }
        if (browseButton != null) {
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
