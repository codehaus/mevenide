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
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.JDomProjectUnmarshaller;
import org.mevenide.genapp.GenAppTemplateFinder;
import org.mevenide.genapp.TemplateInfo;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;
import org.openide.modules.InstalledFileLocator;



/**
 * Wizard for creating maven projects.
 */
public class MavenNewWizardIterator extends GenAppWizardIterator {

    private static final Log logger = LogFactory.getLog(MavenNewWizardIterator.class);
//    private static final String TEMPLATE_LOC_ATTR = "MavenTemplateLocation"; //NOI18N

    private static final long serialVersionUID = 13334234343323432L;
    private String templatename;
    
    /** Create a new wizard iterator. */
    public MavenNewWizardIterator() {
    }
    
    protected WizardDescriptor.Panel[] createPanels () {
        return new WizardDescriptor.Panel[] {
                new CreateProjectPanel()
            };
    }
    
    protected String[] createSteps() {
            return new String[] {
                "Setup Maven Project", 
            };
    }
    
    
    public void initialize(TemplateWizard wiz) {
        FileObject templateFO = Templates.getTemplate(wiz);
        templatename = (String)templateFO.getAttribute("templatename");
        if (templatename == null) {
            throw new IllegalStateException("no templatename attribute defined on template.");
        }

        GenAppTemplateFinder finder = new GenAppTemplateFinder(DefaultQueryContext.getNonProjectContextInstance());
        TemplateInfo[] infos = finder.getTemplates(getCustomTemplateLocation().getParentFile());
        TemplateInfo myInfo = null;
        for (int i = 0; i < infos.length; i++) {
            if (templatename.equals(infos[i].getName())) {
                myInfo = infos[i];
                break;
            }
        }
        if (myInfo == null) {
            throw new IllegalStateException("template by name '" + templatename +  "' not installed.");
        }
        wiz.putProperty(TEMPLATE, myInfo);
        super.initialize(wiz);
    }
    
   /**
     * overridable from subclasses
     */
    public File getCustomTemplateLocation() {
        return new File(InstalledFileLocator.getDefault().locate("maven-project-templates", null, false), templatename);
    }    
    
}
