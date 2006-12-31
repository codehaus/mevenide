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

package org.mevenide.ui.eclipse.goals.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.goals.model.Element;
import org.mevenide.util.StringUtils;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: CustomPatternFilter.java,v 1.1 31 mars 2004 Exp gdodinet 
 * 
 */
public class CustomPatternFilter extends ViewerFilter {
	public static final String CUSTOM_FILTERS_KEY = "mevenide.goals.outline.filter.custom"; //$NON-NLS-1$
	public static final String APPLY_CUSTOM_FILTERS_KEY = "mevenide.goals.outline.filter.custom.apply"; //$NON-NLS-1$

	private List filterPatterns = new ArrayList();
	private boolean shouldApply;

	
	public CustomPatternFilter() {
		shouldApply = getPreferenceStore().getBoolean(APPLY_CUSTOM_FILTERS_KEY);
		setPatternFilters(getPreferenceStore().getString(CUSTOM_FILTERS_KEY));
				
	}
	
	public void setPatternFilters(String customRegexFilters) {
		if ( !StringUtils.isNull(customRegexFilters) ) {
			StringTokenizer tokenizer = new StringTokenizer(customRegexFilters, ","); //$NON-NLS-1$
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
	    //log.debug("Custom filters are " + (shouldApply ? "enabled" : "disabled"));
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

    /**
     * @return the preference store to use in this object
     */
    private IPersistentPreferenceStore getPreferenceStore() {
        return Mevenide.getInstance().getCustomPreferenceStore();
    }
}
