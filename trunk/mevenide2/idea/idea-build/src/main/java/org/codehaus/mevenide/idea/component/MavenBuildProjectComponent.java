/*
   @(#)MavenBuildProjectComponent.java   2006-10-24
 

                                 Apache License
                           Version 2.0, January 2004
                        http://www.apache.org/licenses/

   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION

   1. Definitions.

      "License" shall mean the terms and conditions for use, reproduction,
      and distribution as defined by Sections 1 through 9 of this document.

      "Licensor" shall mean the copyright owner or entity authorized by
      the copyright owner that is granting the License.

      "Legal Entity" shall mean the union of the acting entity and all
      other entities that control, are controlled by, or are under common
      control with that entity. For the purposes of this definition,
      "control" means (i) the power, direct or indirect, to cause the
      direction or management of such entity, whether by contract or
      otherwise, or (ii) ownership of fifty percent (50%) or more of the
      outstanding shares, or (iii) beneficial ownership of such entity.

      "You" (or "Your") shall mean an individual or Legal Entity
      exercising permissions granted by this License.

      "Source" form shall mean the preferred form for making modifications,
      including but not limited to software source code, documentation
      source, and configuration files.

      "Object" form shall mean any form resulting from mechanical
      transformation or translation of a Source form, including but
      not limited to compiled object code, generated documentation,
      and conversions to other media types.

      "Work" shall mean the work of authorship, whether in Source or
      Object form, made available under the License, as indicated by a
      copyright notice that is included in or attached to the work
      (an example is provided in the Appendix below).

      "Derivative Works" shall mean any work, whether in Source or Object
      form, that is based on (or derived from) the Work and for which the
      editorial revisions, annotations, elaborations, or other modifications
      represent, as a whole, an original work of authorship. For the purposes
      of this License, Derivative Works shall not include works that remain
      separable from, or merely link (or bind by name) to the interfaces of,
      the Work and Derivative Works thereof.

      "Contribution" shall mean any work of authorship, including
      the original version of the Work and any modifications or additions
      to that Work or Derivative Works thereof, that is intentionally
      submitted to Licensor for inclusion in the Work by the copyright owner
      or by an individual or Legal Entity authorized to submit on behalf of
      the copyright owner. For the purposes of this definition, "submitted"
      means any form of electronic, verbal, or written communication sent
      to the Licensor or its representatives, including but not limited to
      communication on electronic mailing lists, source code control systems,
      and issue tracking systems that are managed by, or on behalf of, the
      Licensor for the purpose of discussing and improving the Work, but
      excluding communication that is conspicuously marked or otherwise
      designated in writing by the copyright owner as "Not a Contribution."

      "Contributor" shall mean Licensor and any individual or Legal Entity
      on behalf of whom a Contribution has been received by Licensor and
      subsequently incorporated within the Work.

   2. Grant of Copyright License. Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      copyright license to reproduce, prepare Derivative Works of,
      publicly display, publicly perform, sublicense, and distribute the
      Work and such Derivative Works in Source or Object form.

   3. Grant of Patent License. Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      (except as stated in this section) patent license to make, have made,
      use, offer to sell, sell, import, and otherwise transfer the Work,
      where such license applies only to those patent claims licensable
      by such Contributor that are necessarily infringed by their
      Contribution(s) alone or by combination of their Contribution(s)
      with the Work to which such Contribution(s) was submitted. If You
      institute patent litigation against any entity (including a
      cross-claim or counterclaim in a lawsuit) alleging that the Work
      or a Contribution incorporated within the Work constitutes direct
      or contributory patent infringement, then any patent licenses
      granted to You under this License for that Work shall terminate
      as of the date such litigation is filed.

   4. Redistribution. You may reproduce and distribute copies of the
      Work or Derivative Works thereof in any medium, with or without
      modifications, and in Source or Object form, provided that You
      meet the following conditions:

      (a) You must give any other recipients of the Work or
          Derivative Works a copy of this License; and

      (b) You must cause any modified files to carry prominent notices
          stating that You changed the files; and

      (c) You must retain, in the Source form of any Derivative Works
          that You distribute, all copyright, patent, trademark, and
          attribution notices from the Source form of the Work,
          excluding those notices that do not pertain to any part of
          the Derivative Works; and

      (d) If the Work includes a "NOTICE" text file as part of its
          distribution, then any Derivative Works that You distribute must
          include a readable copy of the attribution notices contained
          within such NOTICE file, excluding those notices that do not
          pertain to any part of the Derivative Works, in at least one
          of the following places: within a NOTICE text file distributed
          as part of the Derivative Works; within the Source form or
          documentation, if provided along with the Derivative Works; or,
          within a display generated by the Derivative Works, if and
          wherever such third-party notices normally appear. The contents
          of the NOTICE file are for informational purposes only and
          do not modify the License. You may add Your own attribution
          notices within Derivative Works that You distribute, alongside
          or as an addendum to the NOTICE text from the Work, provided
          that such additional attribution notices cannot be construed
          as modifying the License.

      You may add Your own copyright statement to Your modifications and
      may provide additional or different license terms and conditions
      for use, reproduction, or distribution of Your modifications, or
      for any such Derivative Works as a whole, provided Your use,
      reproduction, and distribution of the Work otherwise complies with
      the conditions stated in this License.

   5. Submission of Contributions. Unless You explicitly state otherwise,
      any Contribution intentionally submitted for inclusion in the Work
      by You to the Licensor shall be under the terms and conditions of
      this License, without any additional terms or conditions.
      Notwithstanding the above, nothing herein shall supersede or modify
      the terms of any separate license agreement you may have executed
      with Licensor regarding such Contributions.

   6. Trademarks. This License does not grant permission to use the trade
      names, trademarks, service marks, or product names of the Licensor,
      except as required for reasonable and customary use in describing the
      origin of the Work and reproducing the content of the NOTICE file.

   7. Disclaimer of Warranty. Unless required by applicable law or
      agreed to in writing, Licensor provides the Work (and each
      Contributor provides its Contributions) on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
      implied, including, without limitation, any warranties or conditions
      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A
      PARTICULAR PURPOSE. You are solely responsible for determining the
      appropriateness of using or redistributing the Work and assume any
      risks associated with Your exercise of permissions under this License.

   8. Limitation of Liability. In no event and under no legal theory,
      whether in tort (including negligence), contract, or otherwise,
      unless required by applicable law (such as deliberate and grossly
      negligent acts) or agreed to in writing, shall any Contributor be
      liable to You for damages, including any direct, indirect, special,
      incidental, or consequential damages of any character arising as a
      result of this License or out of the use or inability to use the
      Work (including but not limited to damages for loss of goodwill,
      work stoppage, computer failure or malfunction, or any and all
      other commercial damages or losses), even if such Contributor
      has been advised of the possibility of such damages.

   9. Accepting Warranty or Additional Liability. While redistributing
      the Work or Derivative Works thereof, You may choose to offer,
      and charge a fee for, acceptance of support, warranty, indemnity,
      or other liability obligations and/or rights consistent with this
      License. However, in accepting such obligations, You may act only
      on Your own behalf and on Your sole responsibility, not on behalf
      of any other Contributor, and only if You agree to indemnify,
      defend, and hold each Contributor harmless for any liability
      incurred by, or claims asserted against, such Contributor by reason
      of your accepting any such warranty or additional liability.

   END OF TERMS AND CONDITIONS

   Copyright 2006 Ralf Quebbemann

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */


package org.codehaus.mevenide.idea.component;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.uiDesigner.core.GridConstraints;
import org.codehaus.mevenide.idea.action.ActionUtils;
import org.codehaus.mevenide.idea.action.AddPluginAction;
import org.codehaus.mevenide.idea.action.AddPomAction;
import org.codehaus.mevenide.idea.action.FilterAction;
import org.codehaus.mevenide.idea.action.PluginConfigurationActionListener;
import org.codehaus.mevenide.idea.action.PomTreeMouseActionListener;
import org.codehaus.mevenide.idea.action.RemovePluginAction;
import org.codehaus.mevenide.idea.action.RemovePomAction;
import org.codehaus.mevenide.idea.action.RunGoalsAction;
import org.codehaus.mevenide.idea.action.ShowMavenOptionsAction;
import org.codehaus.mevenide.idea.action.SortAction;
import org.codehaus.mevenide.idea.action.ToolWindowKeyListener;
import org.codehaus.mevenide.idea.build.util.BuildConstants;
import org.codehaus.mevenide.idea.common.MavenBuildPluginSettings;
import org.codehaus.mevenide.idea.common.MavenBuildProjectPluginSettings;
import org.codehaus.mevenide.idea.common.util.ErrorHandler;
import org.codehaus.mevenide.idea.gui.PomTree;
import org.codehaus.mevenide.idea.gui.form.MavenBuildProjectToolWindowForm;
import org.codehaus.mevenide.idea.gui.form.MavenProjectConfigurationForm;
import org.codehaus.mevenide.idea.helper.ActionContext;
import org.codehaus.mevenide.idea.model.MavenPluginDocument;
import org.codehaus.mevenide.idea.model.MavenProjectDocument;
import org.codehaus.mevenide.idea.model.MavenProjectDocumentImpl;
import org.codehaus.mevenide.idea.util.GuiUtils;
import org.codehaus.mevenide.idea.util.IdeaMavenPluginException;
import org.codehaus.mevenide.idea.util.PluginConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.maven.settings.x100.SettingsDocument;
import org.apache.xmlbeans.XmlOptions;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class MavenBuildProjectComponent extends AbstractComponent
    implements ProjectComponent, Configurable, JDOMExternalizable {

  private MavenBuildApplicationComponent applicationComponent;
  private static final Logger LOG = Logger.getLogger(MavenBuildProjectComponent.class);
  private ActionContext actionContext = new ActionContext();
  private Set<MavenProjectDocument> mavenPomList = new LinkedHashSet<MavenProjectDocument>();

  /**
   * Constructs ...
   *
   * @param project Document me!
   */
  public MavenBuildProjectComponent(Project project) {
    actionContext.setPluginProject(project);
  }

  private MavenBuildProjectToolWindowForm createMavenToolWindowForm() {
    MavenBuildProjectToolWindowForm toolWindowForm =
        new MavenBuildProjectToolWindowForm();
    DefaultActionGroup group = new DefaultActionGroup();
    AnAction actionAddPom = new AddPomAction(actionContext, PluginConstants.ACTION_COMMAND_ADD_POM,
        "Adds a POM to the project",
        IconLoader.getIcon(PluginConstants.ICON_ADD_POM));
    AnAction actionRemovePom = new RemovePomAction(actionContext,
        PluginConstants.ACTION_COMMAND_REMOVE_POM,
        "Removes a POM from the project",
        IconLoader.getIcon(PluginConstants.ICON_REMOVE_POM));
    AnAction actionAddPlugin = new AddPluginAction(actionContext,
        PluginConstants.ACTION_COMMAND_ADD_PLUGIN,
        PluginConstants.ACTION_COMMAND_ADD_PLUGIN,
        IconLoader.getIcon(PluginConstants.ICON_ADD_PLUGIN));
    AnAction actionRemovePlugin = new RemovePluginAction(actionContext,
        PluginConstants.ACTION_COMMAND_REMOVE_PLUGIN,
        PluginConstants.ACTION_COMMAND_REMOVE_PLUGIN,
        IconLoader.getIcon(PluginConstants.ICON_REMOVE_PLUGIN));
    AnAction actionRunGoals =
        new RunGoalsAction(actionContext, PluginConstants.ACTION_COMMAND_RUN_GOALS,
            PluginConstants.ACTION_COMMAND_RUN_GOALS,
            IconLoader.getIcon(PluginConstants.ICON_RUN));
    AnAction actionSortAsc = new SortAction(actionContext, PluginConstants.ACTION_COMMAND_SORT_ASC,
        PluginConstants.ACTION_COMMAND_SORT_ASC,
        IconLoader.getIcon(PluginConstants.ICON_SORT_ASC));
    AnAction showMavenOptions =
        new ShowMavenOptionsAction(actionContext, PluginConstants.ACTION_COMMAND_SHOW_MAVEN_OPTIONS,
            PluginConstants.ACTION_COMMAND_SHOW_MAVEN_OPTIONS,
            IconLoader.getIcon(PluginConstants.ICON_SHOW_MAVEN_OPTIONS));
    AnAction filter =
        new FilterAction(actionContext, PluginConstants.ACTION_COMMAND_FILTER,
            PluginConstants.ACTION_COMMAND_FILTER,
            actionContext.getProjectPluginSettings().isUseFilter() ?
                IconLoader.getIcon(PluginConstants.ICON_FILTER_APPLIED) :
                IconLoader.getIcon(PluginConstants.ICON_FILTER));

    group.add(actionAddPom);
    group.add(actionRemovePom);
    group.addSeparator();
    group.add(actionAddPlugin);
    group.add(actionRemovePlugin);
    group.addSeparator();
    group.add(actionRunGoals);
    group.addSeparator();
    group.add(actionSortAsc);
    group.add(showMavenOptions);
    group.add(filter);
    group.addSeparator();
    ActionToolbar actionToolbar =
        ActionManager.getInstance().createActionToolbar("Maven Toolbar", group, true);
    actionToolbar.getComponent().setEnabled(false);
    toolWindowForm.getRootComponent().add(
        actionToolbar.getComponent(),
        new GridConstraints(
            0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null));
    ToolWindowKeyListener keyListener = new ToolWindowKeyListener(actionContext);
    toolWindowForm.getTextFieldCmdLine().addKeyListener(keyListener);
    return toolWindowForm;
  }

  /**
   * Method description
   *
   * @throws ConfigurationException
   */
  public void apply() throws ConfigurationException {
    MavenProjectConfigurationForm form =
        (MavenProjectConfigurationForm) actionContext.getGuiContext().getProjectConfigurationForm();
    MavenBuildProjectPluginSettings pluginSettings = actionContext.getProjectPluginSettings();
    if (form != null) {
      form.getData(pluginSettings);
    }
  }

  /**
   * Method description
   *
   * @return Document me!
   */
  public JComponent createComponent() {
    return actionContext.getGuiContext().getProjectConfigurationForm().getRootComponent();
  }

  private void createPomTree()
      throws org.apache.xmlbeans.XmlException, java.io.IOException, IdeaMavenPluginException {
    for (MavenProjectDocument mavenProjectDocument : mavenPomList) {
      ActionUtils.addSinglePomToTree(actionContext, mavenProjectDocument);
      // Register a document listener that listens for changes      
    }
  }

  /**
   * Method description
   */
  public void disposeComponent() {

    // empty
  }

  /**
   * Method description
   */
  public void disposeUIResources() {
//    actionContext.getGuiContext().setProjectConfigurationForm(null);
  }

  /**
   * Method description
   */
  public void initComponent() {
    MavenBuildPluginSettings pluginSettings;
    Application application = ApplicationManager.getApplication();

    applicationComponent =
        application.getComponent(MavenBuildApplicationComponent.class);
    if (applicationComponent != null) {
      pluginSettings =
          applicationComponent.getActionContext().getApplicationPluginSettings();
      actionContext.setApplicationPluginSettings(pluginSettings);
      LOG.debug("Scan for existing POMs is: " + pluginSettings.isScanForExistingPoms());
    }

    actionContext.getGuiContext()
        .setMavenToolWindowForm(createMavenToolWindowForm());

    MavenProjectConfigurationForm form =
        (MavenProjectConfigurationForm) actionContext.getGuiContext().getProjectConfigurationForm();
    if (form == null) {
      form = new MavenProjectConfigurationForm(actionContext);
      PluginConfigurationActionListener actionListener =
          new PluginConfigurationActionListener(form);
      form.getButtonMavenHomeDir().addActionListener(actionListener);
      form.getButtonAlternativeSettingsFile().addActionListener(actionListener);

      actionContext.getGuiContext().setProjectConfigurationForm(form);
    }
  }

  private void initToolWindow(JTree tree) {
    JComponent pomContentPanel;
    ToolWindowManager toolWindowManager =
        ToolWindowManager.getInstance(actionContext.getPluginProject());

    MavenBuildProjectPluginSettings pluginSettings = actionContext.getProjectPluginSettings();
    MavenBuildProjectToolWindowForm form =
        (MavenBuildProjectToolWindowForm) actionContext.getGuiContext().getMavenToolWindowForm();
    form.getScrollpane().setViewportView(tree);
    form.getTextFieldCmdLine()
        .setText(pluginSettings.getMavenCommandLineParams());
    pomContentPanel = form.getRootComponent();

    // createToolbar(mavenToolWindowForm);
    ToolWindow pomToolWindow =
        toolWindowManager.registerToolWindow(PluginConstants.BUILD_TOOL_WINDOW_ID,
            pomContentPanel, ToolWindowAnchor.RIGHT);

    pomToolWindow.setIcon(GuiUtils.createImageIcon(PluginConstants.ICON_APPLICATION_EMBLEM_SMALL));
  }

  /**
   * Method description
   */
  public void projectClosed() {
    unregisterToolWindow();
  }

  /**
   * Method description
   */
  public void projectOpened() {
    PomTree pomTree;
    MavenBuildPluginSettings pluginSettings = null;

    ProjectRootManager projectRootManager =
        ProjectRootManager.getInstance(actionContext.getPluginProject());

    // should be non null, because it was queried in initComponent()
    if (applicationComponent != null) {
      pluginSettings = actionContext.getApplicationPluginSettings();
    }

    Icon goalIcon = GuiUtils.createImageIcon(PluginConstants.ICON_APPLICATION_SMALL);
    Icon pomIcon = IconLoader.getIcon(PluginConstants.ICON_POM_SMALL);

    pomTree = new PomTree(PluginConstants.TREE_ROOT_NODE_TITLE, pomIcon,
        goalIcon);
    pomTree.addMouseListener(new PomTreeMouseActionListener(actionContext));

    if ((pluginSettings != null) && pluginSettings.isScanForExistingPoms()) {
      mavenPomList = getPomFilesOfProject(projectRootManager);
    }

    pomTree.setRootVisible(true);
    initToolWindow(pomTree);
    try {
      createPomTree();
    } catch (Exception e) {
      ErrorHandler.processAndShowError(actionContext.getPluginProject(), e);
    }
  }

  /**
   * Method description
   *
   * @param element Document me!
   *
   * @throws InvalidDataException
   */
  public void readExternal(Element element) throws InvalidDataException {
    MavenBuildProjectPluginSettings pluginSettings = actionContext.getProjectPluginSettings();

    pluginSettings.setMavenHome(
        JDOMExternalizerUtil.readField(element, PluginConstants.CONFIG_ELEMENT_MAVEN_EXECUTABLE));
    pluginSettings.setMavenCommandLineParams(JDOMExternalizerUtil.readField(element,
        PluginConstants.CONFIG_ELEMENT_MAVEN_COMMAND_LINE));
    pluginSettings.setVmOptions(JDOMExternalizerUtil.readField(element,
        PluginConstants.CONFIG_ELEMENT_VM_OPTIONS));
    pluginSettings.setUseMavenEmbedder(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
        PluginConstants.CONFIG_ELEMENT_USE_MAVEN_EMBEDDER)));
    pluginSettings.setUseFilter(Boolean.valueOf(JDOMExternalizerUtil.readField(element,
        PluginConstants.CONFIG_ELEMENT_USE_FILTER)));
    super.readExternal(actionContext.getProjectPluginSettings(), element);

    String mavenHomeDir = System.getProperty("user.home") + System.getProperty("file.separator")
        + ".m2";
    LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
    File settingsFile = new File(mavenHomeDir + System.getProperty("file.separator")
        + "settings.xml");

    if (settingsFile.exists()) {
      try {
        XmlOptions xmlOptions = new XmlOptions();
        Map<String, String> xmlOptionsMap = new Hashtable<String, String>();

        xmlOptionsMap.put("", "http://maven.apache.org/Settings/1.0.0");
        xmlOptions.setLoadSubstituteNamespaces(xmlOptionsMap);
        SettingsDocument settingsDocument = SettingsDocument.Factory.parse(new File(mavenHomeDir
            + System.getProperty("file.separator")
            + "settings.xml"), xmlOptions);

        if (settingsDocument != null) {
          if (!StringUtils.isEmpty(settingsDocument.getSettings().getLocalRepository())) {
            pluginSettings.setMavenRepository(settingsDocument.getSettings().getLocalRepository());
          }
        }
      } catch (Exception e) {
        LOG.error(e);

//        throw new InvalidDataException(e.getCause());
      }
    } else {
      pluginSettings.setMavenRepository(mavenHomeDir + System.getProperty("file.separator")
          + "repository");
    }

    LOG.debug("Location of Maven Repository is: " + pluginSettings.getMavenRepository());

    Element pomListElement = element.getChild("pom-list");

    if (pomListElement != null) {
      List myPomListChildren = pomListElement.getChildren("pom");

      for (Object aPomListChildren : myPomListChildren) {
        Element childElement = (Element) aPomListChildren;

        if (childElement != null) {
          Element pomOptionElement = childElement.getChild("option");
          String pomPath = pomOptionElement.getAttributeValue("value");
          VirtualFile virtualFile = localFileSystem.findFileByPath(pomPath);

          if (virtualFile != null) {
            MavenProjectDocument mavenProjectDocument = new MavenProjectDocumentImpl(virtualFile);

            mavenPomList.add(mavenProjectDocument);
            LOG.debug("Adding POM: " + virtualFile.getPath());

            Element pluginListChildren = childElement.getChild("plugin-list");

            if (pluginListChildren != null) {
              List myPluginListChildren = pluginListChildren.getChildren("option");

              for (Object pluginListElement : myPluginListChildren) {
                Element pluginChildElement = (Element) pluginListElement;
                String pluginPath = pluginChildElement.getAttributeValue("value");
                VirtualFile pluginJarArchive = localFileSystem.findFileByPath(pluginPath);

                try {
                  MavenPluginDocument mavenPluginDocument =
                      ActionUtils.createMavenPluginDocument(pluginJarArchive, true);
                  if (mavenPluginDocument != null) {
                    mavenProjectDocument.getPluginDocumentList().add(mavenPluginDocument);
                  }
                } catch (Exception e) {
                  LOG.error(e);

                  throw new InvalidDataException(e.getCause());
                }

                LOG.debug("Adding Plugin: " + pluginPath);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Method description
   */
  public void reset() {
    MavenProjectConfigurationForm form =
        (MavenProjectConfigurationForm) actionContext.getGuiContext().getProjectConfigurationForm();
    MavenBuildProjectPluginSettings pluginSettings = actionContext.getProjectPluginSettings();

    if (form != null) {
      form.setData(pluginSettings);
    }
  }

  private void unregisterToolWindow() {
    ToolWindowManager toolWindowManager =
        ToolWindowManager.getInstance(actionContext.getPluginProject());

    toolWindowManager.unregisterToolWindow(PluginConstants.BUILD_TOOL_WINDOW_ID);
  }

  /**
   * Method description
   *
   * @param element Document me!
   *
   * @throws WriteExternalException
   */
  public void writeExternal(Element element) throws WriteExternalException {
    MavenBuildProjectPluginSettings pluginSettings = actionContext.getProjectPluginSettings();

    JDOMExternalizerUtil.writeField(element, PluginConstants.CONFIG_ELEMENT_MAVEN_EXECUTABLE,
        pluginSettings.getMavenHome());
    JDOMExternalizerUtil.writeField(element, BuildConstants.MAVEN_OPTION_SETTINGS_FILE,
        pluginSettings.getMavenSettingsFile());
    JDOMExternalizerUtil.writeField(element, PluginConstants.CONFIG_ELEMENT_MAVEN_COMMAND_LINE,
        pluginSettings.getMavenCommandLineParams());
    JDOMExternalizerUtil.writeField(element, PluginConstants.CONFIG_ELEMENT_VM_OPTIONS,
        pluginSettings.getVmOptions());
    JDOMExternalizerUtil.writeField(element, PluginConstants.CONFIG_ELEMENT_USE_MAVEN_EMBEDDER,
        Boolean.toString(pluginSettings.isUseMavenEmbedder()));
    JDOMExternalizerUtil.writeField(element, PluginConstants.CONFIG_ELEMENT_USE_FILTER,
        Boolean.toString(pluginSettings.isUseFilter()));
    super.writeExternal(pluginSettings, element);

    Element pomListElement = new Element("pom-list");

    element.addContent(pomListElement);
    List<MavenProjectDocument> pomDocumentList = actionContext.getPomDocumentList();
    for (MavenProjectDocument pomFile : pomDocumentList) {
      Element pomElement = new Element("pom");

      pomListElement.addContent(pomElement);
      JDOMExternalizerUtil.writeField(pomElement, "path", pomFile.getPomFile().getPath());

      Element pluginListElement = new Element("plugin-list");

      pomElement.addContent(pluginListElement);

      List<MavenPluginDocument> pluginDocList = pomFile.getPluginDocumentList();

      for (MavenPluginDocument pluginDoc : pluginDocList) {

        // Store only those plugins, which were manually added by the user
        if (!pluginDoc.isMemberOfPom()) {
          JDOMExternalizerUtil.writeField(pluginListElement, "path", pluginDoc.getPluginPath());
        }
      }
    }
  }

  /**
   * Method description
   *
   * @return Document me!
   */
  @NotNull
  public String getComponentName() {
    return PluginConstants.PROJECT_COMPONENT_NAME;
  }

  /**
   * Method description
   *
   * @return Document me!
   */
  @Nls
  public String getDisplayName() {
    return PluginConstants
        .PLUGIN_PROJECT_DISPLAY_NAME;    // To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * Method description
   *
   * @return Document me!
   */
  @Nullable
  @NonNls
  public String getHelpTopic() {
    return null;
  }

  /**
   * Method description
   *
   * @return Document me!
   */
  public Icon getIcon() {
    return GuiUtils
        .createImageIcon(PluginConstants
            .ICON_APPLICATION_BIG);    // To change body of implemented methods use File | Settings | File Templates.
  }

  private void readPomFiles(VirtualFile virtualFile, Set<MavenProjectDocument> pomFileList) {
    VirtualFile[] children = virtualFile.getChildren();

    if (children == null) {
      return;
    }

    for (VirtualFile child : children) {
      if (child.getName().equals(PluginConstants.POM_FILE_NAME)) {
        if (!isPomInList(pomFileList, child)) {
          pomFileList.add(new MavenProjectDocumentImpl(child));
        }
      }

      readPomFiles(child, pomFileList);
    }
  }

  private Set<MavenProjectDocument> getPomFilesOfProject(ProjectRootManager projectRootManager) {
    VirtualFile[] contentRoots = projectRootManager.getContentRoots();

    for (VirtualFile contentRoot : contentRoots) {
      readPomFiles(contentRoot, mavenPomList);
    }

    return mavenPomList;
  }

  /**
   * Method description
   *
   * @return Document me!
   */
  public boolean isModified() {
    MavenProjectConfigurationForm form =
        (MavenProjectConfigurationForm) actionContext.getGuiContext().getProjectConfigurationForm();
    MavenBuildProjectPluginSettings pluginSettings = actionContext.getProjectPluginSettings();
    return (form != null) && form.isModified(pluginSettings);
  }

  private boolean isPomInList(Set<MavenProjectDocument> pomFileList, VirtualFile child) {
    for (MavenProjectDocument existingEntry : pomFileList) {
      if (existingEntry.getPomFile().getPath().equals(child.getPath())) {
        return true;
      }
    }

    return false;
  }
}
