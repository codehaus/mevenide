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
package org.mevenide.ui.eclipse.launch.goals.model;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.swt.graphics.Image;
import org.mevenide.IGoalsGrabber;
import org.mevenide.ui.eclipse.MavenPlugin;


public class GoalsProvider extends MevenideProvider {
	
    public GoalsProvider(IGoalsGrabber goalsGrabber) {
        super(goalsGrabber);
    }

    public Object[] getElements(Object inputElement) {
        if (!(inputElement instanceof String)) {
            throw new RuntimeException(
                "unexpected problem. expected String, found : "
                    + inputElement.getClass());
        }
        //return MavenGoals.getMavenGoals().getSubGoals((String) inputElement);
        Collection goalsFor = goalsGrabber.getGoals((String) inputElement);
        if (goalsFor != null) {
            Object[] goalz = goalsFor.toArray();
            Arrays.sort(goalz, String.CASE_INSENSITIVE_ORDER);
            return goalz;
        }
        return new String[0];
    }

    public Image getImage(Object element) {
        return MavenPlugin.getImageDescriptor("goal-16.gif").createImage();
        //return null;
    }
}