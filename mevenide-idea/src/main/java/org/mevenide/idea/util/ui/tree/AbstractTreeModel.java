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

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * @author Arik
 */
public abstract class AbstractTreeModel extends DefaultTreeModel {

    protected AbstractTreeModel() {
        super(null);

    }

    protected AbstractTreeModel(final TreeNode pRoot) {
        super(pRoot);
    }

    protected AbstractTreeModel(final TreeNode pRoot,
                                final boolean pAsksAllowsChildren) {
        super(pRoot, pAsksAllowsChildren);
    }

    public MutableTreeNode getMutableRoot() {
        return (MutableTreeNode) root;
    }

    protected static TreeNode findNode(final TreeNode pNode,
                                       final NodeVisitor pVisitor) {
        return findNode(pNode, pVisitor, Integer.MAX_VALUE);
    }

    protected static TreeNode findNode(final TreeNode pNode,
                                       final NodeVisitor pVisitor,
                                       final int pLevel) {
        return findNode(pNode, pVisitor, pLevel, 0);
    }

    private static TreeNode findNode(final TreeNode pNode,
                                     final NodeVisitor pVisitor,
                                     final int pLevel,
                                     final int pCurrentLevel) {
        if(pVisitor.accept((pNode)))
            return pNode;

        final int childCount = pNode.getChildCount();
        for(int i = 0; i < childCount; i++) {
            final TreeNode node = pNode.getChildAt(i);
            if(pLevel == pCurrentLevel) {
                if(pVisitor.accept(node))
                    return node;
            }
            else {
                final TreeNode acceptedNode = findNode(node,
                                                       pVisitor,
                                                       pLevel,
                                                       pCurrentLevel + 1);
                if(acceptedNode != null)
                    return acceptedNode;
            }
        }

        return null;
    }

    protected static interface NodeVisitor {
        boolean accept(TreeNode pNode);
    }
}
