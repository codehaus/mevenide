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
package org.mevenide.netbeans.project.writer;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.mevenide.context.IProjectContext;
import org.mevenide.context.IQueryContext;

import org.mevenide.netbeans.api.project.MavenProject;
import org.mevenide.netbeans.project.queries.MavenFileOwnerQueryImpl;
import org.mevenide.project.dependency.DependencyMatcher;
import org.mevenide.project.dependency.ExactDependencyReplacer;
import org.mevenide.project.dependency.IDependencyReplacer;
import org.mevenide.project.io.CarefulProjectMarshaller;
import org.mevenide.project.io.ElementContentProvider;
import org.mevenide.project.io.IContentProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.UserQuestionException;

/**
 * 
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public final class DependencyUpdater {

    private DependencyUpdater() {
    }
    
    public static void checkOpenedProjects(String artId, String grId, String version,
                                           String newArt, String newGrp, String newVersion) 
         throws Exception
    {
        
        MavenFileOwnerQueryImpl q = MavenFileOwnerQueryImpl.getInstance();
        Set projects = q.getOpenedProjects();
        Iterator it = projects.iterator();
        HashMap contextToProjectMap = new HashMap();
        while (it.hasNext()) {
            MavenProject proj = (MavenProject)it.next();
            contextToProjectMap.put(proj.getContext(), proj);
        }
        IQueryContext[] contexts = new IQueryContext[contextToProjectMap.size()];
        contexts = (IQueryContext[])contextToProjectMap.keySet().toArray(contexts);
        ExactDependencyReplacer replacer = new ExactDependencyReplacer(artId, grId, version, newArt, newGrp, newVersion);
        IQueryContext[] matching = DependencyMatcher.matchingContexts(contexts, replacer);
        if (matching.length > 0) {
            Set projs = new HashSet();
            for (int i = 0; i < matching.length; i++) {
                projs.add(contextToProjectMap.get(matching[i]));
            }
            JList lst = new JList();
            lst.setListData(projs.toArray());
            JButton[] buttons = new JButton[] {
                new JButton("Proceed All"),
                new JButton("Proceed Selected"),
                new JButton("Cancel")
            };
            NotifyDescriptor dd = new NotifyDescriptor(createPanel(lst), 
                                                       "Select Projects to update",
                                                       NotifyDescriptor.YES_NO_CANCEL_OPTION, 
                                                       NotifyDescriptor.QUESTION_MESSAGE,
                                                       buttons, 
                                                       buttons[0]);
            Object ret = DialogDisplayer.getDefault().notify(dd);
            if (ret == buttons[0]) {
                writePOMs(replacer, projs);
            }
            if (ret == buttons[1]) {
               Object[] sels = lst.getSelectedValues();
               writePOMs(replacer, Arrays.asList(sels));
            }
        }

    }
    
    private static void writePOMs(IDependencyReplacer replacer, Collection projects) throws Exception {
        Iterator it = projects.iterator();
        while (it.hasNext()) {
            MavenProject project = (MavenProject)it.next();
            IProjectContext context = project.getContext().getPOMContext();
            org.jdom.Element[] roots = context.getRootElementLayers();
            File[] files = context.getProjectFiles();
            //write now
            Writer writer = null;
            CountNewLinesReader reader = null;
            FileLock lock = null;
            try {
                for (int i = 0; i < files.length; i++) {
                    IContentProvider provider = new ElementContentProvider(roots[i]);
                    provider = DependencyMatcher.replace(replacer, provider);
                    FileObject fo = FileUtil.toFileObject(files[i]);
                    // read the current stream first..
                    reader = new CountNewLinesReader(fo.getInputStream());
                    SAXBuilder builder = new SAXBuilder();
                    Document originalDoc = builder.build(reader);
                    reader.close();
                    lock = fo.lock();
                    CarefulProjectMarshaller marshall = new CarefulProjectMarshaller(
                            NbProjectWriter.figureOutFormat(roots[i], reader));
                    writer = new OutputStreamWriter(fo.getOutputStream(lock));
                    marshall.marshall(writer, provider, originalDoc);
                }
            } catch (UserQuestionException exc) {
                throw new IOException("Cannot obtain lock. User interaction required.");
            }
            finally {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
                if (lock != null) {
                    lock.releaseLock();
                }
            }
        }
    }    

    
    private static JPanel createPanel(JList list) {
        JPanel toReturn = new JPanel();
        java.awt.GridBagConstraints gridBagConstraints;
        JLabel jLabel1 = new javax.swing.JLabel();
        JScrollPane jScrollPane1 = new javax.swing.JScrollPane();

        toReturn.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("<html><p>These projects contain the edited project as dependency.</p>"
                 + "<p>\nYou can update their dependency definitions automatically.</p></html>");
        jLabel1.setPreferredSize(new java.awt.Dimension(500, 35));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        toReturn.add(jLabel1, gridBagConstraints);

        jScrollPane1.setViewportView(list);
        list.setCellRenderer(new ProjRenderer());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        toReturn.add(jScrollPane1, gridBagConstraints);            
        return toReturn;
    }
    
    
    private static class ProjRenderer extends DefaultListCellRenderer {
        
        public Component getListCellRendererComponent(JList list, 
                Object value, 
                int index, 
                boolean isSelected,
                boolean cellHasFocus) {

            JLabel retValue;
            retValue = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            MavenProject proj = (MavenProject)value;
            retValue.setText(proj.getDisplayName());
            return retValue;
        }
        
    }
}

