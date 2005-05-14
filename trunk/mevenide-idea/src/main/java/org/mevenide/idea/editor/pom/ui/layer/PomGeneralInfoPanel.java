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
package org.mevenide.idea.editor.pom.ui.layer;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;
import org.mevenide.idea.util.ui.text.JTextComponentJDomBinder;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.Dimension;

/**
 * @author Arik
 */
public class PomGeneralInfoPanel extends AbstractPomLayerPanel {
    private static final Res RES = Res.getInstance(PomGeneralInfoPanel.class);

    private final JComboBox pomVersionField = new JComboBox(new String[]{"3"});
    private final TextFieldWithBrowseButton extendField = new TextFieldWithBrowseButton();

    private final JTextField nameField = new JTextField();
    private final JTextField versionField = new JTextField();
    private final JTextField artifactIdField = new JTextField();
    private final JTextField groupIdField = new JTextField();
    private final JTextField inceptionYearField = new JTextField();
    private final JTextField urlField = new JTextField();
    private final JTextField logoUrlField = new JTextField();

    private final JTextField shortDescField = new JTextField();
    private final JTextArea descField = new JTextArea();

    private final JTextField packageField = new JTextField();
    private final JTextField issueTrackingUrlField = new JTextField();

    public PomGeneralInfoPanel(final org.jdom.Document pProjectDoc,
                               final com.intellij.openapi.project.Project pProject,
                               final Document pPomDocument) {
        super(pProjectDoc, pProject, pPomDocument);

        initComponents();
        layoutComponents();
        initBindings();
    }

    private void initComponents() {
        pomVersionField.setEnabled(false);

        final FileChooserDescriptor chooserDescriptor =
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor();
        extendField.addBrowseFolderListener(RES.get("choose.pom.parent"),
                                            RES.get("choose.pom.parent.desc"),
                                            project,
                                            chooserDescriptor);

        inceptionYearField.setPreferredSize(new Dimension(60, 20));
        descField.setPreferredSize(new Dimension(300, 80));
    }

    private void layoutComponents() {
        final FormLayout layout = new FormLayout(
                "" +
                    "right:min:grow(0.05), 2dlu, fill:pref:grow(0.45), 0dlu, " + //first column
                    "right:min:grow(0.05), 2dlu, fill:pref:grow(0.45)",          //second column
                "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, RES.getBundle(), this);
        builder.setDefaultDialogBorder();
        builder.setComponentFactory(new CustomFormsComponentFactory());

        //
        //maven pom info
        //
        builder.appendSeparator(RES.get("maven.pom.title"));
        builder.append(RES.get("pom.version.label"), pomVersionField);
        builder.nextLine();
        builder.append(RES.get("pom.extend.label"), extendField, 5);

        //
        //project general info
        //
        builder.appendSeparator(RES.get("project.info.title"));
        builder.append(RES.get("project.name"), nameField);
        builder.append(RES.get("project.version"), versionField);
        builder.append(RES.get("project.artifact.id"), artifactIdField);
        builder.append(RES.get("project.group.id"), groupIdField);
        builder.append(RES.get("project.url"), urlField);
        builder.append(RES.get("project.logo.url"), logoUrlField);
        builder.append(RES.get("project.inception.year"), inceptionYearField);
        builder.append(RES.get("project.package"), packageField);

        //
        //project descriptions
        //
        builder.appendSeparator(RES.get("project.desc.title"));
        builder.append(RES.get("project.short.desc"), shortDescField, 5);
        builder.append(RES.get("project.desc"), new JScrollPane(descField), 5);

        //
        //project management
        //
        builder.appendSeparator(RES.get("project.mgmt.title"));
        builder.append(RES.get("project.issues.url"), issueTrackingUrlField, 5);
    }

    private void initBindings() {
        synchronized (this) {
            JTextComponentJDomBinder.bind(extendField.getTextField(), projectElt, "extend");
            JTextComponentJDomBinder.bind(nameField, projectElt, "name");
            JTextComponentJDomBinder.bind(versionField, projectElt, "currentVersion");
            JTextComponentJDomBinder.bind(artifactIdField, projectElt, "artifactId");
            JTextComponentJDomBinder.bind(groupIdField, projectElt, "groupId");
            JTextComponentJDomBinder.bind(inceptionYearField, projectElt, "inceptionYear");
            JTextComponentJDomBinder.bind(urlField, projectElt, "url");
            JTextComponentJDomBinder.bind(logoUrlField, projectElt, "logo");
            JTextComponentJDomBinder.bind(shortDescField, projectElt, "shortDescription");
            JTextComponentJDomBinder.bind(descField, projectElt, "description");
            JTextComponentJDomBinder.bind(packageField, projectElt, "package");
            JTextComponentJDomBinder.bind(issueTrackingUrlField, projectElt, "issueTrackingUrl");
        }
    }

    public boolean isModified() {
        return
                JTextComponentJDomBinder.isDirty(extendField.getTextField()) ||
                JTextComponentJDomBinder.isDirty(nameField) ||
                JTextComponentJDomBinder.isDirty(versionField) ||
                JTextComponentJDomBinder.isDirty(artifactIdField) ||
                JTextComponentJDomBinder.isDirty(groupIdField) ||
                JTextComponentJDomBinder.isDirty(inceptionYearField) ||
                JTextComponentJDomBinder.isDirty(urlField) ||
                JTextComponentJDomBinder.isDirty(logoUrlField) ||
                JTextComponentJDomBinder.isDirty(shortDescField) ||
                JTextComponentJDomBinder.isDirty(descField) ||
                JTextComponentJDomBinder.isDirty(packageField) ||
                JTextComponentJDomBinder.isDirty(issueTrackingUrlField);

    }
}
