/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
        /**
	 * we assume that fileName follows that kind of pattern that is pretty 
	 * general : "^(.*)-(\\d.*)\\.(\\w+)$"
	 *  (.|(-\\D)*)-((\\d)+(.*))\\.(\\w+)
	 * so we have $2 => version ; $2 => extension 
         * 
	 * This assumes also that the file has not a multi-extension (e.g. tar.gz)
	 * someone please provide with a more correct pattern$
         */
        private static final Pattern p = Pattern.compile("^((?:[^-]|(?:-\\D))*)-(\\d+(?:-|\\.|\\w)*)\\.(\\w+)$");
	
	public DependencySplitter(String fileNameToSplit) {
		this.fileName = fileNameToSplit;
	}

	/**
	 *  
	 * we should provide a mecanism to allow the user to specify more patterns that
	 * will be successfully applied still success considering, e.g.,
	 * that (artifactId, version and extension not null) implies success  
	 *
	 * @see IDependencySplitter#split()
	 */
	public DependencyParts split() {
		Matcher m = p.matcher(fileName);
		
		DependencyParts dependencyParts = new DependencyParts();
                if (m.matches()) {
                    dependencyParts.artifactId = m.group(1);
                    dependencyParts.version = m.group(2);
                    dependencyParts.extension = m.group(3);
		}
		
		return dependencyParts;
	}
}
