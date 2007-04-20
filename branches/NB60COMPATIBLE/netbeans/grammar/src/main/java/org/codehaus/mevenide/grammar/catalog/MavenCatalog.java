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


package org.codehaus.mevenide.grammar.catalog;

import org.netbeans.modules.xml.catalog.spi.*;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
/**
 *
 * @author Milos Kleint
 */
public class MavenCatalog implements CatalogReader, CatalogDescriptor, org.xml.sax.EntityResolver {

    private static final String POM_4_0_0 = "http://maven.apache.org/maven-v4_0_0.xsd"; // NOI18N
    private static final String ID_POM_4_0_0 = "SCHEMA:" + POM_4_0_0; // NOI18N
    private static final String SETTINGS_1_0_0 = "http://maven.apache.org/xsd/settings-1.0.0.xsd"; // NOI18N
    private static final String ID_SETTINGS_1_0_0 = "SCHEMA:" + SETTINGS_1_0_0; // NOI18N
    private static final String PROFILES_1_0_0 = "http://maven.apache.org/xsd/profiles-1.0.0.xsd"; // NOI18N
    private static final String ID_PROFILES_1_0_0 = "SCHEMA:" + PROFILES_1_0_0; // NOI18N
    private static final String ASSEMBLY_1_0_0 = "http://maven.apache.org/xsd/assembly-1.0.0.xsd"; // NOI18N
    private static final String ID_ASSEMBLY_1_0_0 = "SCHEMA:" + ASSEMBLY_1_0_0; // NOI18N
            
    private static final String URL_POM_4_0_0 ="nbres:/org/codehaus/mevenide/grammar/maven-4.0.0.xsd"; // NOI18N
    private static final String URL_SETTINGS_1_0_0 ="nbres:/org/codehaus/mevenide/grammar/settings-1.0.0.xsd"; // NOI18N
    private static final String URL_PROFILES_1_0_0 ="nbres:/org/codehaus/mevenide/grammar/profiles-1.0.0.xsd"; // NOI18N
    private static final String URL_ASSEMBLY_1_0_0 ="nbres:/org/codehaus/mevenide/grammar/assembly-1.0.0.xsd"; // NOI18N
    
    /** Creates a new instance of MavenCatalog */
    public MavenCatalog() {
    }
    
    /**
     * Get String iterator representing all public IDs registered in catalog.
     * @return null if cannot proceed, try later.
     */
    public java.util.Iterator getPublicIDs() {
        java.util.List list = new java.util.ArrayList();
        list.add(ID_POM_4_0_0);
        list.add(ID_SETTINGS_1_0_0);
        list.add(ID_PROFILES_1_0_0);
        list.add(ID_ASSEMBLY_1_0_0);
        return list.listIterator();
    }
    
    /**
     * Get registered systemid for given public Id or null if not registered.
     * @return null if not registered
     */
    public String getSystemID(String publicId) {
        if (ID_POM_4_0_0.equals(publicId))
            return URL_POM_4_0_0;
        else if (ID_SETTINGS_1_0_0.equals(publicId))
            return URL_SETTINGS_1_0_0;
        else if ((ID_PROFILES_1_0_0.equals(publicId))) 
            return URL_PROFILES_1_0_0;
        else if (ID_ASSEMBLY_1_0_0.equals(publicId))
            return URL_ASSEMBLY_1_0_0;
        else return null;
    }
    
    /**
     * Refresh content according to content of mounted catalog.
     */
    public void refresh() {
    }
    
    /**
     * @throws UnsupportedOpertaionException if not supported by the implementation.
     */
    public void addCatalogListener(CatalogListener l) {
    }
    
    /**
     * @throws UnsupportedOpertaionException if not supported by the implementation.
     */
    public void removeCatalogListener(CatalogListener l) {
    }
    
    /** Registers new listener.  */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
    }
    
     /** Unregister the listener.  */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
    }
    
    /**
     * @return I18N display name
     */
    public String getDisplayName() {
        return NbBundle.getMessage (MavenCatalog.class, "LBL_MavenCatalog");  //NOI18N
    }
    
    /**
     * Return visuaized state of given catalog.
     * @param type of icon defined by JavaBeans specs
     * @return icon representing current state or null
     */
    public java.awt.Image getIcon(int type) {
        return Utilities.loadImage("org/codehaus/mevenide/netbeans/Maven2Icon.gif"); // NOI18N
    }
    
    /**
     * @return I18N short description
     */
    public String getShortDescription() {
        return NbBundle.getMessage (MavenCatalog.class, "DESC_MavenCatalog");     //NOI18N
    }
    
   /**
     * @param publicId publicId for resolved entity (null in our case)
     * @param systemId systemId for resolved entity
     * @return InputSource for publicId/systemId 
     */    
    public org.xml.sax.InputSource resolveEntity(String publicId, String systemId) throws org.xml.sax.SAXException, java.io.IOException {
        if (POM_4_0_0.equals(systemId)) {
            return new org.xml.sax.InputSource(URL_POM_4_0_0);
        } else if (SETTINGS_1_0_0.equals(systemId)) {
            return new org.xml.sax.InputSource(URL_SETTINGS_1_0_0);
        } else if (PROFILES_1_0_0.equals(systemId)) {
            return new org.xml.sax.InputSource(URL_PROFILES_1_0_0);
        } else if (ASSEMBLY_1_0_0.equals(systemId)) {
            return new org.xml.sax.InputSource(URL_ASSEMBLY_1_0_0);
        } else {
            return null;
        }
    }
    
    /**
     * Get registered URI for the given name or null if not registered.
     * @return null if not registered
     */
    public String resolveURI(String name) {
        return null;
    }
    /**
     * Get registered URI for the given publicId or null if not registered.
     * @return null if not registered
     */ 
    public String resolvePublic(String publicId) {
        return null;
    }
}
