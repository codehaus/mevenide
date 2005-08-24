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
package org.mevenide.ui.eclipse.editors.pom;

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