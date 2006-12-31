/*
 * XmlFilenameProvider.java
 *
 * Created on 12. September 2004, 08:48
 */

package org.mevenide.javahelp;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 *
 * @author Peter Nabbefeld
 */
public class XmlToFilenameMapper extends DefaultHandler {

    // This class is static, so it can be instantiated from the main method (for simple testing).
    // There will be better testing support in future releases.
    private static class FilesetFilter implements FilenameFilter {
        
        private static final String FSEP = System.getProperty("file.separator");
        
        private String basedir;
        private String wildcard;
        private Pattern pattern;
        private List exclude;
        
        FilesetFilter(String basedir, String wildcard, List exclude) throws IOException {
            this.basedir  = (new File(basedir)).getCanonicalPath();
            this.wildcard = createPatternFromWildcard(wildcard);
            this.pattern  = Pattern.compile(wildcard);
            this.exclude  = exclude;
        }
        
        public boolean accept(File dir, String name) {
            try {
                if (dir.getCanonicalPath().startsWith(basedir)) {
                    String reldir = dir.getCanonicalPath().substring(basedir.length());
                    reldir = reldir.replace(FSEP.charAt(0), '/');
                    if (!reldir.endsWith("/"))
                        reldir += "/";
                    String fullname = reldir + name;
                    return pattern.matcher(fullname).matches();
                }
            } catch (IOException ioe) {
                return false;
            }
            return false;
        }
        
        private String createPatternFromWildcard(String wildcard) {
            if (FSEP.length() != 1)
                throw new UnsupportedOperationException("Only file systems with one-character file-separator supported. Please tell me about Your OS and file-separator, so I'll be able to support it.");
            String wc = wildcard.replace(FSEP.charAt(0), '/');
            int p = wc.lastIndexOf('/');
            String dir = (p < 0) ? "" : wc.substring(0, p);
            if (dir.length() > 0 && dir.charAt(0) == '/')
                dir  = dir.substring(1);
            if (dir.length() > 0)
                dir += '/';
            String nm  = wc.substring(p + 1);
            dir = dir.replaceAll("\\.","\\\\.");
            dir = dir.replaceAll("\\?",".{1}");
            dir = dir.replaceAll("^\\*\\*/","/?.*?/?");
            dir = dir.replaceAll("/\\*\\*/","/?.*?/?");
            nm = nm.replaceAll("\\.","\\\\.");
            nm = nm.replaceAll("\\?","[^/]{1}");
            nm = nm.replaceAll("\\*","[^/]*");
            return dir + nm;
        }
        private boolean testPattern(String str) {
            return Pattern.matches(wildcard, str);
        }
    }
    
    private static String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static String XML_SCHEMA      = "http://www.w3.org/2001/XMLSchema";
    private static String SCHEMA_SOURCE   = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    
    private SAXParser parser;
    private File xmlfile;
    private File targetdir;
    private String target;
    private File[][] incFiles;
    private int ptrIncludes;
    private int ptrIncFiles;
    
    /** Creates a new instance of XmlFilenameProvider */
    public XmlToFilenameMapper(File targetdir, File xmlfile) throws ParserConfigurationException, SAXException {
        this.targetdir = targetdir;
        java.net.URL url = XmlToFilenameMapper.class.getResource("../../plugin-resources/help-xdocs-reference-20040910.xsd");
        File schema = new File(url.getPath());
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(true);
        SAXParser parser = factory.newSAXParser();
        try {
            parser.setProperty(SCHEMA_LANGUAGE,XML_SCHEMA);
            parser.setProperty(SCHEMA_SOURCE,schema);
        } catch (SAXNotRecognizedException snre) {
            System.err.println("SAX parser is not JAXP 1.2 compliant");
        } catch (SAXNotSupportedException snse) {
            System.err.println("SAX parser is not JAXP 1.2 compliant");
        }
    }
    
    public void init(String target) throws SAXException, IOException {
        assert parser != null && xmlfile != null && target != null;
        this.target = target;
        parser.parse(xmlfile, this);
        ptrIncludes = 0;
        do {
             incFiles = getIncludedFiles(ptrIncludes++);
        } while (ptrIncludes < includes.size() && (incFiles == null || incFiles.length < 1));
        ptrIncFiles = 0;
    }
    public boolean hasNext() {
        return (incFiles != null && incFiles.length > 0);
    }
    public File[] next() throws IOException {
        if (incFiles == null || incFiles.length < 1)
            return null;
        File[] next = incFiles[ptrIncFiles++];
        if (ptrIncFiles == incFiles.length) {
            do {
                 incFiles = getIncludedFiles(ptrIncludes++);
            } while (ptrIncludes < includes.size() && (incFiles == null || incFiles.length < 1));
            ptrIncFiles = 0;
        }
        return next;
    }

    public File getDestination(File[] files) {
        assert files.length == 2;
        return files[0];
    }
    public File getSource(File[] files) {
        assert files.length == 2;
        return files[1];
    }

//========================================================
// DefaultHandler
//========================================================

    private static final int STATE_REFERENCE = 0;
    private static final int STATE_TARGET    = 1;
    private static final int STATE_DIRECTORY = 2;
    private static final int STATE_INCLUDE   = 3;
    
    private static final String TAG_TARGET    = "target";
    private static final String TAG_DIRECTORY = "directory";
    private static final String TAG_INCLUDE   = "include";
    
    private ArrayList includes;
    private boolean found_target;
    private int state;
    private String dir;
    private String name;

    public void startDocument() throws SAXException {
        super.startDocument();
        found_target = false;
        includes = new ArrayList();
        state    = STATE_REFERENCE;
    }
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (state == STATE_REFERENCE && qName.equals(TAG_TARGET)) {
            found_target = attributes.getValue("name").equals(target);
            state = STATE_TARGET;
        } else if (found_target) {
            if (state == STATE_TARGET && qName.equals(TAG_DIRECTORY)) {
                dir   = attributes.getValue("path");
                state = STATE_DIRECTORY;
            } else if (state == STATE_DIRECTORY && qName.equals(TAG_INCLUDE)) {
                name  = attributes.getValue("file");
                state = STATE_INCLUDE;
            }
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (state == STATE_TARGET && qName.equals(TAG_TARGET)) {
            state = STATE_REFERENCE;
        } else if (found_target) {
            if (state == STATE_DIRECTORY && qName.equals(TAG_DIRECTORY)) {
                state = STATE_TARGET;
            } else if (state == STATE_INCLUDE && qName.equals(TAG_INCLUDE)) {
                state = STATE_DIRECTORY;
                includes.add(new String[]{dir, name});
            }
        }
    }

//========================================================
// Private methods to get the included files
//========================================================

    private File[][] getIncludedFiles(int ptr) throws IOException {
        ArrayList list = new ArrayList();
        String[] entry = (String[])includes.get(ptr);
        String basedir = entry[0];
        String fname   = entry[1];
        FilesetFilter filter = new FilesetFilter(basedir, fname, null);
        getIncludedFiles0(list, filter.basedir, new File(filter.basedir), filter);
        int max = list.size();
        File[][] resfiles = new File[max][2];
        for(int i = 0; i < max; i++) {
            resfiles[i] = (File[])list.get(i);
        }
        return resfiles;
    }
    
    private void getIncludedFiles0(List list, String basedir, File dir, FilenameFilter filter) throws IOException {
        File[] files = dir.listFiles(filter);
        String dfDir = targetdir.getCanonicalPath();
        String dfName;
        for(int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                getIncludedFiles0(list, basedir, files[i], filter);
            } else if (files[i].isFile()) {
                dfName = files[i].getCanonicalPath().substring(basedir.length());
                list.add(new File[]{new File(dfDir + dfName), files[i]});
            }
        }
    }

//========================================================
// Method to test the class
//========================================================

    public final static void main(String[] args) throws Exception {
        FilesetFilter filter = new FilesetFilter("C:\\cvs\\mevenide\\maven-plugins\\maven-javahelp-plugin\\src", 
                                                 "**/*.xsd",
                                                 null);
        System.out.println("basedir  = " + filter.basedir);
        System.out.println("wildcard = " + filter.wildcard);
        System.out.println("exclude  = " + filter.exclude);
        System.out.println("* 1 *");
        System.out.println("testing: a.xsd");
        System.out.println("result:  " + filter.testPattern("a.xsd"));
        System.out.println("testing: /a.xsd");
        System.out.println("result:  " + filter.testPattern("/a.xsd"));
        System.out.println("testing: /bcd/a.xsd");
        System.out.println("result:  " + filter.testPattern("/bcd/a.xsd"));
        System.out.println("testing: /xsd/a.xsd");
        System.out.println("result:  " + filter.testPattern("/xsd/a.xsd"));
        System.out.println("testing: /xsd/uvw/m.xsd");
        System.out.println("result:  " + filter.testPattern("/xsd/uvw/m.xsd"));
        System.out.println("testing: /xsd/uvw/.xsd");
        System.out.println("result:  " + filter.testPattern("/xsd/uvw/.xsd"));
        System.out.println("testing: /xsd/uvw/m.xsu");
        System.out.println("result:  " + filter.testPattern("/xsd/uvw/m.xsu"));
        System.out.println("--------------------------");
    }
}
