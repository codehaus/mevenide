package org.mevenide.ui.eclipse.editors.jelly;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

public class TagScanner extends RuleBasedScanner {
    
    public TagScanner(final XMLConfiguration config, final IToken defaultToken) {
        IRule[] rules = new IRule[4];

        rules[0] = new WhitespaceRule(new XMLWhitespaceDetector());
        rules[1] = new SingleLineRule("\"", "\"", config.getToken(IPreferenceConstants.SOURCE_EDITOR_AV_COLOR), '\\');
        rules[2] = new SingleLineRule("\'", "\'", config.getToken(IPreferenceConstants.SOURCE_EDITOR_AV_COLOR), '\\');
        rules[3] = new IRule() {
            public IToken evaluate(ICharacterScanner scanner) {
                int c = scanner.read();
                
                if (c == -1) {
                    return Token.EOF;
                } else if (c == '"' || c == '\'') {
					return config.getToken(IPreferenceConstants.SOURCE_EDITOR_AV_COLOR);
				}
                while (c != -1 && c != '"' && c != '\'' && !Character.isWhitespace((char) c) && c!= '>') {
                    c = scanner.read();
                }
                
                if (c != -1 && c!= '>') {
                    scanner.unread();
                } 

                return defaultToken;
            }
        };
        
        setRules(rules);
    }

}
