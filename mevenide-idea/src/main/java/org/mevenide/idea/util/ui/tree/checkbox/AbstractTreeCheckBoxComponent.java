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
package org.mevenide.idea.util.ui.tree.checkbox;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Arik
 */
public abstract class AbstractTreeCheckBoxComponent extends JPanel
    implements ConfigurableTreeCheckBoxComponent {
    private static final Log LOG = LogFactory.getLog(AbstractTreeCheckBoxComponent.class);

    private static final String CHECKOX_COMPONENT_NAME = "TreeRendererCheckbox";
    private static final String LABEL_COMPONENT_NAME = "TreeRendererLabel";

    private final JCheckBox checkBox;
    private final JLabel label;
    private final Color labelSelectionColor = UIManager.getColor(
        "Tree.selectionForeground");
    private final Color labelColor = UIManager.getColor("Tree.textForeground");

    protected AbstractTreeCheckBoxComponent(final JCheckBox pCheckBox,
                                            final JLabel pLabel) {
        super(new GridBagLayout());

        checkBox = pCheckBox;
        label = pLabel;

        checkBox.setMargin(new Insets(0, 0, 0, 0));
        checkBox.setBorderPaintedFlat(true);
        checkBox.setContentAreaFilled(false);
        checkBox.setName(CHECKOX_COMPONENT_NAME);
        label.setName(LABEL_COMPONENT_NAME);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 0, 0);
        add(checkBox, c);

        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 5, 0, 5);
        add(label, c);

        setOpaque(true);
    }

    public JCheckBox getCheckBox() {
        return checkBox;
    }

    public JLabel getLabel() {
        return label;
    }

    public void configure(final JTree pTree,
                          final Object pValue,
                          final boolean pSelected,
                          final boolean pExpanded,
                          final boolean pLeaf,
                          final int pRow,
                          final boolean pHasFocus) {

        final String stringValue = pTree.convertValueToText(pValue,
                                                            pSelected,
                                                            pExpanded,
                                                            pLeaf,
                                                            pRow,
                                                            pHasFocus);
        label.setText(stringValue);

        //
        //set label color, based on selection
        //
        if (pSelected)
            label.setForeground(labelSelectionColor);
        else
            label.setForeground(labelColor);

        //
        //set component orientation
        //
        setComponentOrientation(pTree.getComponentOrientation());
        checkBox.setComponentOrientation(pTree.getComponentOrientation());
        label.setComponentOrientation(pTree.getComponentOrientation());

        //
        //here we set the checkbox's visibility (if it is not a checkbox node)
        //and whether it is enabled or not (checkable). We find that out
        //using the tree model, which should implement CheckBoxTreeModel for
        //rendering checkboxes, and MutableCheckBoxTreeModel if checkbox
        //editing is desired.
        //
        final TreeModel model = pTree.getModel();
        if (model instanceof CheckBoxTreeModel) {
            final CheckBoxTreeModel cbModel = (CheckBoxTreeModel) model;

            final TreeNode node = (TreeNode) pValue;
            final boolean shouldDisplay = cbModel.shouldDisplayCheckBox(node);
            checkBox.setVisible(shouldDisplay);

            if (shouldDisplay) {
                checkBox.setSelected(cbModel.isChecked(node));

                //
                //determine if we should enable the checkbox, based on the model
                //
                if (cbModel instanceof MutableCheckBoxTreeModel) {
                    final MutableCheckBoxTreeModel mutableCbModel = (MutableCheckBoxTreeModel) model;
                    checkBox.setEnabled(mutableCbModel.isCheckable(node) && pTree.isEnabled());
                }
                else
                    checkBox.setEnabled(false);
            }
        }
        else {
            checkBox.setVisible(false);
            checkBox.setEnabled(false);
        }

        //
        //set the checkbox and panel to be non-opaque, so that the tree background can
        //be seen behind them
        //
        if (pSelected)
            setBackground(UIManager.getColor("Tree.selectionBackground"));
        else {
            //todo: perhaps we should always paint using the tree's background color?
            final Color bColor = UIManager.getColor("Tree.textBackground");
            setBackground(bColor == null ? pTree.getBackground() : bColor);
        }
    }
}
