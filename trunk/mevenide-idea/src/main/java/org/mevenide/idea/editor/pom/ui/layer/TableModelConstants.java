package org.mevenide.idea.editor.pom.ui.layer;

import org.mevenide.idea.util.ui.table.CRUDTablePanel;

/**
 * @author Arik
 */
public abstract class TableModelConstants {
    public static final CRUDTablePanel.CRUDXmlPsiDescriptor MAILING_LISTS = new CRUDTablePanel.SimpleCRUDXmlPsiDescriptor(
            "mailingLists",
            "mailingList",
            new String[]{
                "Name",
                "Subscribe",
                "Unsubscribe",
                "Archive"
            }, new String[]{
                "name",
                "subscribe",
                "unsubscribe",
                "archive"
            }
    );

    public static final CRUDTablePanel.CRUDXmlPsiDescriptor BRANCHES = new CRUDTablePanel.SimpleCRUDXmlPsiDescriptor(
            "branches",
            "branch",
            new String[]{
                "Tag"
            }, new String[]{
                "tag"
            }
    );

    public static final CRUDTablePanel.CRUDXmlPsiDescriptor DEPENDENCIES = new CRUDTablePanel.SimpleCRUDXmlPsiDescriptor(
            "dependencies",
            "dependency",
            new String[]{
                "Group ID",
                "Artifact ID",
                "Version",
                "Type"
            }, new String[]{
                "groupId",
                "artifactId",
                "version",
                "type"
            }
    );

    public static final CRUDTablePanel.CRUDXmlPsiDescriptor DEVELOPERS = new CRUDTablePanel.SimpleCRUDXmlPsiDescriptor(
            "developers",
            "developer",
            new String[]{
                "Name",
                "ID",
                "E-Mail",
                "Organization",
                "URL",
                "Timezone"
            }, new String[]{
                "name",
                "id",
                "email",
                "organization",
                "url",
                "timezone"
            }
    );

    public static final CRUDTablePanel.CRUDXmlPsiDescriptor CONTRIBUTORS = new CRUDTablePanel.SimpleCRUDXmlPsiDescriptor(
            "contributors",
            "contributor",
            new String[]{
                "Name",
                "ID",
                "E-Mail",
                "Organization",
                "URL",
                "Timezone"
            }, new String[]{
                "name",
                "id",
                "email",
                "organization",
                "url",
                "timezone"
            }
    );

    public static final CRUDTablePanel.CRUDXmlPsiDescriptor VERSIONS = new CRUDTablePanel.SimpleCRUDXmlPsiDescriptor(
            "versions",
            "version",
            new String[]{
                "ID",
                "Name",
                "Tag"
            }, new String[]{
                "id",
                "name",
                "tag"
            }
    );
}
