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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Arik
 */
public class TreeCheckBoxEditor extends AbstractCellEditor
    implements TreeCellEditor, TreeSelectionListener {
    private static final Log LOG = LogFactory.getLog(TreeCheckBoxEditor.class);

    private JTree tree;
    private TreePath path;
    private TreeCheckBoxEditingComponent editor = new TreeCheckBoxEditingComponent();
    private Object value;

    public TreeCheckBoxEditor() {
        editor.getCheckBox().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final TreeModel model = tree.getModel();
                if (!(model instanceof MutableCheckBoxTreeModel))
                    return;

                final MutableCheckBoxTreeModel cbModel = (MutableCheckBoxTreeModel) model;
                final TreeNode node = (TreeNode) value;

                if (!cbModel.shouldDisplayCheckBox(node) || !cbModel.isCheckable(node))
                    return;

                final JCheckBox source = (JCheckBox) e.getSource();
                cbModel.setChecked(node, source.isSelected());
                source.setSelected(cbModel.isChecked(node));
            }
        });
    }

    public Object getCellEditorValue() {
        final TreeModel model = tree.getModel();
        if (!(model instanceof CheckBoxTreeModel))
            return false;

        final CheckBoxTreeModel cbModel = (CheckBoxTreeModel) model;
        final TreeNode node = (TreeNode) value;

        return cbModel.isChecked(node);
    }

    public Component getTreeCellEditorComponent(final JTree pTree,
                                                final Object pValue,
                                                final boolean pSelected,
                                                final boolean pExpanded,
                                                final boolean pLeaf,
                                                final int pRow) {
        //
        //make sure we use the given tree. Although it is illegal
        //to use this renderer for more than one tree, it might
        //happen from misuse - this is a simple protection against it
        //
        setTree(pTree);

        //
        //save the value and path for later
        //
        this.value = pValue;
        path = pTree.getPathForRow(pRow);

        //
        //configure the component according to the node state
        //
        editor.configure(pTree,
                         pValue,
                         pSelected,
                         pExpanded,
                         pLeaf,
                         pRow,
                         pTree.hasFocus());

        return editor;
    }

    public void setTree(final JTree pTree) {
        if (tree == pTree)
            return;

        if (tree != null)
            tree.removeTreeSelectionListener(this);

        tree = pTree;
        tree.addTreeSelectionListener(this);
    }

    public void valueChanged(final TreeSelectionEvent e) {
        final TreePath newPath = e.getPath();
        if (newPath != null && newPath.equals(path)) {
            final Object lastPathComponent = path.getLastPathComponent();
            final boolean leaf;
            if (lastPathComponent instanceof TreeNode)
                leaf = ((TreeNode) lastPathComponent).isLeaf();
            else {
                LOG.warn("Selection is not a TreeNode instance.");
                leaf = false;
            }

            editor.configure(tree,
                             value,
                             true,
                             tree.isExpanded(path),
                             leaf,
                             tree.getRowForPath(path),
                             tree.hasFocus());
        }
    }
}
