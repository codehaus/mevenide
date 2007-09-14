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
    
    /**
     * pom.xml property that hints netbeans to use a given license template.
     */ 
    public static final String HINT_LICENSE = "netbeans.hint.license"; //NOI18N

    /**
     * maven property that when set forces netbeans to use external maven instance
     * instead of the embedded Maven.
     */ 
    public static final String HINT_USE_EXTERNAL="netbeans.hint.useExternalMaven"; //NOI18N
    
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
    
}
