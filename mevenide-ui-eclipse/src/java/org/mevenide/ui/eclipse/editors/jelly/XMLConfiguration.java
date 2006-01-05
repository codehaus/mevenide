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

package org.mevenide.ui.eclipse.editors.jelly;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.widgets.Shell;
import org.mevenide.ui.eclipse.editors.jelly.contentassist.JellyContentAssistProcessor;
import org.mevenide.ui.eclipse.editors.jelly.contentassist.TagContentAssistProcessor;
import org.mevenide.ui.eclipse.editors.jelly.contentassist.TextContentAssistProcessor;

public class XMLConfiguration extends SourceViewerConfiguration {
	private static final Log log = LogFactory.getLog(XMLConfiguration.class);
	
	private XMLDoubleClickStrategy doubleClickStrategy;
	private XMLTagScanner tagScanner;
	private TagScanner scanner;
	private ColorManager colorManager;

	private Map tokens = new HashMap();
	 
	private AbstractJellyEditor editor;

    private JellyContentAssistProcessor tagContentAssist;
	
	public XMLConfiguration(ColorManager colorManager, AbstractJellyEditor editor) {
		this.colorManager = colorManager;
		this.editor = editor;
		generateToken("TextColor");
        generateToken("ProcessingColor");
        generateToken("NS1Color");
        generateToken("NS2Color");
        generateToken("NS3Color");
        generateToken("NS4Color");
        generateToken("NS5Color");
        generateToken("NS6Color");
        generateToken("NS7Color");
        generateToken("NS8Color");
        generateToken("NS9Color");
        generateToken("NS10Color");
        generateToken("AVColor");
        generateToken("StringColor");
        generateToken("DefinitionColor");
        generateToken("EntityColor");
        generateToken("CommentColor");
        generateToken("TagColor");
	}
    
    private void generateToken(String key) {
        IToken token = new Token(new TextAttribute(colorManager.getColor(key)));
        tokens.put(key, token);
    }

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return ITypeConstants.TYPES;
	}

    public IToken getToken(String key) {
        return (IToken) tokens.get(key);
    }
	
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new XMLDoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected XMLTagScanner getXMLTagScanner() {
		if (tagScanner == null) {
			tagScanner = new XMLTagScanner(this, IPreferenceConstants.SOURCE_EDITOR_COMMENT_COLOR);
		}
		return tagScanner;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        PresentationReconciler reconciler = new PresentationReconciler();
		DefaultDamagerRepairer dr;

		dr = new DefaultDamagerRepairer(new RuleBasedPartitionScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

        dr = new DefaultDamagerRepairer(getXMLTagScanner());
        reconciler.setDamager(dr, ITypeConstants.TEXT);
        reconciler.setRepairer(dr, ITypeConstants.TEXT);
        reconciler.setDamager(dr, ITypeConstants.DECL);
        reconciler.setRepairer(dr, ITypeConstants.DECL);
		reconciler.setDamager(dr, ITypeConstants.START_DECL);
		reconciler.setRepairer(dr, ITypeConstants.START_DECL);
		reconciler.setDamager(dr, ITypeConstants.END_DECL);
		reconciler.setRepairer(dr, ITypeConstants.END_DECL);
        reconciler.setDamager(dr, ITypeConstants.COMMENT);
        reconciler.setRepairer(dr, ITypeConstants.COMMENT);
        
		dr = new DefaultDamagerRepairer(getTagScanner());
        reconciler.setDamager(dr, ITypeConstants.TAG);
        reconciler.setRepairer(dr, ITypeConstants.TAG);
        reconciler.setDamager(dr, ITypeConstants.ENDTAG);
        reconciler.setRepairer(dr, ITypeConstants.ENDTAG);
        reconciler.setDamager(dr, ITypeConstants.EMPTYTAG);
        reconciler.setRepairer(dr, ITypeConstants.EMPTYTAG);
        reconciler.setDamager(dr, ITypeConstants.PI);
        reconciler.setRepairer(dr, ITypeConstants.PI);

		return reconciler;
	}

	private TagScanner getTagScanner() {
	    if ( scanner == null ) {
	        scanner = new TagScanner(this, getToken(IPreferenceConstants.SOURCE_EDITOR_TAG_COLOR));
	    }
	    return scanner;
    }

    public IContentAssistant getContentAssistant(ISourceViewer viewer) {
		ContentAssistant assistant = new ContentAssistant();
		try {
		    ContentAssistant assi = new ContentAssistant();
	        JellyContentAssistProcessor contentAssistForText = new TextContentAssistProcessor(editor);
	        tagContentAssist = new TagContentAssistProcessor(editor);
           
	        assi.setContentAssistProcessor(contentAssistForText, IDocument.DEFAULT_CONTENT_TYPE);
	        assi.setContentAssistProcessor(tagContentAssist, ITypeConstants.TAG);
	        assi.setContentAssistProcessor(contentAssistForText, ITypeConstants.TEXT);
	        assi.setContentAssistProcessor(tagContentAssist, ITypeConstants.ENDTAG);
	        assi.setContentAssistProcessor(tagContentAssist, ITypeConstants.EMPTYTAG);
			assi.enableAutoActivation(true);
	        assi.enableAutoInsert(true);
			assi.install(viewer);
			
			return assi;
			   
		} 
	    catch (Exception e) {
			log.error("Unable to create ContentAssistant. Unasigned assistant will be returned.", e);
		}
		return assistant;
	}
	
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				//return  new SourceViewerInformationControl(parent);
				return null;
			}
		};
	}
	
    public JellyContentAssistProcessor getTagContentAssist() {
        return tagContentAssist;
    }
}