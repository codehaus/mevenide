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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
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
        Project proj = context.getPOMContext().getFinalProject();
        if (proj == null) {
            return null;
        }
        StringTokenizer tok = new StringTokenizer(key, ".", false);
        // skip "pom."
        tok.nextToken();
        Object currentReflectionObject = proj;
        while (tok.hasMoreTokens()) {
            String next = tok.nextToken();
//            next = "get" + Character.toUpperCase(next.charAt(0)) + next.substring(1);
            try {
                Field f = currentReflectionObject.getClass().getDeclaredField(next);
                f.setAccessible(true);
                currentReflectionObject = f.get(currentReflectionObject);
                f.setAccessible(false);
            } catch (NoSuchFieldException exc) {
                currentReflectionObject = null;
                logger.error("wrong pom definition=" + key, exc);
            } catch (Exception exc3) {
                // illegataccess + illegalargument
                currentReflectionObject = null;
                logger.error("wrong pom definition=" + key, exc3);
            }
            if (currentReflectionObject == null) {
                break;
            }
        }
        if (currentReflectionObject != null && currentReflectionObject instanceof String) {
            return currentReflectionObject.toString();
        }
        if (currentReflectionObject != null && (! (currentReflectionObject instanceof String))) {
            logger.warn("not a string pom value=" + key + " class=" + currentReflectionObject.getClass());
        }
        return null;
    }
    
    public void reload() {
    }
    
}
