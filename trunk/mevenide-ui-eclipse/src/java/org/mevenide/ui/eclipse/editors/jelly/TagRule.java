package org.mevenide.ui.eclipse.editors.jelly;


import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;


public class TagRule implements IPredicateRule {
	
	public static final IToken TAG = new Token(ITypeConstants.TAG);
    public static final IToken ENDTAG = new Token(ITypeConstants.ENDTAG);
    public static final IToken TEXT = new Token(ITypeConstants.TEXT);
    public static final IToken PI = new Token(ITypeConstants.PI);
    public static final IToken DECLARATION = new Token(ITypeConstants.DECL);
	public static final IToken START_DECLARATION = new Token(ITypeConstants.START_DECL);
	public static final IToken END_DECLARATION = new Token(ITypeConstants.END_DECL);
    public static final IToken COMMENT = new Token(ITypeConstants.COMMENT);
    public static final IToken EMPTYTAG = new Token(ITypeConstants.EMPTYTAG);

    public IToken evaluate(ICharacterScanner scanner) {
        IToken result = Token.EOF;
        int c = scanner.read();
        if (c == -1) {
            return Token.EOF;
        }
        if (c == ']') {
            c = scanTo(scanner, ">", true);
            return END_DECLARATION;
        }
        else if (c != '<') {
            while (c != -1 && c != '<' && c != ']') {
                c = scanner.read();
            }
            scanner.unread();
            return TEXT;
        }
        else {
            result = TAG;
            c = scanner.read();
            switch (c) {
                case '!' :
                    result = DECLARATION;
                    c = scanner.read();
                    if (c == '-') {
                        c = scanner.read();
                        if (c == '-') {
                            c = scanner.read();
                            result = COMMENT;
                            c = scanTo(scanner, "-->", false);
                        }
                        else {
                            c = findFirstOf(scanner, '>', '[', true);
                            if (c == '>') {
                                return DECLARATION;
                            }
                            else {
                                return START_DECLARATION;
                            }
                        }
                    }
                    else {
                        scanner.unread();
                        if (isNext(scanner, "[CDATA[")) {
                            result = TEXT;
                            c = scanTo(scanner, "]]>", false);
                        }
                        else {
                            c = findFirstOf(scanner, '>', '[', true);
                            if (c == '>') {
                                return DECLARATION;
                            }
                            else {
                                return START_DECLARATION;
                            }
                        }
                    }
                    break;
                case '?' :
                    result = PI;
                    c = scanTo(scanner, "?>", false);
                    break;
                case '>' :
                    break;
                case '/' :
                    result = ENDTAG;
                    c = scanTo(scanner, ">", true);
                    break;
                default :
                    c = scanTo(scanner, ">", true);
                    if (c != -1) {
                        scanner.unread();
                        scanner.unread();
                        if (scanner.read() == '/') {
                            result = EMPTYTAG;
                        }
                        scanner.read();
                    }
                    break;
            }
        }
        return result;
    }
    
   private boolean isNext(ICharacterScanner scanner, String s) {
		int pos = 0;
		
		while (pos < s.length()) {
			int c = scanner.read();
			if (c != s.charAt(pos)) {
				for (int i = 0; i < pos; i++) {
					scanner.unread();
				}
				return false;
			}
			pos++;
		}
		
		return true;
	}

	private int scanTo(ICharacterScanner scanner, String end, boolean quoteEscapes) {
        int c = 0, 
            i = 0;
        boolean inSingleQuote = false, 
                inDoubleQuote = false;
        
        do {
            c = scanner.read();
            if (c == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
                i = 0;
            }
            else if (c == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
                i = 0;
            }
            else if (!inSingleQuote && !inDoubleQuote) {
                if (c == end.charAt(i)) {
                    i++;
                }
                else if (i > 0) {
                    i = 0;
                }
            }
            if (i >= end.length()) {
                return c;
            }
        }
        while (c != -1);
        return c;
    }

	private int findFirstOf(ICharacterScanner scanner, char one, char other, boolean quoteEscapes) {
		int c;
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        do {
            c = scanner.read();
            if (c == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
            }
            else if (c == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
            }
            else if (!inSingleQuote && !inDoubleQuote) {
                if (c == one) {
                    return c;
                }
                else if (c == other) {
                    return c;
                }
            }
        }
        while (c != -1);
        return c;
	}
	
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
        return evaluate(scanner);
    }

    public IToken getSuccessToken() {
        return Token.EOF;
    }
}