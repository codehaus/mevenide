/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.project.dependency;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencySplitter implements IDependencySplitter {
	
	private String fileName;
	
	public DependencySplitter(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * we assume that fileName follows that kind of pattern that is pretty 
	 * general : "(.|(-\\D)*)-((\\d)+(.*))\\.(\\w+)"
	 * 
	 * so we have $4 => version ; $7 => extension 
	 * 
	 * This assumes also that the file has not a multi-extension (e.g. tar.gz)
	 * someone please provide with a more correct pattern$
	 * 
	 *  
	 * we should provide a mecanism to allow the user to specify more patterns that
	 * will be successfully applied still success considering, e.g.,
	 * that (artifactId, version and extension not null) implies success  
	 *
	 * @see IDependencySplitter#split()
	 */
	public DependencyParts split() {
	
		Pattern p = Pattern.compile("(.|(-\\D)*)-((\\d)+(.*))\\.(\\w+)");
		
		return applySplitStrategy(p, 3, 6);
		
	}

	private DependencyParts applySplitStrategy(Pattern p, int expectedVersionIndex, int expectedExtensionIndex) {
		Matcher m = p.matcher(fileName);
		
		String[] allGroups = new String[m.groupCount() + 1];
		
		int i = 0;
		while ( i < m.groupCount() + 1 && m.find(i) ) {
			allGroups[i] = m.group(i);
			i++;
		}
		
		DependencyParts dependencyParts = new DependencyParts();
		dependencyParts.version = allGroups[expectedVersionIndex];
		dependencyParts.extension = allGroups[expectedExtensionIndex];
		if ( dependencyParts.version != null ) {
			dependencyParts.artifactId = fileName.substring(0, fileName.indexOf(dependencyParts.version) - 1);
		}
		
		return dependencyParts;
	}
}
