/* 
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 */
package org.mevenide.ui.eclipse.dialog.goals.model;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.swt.graphics.Image;
import org.mevenide.IGoalsGrabber;
import org.mevenide.ui.eclipse.MavenPlugin;


public class PluginsProvider extends MevenideProvider {
	
    public PluginsProvider(IGoalsGrabber goalsGrabber) {
        super(goalsGrabber);
    }

    public Object[] getElements(Object inputElement) {
        //return MavenGoals.getMavenGoals().getPrimaryGoals();
        Collection plugins = goalsGrabber.getPlugins();
        if (plugins != null) {
            Object[] catz = plugins.toArray();
            Arrays.sort(catz, String.CASE_INSENSITIVE_ORDER);
            return catz;
        }
        return new String[0];
    }

    public Image getImage(Object element) {
        return MavenPlugin.getImageDescriptor("plugin-16.gif").createImage();
        //return null;
    }

}