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
package org.mevenide.netbeans.project.wizards;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.genapp.TemplateInfo;
import org.mevenide.netbeans.project.exec.BeanRunContext;
import org.mevenide.netbeans.project.exec.DefaultRunConfig;
import org.mevenide.netbeans.project.exec.MavenExecutor;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;



/**
 * Wizard for creating maven projects.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class GenAppWizardIterator implements TemplateWizard.Iterator {

    private static final Log logger = LogFactory.getLog(GenAppWizardIterator.class);
//    private static final String TEMPLATE_LOC_ATTR = "MavenTemplateLocation"; //NOI18N

    private static final long serialVersionUID = 13334234343323432L;
    
    private transient int index;
    private transient WizardDescriptor.Panel[] panels;
    
    public static final String TEMPLATE = "gatemplate"; //NOI18N
    
    public static final String PROPERTY_PREFIX = "property.";
    
    /** Create a new wizard iterator. */
    public GenAppWizardIterator() {
    }
    
    protected WizardDescriptor.Panel[] createPanels () {
        return new WizardDescriptor.Panel[] {
                new GenAppTemplatePanel(),
                new GenAppPropsPanel()
            };
    }
    
    protected String[] createSteps() {
            return new String[] {
                "Choose GenApp template",
                "Fill out properties"
            };
    }
    
    
    public java.util.Set instantiate (org.openide.loaders.TemplateWizard wiz) throws java.io.IOException {
        Set resultSet = new HashSet ();
        String dir = (String)wiz.getProperty("projectDir");        //NOI18N
        String name = (String)wiz.getProperty("artifactID");        //NOI18N
        FileObject dirFo = FileUtil.toFileObject(new File(dir));
        if (dirFo == null) {
            throw new IOException("Base Directory doesn't exist");
        }
//        if (templateRoot == null) {
//            throw new IOException("Wrong template definition. File a bug against mevenide");
//        }
        FileObject projectDir = dirFo.createFolder(name);
        
        // now let's construct what gets executed.
        TemplateInfo info = (TemplateInfo)wiz.getProperty(TEMPLATE);
        String[] params = info.getParameters();
        boolean customTemplateLoc = getCustomTemplateLocation() != null;
        String[] add = new String[params.length + (customTemplateLoc ? 3: 2)];
        add[0] = "genapp";
        add[1] = "-Dmaven.genapp.template=" + info.getName();
        for (int i = 0; i < params.length; i++) {
            String val = (String)wiz.getProperty(PROPERTY_PREFIX + params[i]);
            if (val == null) {
                val = info.getDefaultValue(params[i]);
            }
            add[i + 2] = "-Dmaven.genapp.template." + params[i] + "=" + val;
        }
        if (customTemplateLoc) {
            add[add.length - 1] = "-Dmaven.genapp.template.dir=" + getCustomTemplateLocation().getAbsolutePath();
        }
        BeanRunContext context = new BeanRunContext("GenApp", 
                    ConfigUtils.getDefaultLocationFinder().getMavenHome(), 
                    FileUtil.toFile(projectDir), add);
        MavenExecutor exec = new MavenExecutor(context, "", Collections.EMPTY_SET, new DefaultRunConfig());
        ExecutorTask task = ExecutionEngine.getDefault().execute("Maven", exec, exec.getInputOutput());
        // wait finished kind of ugly, but what can we do here?
        task.waitFinished();
        
        resultSet.add (DataObject.find(projectDir));
        return resultSet;
    }
    
    /**
     * overridable from subclasses
     */
    public File getCustomTemplateLocation() {
        return null;
    }
    
    public void initialize(TemplateWizard wiz) {
        FileObject templateFO = Templates.getTemplate(wiz);
        wiz.putProperty("projectDir", System.getProperty("user.home"));
        index = 0;
        panels = createPanels();
        // Make sure list of steps is accurate.
        String[] steps = createSteps();
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            if (steps[i] == null) {
                // Default step name to component name of panel.
                // Mainly useful for getting the name of the target
                // chooser to appear in the list of steps.
                steps[i] = c.getName();
            }
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent)c;
                // Step #.
                jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i)); // NOI18N
                // Step name (actually the whole list for reference).
                jc.putClientProperty("WizardPanel_contentData", steps); // NOI18N
            }
        }
    }
    
    public void uninitialize(TemplateWizard wizard) {
//        wizard.putProperty("projectDir",null); //NOI18N
        wizard.putProperty("artifactID",null); //NOI18N
        wizard = null;
        panels = null;
    }
    
    public String name() {
        return MessageFormat.format("name {0}/{1}",
            new Object[] {new Integer (index + 1), new Integer (panels.length) });                                
    }
    
    public boolean hasNext() {
        return index < panels.length - 1;
    }
    public boolean hasPrevious() {
        return index > 0;
    }
    public void nextPanel() {
        if (!hasNext()) throw new NoSuchElementException();
        index++;
    }
    public void previousPanel() {
        if (!hasPrevious()) throw new NoSuchElementException();
        index--;
    }
    public WizardDescriptor.Panel current () {
        return panels[index];
    }
    
    // If nothing unusual changes in the middle of the wizard, simply:
    public final void addChangeListener(ChangeListener l) {}
    public final void removeChangeListener(ChangeListener l) {}
    
}
