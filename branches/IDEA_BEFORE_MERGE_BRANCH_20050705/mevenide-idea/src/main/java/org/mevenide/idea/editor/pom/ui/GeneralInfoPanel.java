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
package org.mevenide.idea.editor.pom.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ScrollPaneFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.*;
import javax.swing.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;
import org.mevenide.idea.util.ui.RelativeTextFieldWithBrowseButton;

/**
 * @author Arik
 */
public class GeneralInfoPanel extends AbstractPomLayerPanel {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(GeneralInfoPanel.class);

    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(GeneralInfoPanel.class);

    private final JComboBox pomVersionField = new JComboBox(new String[]{"3"});
    private final TextFieldWithBrowseButton extendField;

    private final JTextField nameField = new JTextField();
    private final JTextField versionField = new JTextField();
    private final JTextField artifactIdField = new JTextField();
    private final JTextField groupIdField = new JTextField();
    private final JTextField inceptionYearField = new JTextField();
    private final JTextField urlField = new JTextField();
    private final TextFieldWithBrowseButton logoUriField;

    private final JTextField shortDescField = new JTextField();
    private final JTextArea descField = new JTextArea();

    private final JTextField packageField = new JTextField();
    private final JTextField issueTrackingUrlField = new JTextField();

    private final JTextField orgNameField = new JTextField();
    private final JTextField orgUrlField = new JTextField();
    private final TextFieldWithBrowseButton orgLogoUrlField;

    protected final PsiProject project;
    protected final BeanAdapter projectModel;
    protected final BeanAdapter orgModel;

    public GeneralInfoPanel(final PsiProject psiProject) {
        project = psiProject;
        projectModel = new BeanAdapter(project, true);
        orgModel = new BeanAdapter(project.getOrganization(), true);

        final VirtualFile virtualFile = project.getXmlFile().getVirtualFile();
        if (virtualFile == null)
            throw new IllegalStateException("PSI file has no virtual project.");
        final VirtualFile dir = virtualFile.getParent();

        extendField = new RelativeTextFieldWithBrowseButton(dir);
        logoUriField = new RelativeTextFieldWithBrowseButton(dir);
        orgLogoUrlField = new RelativeTextFieldWithBrowseButton(dir);

        initComponents();
        layoutComponents();
        bindComponents();
    }

    private void initComponents() {
        pomVersionField.setEnabled(false);

        extendField.addBrowseFolderListener(
                RES.get("choose.pom.parent"),
                RES.get("choose.pom.parent.desc"),
                project.getXmlFile().getProject(),
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());

        logoUriField.addBrowseFolderListener(
                RES.get("choose.org.logo"),
                RES.get("choose.org.logo.desc"),
                project.getXmlFile().getProject(),
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());

        orgLogoUrlField.addBrowseFolderListener(
                RES.get("choose.org.logo"),
                RES.get("choose.org.logo.desc"),
                project.getXmlFile().getProject(),
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());

        inceptionYearField.setPreferredSize(new Dimension(60, 20));
        descField.setPreferredSize(new Dimension(300, 80));
    }

    private void layoutComponents() {
        final FormLayout layout = new FormLayout(
                "" +
                        "right:min:grow(0.05), 2dlu, fill:min:grow(0.45), 0dlu, " + //first column
                        "right:min:grow(0.05), 2dlu, fill:min:grow(0.45)",
                //second column
                "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
        builder.setComponentFactory(CustomFormsComponentFactory.getInstance());

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
        builder.append(RES.get("project.logo.url"), logoUriField);
        builder.append(RES.get("project.inception.year"), inceptionYearField);
        builder.append(RES.get("project.package"), packageField);

        //
        //project descriptions
        //
        builder.appendSeparator(RES.get("project.desc.title"));
        builder.append(RES.get("project.short.desc"), shortDescField, 5);
        builder.append(RES.get("project.desc"),
                       ScrollPaneFactory.createScrollPane(descField),
                       5);

        //
        //project management
        //
        builder.appendSeparator(RES.get("project.mgmt.title"));
        builder.append(RES.get("project.issues.url"), issueTrackingUrlField, 5);

        //
        //organization
        //
        builder.appendSeparator(RES.get("project.org.title"));
        builder.append(RES.get("project.org.name"), orgNameField);
        builder.append(RES.get("project.org.url"), orgUrlField);
        builder.append(RES.get("project.org.logo"), orgLogoUrlField);
    }

    private void bindComponents() {
        Bindings.bind(extendField.getTextField(), projectModel.getValueModel("extend"));
        Bindings.bind(nameField, projectModel.getValueModel("name"));
        Bindings.bind(versionField, projectModel.getValueModel("currentVersion"));
        Bindings.bind(artifactIdField, projectModel.getValueModel("artifactId"));
        Bindings.bind(groupIdField, projectModel.getValueModel("groupId"));
        Bindings.bind(inceptionYearField, projectModel.getValueModel("inceptionYear"));
        Bindings.bind(urlField, projectModel.getValueModel("url"));
        Bindings.bind(logoUriField.getTextField(), projectModel.getValueModel("logoUri"));
        Bindings.bind(shortDescField, projectModel.getValueModel("shortDescription"));
        Bindings.bind(descField, projectModel.getValueModel("description"));
        Bindings.bind(packageField, projectModel.getValueModel("packageName"));
        Bindings.bind(issueTrackingUrlField,
                      projectModel.getValueModel("issueTrackingUrl"));
        Bindings.bind(orgNameField, orgModel.getValueModel("name"));
        Bindings.bind(orgUrlField, orgModel.getValueModel("url"));
        Bindings.bind(orgLogoUrlField.getTextField(), orgModel.getValueModel("logoUri"));
    }
}
