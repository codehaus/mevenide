/* ==========================================================================
 * Copyright 2007 Mevenide Team
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

package org.codehaus.mevenide.netbeans.queries;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.Charset;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.FileUtilities;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.Constants;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.netbeans.spi.queries.FileEncodingQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.Exceptions;

/**
 *
 * @author mkleint
 */
public class MavenFileEncodingQueryImpl extends  FileEncodingQueryImplementation {

    private NbMavenProject project;
    private static final String ENCODING_PARAM = "encoding"; //NOI18N
    
    public MavenFileEncodingQueryImpl(NbMavenProject proj) {
        project = proj;
    }

    public Charset getEncoding(FileObject file) {
        String defEnc = PluginPropertyUtils.getPluginProperty(project, 
                    Constants.GROUP_APACHE_PLUGINS, Constants.PLUGIN_COMPILER,ENCODING_PARAM, "compile"); //NOI18N
        MavenProject mp = project.getOriginalMavenProject();
        if (mp != null) {
            //TODO instead of SD
            FileObject src = FileUtilities.convertStringToFileObject(mp.getBuild().getSourceDirectory());
            if (src != null && FileUtil.isParentOf(src, file)) {
                String compileEnc = defEnc;
                if (compileEnc != null) {
                    return Charset.forName(compileEnc);
                }
            }
            FileObject testsrc = FileUtilities.convertStringToFileObject(mp.getBuild().getTestSourceDirectory());
            if (testsrc != null && FileUtil.isParentOf(testsrc, file)) {
                String testcompileEnc = PluginPropertyUtils.getPluginProperty(project, 
                        Constants.GROUP_APACHE_PLUGINS, Constants.PLUGIN_COMPILER, ENCODING_PARAM, "testCompile"); //NOI18N
                if (testcompileEnc != null) {
                    return Charset.forName(testcompileEnc);
                }
            }
        }

        //possibly more complicated with resources, one can have explicit declarations in the
        // pom plugin configuration.
        try {
            if (isWithin(project.getResources(false), file)) {
                String resourceEnc = PluginPropertyUtils.getPluginProperty(project,
                        Constants.GROUP_APACHE_PLUGINS, Constants.PLUGIN_RESOURCES, ENCODING_PARAM, "resources"); //NOI18N
                if (resourceEnc != null) {
                    return Charset.forName(resourceEnc);
                }
            }
            
        } catch (MalformedURLException x) {
            Exceptions.printStackTrace(x);
        }
        
        try {
            if (isWithin(project.getResources(true), file)) {
                String testresourceEnc = PluginPropertyUtils.getPluginProperty(project, 
                        Constants.GROUP_APACHE_PLUGINS, Constants.PLUGIN_RESOURCES, ENCODING_PARAM, "testResources"); //NOI18N
                if (testresourceEnc != null) {
                    return Charset.forName(testresourceEnc);
                }
            }
        } catch (MalformedURLException malformedURLException) {
            Exceptions.printStackTrace(malformedURLException);
        }
        return Charset.defaultCharset();
    }
    
    private boolean isWithin(URI[] res, FileObject file) throws MalformedURLException {
        for (URI ur : res) {
            FileObject fo = URLMapper.findFileObject(ur.toURL());
            if (fo != null && FileUtil.isParentOf(fo, file)) {
                return true;
            } 
        }
        return false;
        
    }
    

}