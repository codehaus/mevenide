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
package org.mevenide.properties.resolver;

import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.mevenide.context.IQueryContext;
import org.mevenide.properties.IPropertyFinder;


/**
 * IQueryContext based replacement for ProjectWalker. fits into the resolver pattern.
 * Is used in PropertyFilesAggregator to resolve ${pom. values)
 * I guess we should rename the propertyFilesAgreggator.
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 *
 */
public class ProjectWalker2 implements IPropertyFinder {
    private static final Log logger = LogFactory.getLog(ProjectWalker2.class);
    
    private IQueryContext context;
    public ProjectWalker2(IQueryContext qcontext) {
        context = qcontext;
    }
    
    
    public String getValue(String key) {
        if (!key.startsWith("pom.")) {
            return null;
        }
        Element proj = context.getPOMContext().getRootProjectElement();
        if (proj == null) {
            return null;
        }
        StringTokenizer tok = new StringTokenizer(key, ".", false);
        // skip "pom."
        tok.nextToken();
        Element currentElement = proj;
        while (tok.hasMoreTokens()) {
            String next = tok.nextToken();
            List nextEl = currentElement.getChildren(next);
            if (nextEl != null && nextEl.size() == 1) {
                currentElement = (Element)nextEl.iterator().next();
            } else {
                currentElement = null;
                break;
            }
        }
        if (currentElement != null) {
            return currentElement.getText();
        }
        logger.debug("could not find=" + key + ". Maybe it's not defined in the current POM or it's badly formed");
        return null;
    }
    
    public void reload() {
    }
    
}
