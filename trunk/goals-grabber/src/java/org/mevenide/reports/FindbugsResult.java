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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.mevenide.context.IQueryContext;

/**
 * Class encapsulating the result of findbugs report. Reads the raw findbugs report and makes it accessible from code.
 * Assumes the report file is present. Was done against 0.8.4 version of findbugs report
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public final class FindbugsResult {
    private static final Log logger = LogFactory.getLog(FindbugsResult.class);
    
    private IQueryContext context;
    private boolean loaded;
    private Object LOCK = new Object();
    private Map violations;
    /** Creates a new instance of FindbugsResult */
    public FindbugsResult(IQueryContext con) {
        context = con;
    }
    
    public String[] getClassNames() {
        synchronized (LOCK) {
            if (!loaded) {
                loadReport();
            }
        }
        String[] files = new String[violations.size()];
        files = (String[])violations.keySet().toArray(files);
        return files;
    }
    
    public List getViolationsForClass(String className) {
        synchronized (LOCK) {
            if (!loaded) {
                loadReport();
                loaded = true;
            }
        }
        return (List)violations.get(className);
    }
    
    private void loadReport() {
        File reportFile = new File(context.getResolver().getResolvedValue("maven.build.dir"), "findbugs-raw-report.xml"); //NOI18N
        violations = new TreeMap(new StringComparator());
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
                        String name = el.getAttributeValue("classname");
                        if (name != null) {
                            List viols = new ArrayList();
                            List vs = el.getChildren("BugInstance");
                            if (vs != null) {
                                Iterator vsIter = vs.iterator();
                                while (vsIter.hasNext()) {
                                    Element vsElem = (Element)vsIter.next();
                                    Violation v = new Violation();
                                    v.setClassName(name);
                                    v.setLine(vsElem.getAttributeValue("line"));
                                    v.setPriority(vsElem.getAttributeValue("priority"));
                                    v.setType(vsElem.getAttributeValue("type"));
                                    v.setMessage(vsElem.getAttributeValue("message"));
                                    viols.add(v);
                                }
                                if (viols.size() > 0) {
                                    violations.put(name, viols);
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
        private String className;
        private String message;
        private String priority;
        private String type;

        Violation() {
            
        }
        
        public String getLine() {
            return line;
        }

        void setLine(String line) {
            this.line = line;
        }

        public String getClassName() {
            return className;
        }

        void setClassName(String className) {
            this.className = className;
        }


        public String getMessage() {
            return message;
        }

        void setMessage(String message) {
            this.message = message;
        }

        public String getPriority() {
            return priority;
        }

        void setPriority(String priority) {
            this.priority = priority;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
    
   private static class StringComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            String file1 = (String)o1;
            String file2 = (String)o2;
            return file1.compareTo(file2);
        }
        
    }    
}
