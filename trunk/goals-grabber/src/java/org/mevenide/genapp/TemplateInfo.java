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
package org.mevenide.genapp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;
import org.mevenide.context.IQueryContext;

/**
 * Information about a genapp template
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class TemplateInfo {
    
    private File directory;
    private IQueryContext context;
    
    private String[] params;
    private Properties templateProperties; 
    
    /** Creates a new instance of TemplateInfo */
    TemplateInfo(File dir, IQueryContext cont) {
        assert dir != null;
        directory = dir;
        context = cont;
    }
    
    public String getName() {
        return directory.getName();
    }
    
    public String getDisplayName() {
        // how to figure a reasonable name to display
        return getName();
    }
    
    public File[] getRepackageRooots() {
        loadProperties();
        String key = "maven.genapp.repackage"; //NOI18N
        String val = templateProperties.getProperty(key); //NOI18N
        if (val == null) {
            val = context.getResolver().getResolvedValue(key);
        }
        if (val != null) {
            StringTokenizer tok = new StringTokenizer(val, ",");
            int count = tok.countTokens();
            File[] toRet = new File[count];
            for (int i = 0; i < count; i++) {
                toRet[i] = new File(directory, tok.nextToken());
            }
            return toRet;
        } else {
            return new File[0];
        }
    }
    
    public boolean compliesWithFilter(File file) {
        //TODO
        return false;
    }
    
    public String[] getParameters() {
        if (params == null) {
            loadProperties();
            String val = templateProperties.getProperty("maven.genapp.param"); //NOI18N
            if (val == null) {
               val = context.getResolver().getResolvedValue("maven.genapp.param"); //NOI18N
            }
            if (val != null) {
                StringTokenizer tok = new StringTokenizer(val, ",");
                int count = tok.countTokens();
                String[] toRet = new String[count];
                for (int i = 0; i < count; i++) {
                    toRet[i] = tok.nextToken();
                }
                params = toRet;
            } else {
                params = new String[0];
            }
        }
        return params;
    }
    
    public String getDefaultValue(String parameter) {
        loadProperties();
        String key = "maven.genapp.default." + parameter; //NOI18N
        String val = templateProperties.getProperty(key); //NOI18N
        if (val == null) {
            val = context.getResolver().getResolvedValue(key);
        }
        return val;
    }
    
    public String getPromptText(String parameter) {
        loadProperties();
        String key = "maven.genapp.prompt." + parameter; //NOI18N
        String val = templateProperties.getProperty(key); //NOI18N
        if (val == null) {
            val = context.getResolver().getResolvedValue(key);
        }
        return val;
    }
    
    public boolean hasCustomScript() {
        return new File(directory, "template.jelly").exists(); //NOI18N
    }
    
    private void loadProperties() {
        if (templateProperties == null) {
            templateProperties = new Properties();
            InputStream stream = null;
            File file = new File(directory, "template.properties"); //NOI18N
            if (file.exists()) {
                try {
                    stream = new BufferedInputStream(new FileInputStream(file));
                    templateProperties.load(stream);
                } catch (IOException exc) {
                    
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException exc) {
                            
                        }
                    }
                }
            }
        }
    }
    
}
