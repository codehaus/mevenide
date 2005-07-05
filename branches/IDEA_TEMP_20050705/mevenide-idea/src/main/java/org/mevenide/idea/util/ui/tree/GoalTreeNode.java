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
package org.mevenide.idea.util.ui.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import org.mevenide.idea.Res;

/**
 * @author Arik
 */
public class GoalTreeNode extends DefaultMutableTreeNode {
    private static final Res RES = Res.getInstance(GoalTreeNode.class);
    private final String description;
    private final String[] prereqs;

    public GoalTreeNode(final String pGoal) {
        this(pGoal, null, null);
    }

    public GoalTreeNode(final String pGoal, final String pDescription) {
        this(pGoal, pDescription, null);
    }

    public GoalTreeNode(final String pGoal, final String[] pPrereqs) {
        this(pGoal, null, pPrereqs);
    }

    public GoalTreeNode(final String pGoal,
                        final String pDescription,
                        final String[] pPrereqs) {
        super(pGoal);

        if (pGoal == null || pGoal.trim().length() == 0)
            throw new IllegalArgumentException(RES.get("empty.goal.name"));

        if (pDescription == null || pDescription.trim().length() == 0 || pDescription.equalsIgnoreCase(
            "null"))
            description = null;
        else
            description = pDescription;

        if (pPrereqs == null)
            prereqs = new String[0];
        else
            prereqs = pPrereqs;
    }

    public String getUserObject() {
        return (String) super.getUserObject();
    }

    public String getGoal() {
        return getUserObject();
    }

    public String getDescription() {
        return description;
    }

    public String[] getPrereqs() {
        return prereqs;
    }

    public String toString() {
        if (description == null || description.trim().length() == 0)
            return super.toString();
        else
            return super.toString() + " - " + description;
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }
}
