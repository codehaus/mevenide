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
package org.mevenide.reports;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mevenide.Environment;
import org.mevenide.project.dependency.DependencySplitter;


/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: ReportsFinder.java,v 1.1 21 sept. 2003 Exp gdodinet 
 * 
 */
public class DefaultReportsFinder implements IReportsFinder {
	/**
	 * implementation of GrepInputStream found @ http://www.heise.de/ix/artikel/E/1996/06/142/05.shtml
	 * 
	 * @author Ute Schneider
	 */
	private class GrepInputStream extends FilterInputStream {
		private String substring;
		private BufferedReader bufferedReader;
		
		public GrepInputStream(DataInputStream inputStream, String substringToSearchFor) {
			super(inputStream);
			this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			this.substring = substringToSearchFor;
		}

		public final String readLine() throws IOException {
			String line;
			do {
				line = bufferedReader.readLine();
			}
			while ((line != null) && line.indexOf(substring) == -1);
			return line;
		}
	}
	
	
	public String[] findReports() throws Exception {
		String searchedString = "<doc:registerReport";
		File pluginsDir = new File(Environment.getMavenPluginsInstallDir());
		String[] pluginsContainingRegisterReport = grep(searchedString, pluginsDir);
		for (int i = 0; i < pluginsContainingRegisterReport.length; i++) {
			DependencySplitter splitter = new DependencySplitter(pluginsContainingRegisterReport[i]);
			pluginsContainingRegisterReport[i] = splitter.split().artifactId;
        }
		return pluginsContainingRegisterReport;
	}
	
	
	private String[] grep(String searchedString, File rootSearch) throws IOException {
		List plugins = new ArrayList();

		try {
			String[] files = getJellyFiles(rootSearch);
			for (int i = 0; i < files.length; i++) {
            	
				String f = files[i];
            	
				DataInputStream d;
	            
				d = new DataInputStream(new FileInputStream(f));
	            
				GrepInputStream g = new GrepInputStream(d, searchedString);
	
				String line;
				for (;;) {
					line = g.readLine();
					if (line == null) {
						break;
					}
					String pluginName = new File(f).getParentFile().getName();
					if ( !plugins.contains(pluginName) ) {
					    plugins.add(pluginName);
					}
				}
				g.close();
			}

			return toStringArray(plugins);
		}
		catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
	

	private String[] getJellyFiles(File root) {
		List fileList = new ArrayList(); 
		File[] files = root.listFiles(
			new FileFilter() {
				public boolean accept(File pathname) {
					return "plugin.jelly".equals(pathname.getName()) || pathname.isDirectory();
				} 
			}
		);
		for (int i = 0; i < files.length; i++) {
			if ( files[i].isDirectory() ) {
				fileList.addAll(Arrays.asList(getJellyFiles(files[i])));
			}
			else {
				fileList.add(files[i].getAbsolutePath());
			}
		}
		String[] result = toStringArray(fileList);
		return result;
	}


	private String[] toStringArray(List stringList) {
		String[] result = new String[stringList.size()];
		for (int i = 0; i < stringList.size(); i++) {
			result[i] = (String)stringList.get(i);        
		}
		return result;
	}
}
