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
package org.mevenide.netbeans.project.exec;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditorSupport;
import javax.swing.JPanel;
import org.mevenide.netbeans.project.goals.GoalUtils;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class GoalPropEditor extends PropertyEditorSupport
{
    /** Creates new Goal */
    public GoalPropEditor()
    {
    }
    
    public Component getCustomEditor()
    {
        return createCustomEditor(this);
    }
    
    public boolean supportsCustomEditor()
    {
        return true;
    }
    
    private static JPanel createCustomEditor(final GoalPropEditor editor)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints con = new GridBagConstraints();
        con.insets = new Insets(12, 12, 12, 12);
        final CustomGoalsPanel innerpanel = new CustomGoalsPanel(GoalUtils.createDefaultGoalsProvider());
        innerpanel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                editor.setValue(innerpanel.getGoalsToExecute());
            }
        });
        panel.add(innerpanel, con);
        innerpanel.setGoalsToExecute(editor.getAsText());
        return panel;
    }
    
}
