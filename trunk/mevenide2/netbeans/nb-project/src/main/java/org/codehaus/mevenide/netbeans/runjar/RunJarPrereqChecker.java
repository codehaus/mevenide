/*
 *  Copyright 2008 mkleint.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.codehaus.mevenide.netbeans.runjar;

import java.awt.Dialog;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.codehaus.mevenide.netbeans.MavenSourcesImpl;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.api.execute.ActiveJ2SEPlatformProvider;
import org.codehaus.mevenide.netbeans.api.execute.PrerequisitesChecker;
import org.codehaus.mevenide.netbeans.api.execute.RunConfig;
import org.codehaus.mevenide.netbeans.customizer.CustomizerProviderImpl;
import org.codehaus.mevenide.netbeans.execute.ActionToGoalUtils;
import org.codehaus.mevenide.netbeans.execute.UserActionGoalProvider;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.mevenide.netbeans.execute.model.io.xpp3.NetbeansBuildActionXpp3Reader;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ActionProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.MouseUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author mkleint
 */
public class RunJarPrereqChecker implements PrerequisitesChecker {

    private String mainClass;
    
    public boolean checkRunConfig(String actionName, RunConfig config) {
        if ((ActionProvider.COMMAND_RUN.equals(actionName) ||
                ActionProvider.COMMAND_DEBUG.equals(actionName)) &&  
                ProjectURLWatcher.TYPE_JAR.equals(
                      config.getProject().getLookup().lookup(ProjectURLWatcher.class).getPackagingType())) 
        {
            Set<Map.Entry<Object, Object>> entries = config.getProperties().entrySet();
            String mc = null;
            for (Map.Entry<Object, Object> str : entries) {
                if ("exec.executable".equals(str.getKey())) { //NOI18N
                    // check for "java" and replace it with absolute path to 
                    // project j2seplaform's java.exe
                    String val = (String) str.getValue();
                    if ("java".equals(val)) { //NOI18N
                        ActiveJ2SEPlatformProvider plat = config.getProject().getLookup().lookup(ActiveJ2SEPlatformProvider.class);
                        assert plat != null;
                        FileObject fo = plat.getJavaPlatform().findTool(val);
                        if (fo != null) {
                            File fl = FileUtil.toFile(fo);
                            config.getProperties().setProperty("exec.executable", fl.getAbsolutePath()); //NOI18N
                        }
                    }
                }
                String val = (String) str.getValue();
                if (val.contains("${packageClassName}")) { //NOI18N
                    //show dialog to choose main class.
                    if (mc == null) {
                        mc = eventuallyShowDialog(config.getProject(), actionName);
                    }
                    if (mc == null) {
                        return false;
                    }
                    val = val.replace("${packageClassName}", mc); //NOI18N
                    str.setValue(val);
                }
            }
        }
        return true;
    }

    private String eventuallyShowDialog(NbMavenProject project, String actionName) {
        if (mainClass != null) {
            return mainClass;
        }
        List<FileObject> roots = new ArrayList<FileObject>();
        Sources srcs =  ProjectUtils.getSources(project);
        SourceGroup[] grps = srcs.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        for (int i = 0; i < grps.length; i++) {
            SourceGroup sourceGroup = grps[i];
            if (MavenSourcesImpl.NAME_SOURCE.equals(sourceGroup.getName())) {
                roots.add(sourceGroup.getRootFolder());
            }
        }
        grps = srcs.getSourceGroups(MavenSourcesImpl.TYPE_GEN_SOURCES);
        for (int i = 0; i < grps.length; i++) {
            SourceGroup sourceGroup = grps[i];
            roots.add(sourceGroup.getRootFolder());
        }
        final JButton okButton  = new JButton (NbBundle.getMessage (RunJarPrereqChecker.class, "LBL_ChooseMainClass_OK"));
//        JButton okButton.getAccessibleContext().setAccessibleDescription (NbBundle.getMessage (RunJarPanel.class, "AD_ChooseMainClass_OK"));


        final MainClassChooser panel = new MainClassChooser(roots.toArray(new FileObject[0]));
        Object[] options = new Object[]{
            okButton,
            DialogDescriptor.CANCEL_OPTION
        };
        panel.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                if (e.getSource() instanceof MouseEvent && MouseUtils.isDoubleClick(((MouseEvent) e.getSource()))) {
                    // click button and finish the dialog with selected class
                    okButton.doClick();
                } else {
                    okButton.setEnabled(panel.getSelectedMainClass() != null);
                }
            }
        });
        panel.rbSession.setSelected(true);
        okButton.setEnabled(false);
        DialogDescriptor desc = new DialogDescriptor(
                panel,
                NbBundle.getMessage(RunJarPrereqChecker.class, "LBL_ChooseMainClass_Title"),
                true,
                options,
                options[0],
                DialogDescriptor.BOTTOM_ALIGN,
                null,
                null);
        //desc.setMessageType (DialogDescriptor.INFORMATION_MESSAGE);
        Dialog dlg = DialogDisplayer.getDefault().createDialog(desc);
        dlg.setVisible(true);
        if (okButton == desc.getValue()) {
            if (panel.rbSession.isSelected()) {
                mainClass = panel.getSelectedMainClass();
            } else if (panel.rbPermanent.isSelected()) {
                writeMapping(actionName, project, panel.getSelectedMainClass());
            }
            return panel.getSelectedMainClass();
        }
        return null;
    }

    private void writeMapping(String actionName, NbMavenProject project, String clazz) {
        try {
            UserActionGoalProvider usr = project.getLookup().lookup(org.codehaus.mevenide.netbeans.execute.UserActionGoalProvider.class);
            ActionToGoalMapping mapping = new NetbeansBuildActionXpp3Reader().read(new StringReader(usr.getRawMappingsAsString()));
            NetbeansActionMapping mapp = ActionToGoalUtils.getDefaultMapping(actionName, project);
            mapping.addAction(mapp);
            Set<Map.Entry<Object, Object>> entries = mapp.getProperties().entrySet();
            String mc = null;
            for (Map.Entry<Object, Object> str : entries) {
                String val = (String) str.getValue();
                if (val.contains("${packageClassName}")) { //NOI18N
                    //show dialog to choose main class.
                    val = val.replace("${packageClassName}", clazz); //NOI18N
                    str.setValue(val);
                }
            }
            CustomizerProviderImpl.writeNbActionsModel(project.getProjectDirectory(), mapping);
        } catch (Exception e) {
            Exceptions.attachMessage(e, "Cannot persist action configuration.");
            Exceptions.printStackTrace(e);
        }
    }
}
