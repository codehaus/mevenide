/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.idea.util.ui.text;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.Document;
import org.mevenide.idea.Res;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/**
 * @author Arik
 */
public class JTextComponentJDomBinder implements DocumentListener {
    private static final Log LOG = LogFactory.getLog(JTextComponentJDomBinder.class);
    private static final Res RES = Res.getInstance(JTextComponentJDomBinder.class);

    private final Object LOCK = new Object();

    private final Element root;
    private final String[] children;

    public JTextComponentJDomBinder(final Element pRoot) {
        this(pRoot, (String[]) null);
    }

    public JTextComponentJDomBinder(final Element pRoot,
                                    final String pChildName) {
        this(pRoot, new String[]{pChildName});
    }

    public JTextComponentJDomBinder(final Element pRoot,
                                    final String[] pChildren) {
        if (pRoot == null)
            throw new IllegalArgumentException(RES.get("null.arg", "root"));
        root = pRoot;

        if (pChildren == null)
            children = new String[0];
        else
            children = pChildren;
    }

    protected String getDocumentText(final DocumentEvent pEvent) {
        String data;
        try {
            data = pEvent.getDocument().getText(0, pEvent.getDocument().getLength());
            if (data != null && data.length() == 0)
                data = null;
        }
        catch (BadLocationException e) {
            LOG.error(e.getMessage(), e);
            data = null;
        }
        return data;
    }

    protected Element getElement(final boolean pCreateIfNotFound) {
        Element context = root;
        for (String aChildren : children) {
            Element child = context.getChild(aChildren);
            if (child == null) {
                if(pCreateIfNotFound) {
                    child = new Element(aChildren);
                    context.addContent(child);
                }
                else
                    return null;
            }
            context = child;
        }
        return context;
    }

    private void setText(final String pDocumentText) {
        if(pDocumentText == null || pDocumentText.length() == 0) {
            Element context = getElement(false);
            if(context != null)
                context.getParent().removeContent(context);
        }
        else {
            Element context = getElement(true);
            context.setText(pDocumentText);
        }
    }

    public final void insertUpdate(final DocumentEvent pEvent) {
        synchronized (LOCK) {
            final String documentText = getDocumentText(pEvent);
            pEvent.getDocument().putProperty("dirty", true);
            setText(documentText);
        }
    }

    public final void removeUpdate(final DocumentEvent pEvent) {
        synchronized (LOCK) {
            final String documentText = getDocumentText(pEvent);
            pEvent.getDocument().putProperty("dirty", true);
            setText(documentText);
        }
    }

    public final void changedUpdate(final DocumentEvent pEvent) {
        synchronized (LOCK) {
            final String documentText = getDocumentText(pEvent);
            pEvent.getDocument().putProperty("dirty", true);
            setText(documentText);
        }
    }

    public static boolean isDirty(final JTextComponent pComponent) {
        final Object dirty = pComponent.getDocument().getProperty("dirty");
        if(dirty == null)
            return false;

        if(dirty instanceof Boolean)
            return (Boolean) dirty;

        return Boolean.valueOf(dirty.toString());
    }

    public static void bind(final JTextComponent pComponent,
                            final Document pDocument,
                            final String pElementName) {
        bind(pComponent, pDocument, new String[]{pElementName});
    }

    public static void bind(final JTextComponent pComponent,
                            final Document pDocument,
                            final String[] pElementNames) {
        bind(pComponent, pDocument.getRootElement(), pElementNames);
    }

    public static void bind(final JTextComponent pComponent, final Element pElement) {
        bind(pComponent, pElement, (String)null);
    }

    public static void bind(final JTextComponent pComponent,
                            final Element pElement,
                            final String pElementName) {
        bind(pComponent, pElement, new String[]{pElementName});
    }

    public static void bind(final JTextComponent pComponent,
                            final Element pElement,
                            final String[] pElementNames) {
        final JTextComponentJDomBinder listener = new JTextComponentJDomBinder(
                pElement, pElementNames);

        //
        //before we attach the listener to the component, we should first synchronize
        //them by getting the current text from the JDom element, and set it as the
        //current text for the component. Only AFTER that we can attach the listener -
        //otherwise, the 'setText' method will trigger a DocumentEvent which will
        //set the text back into the JDom element. This is not critical, but is a
        //pointless duplication which can be avoided.
        //
        final Element contextElt = listener.getElement(false);
        if(contextElt != null) {
            final String text = contextElt.getText();
            pComponent.setText(text);
        }

        //
        //attach the listener to the component
        //
        pComponent.getDocument().addDocumentListener(listener);
    }
}
