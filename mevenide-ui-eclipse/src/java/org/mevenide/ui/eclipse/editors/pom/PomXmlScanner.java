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

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.mevenide.ui.eclipse.MevenideColors;

/**
 * Defines rules for scanning XML processing instruction tokens
 * within an XML document.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PomXmlScanner extends RuleBasedScanner {

	public PomXmlScanner() {
		IToken processingInstructionToken = new Token(
			new TextAttribute(MevenideColors.LIGHT_GRAY)
		);

		IRule[] rules = new IRule[2];
		
		rules[0] = new SingleLineRule("<?", "?>", processingInstructionToken);  //$NON-NLS-1$//$NON-NLS-2$
		rules[1] = new WhitespaceRule(new PomXmlWhitespaceDetector());

		setRules(rules);
	}
}
