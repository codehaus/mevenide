/* ==========================================================================
 * Copyright 2006 Mevenide Team
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



package org.codehaus.mevenide.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;

import org.codehaus.mevenide.idea.build.MavenOptions;
import org.codehaus.mevenide.idea.build.util.BuildConstants;
import org.codehaus.mevenide.idea.helper.ActionContext;
import org.codehaus.mevenide.idea.util.GuiUtils;
import org.codehaus.mevenide.idea.util.PluginConstants;

import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.*;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class ShowMavenOptionsAction extends AbstractBaseAction {
    private JPopupMenu mavenOptionsPopupMenu;

    public ShowMavenOptionsAction() {}

    /**
     * Constructs ...
     *
     * @param context     Document me!
     * @param text        Document me!
     * @param description Document me!
     * @param icon        Document me!
     */
    public ShowMavenOptionsAction(ActionContext context, String text, String description, Icon icon) {
        super(text, description, icon);
        this.actionContext = context;
        this.mavenOptionsPopupMenu = createPopup();
    }

    /**
     * Method description
     *
     * @param actionEvent Document me!
     */
    public void actionPerformed(AnActionEvent actionEvent) {
        String actionText = actionEvent.getPresentation().getText();
        MouseEvent event = (MouseEvent) actionEvent.getInputEvent();

        if (actionText.equals(PluginConstants.ACTION_COMMAND_SHOW_MAVEN_OPTIONS)) {
            getAndUpdateMavenOptionsPopupMenu(actionContext, mavenOptionsPopupMenu).show(event.getComponent(),
                                              event.getX(), event.getY());

//          ActionUtils.chooseAndAddPluginToPom(context);
        }
    }

    private JPopupMenu createPopup() {
        JPopupMenu popupMenu = new JPopupMenu();

        GuiUtils.addCheckBoxMenuItem(popupMenu, BuildConstants.MAVEN_OPTION_STRICT_CHECKSUM,
                                     new MavenOptionsMenuActionListener(actionContext));
        GuiUtils.addCheckBoxMenuItem(popupMenu, BuildConstants.MAVEN_OPTION_LAX_CHECKSUM,
                                     new MavenOptionsMenuActionListener(actionContext));
        GuiUtils.addCheckBoxMenuItem(popupMenu, BuildConstants.MAVEN_OPTION_FAIL_FAST,
                                     new MavenOptionsMenuActionListener(actionContext));
        GuiUtils.addCheckBoxMenuItem(popupMenu, BuildConstants.MAVEN_OPTION_FAIL_AT_END,
                                     new MavenOptionsMenuActionListener(actionContext));
        GuiUtils.addCheckBoxMenuItem(popupMenu, BuildConstants.MAVEN_OPTION_FAIL_NEVER,
                                     new MavenOptionsMenuActionListener(actionContext));
        GuiUtils.addCheckBoxMenuItem(popupMenu, BuildConstants.MAVEN_OPTION_BATCH_MODE,
                                     new MavenOptionsMenuActionListener(actionContext));
        GuiUtils.addCheckBoxMenuItem(popupMenu, BuildConstants.MAVEN_OPTION_UPDATE_PLUGINS,
                                     new MavenOptionsMenuActionListener(actionContext));
        GuiUtils.addCheckBoxMenuItem(popupMenu, BuildConstants.MAVEN_OPTION_NON_RECURSIVE,
                                     new MavenOptionsMenuActionListener(actionContext));
        GuiUtils.addCheckBoxMenuItem(popupMenu, BuildConstants.MAVEN_OPTION_NO_PLUGIN_REGISTRY,
                                     new MavenOptionsMenuActionListener(actionContext));
        GuiUtils.addCheckBoxMenuItem(popupMenu, BuildConstants.MAVEN_OPTION_UPDATE_SNAPSHOTS,
                                     new MavenOptionsMenuActionListener(actionContext));
        GuiUtils.addCheckBoxMenuItem(popupMenu, BuildConstants.MAVEN_OPTION_SKIP_TESTS,
                                     new MavenOptionsMenuActionListener(actionContext));
        GuiUtils.addCheckBoxMenuItem(popupMenu, BuildConstants.MAVEN_OPTION_NO_PLUGIN_UPDATES,
                                     new MavenOptionsMenuActionListener(actionContext));
        GuiUtils.addCheckBoxMenuItem(popupMenu, BuildConstants.MAVEN_OPTION_CHECK_PLUGIN_UPDATES,
                                     new MavenOptionsMenuActionListener(actionContext));
        GuiUtils.addCheckBoxMenuItem(popupMenu, BuildConstants.MAVEN_OPTION_DEBUG,
                                     new MavenOptionsMenuActionListener(actionContext));
        GuiUtils.addCheckBoxMenuItem(popupMenu, BuildConstants.MAVEN_OPTION_ERRORS,
                                     new MavenOptionsMenuActionListener(actionContext));
        GuiUtils.addCheckBoxMenuItem(popupMenu, BuildConstants.MAVEN_OPTION_OFFLINE,
                                     new MavenOptionsMenuActionListener(actionContext));
        GuiUtils.addCheckBoxMenuItem(popupMenu, BuildConstants.MAVEN_OPTION_REACTOR,
                                     new MavenOptionsMenuActionListener(actionContext));

        return popupMenu;
    }

    /**
     * Returns the Maven options popup menu. Before returning the menu, the project options are
     * evaluated, so that the popup menu reflects the currently activated options in the settings
     * dialog.
     *
     * @param actionContext         The action context.
     * @param mavenOptionsPopupMenu The options popup menu.
     *
     * @return The popup menu.
     */
    public JPopupMenu getAndUpdateMavenOptionsPopupMenu(ActionContext actionContext, JPopupMenu mavenOptionsPopupMenu) {
        MenuElement[] menuElements = mavenOptionsPopupMenu.getSubElements();
        MavenOptions options = actionContext.getProjectPluginSettings().getMavenOptions();
        Hashtable optionMap = options.getMavenOptionList();
        Enumeration optionEnumerator = optionMap.keys();
        List<String> menuElementListAsString = new ArrayList<String>();

        for (MenuElement menuElement : menuElements) {
            JCheckBoxMenuItem item = (JCheckBoxMenuItem) menuElement;

            menuElementListAsString.add(item.getText());
        }

        List<MenuElement> menuElementList = Arrays.asList(menuElements);

        while (optionEnumerator.hasMoreElements()) {
            String optionName = (String) optionEnumerator.nextElement();
            JCheckBoxMenuItem menuItem =
                (JCheckBoxMenuItem) menuElementList.get(menuElementListAsString.indexOf(optionName));

            menuItem.setSelected((Boolean) optionMap.get(optionName));
        }

        return mavenOptionsPopupMenu;
    }
}
