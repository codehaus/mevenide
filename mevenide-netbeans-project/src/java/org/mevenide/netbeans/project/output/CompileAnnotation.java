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

package org.mevenide.netbeans.project.output;

import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.project.MavenProject;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Annotatable;
import org.openide.text.Annotation;
import org.openide.text.Line;
import org.openide.util.WeakSet;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public final class CompileAnnotation extends Annotation implements PropertyChangeListener, OutputListener {
    private static final Log logger = LogFactory.getLog(CompileAnnotation.class);
    
    private static final Set hyperlinks = new WeakSet(); // Set<Hyperlink>
    private boolean dead = false;
    
    public static void detachAllAnnotations() {
        synchronized (hyperlinks) {
            Iterator it = hyperlinks.iterator();
            while (it.hasNext()) {
                ((CompileAnnotation)it.next()).destroy();
            }
        }
    }
    
    private MavenProject project;
    private File clazzfile;
    private int lineNum;
    private String text;
    
    public CompileAnnotation(MavenProject proj, String clazz, String line, String textAnn) {
        clazzfile = new File(clazz + ".java");
        project = proj;
        text = textAnn;
        try {
            lineNum = Integer.parseInt(line);
        } catch (NumberFormatException exc) {
            lineNum = -1;
        }
        synchronized (hyperlinks) {
            hyperlinks.add(this);
        }
        
    }
    
    
    
    public void outputLineSelected(OutputEvent ev) {
        //           cookie.getLineSet().getCurrent(line).show(Line.SHOW_SHOW);
    }
    
    /** Called when some sort of action is performed on a line.
     * @param ev the event describing the line
     */
    public void outputLineAction(OutputEvent ev) {
        FileObject file = FileUtil.toFileObject(clazzfile);
        if (file == null) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        try {
            DataObject dob = DataObject.find(file);
            EditorCookie ed = (EditorCookie) dob.getCookie(EditorCookie.class);
            if (ed != null && file == dob.getPrimaryFile()) {
                if (lineNum == -1) {
                    ed.open();
                } else {
                    ed.openDocument();
                    try {
                        Line l = ed.getLineSet().getOriginal(lineNum - 1);
                        if (! l.isDeleted()) {
                            l.show(Line.SHOW_GOTO);
                        }
                    } catch (IndexOutOfBoundsException ioobe) {
                        // Probably harmless. Bogus line number.
                        ed.open();
                    }
                }
                attachAllInFile(ed, this);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        } catch (DataObjectNotFoundException donfe) {
            logger.warn("DO not found.", donfe);
        } catch (IOException ioe) {
            logger.warn(ioe);
        }
    }
    
    private static void attachAllInFile(EditorCookie cook, CompileAnnotation annot) {
        Set newSet = null;
        synchronized (hyperlinks) {
            newSet = new HashSet(hyperlinks);
        }
        Iterator it = newSet.iterator();
        while (it.hasNext()) {
            CompileAnnotation ann = (CompileAnnotation)it.next();
            if (ann.getFile().equals(annot.getFile())) {
                if (ann.getLine() != -1) {
                    Line l = cook.getLineSet().getOriginal(ann.getLine() - 1);
                    if (! l.isDeleted()) {
                        ann.attachAsNeeded(l);
                    }
                }
                
            }
        }
    }
    
    /** Called when a line is cleared from the buffer of known lines.
     * @param ev the event describing the line
     */
    public void outputLineCleared(OutputEvent ev) {
        doDetach();
    }
    
    
    
    
    void destroy() {
        doDetach();
        dead = true;
    }
    
    private synchronized void attachAsNeeded(Line l) {
        if (getAttachedAnnotatable() == null) {
            Annotatable ann = l;
            attach(ann);
            /**            Iterator it = hyperlinks.iterator();
            while (it.hasNext()) {
                PmdAnnotation h = (PmdAnnotation)it.next();
                if (h != this) {
                    h.doDetach();
                }
            }
             */
            ann.addPropertyChangeListener(this);
        }
    }
    
    
    private synchronized void doDetach() {
        Annotatable ann = getAttachedAnnotatable();
        if (ann != null) {
            ann.removePropertyChangeListener(this);
            detach();
        }
    }
    
    public void propertyChange(PropertyChangeEvent ev) {
        if (dead) return;
        String prop = ev.getPropertyName();
        if (    prop == null ||
                prop.equals(Annotatable.PROP_TEXT) ||
                prop.equals(Annotatable.PROP_DELETED)) {
            doDetach();
        }
    }
    
    public String getAnnotationType() {
        return "org-mevenide-netbeans-project-error"; // NOI18N
    }
    
    public String getShortDescription() {
        return text;
    }
    
    public File getFile() {
        return clazzfile;
    }
    
    public int getLine() {
        return lineNum;
    }
    
    public String toString() {
        return "javaerror[" + clazzfile + ":" + lineNum + ":" + text + "]"; // NOI18N
    }
    
}
