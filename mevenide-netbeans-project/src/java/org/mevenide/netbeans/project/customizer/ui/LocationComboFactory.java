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
package org.mevenide.netbeans.project.customizer.ui;

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.mevenide.context.IProjectContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.properties.IPropertyLocator;
import org.openide.util.Utilities;


/**
 * Factory for creating and configuring the OriginChange instances.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public final class LocationComboFactory {
    
    private LocationComboFactory() {
        
    }
    
    public static OriginChange createPropertiesChange(MavenProject project) {
        LocationComboBox box = new LocationComboBox(false);
        boolean justSingle = true;
        int level = 1;
        IProjectContext pom = project.getContext().getPOMContext();
        if (pom != null) {
            justSingle = pom.getProjectDepth() == 1;
            level = pom.getProjectDepth();
        }
        Collection col = new ArrayList();
        
        Icon icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocDefault.png"));
        Icon mvIcon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToDefault.png"));
        col.add(new LocationComboBox.LocationWrapper(
                    "Default value", icon,
                     "Reset to Default", mvIcon,
                     null, 
                     IPropertyLocator.LOCATION_DEFAULTS));
        // no defined value
        col.add(new LocationComboBox.LocationWrapper(
                     "Default value", icon,
                     null, null,
                     null, 
                     IPropertyLocator.LOCATION_NOT_DEFINED));
        icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocNotDefined.png"));
        col.add(new LocationComboBox.LocationWrapper(
                     "Defined in System Environment Variable", icon,
                     null, null, 
                     null, 
                     IPropertyLocator.LOCATION_SYSENV));
        
        icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocUser.png"));
        mvIcon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToUser.png"));
        col.add(new LocationComboBox.LocationWrapper(
                        "Defined in user's build.properties", icon,
                        "Move to User", mvIcon, 
                        FileUtilities.locationToFile(IPropertyLocator.LOCATION_USER_BUILD, project), 
                        IPropertyLocator.LOCATION_USER_BUILD));
        
        // let's add project pom wrappers
        if (level > 0) {
            icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocProject.png"));
            mvIcon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToProject.png"));
            col.add(new LocationComboBox.LocationWrapper(
                           "Defined in project.properties", icon,
                           "Move to Project", mvIcon,
                           FileUtilities.locationToFile(IPropertyLocator.LOCATION_PROJECT, project), 
                           IPropertyLocator.LOCATION_PROJECT));
            icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocBuild.png"));
            mvIcon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToBuild.png"));
            col.add(new LocationComboBox.LocationWrapper(
                           "Defined in build.properties", icon,
                           "Move to Build", mvIcon, 
                           FileUtilities.locationToFile(IPropertyLocator.LOCATION_PROJECT_BUILD, project), 
                           IPropertyLocator.LOCATION_PROJECT_BUILD));
        }
        // parent pom wrappers..
        if (level > 1) {
            icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocParentProject.png"));
            mvIcon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToParentProject.png"));
            col.add(new LocationComboBox.LocationWrapper(
                            "Defined in parent project.properties", icon,
                            "Move to Parent Project",  mvIcon, 
                            FileUtilities.locationToFile(IPropertyLocator.LOCATION_PARENT_PROJECT, project),
                            IPropertyLocator.LOCATION_PARENT_PROJECT));
            icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocParentBuild.png"));
            mvIcon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToParentBuild.png"));
            col.add(new LocationComboBox.LocationWrapper(
                            "Defined in parent build.properties", icon,
                            "Move to Parent Build", mvIcon, 
                            FileUtilities.locationToFile(IPropertyLocator.LOCATION_PARENT_PROJECT_BUILD, project),
                            IPropertyLocator.LOCATION_PARENT_PROJECT_BUILD));
        }
        // now let's handle the other levels in indefinite manner.
        if (level > 2) {
            for (int i = 3; i <= level; i++) {
                icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocXthParentProject.png"));
                mvIcon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToXthParentProject.png"));
                col.add(new LocationComboBox.LocationWrapper(
                        "Defined in " + (i - 1) + ". parent project.properties", icon,
                        "Move to " + (i - 1) + "Parent Project",  mvIcon,
                        FileUtilities.locationToFile(i * 10 + IQueryContext.PROJECT_PROPS_OFFSET, project),
                        i * 10 + IQueryContext.PROJECT_PROPS_OFFSET));
                icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocXthParentBuild.png"));
                mvIcon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToXthParentBuild.png"));
                col.add(new LocationComboBox.LocationWrapper(
                        "Defined in " + (i - 1) + ". parent build.properties", icon,
                        "Move to " + (i - 1) + ". Parent Build", mvIcon,
                        FileUtilities.locationToFile(i * 10 + IQueryContext.BUILD_PROPS_OFFSET, project),
                        i * 10 + IQueryContext.BUILD_PROPS_OFFSET));
            }
        }
        LocationComboBox.LocationWrapper[] wraps = new LocationComboBox.LocationWrapper[col.size()];
        wraps = (LocationComboBox.LocationWrapper[])col.toArray(wraps);
        box.setItems(wraps);
        return new OriginChange(box);
    }
    
    /**
     * @param project - the project instance
     * @param showText - will show both text and icon if true, otherwise just icon.
     */
    public static OriginChange createPOMChange(MavenProject project, boolean showText) {
        LocationComboBox box = new LocationComboBox(showText);
        int poms = project.getContext().getPOMContext().getProjectDepth();
        Collection col = new ArrayList();
                
        Icon icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocPomFile.png"));
        Icon mvIcon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToDefault.png"));
        col.add(new LocationComboBox.LocationWrapper(
                    "Defined in project's POM file", icon,
                    "Move to POM", mvIcon, 
                    FileUtilities.locationToFile(OriginChange.LOCATION_POM, project), 
                    OriginChange.LOCATION_POM));
        icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocDefault.png"));
        col.add(new LocationComboBox.LocationWrapper(
                    "No defined value", icon,
                    "Remove Definition", mvIcon,
                    null, 
                    IPropertyLocator.LOCATION_NOT_DEFINED));
        if (poms > 1) {
            icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocParentPOM.png"));
            col.add(new LocationComboBox.LocationWrapper(
                        "Defined in POM's parent definition", icon,
                        "Move to Parent", mvIcon, 
                        FileUtilities.locationToFile(OriginChange.LOCATION_POM_PARENT, project), 
                        OriginChange.LOCATION_POM_PARENT));
        }
        if (poms > 2) {
            for (int i = 3; i <= poms; i++) {
                System.err.println("creating pom wrapper for" + (i-1));
                icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocParParPOM.png"));
                col.add(new LocationComboBox.LocationWrapper(
                            "Defined in POM's " + (i - 1) + ". parent definition", icon,
                            "Move to " + (i - 1) + ". Parent", mvIcon, 
                            FileUtilities.locationToFile(i - 1, project), 
                            i - 1));
            }
        }
        LocationComboBox.LocationWrapper[] wraps = new LocationComboBox.LocationWrapper[col.size()];
        wraps = (LocationComboBox.LocationWrapper[])col.toArray(wraps);
        box.setItems(wraps);
        return new OriginChange(box);
    }

}
