package org.mevenide.idea.repository;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.mevenide.repository.RepoPathElement;
import org.mevenide.idea.Res;
import org.mevenide.idea.repository.model.MessageTreeNode;
import org.mevenide.idea.repository.model.NodeDescriptor;
import org.mevenide.idea.repository.model.RepoTreeNode;

/**
 * @author Arik
 */
public class RepositoryTreeCellRenderer extends DefaultTreeCellRenderer {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(RepositoryTreeCellRenderer.class);

    /**
     * The text to display for the root node, if displayed.
     */
    protected static final String ROOT_TEXT = RES.get("repo.tree.root.label");

    /**
     * If the given value is a {@link MessageTreeNode}, show its message. Otherwise, if the node is
     * a {@link org.mevenide.idea.repository.model.RepoTreeNode}, render its {@link org.mevenide.idea.repository.model.RepoTreeNode#getNodeDescriptor()
     * node descriptor}. Otherwise, renders the value's {@link Object#toString()}.
     */
    @Override public Component getTreeCellRendererComponent(JTree tree,
                                                            Object value,
                                                            boolean sel,
                                                            boolean expanded,
                                                            boolean leaf,
                                                            int row,
                                                            boolean hasFocus) {

        final String text;
        if (value instanceof MessageTreeNode)
            text = ((MessageTreeNode) value).getMessage();
        else if (value instanceof RepoTreeNode) {
            final RepoTreeNode repoTreeNode = (RepoTreeNode) value;
            text = convertRepoPathElementToString(repoTreeNode.getNodeDescriptor());
        }
        else
            text = value == null ? "" : value.toString();

        return super.getTreeCellRendererComponent(tree,
                                                  text,
                                                  sel,
                                                  expanded,
                                                  leaf,
                                                  row,
                                                  hasFocus);
    }

    protected String convertRepoPathElementToString(final NodeDescriptor desc) {
        switch (desc.getLevel()) {
            case RepoPathElement.LEVEL_ARTIFACT:
                return desc.getArtifactId();

            case RepoPathElement.LEVEL_GROUP:
                return desc.getGroupId();

            case RepoPathElement.LEVEL_ROOT:
                return ROOT_TEXT;

            case RepoPathElement.LEVEL_TYPE:
                return desc.getType();

            case RepoPathElement.LEVEL_VERSION:
                final String ext = desc.getExtension();
                if (ext != null && ext.trim().length() > 0)
                    return desc.getArtifactId() + "-" + desc.getVersion() + "." + ext;
                else
                    return desc.getArtifactId() + "-" + desc.getVersion();

            default:
                return desc.toString();
        }
    }
}
