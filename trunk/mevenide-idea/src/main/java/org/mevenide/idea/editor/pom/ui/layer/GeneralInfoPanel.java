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

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.ui.ScrollPaneFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.lang.reflect.Field;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;
import org.mevenide.idea.util.ui.RelativeTextFieldWithBrowseButton;
import org.mevenide.idea.util.ui.text.XmlPsiDocumentBinder;

/**
 * @author Arik
 */
public class GeneralInfoPanel extends AbstractPomLayerPanel {
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
    private final TextFieldWithBrowseButton logoUrlField;

    private final JTextField shortDescField = new JTextField();
    private final JTextArea descField = new JTextArea();

    private final JTextField packageField = new JTextField();
    private final JTextField issueTrackingUrlField = new JTextField();

    private final JTextField orgNameField = new JTextField();
    private final JTextField orgUrlField = new JTextField();
    private final TextFieldWithBrowseButton orgLogoUrlField;

    public GeneralInfoPanel(final XmlFile pFile) {
        super(pFile);

        final VirtualFile virtualFile = file.getVirtualFile();
        final VirtualFile dir = virtualFile.getParent();

        extendField = new RelativeTextFieldWithBrowseButton(dir);
        logoUrlField = new RelativeTextFieldWithBrowseButton(dir);
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
                file.getProject(),
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());

        logoUrlField.addBrowseFolderListener(
                RES.get("choose.org.logo"),
                RES.get("choose.org.logo.desc"),
                file.getProject(),
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());

        orgLogoUrlField.addBrowseFolderListener(
                RES.get("choose.org.logo"),
                RES.get("choose.org.logo.desc"),
                file.getProject(),
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());

        inceptionYearField.setPreferredSize(new Dimension(60, 20));
        descField.setPreferredSize(new Dimension(300, 80));
        final Field[] fields = this.getClass().getDeclaredFields();
        for (final Field field : fields) {
            try {
                final Object value = field.get(this);
                if (value != null && value instanceof Component) {
                    final Component comp = (Component) value;
                    comp.setName(field.getName());
                }
            }
            catch (IllegalAccessException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private void layoutComponents() {
        final FormLayout layout = new FormLayout(
                "" +
                        "right:min:grow(0.05), 2dlu, fill:min:grow(0.45), 0dlu, " + //first column
                        "right:min:grow(0.05), 2dlu, fill:min:grow(0.45)",          //second column
                                                                                     "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
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
        builder.append(RES.get("project.desc"), ScrollPaneFactory.createScrollPane(descField), 5);

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
        synchronized (this) {
            final XmlPsiDocumentBinder binder = new XmlPsiDocumentBinder(file);

            binder.bind(extendField.getTextField(), "project/extend");
            binder.bind(nameField, "project/name");
            binder.bind(versionField, "project/currentVersion");
            binder.bind(artifactIdField, "project/artifactId");
            binder.bind(groupIdField, "project/groupId");
            binder.bind(inceptionYearField, "project/inceptionYear");
            binder.bind(urlField, "project/url");
            binder.bind(logoUrlField.getTextField(), "project/logo");
            binder.bind(shortDescField, "project/shortDescription");
            binder.bind(descField, "project/description");
            binder.bind(packageField, "project/package");
            binder.bind(issueTrackingUrlField, "project/issueTrackingUrl");
            binder.bind(orgNameField, "project/organization/name");
            binder.bind(orgUrlField, "project/organization/url");
            binder.bind(orgLogoUrlField.getTextField(), "project/organization/logo");
        }
    }
}
