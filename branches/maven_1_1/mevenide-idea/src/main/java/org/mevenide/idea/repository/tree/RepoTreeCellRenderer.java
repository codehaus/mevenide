package org.mevenide.idea.repository.tree;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.mevenide.idea.repository.tree.model.MessageNode;
import org.mevenide.idea.repository.tree.model.RepoTreeNode;
import org.mevenide.idea.util.ui.images.Icons;
import org.mevenide.repository.RepoPathElement;

/**
 * @author Arik
 */
public class RepoTreeCellRenderer extends DefaultTreeCellRenderer {
    private final Icon DEFAULT_CLOSED_ICON;
    private final Icon DEFAULT_OPEN_ICON;
    private final Icon DEFAULT_LEAF_ICON;

    public RepoTreeCellRenderer() {
        DEFAULT_CLOSED_ICON = getClosedIcon();
        DEFAULT_OPEN_ICON = getOpenIcon();
        DEFAULT_LEAF_ICON = getLeafIcon();
    }

    /**
     * If the given value is a {@link org.mevenide.idea.repository.tree.model.MessageTreeNode}, show
     * its message. Otherwise, if the node is a {@link org.mevenide.idea.repository.tree.model.RepoTreeNode},
     * render its {@link org.mevenide.idea.repository.tree.model.RepoTreeNode#getNodeDescriptor()
     * node descriptor}. Otherwise, renders the value's {@link Object#toString()}.
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus) {
        if (value instanceof RepoTreeNode) {
            final RepoPathElement desc = ((RepoTreeNode) value).getPathElement();
            switch (desc.getLevel()) {
                case RepoPathElement.LEVEL_GROUP:
                    setOpenIcon(Icons.REPO_GROUP_OPEN);
                    setClosedIcon(Icons.REPO_GROUP_CLOSED);
                    break;

                case RepoPathElement.LEVEL_TYPE:
                    setTypeIcons(desc);
                    break;

                case RepoPathElement.LEVEL_ARTIFACT:
                    setArtifactIcons(desc);
                    break;

                case RepoPathElement.LEVEL_VERSION:
                    setOpenIcon(Icons.REPO_VERSION);
                    setClosedIcon(Icons.REPO_VERSION);
                    setLeafIcon(Icons.REPO_VERSION);
                    break;

                default:
                    setOpenIcon(DEFAULT_OPEN_ICON);
                    setClosedIcon(DEFAULT_CLOSED_ICON);
                    setLeafIcon(DEFAULT_LEAF_ICON);
            }
        }
        else if (value instanceof MessageNode) {
            setOpenIcon(Icons.REPO_MSG);
            setClosedIcon(Icons.REPO_MSG);
            setLeafIcon(Icons.REPO_MSG);
        }

        return super.getTreeCellRendererComponent(tree,
                                                  getTextForValue(value),
                                                  sel,
                                                  expanded,
                                                  leaf,
                                                  row,
                                                  hasFocus);
    }

    private void setArtifactIcons(final RepoPathElement pDesc) {
        final String type = pDesc.getType();
        if ("jar".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_ARCHIVE);
            setClosedIcon(Icons.REPO_TYPE_ARCHIVE);
        }
        else if ("distribution".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_ARCHIVE);
            setClosedIcon(Icons.REPO_TYPE_ARCHIVE);
        }
        else if ("pom".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_POM);
            setClosedIcon(Icons.REPO_TYPE_POM);
        }
        else if ("license".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_LICENSE);
            setClosedIcon(Icons.REPO_TYPE_LICENSE);
        }
        else if ("src.jar".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_ARCHIVE);
            setClosedIcon(Icons.REPO_TYPE_ARCHIVE);
        }
        else if ("javadoc.jar".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_ARCHIVE);
            setClosedIcon(Icons.REPO_TYPE_ARCHIVE);
        }
        else if ("ejb".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_ARCHIVE);
            setClosedIcon(Icons.REPO_TYPE_ARCHIVE);
        }
        else if ("war".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_ARCHIVE);
            setClosedIcon(Icons.REPO_TYPE_ARCHIVE);
        }
        else if ("ear".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_ARCHIVE);
            setClosedIcon(Icons.REPO_TYPE_ARCHIVE);
        }
        else if ("plugin".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_ARCHIVE);
            setClosedIcon(Icons.REPO_TYPE_ARCHIVE);
        }
        else {
            setOpenIcon(Icons.REPO_ARTIFACT);
            setClosedIcon(Icons.REPO_ARTIFACT);
        }
    }

    private void setTypeIcons(final RepoPathElement pDesc) {
        final String type = pDesc.getType();
        if ("jar".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_JAR_OPEN);
            setClosedIcon(Icons.REPO_TYPE_JAR_OPEN);
        }
        else if ("distribution".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_DIST_OPEN);
            setClosedIcon(Icons.REPO_TYPE_DIST_CLOSED);
        }
        else if ("pom".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_POM_OPEN);
            setClosedIcon(Icons.REPO_TYPE_POM_OPEN);
        }
        else if ("license".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_LICENSE_OPEN);
            setClosedIcon(Icons.REPO_TYPE_LICENSE_CLOSED);
        }
        else if ("src.jar".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_SRC_JAR_OPEN);
            setClosedIcon(Icons.REPO_TYPE_SRC_JAR_CLOSED);
        }
        else if ("javadoc.jar".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_JAVADOC_JAR_OPEN);
            setClosedIcon(Icons.REPO_TYPE_JAVADOC_JAR_CLOSED);
        }
        else if ("ejb".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_EJB_OPEN);
            setClosedIcon(Icons.REPO_TYPE_EJB_CLOSED);
        }
        else if ("war".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_WAR_OPEN);
            setClosedIcon(Icons.REPO_TYPE_WAR_CLOSED);
        }
        else if ("ear".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_EAR_OPEN);
            setClosedIcon(Icons.REPO_TYPE_EAR_OPEN);
        }
        else if ("plugin".equals(type)) {
            setOpenIcon(Icons.REPO_TYPE_PLUGIN_OPEN);
            setClosedIcon(Icons.REPO_TYPE_PLUGIN_CLOSED);
        }
        else {
            setOpenIcon(Icons.REPO_FOLDER_OPEN);
            setClosedIcon(Icons.REPO_FOLDER_CLOSED);
        }
    }

    private String getTextForValue(final Object value) {
        if (value instanceof MessageNode)
            return ((MessageNode) value).getMessage();

        else if (value instanceof RepoTreeNode) {
            final RepoTreeNode node = (RepoTreeNode) value;
            final RepoPathElement pathElement = node.getPathElement();
            return getTextForPathElement(pathElement);
        }
        else
            return value == null ? "" : value.toString();
    }

    private String getTextForPathElement(final RepoPathElement pElt) {
        switch (pElt.getLevel()) {
            case RepoPathElement.LEVEL_ARTIFACT:
                return pElt.getArtifactId();
            case RepoPathElement.LEVEL_GROUP:
                return pElt.getGroupId();
            case RepoPathElement.LEVEL_ROOT:
                return pElt.getURI().toString();
            case RepoPathElement.LEVEL_TYPE:
                return pElt.getType();
            case RepoPathElement.LEVEL_VERSION:
                return pElt.getVersion();
            default:
                return pElt.getURI().toString();
        }
    }
}
