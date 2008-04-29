/* ==========================================================================
 * Copyright 2006 Mevenide Team
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



package org.codehaus.mevenide.idea;

/**
 * A class that holds constants that can be used when dealing with maven 2.
 *
 * @author bkate
 */
public final class MavenConstants {

    // the name of a maven 2 pom file
    public static final String POM_NAME = "pom.xml";

    // dependency scopes
    public static final String SYSTEM_SCOPE = "system";
    public static final String COMPILE_SCOPE = "compile";
    public static final String TEST_SCOPE = "test";
    public static final String PROVIDED_SCOPE = "provided";
    public static final String RUNTIME_SCOPE = "runtime";

    // packaging
    public static final String JAR_PACKAGING = "jar";
    public static final String POM_PACKAGING = "pom";

    // classifiers
    public static final String SOURCES_CLASSIFIER = "sources";
    public static final String JAVADOC_CLASSIFIER = "javadoc";

    // special cased versions
    public static final String SNAPSHOT_VERSION = "SNAPSHOT";

    // no access
    private MavenConstants() {}
}
