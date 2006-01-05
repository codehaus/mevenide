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
package org.mevenide.ui.eclipse.editors.pom;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.mevenide.ui.eclipse.MevenideColors;

/**
 * Defines rules for scanning XML tokens within an XML element (tag).
 * Not really used in the PomXmlSourcePage editor since Maven schema defines
 * only elements, no attributes.  But, hey, someday it may be useful.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PomXmlElementScanner extends RuleBasedScanner {

	public PomXmlElementScanner() {
		IToken literalToken = new Token(new TextAttribute(MevenideColors.GREEN));

		IRule[] rules = new IRule[3];
		rules[0] = new SingleLineRule("\"", "\"", literalToken, '\\'); //double-quote //$NON-NLS-1$ //$NON-NLS-2$
		rules[1] = new SingleLineRule("'", "'", literalToken, '\\');   //single-quote //$NON-NLS-1$ //$NON-NLS-2$
		rules[2] = new WhitespaceRule(new PomXmlWhitespaceDetector()); //whitespace

		setRules(rules);
	}
}
