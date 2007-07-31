/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * =========================================================================
 */
package org.mevenide.grammar.impl;
import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.grammar.TagLib;
import org.mevenide.grammar.TagLibProvider;

/**
 * a TagLibProvider that will retrieve tagLibs from the
 * MAVEN_LOCAL_REPO/plugins/dynatag.cache Will probably not be used on it's own
 * but as part of a aggregating TagLibProvider.
 * 
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenTagLibProvider implements TagLibProvider {
	
	private static final Logger LOGGER = Logger.getLogger(MavenTagLibProvider.class.getName());
	
	private File cacheFile;
	private File pluginDir;
	
	private Map taglibs;
	
	private boolean cacheRead;
	
	private ClassLoader mavenClassLoader;
	
	/** Creates a new instance of MavenTagLibProvider */
	public MavenTagLibProvider(File pluginCache) {
		taglibs = new TreeMap();
		cacheRead = false;
		cacheFile = pluginCache;
		pluginDir = cacheFile.getParentFile();
	}
	
	public MavenTagLibProvider() {
		taglibs = new TreeMap();
		cacheRead = false;
		cacheFile = new File(ConfigUtils.getDefaultLocationFinder().getMavenPluginsDir(), ".dynatag.cache");
		pluginDir = cacheFile.getParentFile();
	}
	
	public String[] getAvailableTags() {
		checkCache();
		Set keys = taglibs.keySet();
		String[] tags = new String[keys.size()];
		tags = (String[]) keys.toArray(tags);
		return tags;
	}
	
	public TagLib retrieveTagLib(String name) {
		checkCache();
		String pluginName = (String) taglibs.get(name);
		if (pluginName != null) {
			File pluginLoc = new File(pluginDir, pluginName);
			File jellyFileLoc = new File(pluginLoc, "plugin.jelly");
			if (jellyFileLoc.exists()) {
				try {
					return new JellyDefineTagLibImpl(jellyFileLoc, mavenClassLoader);
				} 
				catch (Exception exc) {
					// just ignore, something went wrong, no CC.
					LOGGER.log(Level.SEVERE, "Cannot cached plugin.jelly.", exc);
				}
			} 
			else {
				LOGGER.warning("cannot read plugin jelly file=" + jellyFileLoc.getAbsolutePath());
			}
		} 
		else {
			LOGGER.warning("no content in taglibs=" + taglibs.size() + "name="
					+ name + "=");
		}
		return null;
	}
	
	private void checkCache() {
		if (!cacheRead) {
			try {
				readCache();
				cacheRead = true;
			} 
			catch (Exception exc) {
				LOGGER.log(Level.SEVERE, "Cannot read dynatag cache.", exc);
			}
		}
	}
	
	private void readCache() throws Exception {
		taglibs.clear();
		Properties pros = new Properties();
		pros.load(new FileInputStream(cacheFile));
		Enumeration keys = pros.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith("http")) {
				//skip the taglibs defined as URL, no idea what to do with
				// them..
				continue;
			}
			taglibs.put(key, pros.getProperty(key));
			LOGGER.fine("readCache:key=" + key + "=value=" + pros.getProperty(key));
		}
	}

}