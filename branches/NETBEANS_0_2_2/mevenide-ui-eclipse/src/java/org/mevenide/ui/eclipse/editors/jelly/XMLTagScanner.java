package org.mevenide.ui.eclipse.editors.jelly;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

public class XMLTagScanner implements ITokenScanner {
    private IToken token;
    private int offset;
    private int length;
    private boolean returned = false;
    
    public XMLTagScanner(XMLConfiguration config, String type) {
        token = config.getToken(type);
    }

    
    public void setRange(IDocument document, int offset, int length) {
        returned = false;
        this.offset = offset;
        this.length = length;
    }

    public IToken nextToken() {
        if (!returned) {
            returned = true;
            return token;
        }
        
        return Token.EOF;
    }

    public int getTokenOffset() {
        return offset;
    }

    public int getTokenLength() {
        return length;
    }
}