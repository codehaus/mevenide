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
package org.mevenide.ui.eclipse.goals.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.mevenide.ui.eclipse.goals.model.Element;
import org.mevenide.ui.eclipse.goals.model.Goal;
import org.mevenide.ui.eclipse.preferences.PreferencesManager;
import org.mevenide.util.StringUtils;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: CustomPatternFilter.java,v 1.1 31 mars 2004 Exp gdodinet 
 * 
 */
public class CustomPatternFilter extends ViewerFilter {
	private static final Log log = LogFactory.getLog(CustomPatternFilter.class);

	public static final String CUSTOM_FILTERS_KEY = "mevenide.goals.outline.filter.custom";
	public static final String APPLY_CUSTOM_FILTERS_KEY = "mevenide.goals.outline.filter.custom.apply";

	private List filterPatterns = new ArrayList();
	private boolean shouldApply;

	
	public CustomPatternFilter() {
	    PreferencesManager preferencesManager = PreferencesManager.getManager();
		preferencesManager.loadPreferences();
		
		shouldApply = preferencesManager.getBooleanValue(APPLY_CUSTOM_FILTERS_KEY);
		setPatternFilters(preferencesManager.getValue(CUSTOM_FILTERS_KEY));
				
	}
	
	public void setPatternFilters(String customRegexFilters) {
		if ( !StringUtils.isNull(customRegexFilters) ) {
			StringTokenizer tokenizer = new StringTokenizer(customRegexFilters, ",");
			List patterns = new ArrayList(tokenizer.countTokens());
			while ( tokenizer.hasMoreTokens() ) {
				String pattern = tokenizer.nextToken();
				patterns.add(pattern);
			}
			setPatternFilters(patterns);
		}
		else {
			setPatternFilters((List) null);
		}
	}
	
	public boolean select(Viewer viewer, Object parentElement, Object element) {
	    log.debug("Custom fitlers are " + (shouldApply ? "enabled" : "disabled"));
	    if ( !shouldApply || !(element instanceof Element) ) {
	        return true;
	    }
	    if ( filterPatterns.size() > 0 ) {
			for (int j = 0; j < filterPatterns.size(); j++) {
			    Pattern pattern = (Pattern) filterPatterns.get(j);
			    Matcher matcher = pattern.matcher(((Element) element).getFullyQualifiedName());
			    if ( matcher.matches() ) {
			        return false;
			    }
				matcher = pattern.matcher(((Element) element).getName());
				if ( matcher.matches() ) {
			        return false;
			    }
			}
		}
		return true;
	}
	
	public void setPatternFilters(List patternsAsString) {
		filterPatterns.clear();
		if ( patternsAsString == null ) {
		    return;
		}
		for (int i = 0; i < patternsAsString.size(); i++) {
		    Pattern pattern = Pattern.compile((String) patternsAsString.get(i));
		    filterPatterns.add(pattern);
		}
	}

	public void addPatternFilter(String patternAsString) {
	    Pattern pattern = Pattern.compile(patternAsString);
		filterPatterns.add(pattern);
	}
	
	public void apply(boolean shouldApply) {
	    this.shouldApply = shouldApply;
	}
}
