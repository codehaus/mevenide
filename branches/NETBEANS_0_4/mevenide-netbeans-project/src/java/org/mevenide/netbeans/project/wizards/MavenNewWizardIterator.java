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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.DefaultJDOMFactory;
import org.jdom.JDOMFactory;
import org.jdom.output.XMLOutputter;
import org.mevenide.context.JDomProjectUnmarshaller;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;



/**
 * Wizard for creating maven projects.
 */
public class MavenNewWizardIterator implements TemplateWizard.Iterator {

    private static final Log logger = LogFactory.getLog(MavenNewWizardIterator.class);
//    private static final String TEMPLATE_LOC_ATTR = "MavenTemplateLocation"; //NOI18N

    private static final long serialVersionUID = 13334234343323432L;
    
    private transient int index;
    private transient WizardDescriptor.Panel[] panels;
    private transient TemplateWizard wiz;
    
    
    /** Create a new wizard iterator. */
    public MavenNewWizardIterator() {
    }
    
    private WizardDescriptor.Panel[] createPanels () {
        return new WizardDescriptor.Panel[] {
                new CreateProjectPanel()
            };
    }
    
    private String[] createSteps() {
            return new String[] {
                "Setup Maven Project", 
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
        File directory = new File(dir, name);
        unzip(wiz.getTemplate().getPrimaryFile().getInputStream(), directory);
        FileObject proj = projectDir.getFileObject("project","xml");
        if (proj != null) {
            try {
                JDomProjectUnmarshaller mars = new JDomProjectUnmarshaller();
                JDOMFactory factory = new DefaultJDOMFactory();
                Element project = mars.parseRootElement(new InputStreamReader(proj.getInputStream()));
                setElementValue(project, "currentVersion", wiz.getProperty("version"), factory);
                setElementValue(project, "package", wiz.getProperty("packageName"), factory);
                setElementValue(project, "groupId", wiz.getProperty("groupID"), factory);
                setElementValue(project, "artifactId", wiz.getProperty("artifactID"), factory);
                setElementValue(project, "name", wiz.getProperty("artifactID"), factory);
                XMLOutputter outputter = new XMLOutputter();
                outputter.output(project.getDocument(), proj.getOutputStream(proj.lock()));
            } catch (Exception exc) {
                logger.warn("Cannot read template.", exc);
            }
        } else {
            System.out.println("no project file..");
        }
       
        resultSet.add (DataObject.find(projectDir));
        return resultSet;
    }
    
    private void setElementValue(Element parent, String elemName, Object value, JDOMFactory factory) {
        Element child = parent.getChild(elemName);
        if (child == null) {
            child = factory.element(elemName);
            parent.addContent(child);
        }
        child.setText(value != null ? value.toString() : "");
    }
    
    public void initialize(TemplateWizard wiz) {
        this.wiz = wiz;
        FileObject templateFO = Templates.getTemplate(wiz);
//        if (templateFO != null) {
//            String path = (String)templateFO.getAttribute("MavenTemplateLocation"); //NOI18N
//            if (path != null) {
//                templateRoot = Repository.getDefault().getDefaultFileSystem().findResource(path);
//            }
//        }
//        if (templateRoot != null) {
//            FileObject proj = templateRoot.getFileObject("project","xml");
//            if (proj != null) {
//                try {
//                    JDomProjectUnmarshaller mars = new JDomProjectUnmarshaller();
//                    Element project = mars.parseRootElement(new InputStreamReader(proj.getInputStream()));
//                    wiz.putProperty("version", project.getChildText("currentVersion"));
//                    wiz.putProperty("packageName", project.getChildText("package"));
//                    wiz.putProperty("groupID", project.getChildText("groupId"));
//                    wiz.putProperty("artifactID", project.getChildText("artifactId"));
//                } catch (Exception exc) {
//                    logger.warn("Cannot read template.", exc);
//                }
//            }
//        } else {
//            // not properly configured template..
//            logger.error("Cannot Find the root folder for template.");
//        }
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
    public void uninitialize(TemplateWizard wiz) {
        this.wiz.putProperty("projdir",null); //NOI18N
        this.wiz.putProperty("artifactId",null); //NOI18N
        this.wiz.putProperty("groupId",null); //NOI18N
        this.wiz.putProperty("package",null); //NOI18N
        this.wiz.putProperty("version",null); //NOI18N
        this.wiz = null;
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
    
    static String getPackageName (String displayName) {
        StringBuffer builder = new StringBuffer ();
        boolean changeCase = false;
        for (int i=0; i< displayName.length(); i++) {
            char c = displayName.charAt(i);            
            if ((i != 0 && Character.isJavaIdentifierPart (c)) || (i == 0 && Character.isJavaIdentifierStart(c))) {
                if (changeCase) {
                    if (Character.isLetter(c)) {
                        c = Character.toUpperCase(c);
                    }
                    changeCase = false;
                }
                else if (Character.isUpperCase(c)) {
                    c = Character.toLowerCase(c);
                }                    
                builder.append(c);
            }
            else {
                changeCase = true;
            }
        }
        return builder.toString();
    }
    
    private static void unzip(InputStream source, File targetFolder) throws IOException {
        ZipInputStream zip=new ZipInputStream(source);
        try {
            ZipEntry ent;
            while ((ent = zip.getNextEntry()) != null) {
                File f = new File(targetFolder, ent.getName());
                if (ent.isDirectory()) {
                    f.mkdirs();
                } else {
                    f.getParentFile().mkdirs();
                    FileOutputStream out = new FileOutputStream(f);
                    try {
                        FileUtil.copy(zip, out);
                    } finally {
                        out.close();
                    }
                }
            }
        } finally {
            zip.close();
        }
    }
    
}
