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
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.environment.ILocationFinder;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class JDomReportsFinder implements IReportsFinder {
    
    private static final Log log = LogFactory.getLog(JDomReportsFinder.class);
    
    private ILocationFinder finder;
    private File pluginsDir;
    
    public JDomReportsFinder() {
        this(ConfigUtils.getDefaultLocationFinder());
    }
    
    public JDomReportsFinder(ILocationFinder find) {
        this.finder = find;
    }

    public String[] findReports() throws Exception {
        pluginsDir = new File(finder.getMavenPluginsDir());
        File pluginsCache = new File(pluginsDir, "plugins.cache");
        
        Properties properties = new Properties();
        properties.load(new FileInputStream(pluginsCache));
        
        Enumeration elements = properties.keys();
        
        Map pluginCandidates = new HashMap();
        
        while (elements.hasMoreElements()) {
            String key = (String) elements.nextElement();
            if ( key.indexOf(":register") > - 1 ) {
                pluginCandidates.put(key, properties.get(key));
            }
        }
        
        List verifiedCandidates = verifyAndSort(pluginCandidates);
        
        return (String[]) verifiedCandidates.toArray(new String[0]);
        
    }

    private List verifyAndSort(Map pluginCandidates) throws Exception {
        List verified = new ArrayList();
       
        for (Iterator it = pluginCandidates.keySet().iterator(); it.hasNext();) {
            String registarGoal = (String) it.next();
            String pluginHome = (String) pluginCandidates.get(registarGoal);
            if ( isRegistar(registarGoal, pluginHome) ) {
                verified.add(registarGoal.substring(0, registarGoal.indexOf(":register")));
            }
        }
        
        return verified;
    }

    private boolean isRegistar(String registarGoal, String pluginHome) throws Exception {
        File jellyFile = new File(new File(pluginsDir, pluginHome), "plugin.jelly");
        SAXBuilder builder = new SAXBuilder();
        Document document;
        try {
            document = builder.build(jellyFile);
        }
        catch (JDOMException e) {
            String message = "Unable to build Document"; 
            log.error(message, e);
            return false;
        }
        return checkDocument(registarGoal, pluginHome, document);
    }
    
    
    private boolean checkDocument(String registarGoal, String pluginHome, Document document) throws Exception {
        List goals = document.getRootElement().getChildren("goal");
        if ( goals != null ) {
            for (int i = 0; i < goals.size(); i++) {
                Element goal = (Element) goals.get(i);
                if ( goal.getAttributeValue("name").equals(registarGoal) ) {
                    return visit(goal, pluginHome, document);
                }
            }
        }
        return false;
    }

    boolean visit(Element goal, String pluginHome, Document document) throws Exception {
        List children = goal.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Element e = (Element) children.get(i);
            //if found return 
            if ( e.getName().equals("registerReport") && e.getNamespace().getURI().equals("doc") ) {
                return true;
            }
            //if attaingGoal follow link
            if ( e.getName().equals("attainGoal") ) {
                return isRegistar(e.getAttributeValue("name"), pluginHome);
            }
            //else visit node
            boolean found = visit(e, pluginHome, document);
            if (found) {
                return found;
            }
        }
        //goal is not a registrar. try any prereqs
        String prereqs = goal.getAttributeValue("prereqs");
        if ( prereqs != null ) {
	        StringTokenizer tokenizer = new StringTokenizer(prereqs, ",");
	        while ( tokenizer.hasMoreTokens() ) {
	            String prereq = tokenizer.nextToken();
	            return checkDocument(prereq, pluginHome, document);
	        }
        }
        
        //failed 
        return false;
    }

}
