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

package org.mevenide.netbeans.project.output;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.mevenide.context.IQueryContext;

/**
 * Class encapsulating the result of checkstyle report. Reads the raw checkstyle report and makes it accessible from code.
 * Assumes the report file is present. Was done against 3.4 version of checkstyle report
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public final class CheckstyleResult {
    private static final Log logger = LogFactory.getLog(CheckstyleResult.class);
    
    private IQueryContext context;
    private boolean loaded;
    private Object LOCK = new Object();
    private HashMap violations;
    /** Creates a new instance of CheckstyleResult */
    public CheckstyleResult(IQueryContext con) {
        context = con;
    }
    
    public File[] getFiles() {
        synchronized (LOCK) {
            if (!loaded) {
                loadReport();
            }
        }
        File[] files = new File[violations.size()];
        files = (File[])violations.keySet().toArray(files);
        return files;
    }
    
    public List getViolationsForFile(File file) {
        synchronized (LOCK) {
            if (!loaded) {
                loadReport();
                loaded = true;
            }
        }
        return (List)violations.get(file);
    }
    
    private void loadReport() {
        File reportFile = new File(context.getResolver().getResolvedValue("maven.build.dir"), "checkstyle-raw-report.xml");
        violations = new HashMap();
        if (reportFile.exists()) {
            try {
                SAXBuilder builder = new SAXBuilder();
                Document document = builder.build(reportFile);
                //TODO - checkstyle allows to check the version of the report format..
                List files = document.getRootElement().getChildren("file");
                if (files != null && files.size() > 0) {
                    Iterator it = files.iterator();
                    while (it.hasNext()) {
                        Element el = (Element)it.next();
                        String name = el.getAttributeValue("name");
                        if (name != null) {
                            List viols = new ArrayList();
                            File file = new File(name);
                            List vs = el.getChildren("error");
                            if (vs != null) {
                                Iterator vsIter = vs.iterator();
                                while (vsIter.hasNext()) {
                                    Element vsElem = (Element)vsIter.next();
                                    Violation v = new Violation();
                                    v.setFile(file);
                                    v.setLine(vsElem.getAttributeValue("line"));
                                    v.setColumn(vsElem.getAttributeValue("column"));
                                    v.setSeverity(vsElem.getAttributeValue("severity"));
                                    v.setSource(vsElem.getAttributeValue("source"));
                                    v.setMessage(vsElem.getAttributeValue("message"));
                                    viols.add(v);
                                }
                                if (viols.size() > 0) {
                                    violations.put(file, viols);
                                }
                            }
                        }
                    }
                }
            } catch (Exception exc) {
                logger.error("exception when loading report=", exc);
            }
        }
    }
    
    public static class Violation {
        private String line;
        private String column;
        private File file;
        private String message;
        private String severity;
        private String source;

        Violation() {
            
        }
        
        public String getLine() {
            return line;
        }

        void setLine(String line) {
            this.line = line;
        }

        public File getFile() {
            return file;
        }

        void setFile(File file) {
            this.file = file;
        }

        public String getColumn() {
            return column;
        }

        void setColumn(String column) {
            this.column = column;
        }

        public String getMessage() {
            return message;
        }

        void setMessage(String message) {
            this.message = message;
        }

        public String getSeverity() {
            return severity;
        }

        void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getSource() {
            return source;
        }

        void setSource(String source) {
            this.source = source;
        }

 
        
    }
}
