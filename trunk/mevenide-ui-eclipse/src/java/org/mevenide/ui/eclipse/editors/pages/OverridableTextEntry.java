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
import org.mevenide.ui.eclipse.Mevenide;

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

    private static final String INHERITED_TOOLTIP =
        Mevenide.getResourceString("OverridableTextEntry.toggle.tooltip.inherited");
    private static final String OVERRIDEN_TOOLTIP =
        Mevenide.getResourceString("OverridableTextEntry.toggle.tooltip.overriden");

    private Button overrideToggle;
    private boolean inherited;

    public abstract class OverridableSelectionAdapter extends SelectionAdapter {
        public void widgetSelected(SelectionEvent e) {
            toggleOverride();
            if (isInherited()) {
                setText(getParentProjectAttribute());
                updateProject(null);
            }
            else {
                setText(null);
                updateProject("");
            }
            refreshUI();
        }

        public abstract void updateProject(String value);

        public abstract String getParentProjectAttribute();

		public abstract void refreshUI();
    }

    public OverridableTextEntry(Text text, Button overrideToggle) {
        super(text);
        this.overrideToggle = overrideToggle;
    }

    public void addSelectionListener(SelectionListener listener) {
        if (overrideToggle != null) {
            overrideToggle.addSelectionListener(listener);
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
    }

    protected void toggleOverride() {
        setInherited(!inherited);
    }

}
