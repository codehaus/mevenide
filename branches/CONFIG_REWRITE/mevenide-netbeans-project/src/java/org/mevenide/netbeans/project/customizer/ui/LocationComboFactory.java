/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.properties.IPropertyLocator;
import org.openide.util.Utilities;


/**
 * Factory for creating and configuring the OriginChange instances.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class LocationComboFactory {
    
    public static OriginChange createPropertiesChange(MavenProject project) {
        LocationComboBox box = new LocationComboBox(false);
        LocationComboBox.LocationWrapper[] wraps = new LocationComboBox.LocationWrapper[5];
        String[] actions = new String[] {
            OriginChange.ACTION_MOVE_TO_BUILD,
            OriginChange.ACTION_MOVE_TO_USER,
            OriginChange.ACTION_RESET_TO_DEFAULT
        };
        Icon icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocProject.png"));
        wraps[0] = new LocationComboBox.LocationWrapper("Defined in project.properties", icon,
                             FileUtilities.locationToFile(IPropertyLocator.LOCATION_PROJECT, project), 
                             IPropertyLocator.LOCATION_PROJECT,
                             actions);
        actions = new String[] {
            OriginChange.ACTION_MOVE_TO_PROJECT,
            OriginChange.ACTION_MOVE_TO_USER,
            OriginChange.ACTION_RESET_TO_DEFAULT
        };
        icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocBuild.png"));
        wraps[1] = new LocationComboBox.LocationWrapper("Defined in build.properties", icon,
                             FileUtilities.locationToFile(IPropertyLocator.LOCATION_PROJECT_BUILD, project), 
                             IPropertyLocator.LOCATION_PROJECT_BUILD,
                             actions);
        actions = new String[] {
            OriginChange.ACTION_MOVE_TO_PROJECT,
            OriginChange.ACTION_MOVE_TO_BUILD,
            OriginChange.ACTION_RESET_TO_DEFAULT
        };
        icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocUser.png"));
        wraps[2] = new LocationComboBox.LocationWrapper("Defined in user's build.properties", icon,
                             FileUtilities.locationToFile(IPropertyLocator.LOCATION_USER_BUILD, project), 
                             IPropertyLocator.LOCATION_USER_BUILD,
                             actions);
        actions = new String[] {
            OriginChange.ACTION_DEFINE_IN_PROJECT,
            OriginChange.ACTION_DEFINE_IN_BUILD,
            OriginChange.ACTION_DEFINE_IN_USER
        };
        icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocDefault.png"));
        wraps[3] = new LocationComboBox.LocationWrapper("Default value", icon,
                             null, 
                             IPropertyLocator.LOCATION_DEFAULTS,
                             actions);
        icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocNotDefined.png"));
        wraps[4] = new LocationComboBox.LocationWrapper("No defined value", icon,
                             null, 
                             IPropertyLocator.LOCATION_NOT_DEFINED,
                             actions);
        box.setItems(wraps);
        return new OriginChange(box);
    }
    
    /**
     * @param project - the project instance
     * @param showText - will show both text and icon if true, otherwise just icon.
     */
    public static OriginChange createPOMChange(MavenProject project, boolean showText) {
        LocationComboBox box = new LocationComboBox(showText);
        boolean hasParent = project.getOriginalMavenProject().getExtend() != null;
        int size = hasParent ? 3 : 2;
        LocationComboBox.LocationWrapper[] wraps = new LocationComboBox.LocationWrapper[size];
        String[] actions;
        if (hasParent) {
            actions = new String[] {
                OriginChange.ACTION_POM_MOVE_TO_PARENT,
                OriginChange.ACTION_REMOVE_ENTRY
            };
        } else {
            actions = new String[] {
                OriginChange.ACTION_REMOVE_ENTRY
            };
        }
        Icon icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocPomFile.png"));
        wraps[0] = new LocationComboBox.LocationWrapper("Defined in project's POM file", icon,
                             FileUtilities.locationToFile(OriginChange.LOCATION_POM, project), 
                             OriginChange.LOCATION_POM,
                             actions);
        if (hasParent) {
            actions = new String[] {
                OriginChange.ACTION_POM_MOVE_TO_CHILD,
                OriginChange.ACTION_POM_MOVE_TO_PARENT
            };
        } else {
            actions = new String[] {
                OriginChange.ACTION_POM_MOVE_TO_CHILD
            };
        }
        icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocDefault.png"));
        wraps[1] = new LocationComboBox.LocationWrapper("No defined value", icon,
                             null, 
                             IPropertyLocator.LOCATION_NOT_DEFINED,
                             actions);
        if (hasParent) {
            actions = new String[] {
                OriginChange.ACTION_POM_MOVE_TO_CHILD,
                OriginChange.ACTION_REMOVE_ENTRY
            };
            icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocParentPOM.png"));
            wraps[2] = new LocationComboBox.LocationWrapper("Defined in POM's parent definition", icon,
                                 FileUtilities.locationToFile(OriginChange.LOCATION_POM_PARENT, project), 
                                 OriginChange.LOCATION_POM_PARENT,
                                 actions);
        }
        box.setItems(wraps);
        return new OriginChange(box);
    }

}
