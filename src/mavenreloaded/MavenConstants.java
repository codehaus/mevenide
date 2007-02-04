/*
 * Copyright (c) 2006 MIT/Lincoln Laboratory.
 * All rights reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF
 * MIT/Lincoln Laboratory. The copyright notice does
 * not evidence any actual or intended publication of
 * such source code.
 */

package mavenreloaded;


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
    private MavenConstants() {
    }

}
