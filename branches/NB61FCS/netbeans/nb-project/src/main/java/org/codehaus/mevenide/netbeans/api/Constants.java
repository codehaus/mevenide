/*
 *  Copyright 2007 Mevenide Team
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.codehaus.mevenide.netbeans.api;

/**
 * Various constants used across the integration, Maven property names with a meaning in the IDE,
 * plugin groupIds, artifactIds etc.
 * @author mkleint
 */
public interface Constants {
    
    public final String HINT_DEPLOY_J2EE_SERVER_OLD = "netbeans.deployment.server.type"; //NOI18N
    
    public final String HINT_DEPLOY_J2EE_SERVER_ID = "netbeans.deployment.server.id"; //NOI18N
    
    public final String HINT_DEPLOY_J2EE_SERVER = "netbeans.hint.deploy.server"; //NOI18N

    public final String HINT_J2EE_VERSION = "netbeans.hint.j2eeVersion"; //NOI18N
    
    /**
     * Maven property that hints netbeans to use a given license template.
     */ 
    public static final String HINT_LICENSE = "netbeans.hint.license"; //NOI18N

    /**
     * Maven property that when set forces netbeans to use external maven instance
     * instead of the embedded Maven.
     */ 
    public static final String HINT_USE_EXTERNAL="netbeans.hint.useExternalMaven"; //NOI18N
    
    /**
     * Maven property that designates the jdk platform to use in the IDE on classpath for project.
     * Equivalent to the "platform.active" property in Ant based projects.
     * Workaround for issue http://www.netbeans.org/issues/show_bug.cgi?id=104974
     * Will only influence the classpath in the IDE, not the maven build itself.
     */
    public static final String HINT_JDK_PLATFORM="netbeans.hint.jdkPlatform"; //NOI18N

    
    /**
     * Maven property that hints netbeans to handle the project as if it were of given packaging..
     * Influences the available default action mappings, panels in customizers and other UI functionality in the IDE.
     * Useful for cases when you define a custom packaging eg "jar2" but want the ide to handle it as j2se/jar project.
     * Meaningful values include: jar,war,ejb,ear,nbm
     */ 
    public static final String HINT_PACKAGING = "netbeans.hint.packaging"; //NOI18N
    
    /**
     * apache maven default groupid for maven plugins. 
     */ 
    public static final String GROUP_APACHE_PLUGINS = "org.apache.maven.plugins"; //NOI18N
    
    public static final String PLUGIN_COMPILER = "maven-compiler-plugin";//NOI18N
    public static final String PLUGIN_WAR = "maven-war-plugin";//NOI18N
    public static final String PLUGIN_RESOURCES = "maven-resources-plugin";//NOI18N
    public static final String PLUGIN_EJB = "maven-ejb-plugin";//NOI18N
    public static final String PLUGIN_EAR = "maven-ear-plugin";//NOI18N
    public static final String PLUGIN_SUREFIRE = "maven-surefire-plugin";//NOI18N
    
    public static final String ENCODING_PARAM = "encoding"; //NOI18N
    public static final String SOURCE_PARAM = "source";//NOI18N
    public static final String TARGET_PARAM = "target";//NOI18N

    //this property was introduced as part of this proposal:
    //http://docs.codehaus.org/display/MAVENUSER/POM+Element+for+Source+File+Encoding
    public static String ENCODING_PROP = "project.build.sourceEncoding"; //NOI18N

    
}
