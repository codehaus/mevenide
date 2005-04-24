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

import java.io.File;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.SysEnvLocationFinder;
import org.mevenide.properties.IPropertyFinder;
import org.mevenide.properties.IPropertyLocator;
import org.mevenide.properties.IPropertyResolver;

/**
 *
 * @author  <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 */
public class PropertyFilesAggregator implements IPropertyResolver, IPropertyLocator {
    private static final Log logger = LogFactory.getLog(PropertyFilesAggregator.class);
    
    private IPropertyFinder defaults;
    private IPropertyFinder projectWalker;
    
    private IQueryContext context;

    
    /**
     * IQueryContext based constructor. not public, use PropertyResolverfactory
     */
    PropertyFilesAggregator(IQueryContext querycontext, DefaultsResolver defs) {
        context = querycontext;
        defaults = defs;
        initializeContext();
        // cannot use the ILocationFinder here, since Loc finders use resolvers -> cyclic dependency
        String val = getResolvedValue("maven.plugin.unpacked.dir"); //NOI18N
        if (val != null) {
            defs.initPluginPropsFinder(PropertyResolverFactory.getFactory().getPluginDefaultsPropertyFinder(new File(val)));
        }
    }
    
    
    private void initializeContext() {
        projectWalker = new ProjectWalker2(context);
    }
    
    public String getResolvedValue(String key) {
        return getValue(key, true);
    }
   
    
    public String getValue(String key) {
        return getValue(key, false);
    }
    
    protected String getValue(String key, boolean resolve) {
        String toReturn = null;
        if (key.startsWith("pom.") && projectWalker != null) {
            toReturn = projectWalker.getValue(key);
        } else {
            toReturn = checkSysEnv(key);
            if (toReturn == null) {
                toReturn = context.getPropertyValue(key);
            }
            if (toReturn == null && defaults != null ) {
                toReturn = defaults.getValue(key);
            }
        }
        if (resolve && toReturn != null) {
            toReturn = resolve(new StringBuffer(toReturn)).toString();
        }
        return toReturn;
    }
    
    private String checkSysEnv(String key) {
        if ("maven.home.local".equals(key)) {
            return SysEnvLocationFinder.getInstance().getMavenLocalHome();
        }
        if ("maven.home".equals(key)) {
            return SysEnvLocationFinder.getInstance().getMavenHome();
        }
        if ("maven.repo.local".equals(key)) {
            return SysEnvLocationFinder.getInstance().getMavenLocalRepository();
        }
        return null;
    }

    /**
     * IPropertyLocator method, identifying where the property comes from.
     */
    public int getPropertyLocation(String key) {
        int toReturn = IPropertyLocator.LOCATION_NOT_DEFINED;
        if (checkSysEnv(key) != null) {
            return IPropertyLocator.LOCATION_SYSENV;
        }
        if (context.getUserPropertyValue(key) != null) {
            toReturn = IPropertyLocator.LOCATION_USER_BUILD;
        }
        else if (context.getBuildPropertyValue(key) != null) {
            toReturn = IPropertyLocator.LOCATION_PROJECT_BUILD;
        }
        else if (context.getProjectPropertyValue(key) != null) {
            toReturn = IPropertyLocator.LOCATION_PROJECT;
        } else { 
            int depth = context.getPOMContext().getProjectDepth();
            if (depth > 1) {
                // start at 1, we already checked that before, no need to do again..
                // for performance reasons we did it without checking the pomcontext
                int current = 1;
                String val;
                while (current <= depth && toReturn == IPropertyLocator.LOCATION_NOT_DEFINED) {
                    current = current + 1;
                    val = context.getPropertyValueAt(key, current * 10 + IQueryContext.BUILD_PROPS_OFFSET);
                    if (val != null) {
                        toReturn = current * 10 + IQueryContext.BUILD_PROPS_OFFSET;
                    } else {
                        val = context.getPropertyValueAt(key, current * 10 + IQueryContext.PROJECT_PROPS_OFFSET);
                        if (val != null) {
                            toReturn = current * 10 + IQueryContext.PROJECT_PROPS_OFFSET;
                        }
                    }
                }
            }
        }
        if (toReturn == IPropertyLocator.LOCATION_NOT_DEFINED 
                && defaults.getValue(key) != null) {
            toReturn = IPropertyLocator.LOCATION_DEFAULTS;
        }
        return toReturn;
    }
    
    
    protected final StringBuffer resolve(StringBuffer value) {
        StringBuffer toReturn = value;
        int index = value.indexOf("${");
        if (index > -1) {
            int end = value.indexOf("}", index);
            if (end > index + 2) {
                String key = value.substring(index + 2, end);
                String keyvalue = getValue(key, false);
                if (keyvalue != null) {
                    toReturn.replace(index, end + 1, keyvalue);
                    return resolve(toReturn);
                } else {
                    logger.warn("cannot resolve key? '" + key + "'");
                }
            } else {
                logger.warn("badly formed value? '" + value + "'");
            }
        } 
        return toReturn;
    }
    
    /**
     *@deprecated makes no sense for IQueryContext based instances.
     */
    public void reload() {
        // mkleint - makes no sense for IQueryContext based instances.
    }
    

    public String resolveString(String original) {
        if (original ==  null) {
            return null;
        }
        StringBuffer buf = new StringBuffer(original);
        return resolve(buf).toString();
    }        
    
    public boolean isDefinedInLocation(String key, int location) {
        if (location == IPropertyLocator.LOCATION_DEFAULTS) {
            return defaults.getValue(key) != null;
        } 
        return context.getPropertyValueAt(key, location) != null;
    } 
    
    public String getValueAtLocation(String key, int location) {
        if (location == IPropertyLocator.LOCATION_DEFAULTS) {
            return defaults.getValue(key);
        } 
        return context.getPropertyValueAt(key, location);
    }
    
    /**
     * returns all the keys at the given location.
     */
    public Set getKeysAtLocation(int location) {
        if (location == IPropertyLocator.LOCATION_DEFAULTS) {
            if (defaults != null && defaults instanceof DefaultsResolver) {
                return ((DefaultsResolver)defaults).getDefaultKeys();
            }
        }
        return context.getPropertyKeysAt(location);
    }
    
}
