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
package org.mevenide.goals.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: TestUtils.java,v 1.1 21 sept. 2003 Exp gdodinet 
 * 
 */
public abstract class TestUtils {
	private TestUtils() {}
	
	public static void delete(File file) {
		if ( file.isFile() ) {
			file.delete();
		}
		else {
			File[] files = file.listFiles();
			if ( files != null ) {
				for (int i = 0; i < files.length; i++) {
					delete(files[i]);
				}
			}
			file.delete();
		}
	}
	
	public static void copy(String sourceFile, String destFile) throws Exception {

		FileInputStream from = new FileInputStream(sourceFile);
		FileOutputStream to = new FileOutputStream(destFile);
		try {
			byte[] buffer = new byte[4096]; 
			int bytes_read; 
			while ((bytes_read = from.read(buffer)) != -1) {
				to.write(buffer, 0, bytes_read);
			}
		} 
		finally {
			if (from != null) {
				from.close();
			}
			if (to != null) {
				to.close();
			}
		}

	}
}
