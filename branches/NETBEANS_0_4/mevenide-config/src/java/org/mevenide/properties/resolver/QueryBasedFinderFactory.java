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
package org.mevenide.properties.resolver;

import org.mevenide.context.IQueryContext;
import org.mevenide.properties.IPropertyFinder;

/**  
 * factory for creating IPropertyFinder instances that delegates to the IQueryContext.
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 * 
 */
final class QueryBasedFinderFactory {
    
    static IPropertyFinder createUserPropertyFinder(IQueryContext context) {
        return new UserPropertyFinder(context);
    }

    static IPropertyFinder createBuildPropertyFinder(IQueryContext context) {
        return new BuildPropertyFinder(context);
    }

    static IPropertyFinder createProjectPropertyFinder(IQueryContext context) {
        return new ProjectPropertyFinder(context);
    }

    private static abstract class PropertyFinder implements IPropertyFinder {
        protected IQueryContext context;
        
        protected PropertyFinder(IQueryContext querycontext) {
            context = querycontext;
        }

        public void reload() {
            // ignored, querycontext is self-reloading..
        }
    }
    
    private static final class UserPropertyFinder extends PropertyFinder {
        
        public UserPropertyFinder(IQueryContext context) {
            super(context);
        }
        
        public String getValue(String key) {
            return context.getUserPropertyValue(key);
        }
    }
    
    private static final class BuildPropertyFinder extends PropertyFinder {
        
        public BuildPropertyFinder(IQueryContext context) {
            super(context);
        }
        
        public String getValue(String key) {
            return context.getBuildPropertyValue(key);
        }
    }

    private static final class ProjectPropertyFinder extends PropertyFinder {
        
        public ProjectPropertyFinder(IQueryContext context) {
            super(context);
        }
        
        public String getValue(String key) {
            return context.getProjectPropertyValue(key);
        }
    }
    
}
