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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.lang.StringUtils;
import org.codehaus.mevenide.pde.resources.Messages;
import org.codehaus.plexus.util.DirectoryScanner;


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
    
    /** list of excluded files */
    private List excludes = new ArrayList();
    
    /** list of files ot include */
    private List includes = new ArrayList();
    
    public SimpleZipCreator(String directory, String destinationFile) {
        this.directory = directory;
        this.destinationFile = new File(destinationFile);
        this.destinationFile.getParentFile().mkdirs();
    }
    
    public SimpleZipCreator(String directory, String destinationFile, String excludes) {
        this(directory, destinationFile);
        setExcludes(excludes);
    }
    
    public void zip() throws PdeArchiveException {
        
        FileOutputStream output = null;
        
        boolean shouldDeleteDestination = false;
        
        try {
            output = new FileOutputStream(destinationFile);
            ZipOutputStream zipStream = new ZipOutputStream(output);
            
			if ( directory != null ) {
				addDirectory(directory, zipStream);
			}
            addIncludes(zipStream);
            
            zipStream.close();
        }
        catch ( IOException e ) {
            if ( destinationFile != null && destinationFile.exists() ) {
                destinationFile.delete();
            }
            String message = Messages.get("ZipCreator.CannotCreateDestination", destinationFile);
            shouldDeleteDestination = true;
            throw new PdeArchiveException(message, e);
        }
        finally {
            if ( output != null ) {
                try { output.close(); }
                catch ( Exception e ) { }
            }
        }
        if ( shouldDeleteDestination && destinationFile.exists() ) {
            destinationFile.delete();
        }
    }
    
    
    private void addIncludes(ZipOutputStream zipStream) throws PdeArchiveException, IOException {
        if ( includes != null ) {
	        for (int i = 0; i < includes.size(); i++) {
	            Include include = (Include) includes.get(i);
	            String filePath = include.getAbsolutePath();
	            if ( filePath == null ) {
	                throw new PdeArchiveException("ZipCreator.NullIncludePath");
	            }
                File fileToZip = new File(filePath);
                if ( !fileToZip.exists() ) {
                    //@todo handle this case properly by emitting a warning
                    //no need to fail here
                    //throw new PdeArchiveException("ZipCreator.IncludePath.DoesnotExist");
                }
                else {
		            String targetPath = include.getTargetPath();
		            targetPath = targetPath != null ? targetPath : "";
		            targetPath = !targetPath.startsWith("/") ? "/" + targetPath : targetPath; 
		            addFile(fileToZip, targetPath + ("/".equals(targetPath) ? "" : "/") + fileToZip.getName(), zipStream);
                }
	        }
        }
    }

    private void addDirectory(String directory, ZipOutputStream zipStream) throws IOException {
        File[] files = new File(directory).listFiles();
        
        
        for (int i = 0; i < files.length; i++) {
            File fileToZip = files[i];
            
            if ( fileToZip.isDirectory() ) {
                addDirectory(fileToZip.getAbsolutePath(), zipStream);
            }
            else {
                if ( !excludes.contains(fileToZip.getAbsolutePath().substring(this.directory.length() + 1)) ) {
			        String targetPath = fileToZip.getAbsolutePath().substring(this.directory.length());
		            addFile(fileToZip, targetPath, zipStream);
                }
            }
        }
    }

    private void addFile(File fileToZip, String targetPath, ZipOutputStream zipStream) throws IOException, FileNotFoundException {
        ZipEntry e = new ZipEntry(targetPath);
        
        zipStream.putNextEntry(e);
        
        FileInputStream in = null;
        
        byte[] buf = new byte[1024]; 
        try {
            in = new FileInputStream(fileToZip);

            int len;
            while ((len = in.read(buf)) > 0) {
                zipStream.write(buf, 0, len);
            } 
        }
        finally {
            if ( in != null ) { in.close(); }
        }
        zipStream.closeEntry();
    }

    public void setExcludes(String excludes) { 
		if ( directory != null ) {
			DirectoryScanner scanner = new DirectoryScanner();
			scanner.setBasedir(directory);
			scanner.setExcludes(StringUtils.split(excludes, ','));
			scanner.scan();
			this.excludes = Arrays.asList(scanner.getExcludedFiles());
		}
    }
    public void setIncludes(List includes) { this.includes = includes; }
    public void setDestinationFile(File destinationFile) { this.destinationFile = destinationFile; }
    public void setDirectory(String directory) { this.directory = directory; }
}
