package org.mevenide.ui.eclipse.editors.mavenxml;

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
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.widgets.Shell;
import org.mevenide.ui.eclipse.editors.mavenxml.contentassist.MavenXmlContentAssistProcessor;
import org.mevenide.ui.eclipse.editors.mavenxml.contentassist.SourceViewerInformationControl;
import org.mevenide.ui.eclipse.editors.mavenxml.contentassist.TagContentAssistProcessor;
import org.mevenide.ui.eclipse.editors.mavenxml.contentassist.TextContentAssistProcessor;

public class XMLConfiguration extends SourceViewerConfiguration {
	private static final Log log = LogFactory.getLog(XMLConfiguration.class);
	
	private XMLDoubleClickStrategy doubleClickStrategy;
	private XMLTagScanner tagScanner;
	private TagScanner scanner;
	private ColorManager colorManager;

	private Map tokens = new HashMap();
	 
	private MavenXmlEditor editor;
	
	public XMLConfiguration(ColorManager colorManager, MavenXmlEditor editor) {
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
        ITokenScanner scanner = new RuleBasedPartitionScanner();

		dr = new DefaultDamagerRepairer(scanner);
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

        scanner = new XMLTagScanner(this, IPreferenceConstants.SOURCE_EDITOR_TEXT_COLOR);
        dr = new DefaultDamagerRepairer(scanner);
        reconciler.setDamager(dr, ITypeConstants.TEXT);
        reconciler.setRepairer(dr, ITypeConstants.TEXT);

        scanner = new TagScanner(this, getToken(IPreferenceConstants.SOURCE_EDITOR_TAG_COLOR));
        dr = new DefaultDamagerRepairer(scanner);
        reconciler.setDamager(dr, ITypeConstants.TAG);
        reconciler.setRepairer(dr, ITypeConstants.TAG);

        scanner = new TagScanner(this, getToken(IPreferenceConstants.SOURCE_EDITOR_TAG_COLOR));
        dr = new DefaultDamagerRepairer(scanner);
        reconciler.setDamager(dr, ITypeConstants.ENDTAG);
        reconciler.setRepairer(dr, ITypeConstants.ENDTAG);

        scanner = new TagScanner(this, getToken(IPreferenceConstants.SOURCE_EDITOR_TAG_COLOR));
        dr = new DefaultDamagerRepairer(scanner);
        reconciler.setDamager(dr, ITypeConstants.EMPTYTAG);
        reconciler.setRepairer(dr, ITypeConstants.EMPTYTAG);

        scanner = new TagScanner(this, getToken(IPreferenceConstants.SOURCE_EDITOR_PI_COLOR));
        dr = new DefaultDamagerRepairer(scanner);
        reconciler.setDamager(dr, ITypeConstants.PI);
        reconciler.setRepairer(dr, ITypeConstants.PI);

        scanner = new XMLTagScanner(this, IPreferenceConstants.SOURCE_EDITOR_DEFINITION_COLOR);
        dr = new DefaultDamagerRepairer(scanner);
        reconciler.setDamager(dr, ITypeConstants.DECL);
        reconciler.setRepairer(dr, ITypeConstants.DECL);

		scanner = new XMLTagScanner(this, IPreferenceConstants.SOURCE_EDITOR_DEFINITION_COLOR);
		dr = new DefaultDamagerRepairer(scanner);
		reconciler.setDamager(dr, ITypeConstants.START_DECL);
		reconciler.setRepairer(dr, ITypeConstants.START_DECL);

		scanner = new XMLTagScanner(this, IPreferenceConstants.SOURCE_EDITOR_DEFINITION_COLOR);
		dr = new DefaultDamagerRepairer(scanner);
		reconciler.setDamager(dr, ITypeConstants.END_DECL);
		reconciler.setRepairer(dr, ITypeConstants.END_DECL);

        scanner = new XMLTagScanner(this, IPreferenceConstants.SOURCE_EDITOR_COMMENT_COLOR);
        dr = new DefaultDamagerRepairer(scanner);
        reconciler.setDamager(dr, ITypeConstants.COMMENT);
        reconciler.setRepairer(dr, ITypeConstants.COMMENT);
        
		return reconciler;
	}

	public IContentAssistant getContentAssistant(ISourceViewer viewer) {
		ContentAssistant assistant = new ContentAssistant();
		try {
		    ContentAssistant assi = new ContentAssistant();
	        MavenXmlContentAssistProcessor contentAssistForText = new TextContentAssistProcessor(editor);
	        MavenXmlContentAssistProcessor contentAssistForTags = new TagContentAssistProcessor(editor);

			assi.setContentAssistProcessor(contentAssistForText, IDocument.DEFAULT_CONTENT_TYPE);
	        assi.setContentAssistProcessor(contentAssistForTags, ITypeConstants.TAG);
	        assi.setContentAssistProcessor(contentAssistForText, ITypeConstants.TEXT);
	        assi.setContentAssistProcessor(contentAssistForTags, ITypeConstants.ENDTAG);
	        assi.setContentAssistProcessor(contentAssistForTags, ITypeConstants.EMPTYTAG);
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
				return  new SourceViewerInformationControl(parent);
			}
		};
	}
}