/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
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
		
		public GrepInputStream(DataInputStream inputStream, String substring) {
			super(inputStream);
			this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			this.substring = substring;
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
			pluginsContainingRegisterReport[i] = splitter.split()[0];
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
					plugins.add(new File(f).getParentFile().getName());
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
