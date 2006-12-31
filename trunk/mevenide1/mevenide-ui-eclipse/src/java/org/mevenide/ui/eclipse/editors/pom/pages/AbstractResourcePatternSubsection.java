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
package org.mevenide.ui.eclipse.editors.pom.pages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.adapters.properties.ResourcePatternProxy;
import org.mevenide.ui.eclipse.editors.pom.entries.TableEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public abstract class AbstractResourcePatternSubsection {

	public abstract TableEntry createWidget(Composite container, FormToolkit factory, boolean isOverrideable);

	void updateTableEntries(
		TableEntry table,
		List pomCollection,
		List inheritedCollection,
		boolean isIncludePattern) {
		
		table.removeAll();
		if (pomCollection != null && !pomCollection.isEmpty()) {
			table.addEntries(convertToResourcePatternProxies(pomCollection, isIncludePattern));
			table.setInherited(false);
		}
		else if (inheritedCollection != null) {
			table.addEntries(convertToResourcePatternProxies(inheritedCollection, isIncludePattern), true);
			table.setInherited(true);
		}
		else {
			table.setInherited(false);
		}
	}
	
	List convertToResourcePatternProxies(List resourcePatterns, boolean isIncludePattern) {
		List proxies = null;
		if (resourcePatterns != null) {
			proxies = new ArrayList(resourcePatterns.size());
			Iterator itr = resourcePatterns.iterator();
			while (itr.hasNext()) {
				proxies.add(new ResourcePatternProxy((String) itr.next(), isIncludePattern));
			}
		}
		return proxies;
	}

}
