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

package org.mevenide.reports;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.mevenide.context.IQueryContext;

/**
 * Class encapsulating the result of pmd report. Reads the raw pmd report and makes it accessible from code.
 * Assumes the report file is present.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public final class PmdResult {
    private static final Log logger = LogFactory.getLog(PmdResult.class);
    
    private IQueryContext context;
    private boolean loaded;
    private Object LOCK = new Object();
    private TreeMap violations;
    /** Creates a new instance of PmdResult */
    public PmdResult(IQueryContext con) {
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
        File reportFile = new File(context.getResolver().getResolvedValue("maven.build.dir"), "pmd-raw-report.xml");
        violations = new TreeMap(new FileComparator());
        if (reportFile.exists()) {
            try {
                SAXBuilder builder = new SAXBuilder();
                Document document = builder.build(reportFile);
                List files = document.getRootElement().getChildren("file");
                if (files != null && files.size() > 0) {
                    Iterator it = files.iterator();
                    while (it.hasNext()) {
                        Element el = (Element)it.next();
                        String name = el.getAttributeValue("name");
                        if (name != null) {
                            List viols = new ArrayList();
                            File file = new File(name);
                            violations.put(file, viols);
                            List vs = el.getChildren("violation");
                            if (vs != null) {
                                Iterator vsIter = vs.iterator();
                                while (vsIter.hasNext()) {
                                    Element vsElem = (Element)vsIter.next();
                                    Violation v = new Violation();
                                    v.setFile(file);
                                    v.setLine(vsElem.getAttributeValue("line"));
                                    v.setViolationId(vsElem.getAttributeValue("rule"));
                                    v.setViolationText(vsElem.getText());
                                    viols.add(v);
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
        private String violationId;
        private File file;
        private String violationText;

        Violation() {
            
        }
        
        public String getLine() {
            return line;
        }

        void setLine(String line) {
            this.line = line;
        }

        public String getViolationId() {
            return violationId;
        }

        void setViolationId(String viloationId) {
            this.violationId = viloationId;
        }

        public File getFile() {
            return file;
        }

        void setFile(File file) {
            this.file = file;
        }

        public String getViolationText() {
            return violationText;
        }

        void setViolationText(String violationText) {
            this.violationText = violationText;
        }
        
    }
    
    private static class FileComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            File file1 = (File)o1;
            File file2 = (File)o2;
            return file1.getAbsolutePath().compareTo(file2.getAbsolutePath());
        }
        
    }
}
