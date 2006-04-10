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
import java.io.OutputStreamWriter;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.profiles.ProfilesRoot;
import org.apache.maven.profiles.io.xpp3.ProfilesXpp3Writer;
import org.codehaus.plexus.util.IOUtil;
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
            //TODO make it the careful MavenMkleintWriter after it's capable to write everything..
            MavenXpp3Writer writer = new MavenXpp3Writer();
            outStr = pom.getOutputStream(lock);
            writer.write(new OutputStreamWriter(outStr), newModel);
            //writer.write(newModel, doc, outStr);
            outStr.close();
            outStr = null;
        } catch (JDOMException exc){
            exc.printStackTrace();
            throw (IOException) new IOException("Cannot parse the POM by JDOM.").initCause(exc);
        } finally {
            IOUtil.close(inStr);
            IOUtil.close(outStr);
            if (lock != null) {
                lock.releaseLock();
            }
            
        }
    }

    public static void writeProfilesModel(FileObject pomDir, ProfilesRoot profilesRoot) throws IOException {
        InputStream inStr = null;
        FileLock lock = null;
        OutputStream outStr = null;
        OutputStreamWriter wr = null;
        try {
            FileObject fo = pomDir.getFileObject("profiles.xml");
            if (fo == null) {
                fo = pomDir.createData("profiles.xml");
            } else {
                //TODO..
                inStr = fo.getInputStream();
                SAXBuilder builder = new SAXBuilder();
                Document doc = builder.build(inStr);
                inStr.close();
                inStr = null;
            }
            lock = fo.lock();
            //TODO make it the careful MavenMkleintWriter after it's capable to write everything..
            ProfilesXpp3Writer writer = new ProfilesXpp3Writer();
            outStr = fo.getOutputStream(lock);
            wr = new OutputStreamWriter(outStr);
            writer.write(wr, profilesRoot);
            //writer.write(newModel, doc, outStr);
//            outStr.close();
//            outStr = null;
        } catch (JDOMException exc){
            exc.printStackTrace();
            throw (IOException) new IOException("Cannot parse the profiles.xml by JDOM.").initCause(exc);
        } finally {
            IOUtil.close(inStr);
            IOUtil.close(wr);
            if (lock != null) {
                lock.releaseLock();
            }
            
        }
    }
    
}
