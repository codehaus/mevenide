package org.mevenide.idea.settings;

/**
 * @author Arik
 */
public abstract class XMLConstants {
    /**
     * The name of the favorite goals XML element used serializable/deserialization.
     */
    public static final String FAVORITE_GOALS_ELT_NAME = "favorites";
    /**
     * The name of the POM file XML attribute used in serialization.
     */
    public static final String POM_FILE_ATTR_NAME = "mavenDescriptor";
    /**
     * The maven home element name.
     */
    public static final String MAVEN_HOME_ELT_NAME = "mavenHome";
}
