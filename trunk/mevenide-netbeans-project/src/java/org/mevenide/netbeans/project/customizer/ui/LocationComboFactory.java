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

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.properties.IPropertyLocator;
import org.openide.util.Utilities;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class LocationComboFactory {
    
    public static OriginChange createPropertiesChange(MavenProject project, int currentLocation) {
        LocationComboBox box = new LocationComboBox();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        Icon icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocProject.gif"));
        model.addElement(new LocationComboBox.LocationWrapper("Project", icon,
                             FileUtilities.locationToFile(IPropertyLocator.LOCATION_PROJECT, project), 
                             IPropertyLocator.LOCATION_PROJECT ));
        icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocBuild.gif"));
        model.addElement(new LocationComboBox.LocationWrapper("Build", icon,
                             FileUtilities.locationToFile(IPropertyLocator.LOCATION_PROJECT_BUILD, project), 
                             IPropertyLocator.LOCATION_PROJECT_BUILD ));
        icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocUser.gif"));
        model.addElement(new LocationComboBox.LocationWrapper("User", icon,
                             FileUtilities.locationToFile(IPropertyLocator.LOCATION_USER_BUILD, project), 
                             IPropertyLocator.LOCATION_USER_BUILD ));
        icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocDefault.gif"));
        model.addElement(new LocationComboBox.LocationWrapper("Default", icon,
                             null, 
                             IPropertyLocator.LOCATION_DEFAULTS));
        icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/LocNotDefined.gif"));
        model.addElement(new LocationComboBox.LocationWrapper("N/D", icon,
                             null, 
                             IPropertyLocator.LOCATION_NOT_DEFINED));
        box.setModel(model);
        box.startLoggingChanges();
        return new OriginChange(box);
    }
    
}
