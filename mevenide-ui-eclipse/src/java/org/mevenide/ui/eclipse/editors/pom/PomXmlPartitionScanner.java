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

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * Defines rules for scanning XML element tokens (tags) within an XML document.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PomXmlPartitionScanner extends RuleBasedPartitionScanner {
	public final static String XML_DEFAULT = "__XML_DEFAULT";
	public final static String XML_COMMENT = "__XML_COMMENT";
	public final static String XML_ELEMENT = "__XML_ELEMENT";

	public PomXmlPartitionScanner() {

		IToken commentToken = new Token(XML_COMMENT);
		IToken elementToken = new Token(XML_ELEMENT);

		IPredicateRule[] rules = new IPredicateRule[2];

		rules[0] = new MultiLineRule("<!--", "-->", commentToken);
		rules[1] = new PomXmlElementRule(elementToken);

		setPredicateRules(rules);
	}
}
