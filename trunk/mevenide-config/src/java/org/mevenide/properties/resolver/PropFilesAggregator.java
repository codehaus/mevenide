/* ==========================================================================
 * Copyright 2004 Apache Software Foundation
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.properties.IPropertyFinder;
import org.mevenide.properties.IPropertyResolver;

/**
 *
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 */
public final class PropFilesAggregator implements IPropertyResolver {
    private static final Log logger = LogFactory.getLog(PropFilesAggregator.class);
    
    private File projectDir;
    private File userDir;
    private IPropertyFinder project;
    private IPropertyFinder projectBuild;
    private IPropertyFinder userBuild;
    private IPropertyFinder defaults;

    /** Creates a new instance of PropFilesAggregator */
    public PropFilesAggregator(File project, File user, IPropertyFinder defs) {
        projectDir = project;
        userDir = user;
        defaults = defs;
        initialize();
        //TODO - add change listeners to figure out added/remove prop files.
    }
    
    private void initialize() {
    	File fo = new File(projectDir, "project.properties");
        if ( fo.exists() ) {
        	project = new SinglePropFileFinder(fo);
        }
        fo = new File(projectDir, "build.properties");
        if ( fo.exists() ) {
        	projectBuild = new SinglePropFileFinder(fo);
        }
        //TODO.. have some caching for user prop file or reuse one instance in all the 
        // agrregators.
        fo = new File(userDir, "build.properties");
        if ( fo.exists() ) {
        	userBuild = new SinglePropFileFinder(fo);
        }
    }
    public String getResolvedValue(String key) {
        return getValue(key, true);
    }
    
    public String getValue(String key) {
        return getValue(key, false);
    }
    
    private String getValue(String key, boolean resolve) {
        String toReturn = null;
        toReturn = userBuild.getValue(key);
        if (toReturn == null && projectBuild != null ) {
            toReturn = projectBuild.getValue(key);
        }
        if (toReturn == null && project != null ) {
            toReturn = project.getValue(key);
        }
        if (toReturn == null && defaults != null ) {
            toReturn = defaults.getValue(key);
        }
        if (resolve && toReturn != null) {
            toReturn = resolve(new StringBuffer(toReturn)).toString();
        }
        return toReturn;
    }
    
    private StringBuffer resolve(StringBuffer value) {
        StringBuffer toReturn = value;
        int index = value.indexOf("${");
        if (index > -1) {
            int end = value.indexOf("}", index);
            if (end > index + 2) {
                String key = value.substring(index + 2, end);
                String keyvalue = getValue(key, true);
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
    
    public void reload() {
        //TODO have more targetting reload strategy.
    	reload(userBuild);
    	reload(project);
    	reload(projectBuild);
    }
    
	private void reload(IPropertyFinder finder) {
		if ( finder != null ) {
    		finder.reload();
    	}
    }

    
}
