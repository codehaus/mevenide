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
package org.codehaus.mevenide.pde.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.lang.StringUtils;
import org.codehaus.mevenide.pde.resources.Messages;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SimpleZipCreator {
    /** directory to zip */
    private String directory;
    
    /** output file */
    private File destinationFile;  
    
    /** list of comma-separated patterns of excluded files */
    private String[] excludes;
    
    public SimpleZipCreator() { 
    }
    
    public SimpleZipCreator(String directory, String destinationFile) {
        this.directory = directory;
        this.destinationFile = new File(destinationFile);
        this.destinationFile.getParentFile().mkdirs();
    }
    
    public SimpleZipCreator(String directory, String destinationFile, String excludes) {
        this(directory, destinationFile);
        this.excludes = StringUtils.split(excludes, ',');
    }
    
    public void zip() throws PdeArchiveException {
        
        FileOutputStream output = null;
        
        try {
            output = new FileOutputStream(destinationFile);
            ZipOutputStream zipStream = new ZipOutputStream(output);
            addDirectory(directory, zipStream);
            zipStream.close();
        }
        catch ( Exception e ) {
            if ( destinationFile != null && destinationFile.exists() ) {
                destinationFile.delete();
            }
            String message = Messages.get("ZipCreator.CannotCreateDestination", destinationFile);
            throw new PdeArchiveException(message, e);
        }
        finally {
            if ( output != null ) {
                try {
                    output.close();
                }
                catch ( Exception e ) {
                }
            }
        }
    }
    
    
    private void addDirectory(String directory, ZipOutputStream zipStream) throws IOException {
        File[] files = new File(directory).listFiles();
        
        byte[] buf = new byte[1024]; 
        
        for (int i = 0; i < files.length; i++) {
            if ( files[i].isDirectory() ) {
                addDirectory(files[i].getAbsolutePath(), zipStream);
            }
            else {
	            ZipEntry e = new ZipEntry(files[i].getAbsolutePath().substring(this.directory.length()));
	            zipStream.putNextEntry(e);
	            
	            FileInputStream in = null;
	            
	            try {
		            in = new FileInputStream(files[i]);
		
		            int len;
		            while ((len = in.read(buf)) > 0) {
		                zipStream.write(buf, 0, len);
		            } 
	            }
	            finally {
	                if ( in != null ) {
	                    in.close();
	                }
	            }
	            zipStream.closeEntry();
            }
        }
    }

    public String[] getExcludes() { return excludes; }
    public void setExcludes(String[] excludes) { this.excludes = excludes; }
    
    public File getDestinationFile() { return destinationFile; }
    public void setDestinationFile(File destinationFile) { this.destinationFile = destinationFile; }
    
    public String getDirectory() { return directory; }
    public void setDirectory(String directory) { this.directory = directory; }
}
