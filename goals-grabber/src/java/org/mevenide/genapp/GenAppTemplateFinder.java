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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;

import java.io.File;
import java.io.FilenameFilter;
import org.mevenide.properties.IPropertyResolver;

/**
 * creator of genapp templates information.
 * Currently no caching is done, not so often performed operation.
 * @author  Peter Nabbefeld
 * @author  Milos Kleint
 */
public class GenAppTemplateFinder {
    
    private IPropertyResolver props;
    private IQueryContext context;
    
    /**
     * constant for genapp plugin default templates
     */
    public static final String LOCATION_DEFAULT = "GenApp"; //NOI18N
    /**
     * constant for genapp user defined templates
     */
    public static final String LOCATION_USER    = "User"; //NOI18N
    public static final String LOCATION_ALL     = "All"; //NOI18N

    /** Creates a new instance of MavenTemplateEnumerator */
    public GenAppTemplateFinder(IQueryContext cont) {
        context = cont;
        props = context.getResolver();
    }

    public TemplateInfo[] getTemplates(String type) {
        if (LOCATION_USER.equals(type)) {
            return readTemplates(getUserDir());
        }
        if (LOCATION_DEFAULT.equals(type)) {
            return readTemplates(getGenAppDir());
        }
        if (LOCATION_ALL.equals(type)) {
            TemplateInfo[] info1 = readTemplates(getGenAppDir());
            TemplateInfo[] info2 = readTemplates(getUserDir());
            TemplateInfo[] merged = new TemplateInfo[info1.length + info2.length];
            List toReturn = new ArrayList();
            toReturn.addAll(Arrays.asList(info1));
            toReturn.addAll(Arrays.asList(info2));
            return (TemplateInfo[])toReturn.toArray(merged);
            
        }
        throw new IllegalArgumentException("Wrong template type=" + type);
    }

    private File getGenAppDir() {
        ILocationFinder finder = new LocationFinderAggregator(context);
        File pluginDir = new File(finder.getMavenPluginsDir());
        FilenameFilter pluginFilter = new FilenameFilter(){
            public boolean accept(File dir, String name) {
                File f = new File(dir, name);
                return (f.isDirectory() && name.startsWith("maven-genapp-plugin-"));
            }
        };
        String[] plugins = pluginDir.list(pluginFilter);
        if (plugins.length < 1) {
            return null;
        }
        if (plugins.length < 2) {
            return new File(new File(pluginDir, plugins[0]), "plugin-resources");
        }
        // There are more than one version installed, get the latest
        int found = 0;
        int p0 = "maven-genapp-plugin-".length();
        int p1 = plugins[0].indexOf('.');
        int f0 = Integer.parseInt(plugins[0].substring(p0, p1));
        int f1 = Integer.parseInt(plugins[0].substring(p1 + 1));
        int v0, v1;
        for(int i = 1; i < plugins.length; i++) {
            v0 = Integer.parseInt(plugins[i].substring(p0, p1));
            v1 = Integer.parseInt(plugins[i].substring(p1 + 1));
            if (v0 > f0 || (v0 == f0 && v1 > f1)) {
                // More recent plugin
                found = i;
                f0 = v0;
                f1 = v1;
            }
        }
        return new File(new File(pluginDir, plugins[found]), "plugin-resources"); //NOI18N
    }

    private File getUserDir() {
        String str = props.getResolvedValue("maven.genapp.template.repository");
        return str != null ? new File(str) : null; //NOI18N
    }

    private TemplateInfo[] readTemplates(File dir) {
        if (dir != null && dir.exists() && dir.isDirectory()) {
            File[] f = dir.listFiles();
            int nonTemplates = 0;
            for(int i = 0; i < f.length; i++) {
                if (!isTemplateDir(f[i])) {
                    f[i] = null;
                    nonTemplates++;
                }
            }
            TemplateInfo[] c = new TemplateInfo[f.length - nonTemplates];
            for(int i = 0, j = 0; i < f.length; i++) {
                if (f[i] != null) {
                    c[j++] = new TemplateInfo(f[i], context);
                }
            }
            return c;
        }
        return new TemplateInfo[0];
    }

    private boolean isTemplateDir(File dir) {
        if (!dir.isDirectory()) {
            return false;
        }
        File res = new File(dir, "template-resources"); //NOI18N
        if (!res.exists() || !res.isDirectory()) {
            return false;
        }
        res = new File(dir, "template.properties"); //NOI18N
        if (!res.exists() || !res.isFile()) {
            return false;
        }
        return true;
    }

}
