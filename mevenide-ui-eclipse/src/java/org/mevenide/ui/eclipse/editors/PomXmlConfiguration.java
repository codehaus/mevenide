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
package org.mevenide.ui.eclipse.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.mevenide.ui.eclipse.MevenideColors;

/**
 * Configuration for the POM XML document viewer.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PomXmlConfiguration extends SourceViewerConfiguration {

    private PomXmlDoubleClickStrategy doubleClickStrategy;
    private PomXmlElementScanner elementScanner;
    private PomXmlScanner scanner;

    public PomXmlConfiguration() {
    }

    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return new String[] {
            IDocument.DEFAULT_CONTENT_TYPE,
            PomXmlPartitionScanner.XML_COMMENT,
            PomXmlPartitionScanner.XML_ELEMENT 
        };
    }

    public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
        if (doubleClickStrategy == null) {
            doubleClickStrategy = new PomXmlDoubleClickStrategy();
        }
        return doubleClickStrategy;
    }

    private PomXmlScanner getXMLScanner() {
        if (scanner == null) {
            scanner = new PomXmlScanner();
            scanner.setDefaultReturnToken(new Token(new TextAttribute(MevenideColors.BLACK)));
        }
        return scanner;
    }

    private PomXmlElementScanner getXMLElementScanner() {
        if (elementScanner == null) {
            elementScanner = new PomXmlElementScanner();
            elementScanner.setDefaultReturnToken(new Token(new TextAttribute(MevenideColors.DARK_BLUE)));
        }
        return elementScanner;
    }

    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        PresentationReconciler reconciler = new PresentationReconciler();

        DefaultDamagerRepairer repairman = new DefaultDamagerRepairer(getXMLElementScanner());
        reconciler.setDamager(repairman, PomXmlPartitionScanner.XML_ELEMENT);
        reconciler.setRepairer(repairman, PomXmlPartitionScanner.XML_ELEMENT);

		repairman = new DefaultDamagerRepairer(getXMLScanner());
        reconciler.setDamager(repairman, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(repairman, IDocument.DEFAULT_CONTENT_TYPE);

        NonRuleBasedDamagerRepairer unrulyRepairman = new NonRuleBasedDamagerRepairer(new TextAttribute(MevenideColors.DARK_RED));
        reconciler.setDamager(unrulyRepairman, PomXmlPartitionScanner.XML_COMMENT);
        reconciler.setRepairer(unrulyRepairman, PomXmlPartitionScanner.XML_COMMENT);

        return reconciler;
    }

}