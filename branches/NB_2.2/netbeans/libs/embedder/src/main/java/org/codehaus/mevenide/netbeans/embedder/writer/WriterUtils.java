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
import org.apache.maven.model.io.jdom.MavenJDOMWriter;
import org.apache.maven.profiles.ProfilesRoot;
import org.apache.maven.profiles.io.jdom.ProfilesJDOMWriter;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.jdom.SettingsJDOMWriter;
import org.codehaus.plexus.util.IOUtil;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.JDOMFactory;
import org.jdom.input.SAXBuilder;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;

/**
 *
 * @author mkleint
 */
public class WriterUtils {
    
    private static JDOMFactory factory = new DefaultJDOMFactory();
    
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
            MavenJDOMWriter writer = new MavenJDOMWriter();
            outStr = pom.getOutputStream(lock);
            writer.write(newModel, doc, outStr);
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
    
    public static void writeProfilesModel(final FileObject pomDir, final ProfilesRoot profilesRoot) throws IOException {
        pomDir.getFileSystem().runAtomicAction(new FileSystem.AtomicAction() {
            public void run() throws IOException {
                InputStream inStr = null;
                FileLock lock = null;
                OutputStream outStr = null;
                OutputStreamWriter wr = null;
                try {
                    Document doc;
                    FileObject fo = pomDir.getFileObject("profiles.xml");
                    if (fo == null) {
                        fo = pomDir.createData("profiles.xml");
                        doc = factory.document(factory.element("profilesXml"));
                    } else {
                        //TODO..
                        inStr = fo.getInputStream();
                        SAXBuilder builder = new SAXBuilder();
                        doc = builder.build(inStr);
                        inStr.close();
                        inStr = null;
                    }
                    lock = fo.lock();
                    //TODO make it the careful MavenMkleintWriter after it's capable to write everything..
                    ProfilesJDOMWriter writer = new ProfilesJDOMWriter();
                    outStr = fo.getOutputStream(lock);
                    //            writer.write(wr, profilesRoot);
                    writer.write(profilesRoot, doc, outStr);
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
        });
    }
    
    public static void writeSettingsModel(final FileObject m2dir, final Settings settings) throws IOException {
        m2dir.getFileSystem().runAtomicAction(new FileSystem.AtomicAction() {
            public void run() throws IOException {
                InputStream inStr = null;
                FileLock lock = null;
                OutputStream outStr = null;
                
                try {
                    Document doc;
                    
                    FileObject fo = m2dir.getFileObject("settings.xml");
                    if (fo == null) {
                        fo = m2dir.createData("settings.xml");
                        doc = factory.document(factory.element("settings"));
                    } else {
                        //TODO..
                        inStr = fo.getInputStream();
                        SAXBuilder builder = new SAXBuilder();
                        doc = builder.build(inStr);
                        inStr.close();
                        inStr = null;
                    }
                    lock = fo.lock();
                    
                    SettingsJDOMWriter writer = new SettingsJDOMWriter();
                    outStr = fo.getOutputStream(lock);
                    writer.write(settings, doc, outStr);
                } catch (JDOMException exc){
                    exc.printStackTrace();
                    throw (IOException) new IOException("Cannot parse the settings.xml by JDOM.").initCause(exc);
                } finally {
                    IOUtil.close(inStr);
                    IOUtil.close(outStr);
                    if (lock != null) {
                        lock.releaseLock();
                    }
                }
            }
        });
        
    }
    
}
