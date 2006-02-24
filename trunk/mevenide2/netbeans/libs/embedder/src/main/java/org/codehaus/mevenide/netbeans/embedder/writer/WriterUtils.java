/*
 * WriterUtils.java
 *
 * Created on February 21, 2006, 4:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.embedder.writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.maven.model.Model;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;

/**
 *
 * @author mkleint
 */
public class WriterUtils {
    
    /** Creates a new instance of WriterUtils */
    private WriterUtils() {
    }
    
    public static void writePomModel(FileObject pom, Model newModel) throws IOException {
        InputStream inStr = null;
        FileLock lock = null;
        OutputStream outStr = null;
        try {
            inStr = pom.getInputStream();
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(inStr);
            inStr.close();
            inStr = null;
            lock = pom.lock();
            MavenMkleintWriter writer = new MavenMkleintWriter();
            outStr = pom.getOutputStream(lock);
            writer.write(newModel, doc, outStr);
            outStr.close();
            outStr = null;
        } catch (JDOMException exc){
            exc.printStackTrace();
            throw (IOException) new IOException("Cannot parse the POM by JDOM.").initCause(exc);
        } finally {
            if (inStr != null) {
                inStr.close();
            }
            if (outStr != null) {
                outStr.close();
            }
            if (lock != null) {
                lock.releaseLock();
            }
            
        }
    }
    
}
