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
